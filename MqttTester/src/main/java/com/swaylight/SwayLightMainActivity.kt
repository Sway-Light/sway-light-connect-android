package com.swaylight

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Point
import android.graphics.Rect
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.swaylight.library.data.*
import org.eclipse.paho.client.mqttv3.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.atan2


class SwayLightMainActivity : AppCompatActivity() {

    val tag = SwayLightMainActivity::class.java.simpleName

    // UI
    private lateinit var progressView: ConstraintLayout
    private lateinit var ivProgressLogo: ImageView
    private lateinit var tvConnectMessage: TextView
    private lateinit var btNetworkConfig: Button
    private lateinit var rootConstraint: ConstraintLayout
    private lateinit var lightTopConstraint: ConstraintLayout
    private lateinit var modeGroup: LinearLayout
    private lateinit var ivRing: TopLightView
    private lateinit var tvZoom: TextView
    private lateinit var vBrightness: LinearLayout
    private lateinit var ivBrightness: ImageView
    private lateinit var tvBrightness: TextView
    private lateinit var btClockSetting: ImageButton
    private lateinit var btPower: ImageButton
    private lateinit var btDebug: TextView
    private lateinit var btLight: Button
    private lateinit var btMusic: Button
    private lateinit var tvLog: TextView
    private lateinit var logView: ConstraintLayout
    private lateinit var powerOffBlur: View
    private lateinit var ivLogo: ImageView
    private lateinit var sbFftMag: SeekBar

    // fragment
    val fragmentManager = supportFragmentManager
    val musicFragment = SlMusicFragment()
    val lightFragment = SlLightFragment()
    val clockSettingFragment = SlClockSettingFragment()

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
    private lateinit var logoAnim: AnimationDrawable
    private lateinit var logoProgressAnim: AnimationDrawable

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
        Handler(Looper.getMainLooper()).postDelayed({
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
            builder.setPositiveButton("Yes") { _, _ ->
                tvLog.scrollTo(0, 0)
                tvLog.text = ""
            }
            builder.setNegativeButton("Cancel") { _, _ -> }
            builder.show()
            false
        }

        btPower.setOnLongClickListener { v ->
            val animAlpha: Animation
            val animAlphaRev: Animation
            val animAlphaRepeat: Animation
            val animTransTopBottom: Animation
            val animTransLeftRight: Animation
            if (powerOn == SLMode.POWER_ON) {
                // turn power to off
                animAlpha = AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in)
                animAlphaRev = AnimationUtils.loadAnimation(applicationContext, R.anim.fade_out)
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
                ivLogo.setBackgroundResource(R.drawable.logo_anim)
                lightTopConstAnim.start()
                powerOn = SLMode.POWER_OFF
            }else {
                // turn power to on
                animAlpha = AnimationUtils.loadAnimation(applicationContext, R.anim.fade_out)
                animAlphaRev = AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in)
                animAlphaRepeat = AnimationUtils.loadAnimation(applicationContext, R.anim.fade_out)
                animTransTopBottom = AnimationUtils.loadAnimation(applicationContext, R.anim.enter_from_bottom).apply {
                    duration = 700
                    startOffset = 300
                }
                animTransLeftRight = AnimationUtils.loadAnimation(applicationContext, R.anim.exit_to_right).apply {
                    duration = 700
                }
                ivLogo.setBackgroundResource(R.drawable.logo_anim_reverse)
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
                        ivLogo.visibility = View.VISIBLE
                    } else {
                        modeGroup.visibility = View.VISIBLE
                        btDebug.visibility = View.VISIBLE
                        btPower.alpha = 1f
                        ivRing.visibility = View.VISIBLE
                        ivLogo.startAnimation(animAlpha)
//                        findViewById<ScrollView>(R.id.control_scroll_view).visibility = View.VISIBLE
                    }
                }

                override fun onAnimationEnd(animation: Animation?) {
                    if (powerOn == SLMode.POWER_ON) {
                        powerOffBlur.visibility = View.INVISIBLE
                        ivLogo.visibility = View.INVISIBLE
                        findViewById<TextView>(R.id.tv_hint).visibility = View.INVISIBLE
                        findViewById<TextView>(R.id.tv_sway).visibility = View.INVISIBLE
                        findViewById<TextView>(R.id.tv_light).visibility = View.INVISIBLE
                    } else {
                        modeGroup.visibility = View.INVISIBLE
                        btDebug.visibility = View.INVISIBLE
                        btPower.alpha = 0f
                        ivRing.visibility = View.INVISIBLE
//                        findViewById<ScrollView>(R.id.control_scroll_view).visibility = View.INVISIBLE
                    }
                }

                override fun onAnimationRepeat(animation: Animation?) {}
            })

            modeGroup.startAnimation(animTransTopBottom)
//            findViewById<ScrollView>(R.id.control_scroll_view).startAnimation(animTransTopBottom)
            powerOffBlur.startAnimation(animAlpha)
            btDebug.startAnimation(animTransTopBottom)
            btPower.startAnimation(animAlphaRev)
            ivRing.startAnimation(animAlphaRev)
            logoAnim = ivLogo.background as AnimationDrawable
            logoAnim.start()
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
                View.GONE
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
                Handler(Looper.getMainLooper()).postDelayed({
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

        ivRing.setOnTouchListener { _, event ->
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

        lightTopConstraint.setOnTouchListener{ _, event ->
            val delta = if (controlZoomFlag) {
                ((lightSlideStartY - event.rawY) / 50).toInt()
            }else {
                ((lightSlideStartY - event.rawY) / 18).toInt()
            }
            var value: Int
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // 右半邊控制亮度/左半邊控制縮放
                    lightSlideStartY = event.rawY
                    if (event.rawX <= lightTopConstraint.width / 2) {
                        controlZoomFlag = true

                        tvZoom.visibility = View.VISIBLE
                    } else {
                        controlZoomFlag = false
                        vBrightness.visibility = View.VISIBLE
                    }
                    vibrator.vibrate(5)
                }
                MotionEvent.ACTION_UP -> {
                    if (controlZoomFlag) {
                        value = prevZoom + delta
                        if (value > MAX_ZOOM) {
                            value = MAX_ZOOM
                        } else if (value <= 0) {
                            value = 0
                        }
                        prevZoom = value
                        startFadeOutAnim(tvZoom, 500, 500)
                    } else {
                        value = prevBrightness + delta
                        if (value > MAX_BRIGHTNESS) {
                            value = MAX_BRIGHTNESS
                        } else if (value <= 0) {
                            value = 0
                        }
                        prevBrightness = value
                        startFadeOutAnim(vBrightness, 500, 500)
                    }
                    vibrator.vibrate(5)
                }
                else -> {
                    if(controlZoomFlag) {
                        value = prevZoom + delta
                        if(value > MAX_ZOOM) {
                            value = MAX_ZOOM
                        }else if(value < MIN_ZOOM) {
                            value = MIN_ZOOM
                        }
                        ivRing.zoomValue = value
                        Utils.setTextInPercentage(tvZoom, value, MAX_ZOOM)
                        tvZoom.visibility = View.VISIBLE
                        tvZoom.animation?.cancel()
                        if (displayObj.zoom != ivRing.zoomValue) {
                            vibrator.vibrate(5)
                            displayObj.zoom = ivRing.zoomValue
                            client?.publish(SLTopic.MUSIC_MODE_DISPLAY, deviceName, displayObj.instance)
                            client?.publish(SLTopic.LIGHT_MODE_DISPLAY, deviceName, displayObj.instance)
                        }
                    }else {
                        value = prevBrightness + delta
                        if(value > MAX_BRIGHTNESS) {
                            value = MAX_BRIGHTNESS
                        }else if(value <= 0) {
                            value = 0
                        }
                        when(value) {
                            in 100 downTo 65 -> {
                                ivBrightness.background = ContextCompat.getDrawable(applicationContext, R.mipmap.ic_brightness_high)
                            }
                            in 64 downTo 33 -> {
                                ivBrightness.background = ContextCompat.getDrawable(applicationContext, R.mipmap.ic_brightness_medium)
                            }
                            else -> {
                                ivBrightness.background = ContextCompat.getDrawable(applicationContext, R.mipmap.ic_brightness_low)
                            }
                        }
                        ivRing.strokeColor = 0xFFFFFF.plus((102 + 153.times(value.div(100f)).toInt()).shl(24))
                        tvBrightness.text = value.toString()
                        vBrightness.visibility = View.VISIBLE
                        vBrightness.animation?.cancel()
                        if (displayObj.brightness != value) {
                            vibrator.vibrate(5)
                            displayObj.brightness = value
                            client?.publish(SLTopic.MUSIC_MODE_DISPLAY, deviceName, displayObj.instance)
                            client?.publish(SLTopic.LIGHT_MODE_DISPLAY, deviceName, displayObj.instance)
                        }
                    }
                }
            }
            true
        }

        if(!musicFragment.isAdded) {
            fragmentManager.beginTransaction().add(R.id.control_frame, musicFragment).hide(musicFragment).commit()
        }
        if(!lightFragment.isAdded) {
            fragmentManager.beginTransaction().add(R.id.control_frame, lightFragment).commit()
        }
        if(!clockSettingFragment.isAdded) {
            fragmentManager.beginTransaction().add(R.id.full_frame, clockSettingFragment).hide(clockSettingFragment).commit()
        }

        btClockSetting.setOnClickListener {
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.fragment_open_enter, R.anim.fragment_close_exit)
                    .show(clockSettingFragment)
                    .commit()
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
                else -> { }
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

        sbFftMag.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                runOnUiThread { findViewById<TextView>(R.id.tv_fft_mag).text = progress.toString() }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) { }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                client?.publish(
                        SLTopic.OPTION_CONFIG,
                        deviceName,
                        SLOptionConfig(sbFftMag.progress).instance
                )
            }
        })
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
                        Handler(Looper.getMainLooper()).postDelayed({
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
                    updateSubUi(topic, message)
                }

                override fun deliveryComplete(token: IMqttDeliveryToken) {}
            })
        } catch (ex: MqttException) {
            ex.printStackTrace()
        }

    }

    override fun onBackPressed() {
        if(!clockSettingFragment.isHidden) {
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.fragment_open_enter, R.anim.fragment_close_exit)
                    .hide(clockSettingFragment)
                    .commit()
            return
        }else {
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
            super.onBackPressed()
        }
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
        ivProgressLogo = findViewById(R.id.iv_progress_logo)
        ivProgressLogo.setBackgroundResource(R.drawable.logo_anim_repeat)
        logoProgressAnim = ivProgressLogo.background as AnimationDrawable
        logoProgressAnim.isOneShot = false
        logoProgressAnim.start()
        tvConnectMessage = findViewById(R.id.tv_cnt_msg)
        btNetworkConfig = findViewById(R.id.bt_network_config)
        tvLog = findViewById(R.id.tv_log)
        logView = findViewById(R.id.log_view)
        logView.visibility = View.GONE
        rootConstraint = findViewById(R.id.rootConstraint)
        lightTopConstraint = findViewById(R.id.lightTopConstraint)
        btClockSetting = findViewById(R.id.bt_clock_setting)
        btPower = findViewById(R.id.bt_power)
        btDebug = findViewById(R.id.debug_view)
        ivRing = findViewById(R.id.iv_ring)
        tvZoom = findViewById(R.id.tv_zoom)
        vBrightness = findViewById(R.id.brightness_view)
        ivBrightness = findViewById(R.id.iv_brightness)
        tvBrightness = findViewById(R.id.tv_brightness)
        btLight = findViewById(R.id.bt_light)
        btMusic = findViewById(R.id.bt_music)
        modeGroup = findViewById(R.id.mode_group)
        powerOffBlur = findViewById(R.id.power_blur)
        ivLogo = findViewById(R.id.iv_logo)
        sbFftMag = findViewById(R.id.sb_fft_mag)
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
            appendLog("/////$topic:$message")
            return
        }else {
            appendLog("$topic:$message")
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
                runOnUiThread {
                    if (newMode != mode) {
                        modeGroup.isClickable = false
                        modeGroup.performClick()
                        appendLog("update mode to $mode")
                    }
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
                runOnUiThread {
                    if (newPower != powerOn) {
                        btPower.isClickable = false
                        btPower.performLongClick()
                        appendLog("update mode to $powerOn")
                    }
                }
            }
            SLTopic.ROOT + deviceName + SLTopic.LIGHT_MODE_DISPLAY.topic -> {
                val newBright: Int = jsonObj[SLDisplay.BRIGHT] as Int
                val newOffset: Int = jsonObj[SLDisplay.OFFSET] as Int
                val newZoom: Int = jsonObj[SLDisplay.ZOOM] as Int

                runOnUiThread {
                    if (displayObj.brightness != newBright) {
                        prevBrightness = newBright
                        displayObj.brightness = newBright
                        vBrightness.visibility = View.VISIBLE
                        tvBrightness.text = newBright.toString()
                        ivRing.strokeColor = 0xFFFFFF.plus((102 + 153.times(newBright.div(100f)).toInt()).shl(24))
                        startFadeOutAnim(vBrightness, 500, 500)
                    }

                    if (displayObj.zoom != newZoom) {
                        prevZoom = newZoom
                        displayObj.zoom = newZoom
                        ivRing.zoomValue = newZoom
                        tvZoom.visibility = View.VISIBLE
                        Utils.setTextInPercentage(tvZoom, newZoom, MAX_ZOOM)
                        startFadeOutAnim(tvZoom, 500, 500)
                    }

                    if (displayObj.offset != newOffset) {
                        ivRing.offsetValue = newOffset
                        displayObj.offset = newOffset
                        ringPrevRotate = 360f.times(newOffset.div(32))
                    }
                }
            }

            SLTopic.ROOT + deviceName + SLTopic.MUSIC_MODE_DISPLAY -> {
                val newBright: Int = jsonObj[SLDisplay.BRIGHT] as Int
                val newOffset: Int = jsonObj[SLDisplay.OFFSET] as Int
                val newZoom: Int = jsonObj[SLDisplay.ZOOM] as Int

                runOnUiThread {
                    if (displayObj.brightness != newBright) {
                        prevBrightness = newBright
                        displayObj.brightness = newBright
                        vBrightness.visibility = View.VISIBLE
                        tvBrightness.text = newBright.toString()
                        ivRing.strokeColor = 0xFFFFFF.plus((102 + 153.times(newBright.div(100f)).toInt()).shl(24))
                        startFadeOutAnim(vBrightness, 500, 500)
                    }

                    if (displayObj.zoom != newZoom) {
                        prevZoom = newZoom
                        displayObj.zoom = newZoom
                        ivRing.zoomValue = newZoom
                        Utils.setTextInPercentage(tvZoom, newZoom, MAX_ZOOM)
                        tvZoom.visibility = View.VISIBLE
                        startFadeOutAnim(tvZoom, 500, 500)
                    }

                    if (displayObj.offset != newOffset) {
                        ivRing.offsetValue = newOffset
                        displayObj.offset = newOffset
                        ringPrevRotate = 360f.times(newOffset.div(32))
                    }
                }
            }

            SLTopic.ROOT + deviceName + SLTopic.POWER_START_TIME.topic -> {
                clockSettingFragment.onHour = jsonObj[SLClockSetting.HOUR] as Int
                clockSettingFragment.onMinute = jsonObj[SLClockSetting.MIN] as Int
            }

            SLTopic.ROOT + deviceName + SLTopic.POWER_END_TIME.topic -> {
                clockSettingFragment.offHour = jsonObj[SLClockSetting.HOUR] as Int
                clockSettingFragment.offMinute = jsonObj[SLClockSetting.MIN] as Int
            }

            SLTopic.ROOT + deviceName + SLTopic.OPTION_CONFIG.topic -> {
                sbFftMag.progress = jsonObj[SLOptionConfig.FFT_MAG] as Int
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
