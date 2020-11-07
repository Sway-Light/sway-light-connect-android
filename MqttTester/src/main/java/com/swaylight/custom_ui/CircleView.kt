package com.swaylight.custom_ui

import android.content.Context
import android.graphics.*
import android.graphics.drawable.GradientDrawable
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
    var type: Int = 0

    private var size = resources.getDimension(R.dimen.color_circle_radius)
    private val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(size.toInt()*2, size.toInt()*2)
    private val circlePaint = Paint()
    private val ringPaint = Paint()
    private var grad: Shader? = null

    constructor(context: Context, attrs: Nothing?, startColor: Int, endColor: Int, centerColor: Int?, type: Int) : this(context, attrs) {
        this.startColor = startColor
        this.endColor = endColor
        this.centerColor = centerColor
        this.type = type
        when(type) {
            GradientDrawable.SWEEP_GRADIENT -> {
                grad = if (centerColor == null) {
                    SweepGradient(size,  size,
                            intArrayOf(startColor, endColor),
                            floatArrayOf(0f, 1.0f)
                    )
                }else {
                    SweepGradient(size,  size,
                            intArrayOf(startColor, centerColor, endColor),
                            floatArrayOf(0f, 0.5f, 1.0f)
                    )
                }
            }
            GradientDrawable.LINEAR_GRADIENT -> {
                grad = LinearGradient(
                        size, size*2, size, 0f,
                        intArrayOf(endColor, centerColor!!, startColor),
                        floatArrayOf(0.2f, 0.5f, 0.8f),
                        Shader.TileMode.REPEAT
                )
            }
        }
    }

    constructor(context: Context, attrs: Nothing?, startColor: Int, endColor: Int, type: Int) : this(context, attrs) {
        this.startColor = startColor
        this.endColor = endColor
        this.type = type
        when(type) {
            GradientDrawable.SWEEP_GRADIENT -> {
                grad = SweepGradient(size,  size, intArrayOf(startColor, endColor), floatArrayOf(0f, 1.0f))
            }
            GradientDrawable.LINEAR_GRADIENT -> {
                grad = LinearGradient(
                        size, size*2, size, 0f,
                        intArrayOf(endColor, startColor),
                        floatArrayOf(0.2f, 0.8f),
                        Shader.TileMode.REPEAT
                )
            }
        }
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
                type = getInteger(R.styleable.CircleView_android_type, GradientDrawable.SWEEP_GRADIENT)
                when(type) {
                    GradientDrawable.SWEEP_GRADIENT -> {
                        grad = if (centerColor == null) {
                            SweepGradient(size,  size, intArrayOf(startColor, endColor), floatArrayOf(0f, 1.0f))
                        }else {
                            SweepGradient(size,  size, intArrayOf(startColor, centerColor!!, endColor), floatArrayOf(0f, 0.5f, 1.0f))
                        }
                    }
                    GradientDrawable.LINEAR_GRADIENT -> {
                        grad = LinearGradient(size/2, 0f, size/2, size,
                        intArrayOf(endColor, centerColor!!, startColor),
                        floatArrayOf(0.2f, 0.5f, 0.8f),
                        Shader.TileMode.REPEAT)
                    }
                }
            } finally {
                recycle()
            }
        }
    }

    fun setColor(startColor: Int, centerColor: Int?, endColor: Int, type: Int) {
        this.startColor = startColor
        this.centerColor = centerColor
        this.endColor = endColor
        this.type = type
        when(type) {
            GradientDrawable.SWEEP_GRADIENT -> {
                grad = SweepGradient(size,  size, intArrayOf(startColor, endColor), floatArrayOf(0f, 1.0f))
            }
            GradientDrawable.LINEAR_GRADIENT -> {
                if (centerColor == null) {
                    grad = LinearGradient(
                            size, size*2, size, 0f,
                            intArrayOf(endColor, startColor),
                            floatArrayOf(0.2f, 0.8f),
                            Shader.TileMode.REPEAT
                    )
                }else {
                    grad = LinearGradient(
                            size, size*2, size, 0f,
                            intArrayOf(endColor, centerColor, startColor),
                            floatArrayOf(0.2f, 0.5f, 0.8f),
                            Shader.TileMode.REPEAT
                    )
                }
            }
        }
    }

    fun setColor(color: Int) {
        this.startColor = color
        this.centerColor = color
        this.endColor = color
        grad = LinearGradient(
                size, size*2, size, 0f,
                color,
                color,
                Shader.TileMode.REPEAT
        )
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