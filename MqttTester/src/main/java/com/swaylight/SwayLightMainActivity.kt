package com.swaylight

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout

class SwayLightMainActivity : AppCompatActivity() {

    val tag = SwayLightMainActivity::class.java.simpleName

    // UI
    var rootConstraint: ConstraintLayout? = null
    var lightTopConstraint: ConstraintLayout? = null
    var ivRing: ImageView? = null
    var tvZoom: TextView? = null
    var tvBrightness: TextView? = null

    // values
    var ringCenterX = 0
    var ringCenterY = 0
    var ringStartRotate = 0f
    var ringPrevRotate = 0f

    var controlZoomFlag = true
    var prevZoom = 0
    var prevBrightness = 0
    var lightSlideStartY = 0f

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sway_light_main)

        rootConstraint = findViewById(R.id.rootConstraint)
        lightTopConstraint = findViewById(R.id.lightTopConstraint)
        ivRing = findViewById(R.id.iv_ring)
        tvZoom = findViewById(R.id.tv_zoom)
        tvBrightness = findViewById(R.id.tv_brightness)
        rootConstraint?.viewTreeObserver?.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                val params: ConstraintLayout.LayoutParams = lightTopConstraint?.layoutParams as ConstraintLayout.LayoutParams

                params.height = (rootConstraint?.width!! * 0.7).toInt()
                params.width = (rootConstraint?.width!!).toInt()
                lightTopConstraint?.layoutParams = params
                val location = intArrayOf(0, 0)
                ivRing!!.getLocationOnScreen(location)
                ringCenterX = location[0] + ivRing!!.width/2
                ringCenterY = location[1] + ivRing!!.height/2
                Log.d(tag, "ring h:${ivRing!!.height}, w:${ivRing!!.width}")
                Log.d(tag, "x:${ringCenterX}, y:${ringCenterY}")
                // 延後一下在remove listener
                Handler().postDelayed({
                    rootConstraint!!.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }, 100)
            }
        })

        ivRing!!.setOnTouchListener { v, event ->
            val degree = Math.toDegrees(Math.atan2((ringCenterY - event.rawY).toDouble(), (ringCenterX - event.rawX).toDouble())).toInt()
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    ringStartRotate = degree.toFloat()
                }
                MotionEvent.ACTION_UP -> {
                    ringPrevRotate = ivRing!!.rotation
                }
                else -> {
                    ivRing!!.rotation = ringPrevRotate + (degree.toFloat() - ringStartRotate)
                }
            }
            true
        }

        lightTopConstraint!!.setOnTouchListener{ v, event ->
            val delta = ((lightSlideStartY - event.rawY) / 20).toInt()
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // 右半邊控制亮度/左半邊控制縮放
                    lightSlideStartY = event.rawY
                    controlZoomFlag = (event.rawX <= lightTopConstraint!!.width / 2)
                }
                MotionEvent.ACTION_UP -> {
                    if(controlZoomFlag) {
                        var v = prevZoom + delta
                        if(v > 100) {
                            v = 100
                        }else if(v <= 0) {
                            v = 0
                        }
                        prevZoom = v
                    }else {
                        var v = prevBrightness + delta
                        if(v > 100) {
                            v = 100
                        }else if(v <= 0) {
                            v = 0
                        }
                        prevBrightness = v
                    }
                }
                else -> {
                    if(controlZoomFlag) {
                        var v = prevZoom + delta
                        if(v > 100) {
                            v = 100
                        }else if(v <= 0) {
                            v = 0
                        }
                        tvZoom!!.text = "zoom:" + v
                    }else {
                        var v = prevBrightness + delta
                        if(v > 100) {
                            v = 100
                        }else if(v <= 0) {
                            v = 0
                        }
                        tvBrightness!!.text = "brightness:" + v
                    }
                }
            }
            true
        }

    }
}
