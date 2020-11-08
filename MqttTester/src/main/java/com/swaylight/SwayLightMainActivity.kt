package com.swaylight

import android.R.attr.x
import android.R.attr.y
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Point
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.swaylight.custom_ui.TopLightView
import kotlin.math.atan2


class SwayLightMainActivity : AppCompatActivity() {

    val tag = SwayLightMainActivity::class.java.simpleName

    // UI
    private lateinit var rootConstraint: ConstraintLayout
    private lateinit var lightTopConstraint: ConstraintLayout
    private lateinit var modeGroup: LinearLayout
    private lateinit var ivRing: TopLightView
    private lateinit var tvZoom: TextView
    private lateinit var tvBrightness: TextView
    private lateinit var btDebug: View
    private lateinit var btLight: Button
    private lateinit var btMusic: Button

    // values
    var mode = Mode.LIGHT
    var debugClickCount = 0
    var ringCenterX = 0
    var ringCenterY = 0
    var ringStartRotate = 0f
    var ringPrevRotate = 0f

    var controlZoomFlag = true
    var prevZoom = 0
    var prevBrightness = 0
    var lightSlideStartY = 0f

    private val lightRectF = Rect()
    private val musicRectF = Rect()
    private lateinit var lightAnimation: TranslateAnimation
    private lateinit var musicAnimation: TranslateAnimation

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

        btDebug.setOnClickListener{
            debugClickCount++
            if(debugClickCount >= 10) {
                val intent = Intent(this, ConnectActivity::class.java)
                finish()
                startActivity(intent)
            }
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
                Log.d(tag, "ring h:${ivRing.height}, w:${ivRing.width}")
                Log.d(tag, "x:${ringCenterX}, y:${ringCenterY}")

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
                }
            }
            true
        }

        lightTopConstraint.setOnTouchListener{ v, event ->
            val delta = ((lightSlideStartY - event.rawY) / 20).toInt()
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // 右半邊控制亮度/左半邊控制縮放
                    lightSlideStartY = event.rawY
                    controlZoomFlag = (event.rawX <= lightTopConstraint.width / 2)
                }
                MotionEvent.ACTION_UP -> {
                    if (controlZoomFlag) {
                        var v = prevZoom + delta
                        if (v > MAX_ZOOM) {
                            v = MAX_ZOOM
                        } else if (v <= 0) {
                            v = 0
                        }
                        prevZoom = v
                    } else {
                        var v = prevBrightness + delta
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
                        var v = prevZoom + delta
                        if(v > MAX_ZOOM) {
                            v = MAX_ZOOM
                        }else if(v < MIN_ZOOM) {
                            v = MIN_ZOOM
                        }
                        ivRing.zoomValue = v
                        tvZoom.text = "zoom:" + v
                    }else {
                        var v = prevBrightness + delta
                        if(v > MAX_BRIGHTNESS) {
                            v = MAX_BRIGHTNESS
                        }else if(v <= 0) {
                            v = 0
                        }
                        ivRing.strokeColor = ivRing.strokeColor.and(0xFFFFFF).plus((155+v).shl(24))
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
                Mode.MUSIC -> {
                    mode = Mode.LIGHT
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
                Mode.LIGHT -> {
                    mode = Mode.MUSIC
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
        }
//        btLight!!.setOnClickListener {
//            mode = Mode.LIGHT
//            btMusic!!.visibility = View.INVISIBLE
//        }
//        btMusic!!.setOnClickListener {
//            mode = Mode.MUSIC
//            btLight!!.visibility = View.INVISIBLE
//        }
    }

    private fun initUi() {
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

    enum class Mode(val mode: Int) {
        LIGHT(0x02),
        MUSIC(0x03)
    }

    private fun getAngle(center_x: Float, center_y: Float, x: Float, y: Float): Float {
        var angle = Math.toDegrees(atan2((center_y - y).toDouble(), (center_x - x).toDouble())).toFloat()
        if (angle < 0) {
            angle += 360f
        }
        return angle
    }
}
