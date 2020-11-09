package com.swaylight

import android.annotation.SuppressLint
import android.graphics.Point
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.swaylight.custom_ui.TopLightView
import com.swaylight.library.SLMqttClient
import com.swaylight.library.SLMqttManager
import com.swaylight.library.data.SLDisplay
import com.swaylight.library.data.SLMode
import com.swaylight.library.data.SLTopic
import org.eclipse.paho.client.mqttv3.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.atan2


class SwayLightMainActivity : AppCompatActivity() {

    val tag = SwayLightMainActivity::class.java.simpleName

    // UI
    private lateinit var progressView: ConstraintLayout
    private lateinit var btNetworkConfig: Button
    private lateinit var rootConstraint: ConstraintLayout
    private lateinit var lightTopConstraint: ConstraintLayout
    private lateinit var modeGroup: LinearLayout
    private lateinit var ivRing: TopLightView
    private lateinit var tvZoom: TextView
    private lateinit var tvBrightness: TextView
    private lateinit var btDebug: View
    private lateinit var btLight: Button
    private lateinit var btMusic: Button
    private lateinit var tvLog: TextView
    private lateinit var logView: ConstraintLayout

    // values
    var manager: SLMqttManager? = null
    var client: SLMqttClient? = null
    var DATE_FORMAT = SimpleDateFormat("HH:mm:ss.SSS")
    private val MAX_LOG_SIZE = 3000
    private val MQTT_TAG = "mqtt"
    private val qos = 0
    private lateinit var broker: String
    private lateinit var deviceName: String
    private var clientId: String? = null

    var mode = SLMode.LIGHT
    var ringCenterX = 0
    var ringCenterY = 0
    var ringStartRotate = 0f
    var ringPrevRotate = 0f

    var controlZoomFlag = true
    var prevZoom = 4
    var prevBrightness = 100
    var lightSlideStartY = 0f

    private val lightRectF = Rect()
    private val musicRectF = Rect()
    private lateinit var lightAnimation: TranslateAnimation
    private lateinit var musicAnimation: TranslateAnimation

    // MQTT Objects
    var displayObj = SLDisplay(4, 0)

    // const
    val MAX_ZOOM = 32
    val MIN_ZOOM = 4
    val MAX_BRIGHTNESS = 100

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sway_light_main)
        this.window.statusBarColor = ContextCompat.getColor(applicationContext, android.R.color.black)
        supportActionBar?.hide()
        initUi()

        // Block control view and show progress view.
        // Delay 3 secs to show config button.
        progressView.visibility = View.VISIBLE
        Handler().postDelayed({
            btNetworkConfig.visibility = View.VISIBLE
        }, 3000)
        btNetworkConfig.setOnClickListener {
            this.onBackPressed()
        }
        tvLog.movementMethod = ScrollingMovementMethod()
        tvLog.setOnLongClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Clear log?")
            builder.setPositiveButton("Yes") { dialog, which ->
                tvLog.scrollTo(0, 0)
                tvLog.text = ""
            }
            builder.setNegativeButton("Cancel") { dialog, which -> }
            builder.show()
            false
        }

        btDebug.setOnLongClickListener {
            logView.visibility = if (logView.visibility == View.VISIBLE) {
                View.INVISIBLE
            } else {
                View.VISIBLE
            }
            true
        }
        rootConstraint.viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val params: ConstraintLayout.LayoutParams = lightTopConstraint.layoutParams as ConstraintLayout.LayoutParams

                params.height = (rootConstraint.width * 0.9).toInt()
                params.width = (rootConstraint.width)
                lightTopConstraint.layoutParams = params
                val location = intArrayOf(0, 0)
                ivRing.getLocationOnScreen(location)
                ringCenterX = location[0] + ivRing.width / 2
                ringCenterY = location[1] + ivRing.height / 2
//                Log.d(tag, "ring h:${ivRing.height}, w:${ivRing.width}")
//                Log.d(tag, "x:${ringCenterX}, y:${ringCenterY}")

                // 延後一下在remove listener
                Handler().postDelayed({
                    // set btLight, btMusic animation
                    val offset = Point()
                    btLight.getGlobalVisibleRect(lightRectF, offset)
                    btMusic.getGlobalVisibleRect(musicRectF)
                    lightAnimation = TranslateAnimation(
                            Animation.ABSOLUTE,
                            musicRectF.left.toFloat(),
                            Animation.ABSOLUTE,
                            lightRectF.left.minus(offset.x).toFloat(),
                            Animation.ABSOLUTE,
                            0f,
                            Animation.ABSOLUTE,
                            0f,
                    )
                    musicAnimation = TranslateAnimation(
                            Animation.ABSOLUTE,
                            lightRectF.left.minus(lightRectF.width() + offset.x).toFloat(),
                            Animation.ABSOLUTE,
                            lightRectF.left.minus(offset.x).toFloat(),
                            Animation.ABSOLUTE,
                            0f,
                            Animation.ABSOLUTE,
                            0f,
                    )
                    lightAnimation.duration = 400
                    musicAnimation.duration = 400
                    rootConstraint.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }, 100)
            }
        })

        ivRing.setOnTouchListener { v, event ->
            val degree = getAngle(
                    ringCenterX.toFloat(),
                    ringCenterY.toFloat(),
                    event.rawX,
                    event.rawY)
            // degree:
            // 45       90      135
            //          |
            // 0        |       179
            // ---------|----------
            // 0        |      -179
            //          |
            //-45      -90     -135
            Log.d(tag, "angle:$degree, rotation:${ivRing.rotation}")
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    ringStartRotate = degree
                }
                MotionEvent.ACTION_UP -> {
                    ringPrevRotate = ivRing.offsetAngle
                }
                else -> {
                    val offset = (ringPrevRotate + degree - ringStartRotate).div(TopLightView.ANGLE_UNIT)
                    Log.d(tag, "offset:${offset.toInt()}")
                    ivRing.offsetValue = offset.toInt()
                    if (displayObj.offset != ivRing.offsetValue) {
                        displayObj.offset = ivRing.offsetValue
                        client!!.publish(SLTopic.MUSIC_MODE_DISPLAY, deviceName, displayObj.instance)
                        client!!.publish(SLTopic.LIGHT_MODE_DISPLAY, deviceName, displayObj.instance)
                    }
                }
            }
            true
        }

        lightTopConstraint.setOnTouchListener{ v, event ->
            val delta = ((lightSlideStartY - event.rawY) / 20).toInt()
            var v: Int = 0
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // 右半邊控制亮度/左半邊控制縮放
                    lightSlideStartY = event.rawY
                    controlZoomFlag = (event.rawX <= lightTopConstraint.width / 2)
                }
                MotionEvent.ACTION_UP -> {
                    if (controlZoomFlag) {
                        v = prevZoom + delta
                        if (v > MAX_ZOOM) {
                            v = MAX_ZOOM
                        } else if (v <= 0) {
                            v = 0
                        }
                        prevZoom = v
                    } else {
                        v = prevBrightness + delta
                        if (v > MAX_BRIGHTNESS) {
                            v = MAX_BRIGHTNESS
                        } else if (v <= 0) {
                            v = 0
                        }
                        prevBrightness = v
                    }
                }
                else -> {
                    if(controlZoomFlag) {
                        v = prevZoom + delta
                        if(v > MAX_ZOOM) {
                            v = MAX_ZOOM
                        }else if(v < MIN_ZOOM) {
                            v = MIN_ZOOM
                        }
                        ivRing.zoomValue = v
                        tvZoom.text = "zoom:" + v
                        if (displayObj.zoom != ivRing.zoomValue) {
                            displayObj.zoom = ivRing.zoomValue
                            client!!.publish(SLTopic.MUSIC_MODE_DISPLAY, deviceName, displayObj.instance)
                            client!!.publish(SLTopic.LIGHT_MODE_DISPLAY, deviceName, displayObj.instance)
                        }
                    }else {
                        v = prevBrightness + delta
                        if(v > MAX_BRIGHTNESS) {
                            v = MAX_BRIGHTNESS
                        }else if(v <= 0) {
                            v = 0
                        }
                        ivRing.strokeColor = ivRing.strokeColor.and(0xFFFFFF).plus((155 + v).shl(24))
                        tvBrightness.text = "brightness:" + v
                    }
                }
            }
            true
        }

        val fragmentManager = supportFragmentManager
        val musicFragment = SlMusicFragment()
        val lightFragment = SlLightFragment()
        if(!musicFragment.isAdded) {
            fragmentManager.beginTransaction().add(R.id.control_frame, musicFragment).hide(musicFragment).commit()
        }
        if(!lightFragment.isAdded) {
            fragmentManager.beginTransaction().add(R.id.control_frame, lightFragment).commit()
        }

        modeGroup.setOnClickListener {
            when(mode) {
                SLMode.MUSIC -> {
                    mode = SLMode.LIGHT
                    btLight.visibility = View.VISIBLE
                    btMusic.visibility = View.INVISIBLE
                    btLight.startAnimation(lightAnimation)
                    fragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
                            .show(lightFragment)
                            .commit()
                    fragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
                            .hide(musicFragment)
                            .commit()
                }
                SLMode.LIGHT -> {
                    mode = SLMode.MUSIC
                    btMusic.visibility = View.VISIBLE
                    btLight.visibility = View.INVISIBLE
                    btMusic.startAnimation(musicAnimation)
                    fragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                            .show(musicFragment)
                            .commit()
                    fragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                            .hide(lightFragment)
                            .commit()
                }
            }
            client!!.publish(SLTopic.CURR_MODE, deviceName, mode)
        }
    }

    override fun onPause() {
        super.onPause()
        if(client != null) {
            client!!.setCallback(null)
            client!!.disconnect()
        }
    }

    override fun onResume() {
        super.onResume()
        broker = "tcp://" + intent.getStringExtra(getString(R.string.MQTT_BROKER)) + ":1883"
        deviceName = intent.getStringExtra(getString(R.string.DEVICE_NAME))
        clientId = intent.getStringExtra(getString(R.string.MQTT_CLIENT_ID))
        manager = SLMqttManager(applicationContext, broker, deviceName, clientId)
        client = SLMqttManager.getInstance()
        client!!.setCallback(object : MqttCallbackExtended {
            override fun connectComplete(reconnect: Boolean, serverURI: String) {
                try {
                    progressView.visibility = View.INVISIBLE
                    val topic = SLTopic.ROOT + deviceName + "/#"
                    client!!.subscribe(topic, 0)
                    if (reconnect) {
                        appendLog("Reconnect complete")
                    } else {
                        appendLog("Connect complete")
                    }
                    appendLog("subscribe: $topic")
                } catch (e: MqttException) {
                    e.printStackTrace()
                }
                Log.d(MQTT_TAG, "Connected to $broker")
            }

            override fun connectionLost(cause: Throwable) {
                progressView.visibility = View.VISIBLE
                appendLog("Connection LOST")
                Log.d(MQTT_TAG, "Disconnect to $broker")
            }

            @Throws(Exception::class)
            override fun messageArrived(topic: String, message: MqttMessage) {
                appendLog("$topic:\n$message")
            }

            override fun deliveryComplete(token: IMqttDeliveryToken) {}
        })
        manager!!.setMqttActionListener(object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken) {
                progressView.visibility = View.INVISIBLE
                appendLog("長按右上角開關debug畫面")
                appendLog("Connect to $broker success")
                Log.d(MQTT_TAG, "Connect to $broker success")
            }

            override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                progressView.visibility = View.VISIBLE
                appendLog("Connect to $broker fail")
                Log.d(MQTT_TAG, "Connect to $broker fail")
            }
        })
        try {
            manager!!.connect()
        } catch (ex: MqttException) {
            ex.printStackTrace()
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (client != null) {
            try {
                // 先移除callback，callback裡有處理UI，finish後會找不到UI
                client!!.setCallback(null)
                client!!.disconnect()
            } catch (e: MqttException) {
                e.printStackTrace()
            }
            client = null
        }
        finish()
    }

    private fun initUi() {
        progressView = findViewById(R.id.progress_view)
        btNetworkConfig = findViewById(R.id.bt_network_config)
        tvLog = findViewById(R.id.tv_log)
        logView = findViewById(R.id.log_view)
        rootConstraint = findViewById(R.id.rootConstraint)
        lightTopConstraint = findViewById(R.id.lightTopConstraint)
        btDebug = findViewById(R.id.debug_view)
        ivRing = findViewById(R.id.iv_ring)
        tvZoom = findViewById(R.id.tv_zoom)
        tvBrightness = findViewById(R.id.tv_brightness)
        btLight = findViewById(R.id.bt_light)
        btMusic = findViewById(R.id.bt_music)
        modeGroup = findViewById(R.id.mode_group)
    }

    private fun getAngle(center_x: Float, center_y: Float, x: Float, y: Float): Float {
        var angle = Math.toDegrees(atan2((center_y - y).toDouble(), (center_x - x).toDouble())).toFloat()
        if (angle < 0) {
            angle += 360f
        }
        return angle
    }

    @Synchronized
    fun appendLog(str: String) {
        runOnUiThread {
            var strFull = "\n${ControlActivity.DATE_FORMAT.format(Date())} $str${tvLog.text}"
            if (strFull.length > MAX_LOG_SIZE) {
                strFull = strFull.substring(0, MAX_LOG_SIZE)
            }
            tvLog.text = strFull
            while (tvLog.canScrollVertically(-1)) {
                tvLog.scrollBy(0, -1)
            }
        }
    }
}
