package com.swaylight

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Point
import android.graphics.Rect
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Vibrator
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.*
import android.widget.*
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
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.atan2


class SwayLightMainActivity : AppCompatActivity() {

    val tag = SwayLightMainActivity::class.java.simpleName

    // UI
    private lateinit var progressView: ConstraintLayout
    private lateinit var tvConnectMessage: TextView
    private lateinit var btNetworkConfig: Button
    private lateinit var rootConstraint: ConstraintLayout
    private lateinit var lightTopConstraint: ConstraintLayout
    private lateinit var modeGroup: LinearLayout
    private lateinit var ivRing: TopLightView
    private lateinit var tvZoom: TextView
    private lateinit var tvBrightness: TextView
    private lateinit var btPower: ImageButton
    private lateinit var btDebug: TextView
    private lateinit var btLight: Button
    private lateinit var btMusic: Button
    private lateinit var tvLog: TextView
    private lateinit var logView: ConstraintLayout
    private lateinit var powerOffBlur: View

    // values
    var DATE_FORMAT = SimpleDateFormat("HH:mm:ss.SSS")
    private val MAX_LOG_SIZE = 3000
    var powerOn = SLMode.POWER_ON
    var mode = SLMode.LIGHT

    var ringCenterX = 0
    var ringCenterY = 0
    var ringStartRotate = 0f
    var ringPrevRotate = 0f
    var controlZoomFlag = true

    private var lightTopConstHeight: Int = 0
    var prevZoom = 6
    var prevBrightness = 100
    var lightSlideStartY = 0f
    private val lightRectF = Rect()
    private val musicRectF = Rect()
    private lateinit var lightAnimation: TranslateAnimation
    private lateinit var musicAnimation: TranslateAnimation
    private lateinit var lightTopConstAnim: ValueAnimator
    private lateinit var lightTopConstAnimRev: ValueAnimator

    // MQTT Objects
    var manager: SLMqttManager? = null
    var client: SLMqttClient? = null
    var displayObj = SLDisplay(6, 0, 100)
    private val MQTT_TAG = "mqtt"
    private val qos = 0
    private lateinit var broker: String
    private lateinit var deviceName: String
    private var clientId: String? = null
    private var isRetainedDataSynced: Boolean = false

    private lateinit var vibrator: Vibrator

    // const
    val MAX_ZOOM = 32
    val MIN_ZOOM = 6
    val MAX_BRIGHTNESS = 100

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sway_light_main)

        this.window.statusBarColor = ContextCompat.getColor(applicationContext, android.R.color.black)
        supportActionBar?.hide()
        vibrator = getSystemService(android.app.Service.VIBRATOR_SERVICE) as android.os.Vibrator
        initMqtt()
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
        btNetworkConfig.setOnLongClickListener {
            progressView.visibility = View.INVISIBLE
            true
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

        btPower.setOnLongClickListener { v ->
            val animAlpha: Animation
            val animAlphaRepeat: Animation
            val animTransTopBottom: Animation
            val animTransLeftRight: Animation
            if (powerOn == SLMode.POWER_ON) {
                // tern power to off
                animAlpha = AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in)
                animAlphaRepeat = AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in).apply {
                    startOffset = 5000
                    repeatMode = Animation.RESTART
                    repeatCount = 100
                    isFillEnabled = true
//                    fillAfter = true
                }
                animTransTopBottom = AnimationUtils.loadAnimation(applicationContext, R.anim.exit_to_bottom).apply {
                    duration = 500
                }
                animTransLeftRight = AnimationUtils.loadAnimation(applicationContext, R.anim.enter_from_left).apply {
                    duration = 700
                    startOffset = 300
                }
                lightTopConstAnim.start()
                powerOn = SLMode.POWER_OFF
            }else {
                // tern power to on
                animAlpha = AnimationUtils.loadAnimation(applicationContext, R.anim.fade_out)
                animAlphaRepeat = AnimationUtils.loadAnimation(applicationContext, R.anim.fade_out)
                animTransTopBottom = AnimationUtils.loadAnimation(applicationContext, R.anim.enter_from_bottom).apply {
                    duration = 700
                    startOffset = 300
                }
                animTransLeftRight = AnimationUtils.loadAnimation(applicationContext, R.anim.exit_to_right).apply {
                    duration = 700
                }
                lightTopConstAnimRev.start()
                powerOn = SLMode.POWER_ON
            }
            animAlpha.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                    if (powerOn == SLMode.POWER_OFF) {
                        powerOffBlur.visibility = View.VISIBLE
                        findViewById<TextView>(R.id.tv_hint).visibility = View.VISIBLE
                        findViewById<TextView>(R.id.tv_sway).visibility = View.VISIBLE
                        findViewById<TextView>(R.id.tv_light).visibility = View.VISIBLE
                    } else {
                        modeGroup.visibility = View.VISIBLE
                        btDebug.visibility = View.VISIBLE
//                        findViewById<ScrollView>(R.id.control_scroll_view).visibility = View.VISIBLE
                    }
                }

                override fun onAnimationEnd(animation: Animation?) {
                    if (powerOn == SLMode.POWER_ON) {
                        powerOffBlur.visibility = View.INVISIBLE
                        findViewById<TextView>(R.id.tv_hint).visibility = View.INVISIBLE
                        findViewById<TextView>(R.id.tv_sway).visibility = View.INVISIBLE
                        findViewById<TextView>(R.id.tv_light).visibility = View.INVISIBLE
                    } else {
                        modeGroup.visibility = View.INVISIBLE
                        btDebug.visibility = View.INVISIBLE
//                        findViewById<ScrollView>(R.id.control_scroll_view).visibility = View.INVISIBLE
                    }
                }

                override fun onAnimationRepeat(animation: Animation?) {}
            })

            modeGroup.startAnimation(animTransTopBottom)
//            findViewById<ScrollView>(R.id.control_scroll_view).startAnimation(animTransTopBottom)
            powerOffBlur.startAnimation(animAlpha)
            btDebug.startAnimation(animTransTopBottom)
            findViewById<TextView>(R.id.tv_hint).startAnimation(animAlphaRepeat)
            findViewById<TextView>(R.id.tv_sway).startAnimation(animTransLeftRight)
            findViewById<TextView>(R.id.tv_light).startAnimation(animTransLeftRight)

            if(v.isClickable) {
                client?.publish(SLTopic.POWER, deviceName, powerOn)
            }else {
                v.isClickable = true
            }
            true
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
                lightTopConstHeight = params.height
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
                    lightTopConstAnim = ValueAnimator.ofInt(params.height, rootConstraint.height).apply {
                        duration = 500
                        interpolator = AccelerateDecelerateInterpolator()
                        addUpdateListener { valueAnimator ->
                            val newParams: ConstraintLayout.LayoutParams =
                                    (lightTopConstraint.layoutParams as ConstraintLayout.LayoutParams).apply {
                                        height = valueAnimator.animatedValue as Int
                                    }
                            lightTopConstraint.layoutParams = newParams
                        }
                    }
                    lightTopConstAnimRev = ValueAnimator.ofInt(rootConstraint.height, params.height).apply {
                        duration = 500
                        startDelay = 500
                        interpolator = AccelerateDecelerateInterpolator()
                        addUpdateListener { valueAnimator ->
                            val newParams: ConstraintLayout.LayoutParams =
                                    (lightTopConstraint.layoutParams as ConstraintLayout.LayoutParams).apply {
                                        height = valueAnimator.animatedValue as Int
                                    }
                            lightTopConstraint.layoutParams = newParams
                        }
                    }
                    lightAnimation.duration = 500
                    musicAnimation.duration = 500
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
                        client?.publish(SLTopic.MUSIC_MODE_DISPLAY, deviceName, displayObj.instance)
                        client?.publish(SLTopic.LIGHT_MODE_DISPLAY, deviceName, displayObj.instance)
                        vibrator.vibrate(5)
                    }
                }
            }
            true
        }

        lightTopConstraint.setOnTouchListener{ v, event ->
            val delta = ((lightSlideStartY - event.rawY) / 50).toInt()
            var v: Int = 0
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // 右半邊控制亮度/左半邊控制縮放
                    lightSlideStartY = event.rawY
                    if (event.rawX <= lightTopConstraint.width / 2) {
                        controlZoomFlag = true
                        tvZoom.visibility = View.VISIBLE
                    } else {
                        controlZoomFlag = false
                        tvBrightness.visibility = View.VISIBLE
                    }
                    vibrator.vibrate(5)
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
                        startFadeOutAnim(tvZoom, 500, 500)
                    } else {
                        v = prevBrightness + delta
                        if (v > MAX_BRIGHTNESS) {
                            v = MAX_BRIGHTNESS
                        } else if (v <= 0) {
                            v = 0
                        }
                        prevBrightness = v
                        startFadeOutAnim(tvBrightness, 500, 500)
                    }
                    vibrator.vibrate(5)
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
                        tvZoom.text = v.toString()
                        tvZoom.visibility = View.VISIBLE
                        tvZoom.animation?.cancel()
                        if (displayObj.zoom != ivRing.zoomValue) {
                            vibrator.vibrate(5)
                            displayObj.zoom = ivRing.zoomValue
                            client?.publish(SLTopic.MUSIC_MODE_DISPLAY, deviceName, displayObj.instance)
                            client?.publish(SLTopic.LIGHT_MODE_DISPLAY, deviceName, displayObj.instance)
                        }
                    }else {
                        v = prevBrightness + delta
                        if(v > MAX_BRIGHTNESS) {
                            v = MAX_BRIGHTNESS
                        }else if(v <= 0) {
                            v = 0
                        }
                        ivRing.strokeColor = ivRing.strokeColor.and(0xFFFFFF).plus((155 + v).shl(24))
                        tvBrightness.text = v.toString()
                        tvBrightness.visibility = View.VISIBLE
                        tvBrightness.animation?.cancel()
                        if (displayObj.brightness != v) {
                            vibrator.vibrate(5)
                            displayObj.brightness = v
                            client?.publish(SLTopic.MUSIC_MODE_DISPLAY, deviceName, displayObj.instance)
                            client?.publish(SLTopic.LIGHT_MODE_DISPLAY, deviceName, displayObj.instance)
                        }
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

        modeGroup.setOnClickListener { v ->
            val transBg: TransitionDrawable = rootConstraint.background as TransitionDrawable
            val transModeGroup: TransitionDrawable = modeGroup.background as TransitionDrawable
            val transBtLight: TransitionDrawable = btLight.background as TransitionDrawable
            val transBtMusic: TransitionDrawable = btMusic.background as TransitionDrawable
            when(mode) {
                SLMode.MUSIC -> {
                    mode = SLMode.LIGHT
                    ivRing.strokeBgColor = 0xCCC4C4C4.toInt()
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
                    transBg.reverseTransition(500)
                    transModeGroup.reverseTransition(500)
                    transBtLight.reverseTransition(500)
                    transBtMusic.reverseTransition(500)
                    tvLog.setTextColor(Color.BLACK)
                }
                SLMode.LIGHT -> {
                    mode = SLMode.MUSIC
                    ivRing.strokeBgColor = 0x805F5F5F.toInt()
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
                    transBg.startTransition(500)
                    transModeGroup.startTransition(500)
                    transBtLight.reverseTransition(500)
                    transBtMusic.reverseTransition(500)
                    tvLog.setTextColor(Color.WHITE)
                }
            }
            // 為了區分是使用者點擊or程式透過callOnClick()呼叫的:
            // isClickable == true -> 使用者點擊，才publish
            // isClickable == false -> 是從callOnClick()呼叫的，並set isClickable flag
            if(v.isClickable) {
                client?.publish(SLTopic.CURR_MODE, deviceName, mode)
            }else {
                v.isClickable = true
            }
        }
    }

    override fun onPause() {
        super.onPause()
        client?.setCallback(null)
        client?.disconnect()
        isRetainedDataSynced = false
    }

    override fun onResume() {
        super.onResume()
        try {
            manager!!.connect()
            client?.setCallback(object : MqttCallbackExtended {
                override fun connectComplete(reconnect: Boolean, serverURI: String) {
                    try {
                        if (reconnect) {
                            appendLog("Reconnect complete")
                        } else {
                            appendLog("Connect complete")
                        }
                        runOnUiThread {
                            tvConnectMessage.setText(R.string.syncing)
                        }
                        startFadeOutAnim(progressView, 1000, 0)
                        // 等app把來自自己的訊息也收完，再把flag關閉
                        Handler().postDelayed({
                            isRetainedDataSynced = true
                            progressView.visibility = View.INVISIBLE
                        }, 1000)

                    } catch (e: MqttException) {
                        e.printStackTrace()
                    }
                    vibrator.vibrate(20)
                    Log.d(MQTT_TAG, "Connected to $broker")
                }

                override fun connectionLost(cause: Throwable) {
                    runOnUiThread {
                        progressView.visibility = View.VISIBLE
                        tvConnectMessage.setText(R.string.connecting)
                    }
                    vibrator.vibrate(20)
                    appendLog("Connection LOST")
                    Log.d(MQTT_TAG, "Disconnect to $broker")
                }

                @Throws(Exception::class)
                override fun messageArrived(topic: String, message: MqttMessage) {
                    appendLog("$topic:$message")
                    updateSubUi(topic, message)
                }

                override fun deliveryComplete(token: IMqttDeliveryToken) {}
            })
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

    private fun initMqtt() {
        broker = "tcp://" + intent.getStringExtra(getString(R.string.MQTT_BROKER)) + ":1883"
        deviceName = intent.getStringExtra(getString(R.string.DEVICE_NAME)).toString()
        clientId = intent.getStringExtra(getString(R.string.MQTT_CLIENT_ID))
        manager = SLMqttManager(applicationContext, broker, deviceName, clientId)
        client = SLMqttManager.getInstance()
        manager!!.setMqttActionListener(object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken) {
                appendLog("長按右上角開關debug畫面")
                appendLog("Connect to $broker success")
                Log.d(MQTT_TAG, "Connect to $broker success")
                Log.d(MQTT_TAG, "client: $client")
                val topic = SLTopic.ROOT + deviceName + "/#"
                client?.subscribe(topic, 2)
                vibrator.vibrate(20)
                appendLog("subscribe: $topic")
            }

            override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                progressView.visibility = View.VISIBLE
                appendLog("Connect to $broker fail")
                vibrator.vibrate(20)
                Log.d(MQTT_TAG, "Connect to $broker fail")
            }
        })
    }

    private fun initUi() {
        progressView = findViewById(R.id.progress_view)
        tvConnectMessage = findViewById(R.id.tv_cnt_msg)
        btNetworkConfig = findViewById(R.id.bt_network_config)
        tvLog = findViewById(R.id.tv_log)
        logView = findViewById(R.id.log_view)
        rootConstraint = findViewById(R.id.rootConstraint)
        lightTopConstraint = findViewById(R.id.lightTopConstraint)
        btPower = findViewById(R.id.bt_power)
        btDebug = findViewById(R.id.debug_view)
        ivRing = findViewById(R.id.iv_ring)
        tvZoom = findViewById(R.id.tv_zoom)
        tvBrightness = findViewById(R.id.tv_brightness)
        btLight = findViewById(R.id.bt_light)
        btMusic = findViewById(R.id.bt_music)
        modeGroup = findViewById(R.id.mode_group)
        powerOffBlur = findViewById(R.id.power_blur)
    }

    private fun startFadeOutAnim(view: View, duration: Long, startOffset: Long) {
        val anim = AlphaAnimation(1f, 0f).apply {
            interpolator = AccelerateInterpolator()
            this.duration = duration
            this.startOffset = startOffset
        }
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                view.visibility = View.INVISIBLE
            }

            override fun onAnimationRepeat(animation: Animation?) {
            }

        })
        view.animation?.cancel()
        view.startAnimation(anim)
    }

    private fun updateSubUi(topic: String, message: MqttMessage) {
        val jsonObj = JSONObject(String(message.payload))
        val fromMyself = jsonObj["id"] == clientId
        if (fromMyself && isRetainedDataSynced) {
            appendLog("msg from myself")
            return
        }
        when(topic) {
            SLTopic.ROOT + deviceName + SLTopic.CURR_MODE.topic -> {
                val newMode = when (jsonObj[SLMqttClient.VALUE]) {
                    SLMode.LIGHT.modeNum -> {
                        SLMode.LIGHT
                    }
                    SLMode.MUSIC.modeNum -> {
                        SLMode.MUSIC
                    }
                    else -> {
                        return
                    }
                }
                if (newMode != mode) {
                    modeGroup.isClickable = false
                    modeGroup.performClick()
                    appendLog("update mode to $mode")
                }
            }
            SLTopic.ROOT + deviceName + SLTopic.POWER.topic -> {
                val newPower = when (jsonObj[SLMqttClient.VALUE]) {
                    SLMode.POWER_ON.modeNum -> {
                        SLMode.POWER_ON
                    }
                    SLMode.POWER_OFF.modeNum -> {
                        SLMode.POWER_OFF
                    }
                    else -> {
                        return
                    }
                }
                if (newPower != powerOn) {
                    btPower.isClickable = false
                    btPower.performLongClick()
                    appendLog("update mode to $powerOn")
                }
            }
            SLTopic.ROOT + deviceName + SLTopic.LIGHT_MODE_DISPLAY.topic -> {
                val newBright: Int = jsonObj[SLDisplay.BRIGHT] as Int
                val newOffset: Int = jsonObj[SLDisplay.OFFSET] as Int
                val newZoom: Int = jsonObj[SLDisplay.ZOOM] as Int

                if (displayObj.brightness != newBright) {
                    prevBrightness = newBright
                    displayObj.brightness = newBright
                    tvBrightness.visibility = View.VISIBLE
                    tvBrightness.text = newBright.toString()
                    startFadeOutAnim(tvBrightness, 500, 500)
                }

                if (displayObj.zoom != newZoom) {
                    prevZoom = newZoom
                    displayObj.zoom = newZoom
                    ivRing.zoomValue = newZoom
                    tvZoom.text = newZoom.toString()
                    tvZoom.visibility = View.VISIBLE
                    startFadeOutAnim(tvZoom, 500, 500)
                }

                if (displayObj.offset != newOffset) {
                    ivRing.offsetValue = newOffset
                    displayObj.offset = newOffset
                }
            }

            SLTopic.ROOT + deviceName + SLTopic.MUSIC_MODE_DISPLAY -> {

            }
        }
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
