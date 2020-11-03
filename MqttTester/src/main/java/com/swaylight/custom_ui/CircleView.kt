package com.swaylight.custom_ui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import com.swaylight.R


class CircleView(context: Context, attrs: AttributeSet?): View(context, attrs) {


    private val TAG = CircleView::class.java.simpleName
    var startColor: Int = Color.BLACK
    var endColor: Int = Color.BLACK
    var centerColor: Int? = null
    var ringColor: Int = 0xFFFFFFFF.toInt()
    var rotation: Int = 0
    var isCheck: Boolean = false

    private var size = resources.getDimension(R.dimen.color_circle_radius)
    private val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(size.toInt()*2, size.toInt()*2)
    private val circlePaint = Paint()
    private val ringPaint = Paint()
    private var grad: Shader? = null

    constructor(context: Context, attrs: Nothing?, startColor: Int, endColor: Int, centerColor: Int) : this(context, attrs) {
        this.grad = SweepGradient(size,  size, intArrayOf(startColor, centerColor, endColor), floatArrayOf(0f, 0.5f, 1.0f))
        this.startColor = startColor
        this.endColor = endColor
        this.centerColor = centerColor
    }

    constructor(context: Context, attrs: Nothing?, startColor: Int, endColor: Int) : this(context, attrs) {
        this.grad = SweepGradient(size,  size, intArrayOf(startColor, endColor), floatArrayOf(0f, 1.0f))
        this.startColor = startColor
        this.endColor = endColor
    }

    constructor(context: Context, attrs: Nothing?, color: Int) : this(context, attrs) {
        this.grad = SweepGradient(size,  size, color, color)
        this.startColor = color
        this.endColor = color
    }

    init {
        context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.CircleView,
                0, 0).apply {

            try {
                super.setLayoutParams(params)
                startColor = getInteger(R.styleable.CircleView_android_startColor, 0)
                endColor = getInteger(R.styleable.CircleView_android_endColor, 0)
                centerColor = getInteger(R.styleable.CircleView_android_centerColor, 0)
                ringColor = getInteger(R.styleable.CircleView_ringColor, 0xFFFFFFFF.toInt())
                rotation = getFloat(R.styleable.CircleView_android_rotation, 0f).toInt()
                isCheck = getBoolean(R.styleable.CircleView_isCheck, false)
                grad = if (centerColor == null) {
                    SweepGradient(size,  size, intArrayOf(startColor, endColor), floatArrayOf(0f, 1.0f))
                }else {
                    SweepGradient(size,  size, intArrayOf(startColor, centerColor!!, endColor), floatArrayOf(0f, 0.5f, 1.0f))
                }
            } finally {
                recycle()
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        this.layoutParams = params
        super.onDraw(canvas)

        canvas?.rotate(rotation.toFloat(), size, size)
        circlePaint.apply {
            isAntiAlias = true
            isFilterBitmap = true
            shader = grad
        }

        ringPaint.apply {
            isAntiAlias = true
            isFilterBitmap = true
            style = Paint.Style.STROKE
            color = ringColor
            strokeWidth = size * 0.15F
        }

        canvas?.drawCircle(size, size, size*0.7F, circlePaint)
        if(isCheck) {
            canvas?.drawCircle(size, size, size*0.9F, ringPaint)
        }
        invalidate()
    }
}