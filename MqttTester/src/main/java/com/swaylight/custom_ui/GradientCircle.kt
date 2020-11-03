package com.swaylight.custom_ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.SweepGradient
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.swaylight.R
import com.swaylight.R.styleable.*
import com.swaylight.SwayLightMainActivity


class GradientCircle(context: Context, attrs: AttributeSet?): View(context, attrs) {


    private val TAG = GradientCircle::class.java.simpleName
    var startColor: Int = Color.BLACK
    var endColor: Int = Color.BLACK
    var centerColor: Int? = null
    var ringColor: Int = 0xFFFFFFFF.toInt()
    var rotation: Int = 0
    var isCheck: Boolean = false

    private var value = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20F, context.resources.displayMetrics)
    private val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(value.toInt()*2, value.toInt()*2)
    private val circlePaint = Paint()
    private val ringPaint = Paint()

    constructor(context: Context, attrs: Nothing?, startColor: Int, endColor: Int) : this(context, attrs) {
        this.startColor = startColor
        this.endColor = endColor
    }

    constructor(context: Context, attrs: Nothing?, color: Int) : this(context, attrs) {
        this.startColor = color
        this.endColor = color
    }

    init {
        context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.GradientCircle,
                0, 0).apply {

            try {
                super.setLayoutParams(params)
                startColor = getInteger(GradientCircle_android_startColor, 0)
                endColor = getInteger(GradientCircle_android_endColor, 0)
                centerColor = getInteger(GradientCircle_android_centerColor, 0)
                ringColor = getInteger(GradientCircle_ringColor, 0xFFFFFFFF.toInt())
                rotation = getFloat(GradientCircle_android_rotation, 0f).toInt()
                isCheck = getBoolean(GradientCircle_isCheck, false)
            } finally {
                recycle()
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        this.layoutParams = params
        super.onDraw(canvas)

        canvas?.rotate(rotation.toFloat(), value, value)
        circlePaint.isAntiAlias = true
        circlePaint.isFilterBitmap = true
        if(centerColor == 0) {
            circlePaint.shader = SweepGradient(value,  value, intArrayOf(startColor, endColor), floatArrayOf(0f, 1.0f))

        }else {

            circlePaint.shader = SweepGradient(value, value, intArrayOf(startColor, centerColor!!, endColor), floatArrayOf(0f, 0.5f, 1.0f))
        }

        ringPaint.isAntiAlias = true
        ringPaint.isFilterBitmap = true
        ringPaint.style = Paint.Style.STROKE
        ringPaint.color = ringColor
        ringPaint.strokeWidth = value * 0.15F

        canvas?.drawCircle(value, value, value*0.7F, circlePaint)
        if(isCheck) {
            canvas?.drawCircle(value, value, value*0.9F, ringPaint)
        }
        invalidate()
    }
}