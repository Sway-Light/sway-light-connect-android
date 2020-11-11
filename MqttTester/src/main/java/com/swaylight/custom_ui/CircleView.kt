package com.swaylight.custom_ui

import android.content.Context
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup.LayoutParams
import com.swaylight.R

/**
 * TODO: document your custom view class.
 */
class CircleView : View {

    private val tag = CircleView::class.java.simpleName
    private var _width: Float = resources.getDimensionPixelSize(R.dimen.color_circle_radius)*2f
    private var _startColor: Int = Color.BLACK
    private var _centerColor: Int? = Color.WHITE
    private var _endColor: Int = Color.BLACK
    private var _ringColor: Int = 0xFFFFFFFF.toInt()
    private var _isCheck: Boolean = false
    private var _gradientType: Int = GradientDrawable.LINEAR_GRADIENT

    private lateinit var circlePaint: Paint
    private lateinit var ringPaint: Paint
    private lateinit var grad: Shader

    private val colors = IntArray(3)
    private val sweepPosition2: FloatArray = floatArrayOf(0f, 1f)
    private val sweepPosition3: FloatArray = floatArrayOf(0f, 0.5f, 1f)
    private val linearPosition2: FloatArray = floatArrayOf(0.2f, 0.8f)
    private val linearPosition3: FloatArray = floatArrayOf(0.2f, 0.5f, 0.8f)

    var startColor: Int
        get() = _startColor
        set(value) {
            _startColor = value
            colors[0] = _startColor
            invalidatePaintAndMeasurements()
        }

    var centerColor: Int?
        get() = _centerColor
        set(value) {
            _centerColor = value
            if (_centerColor != null) {
                colors[1] = _centerColor!!
            }
            invalidatePaintAndMeasurements()
        }

    var endColor: Int
        get() = _endColor
        set(value) {
            _endColor = value
            colors[2] = _endColor
            invalidatePaintAndMeasurements()
        }

    var ringColor: Int
        get() = _ringColor
        set(value) {
            _ringColor = value
            invalidatePaintAndMeasurements()
        }

    var isCheck: Boolean
        get() = _isCheck
        set(value) {
            _isCheck = value
            Log.d(tag, "set isClick")
            invalidatePaintAndMeasurements()
        }

    var gradientType: Int
        get() = _gradientType
        set(value) {
            _gradientType = value
            invalidatePaintAndMeasurements()
        }
    var width: Float
        get() = _width
        set(value) {
            _width = value
            invalidatePaintAndMeasurements()
        }

    fun setColor(color: Int) {
        this.startColor = color
        this.centerColor = color
        this.endColor = color
    }

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
        val a = context.obtainStyledAttributes(
                attrs, R.styleable.CircleView, defStyle, 0)

        _width = a.getDimension(R.styleable.CircleView_android_width, width)
        this.layoutParams = LayoutParams(_width.toInt(), _width.toInt())
        _startColor = a.getColor(R.styleable.CircleView_android_startColor, startColor)
        _centerColor = a.getColor(R.styleable.CircleView_android_centerColor, centerColor!!)
        _endColor = a.getColor(R.styleable.CircleView_android_endColor, endColor)
        _ringColor = a.getColor(R.styleable.CircleView_ringColor, ringColor)
        _isCheck = a.getBoolean(R.styleable.CircleView_isCheck, isCheck)
        _gradientType = a.getInteger(R.styleable.CircleView_android_type, gradientType)
        circlePaint = Paint()
        ringPaint = Paint()
        colors.apply {
            this[0] = _startColor
            this[1] = _centerColor!!
            this[2] = _endColor
        }

        a.recycle()

        // Update TextPaint and text measurements from attributes
        invalidatePaintAndMeasurements()
    }

    private fun invalidatePaintAndMeasurements() {
        when(_gradientType) {
            GradientDrawable.SWEEP_GRADIENT -> {
                grad = if (_centerColor == null) {
                    SweepGradient(
                            _width/2f,
                            _width/2f,
                            _startColor,
                            _endColor
                    )
                }else {
                    SweepGradient(
                            _width/2f,
                            _width/2f,
                            colors,
                            sweepPosition3
                    )
                }
            }
            GradientDrawable.LINEAR_GRADIENT -> {
                grad = if (_centerColor == null) {
                    LinearGradient(
                            _width/2f,
                            _width,
                            _width/2f,
                            0f,
                            _endColor,
                            _startColor,
                            Shader.TileMode.REPEAT
                    )
                }else {
                    LinearGradient(
                            _width/2f,
                            _width,
                            _width/2f,
                            0f,
                            colors,
                            linearPosition3,
                            Shader.TileMode.REPEAT
                    )
                }
            }
        }
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        val paddingLeft = paddingLeft
        val paddingTop = paddingTop
        val paddingRight = paddingRight
        val paddingBottom = paddingBottom

        val contentWidth = width - paddingLeft - paddingRight
        val contentHeight = height - paddingTop - paddingBottom

        canvas.rotate(rotation, contentWidth/2f, contentWidth/2f)


        ringPaint.apply {
            isAntiAlias = true
            isFilterBitmap = true
            style = Paint.Style.STROKE
            color = ringColor
            strokeWidth = contentWidth/2f * 0.15F
        }
        circlePaint.apply {
            isAntiAlias = true
            isFilterBitmap = true
            shader = grad
        }

        canvas.drawCircle(
                contentWidth/2f,
                contentWidth/2f,
                contentWidth/2f*0.7F,
                circlePaint
        )

        if(_isCheck) {
            canvas.drawCircle(
                    contentWidth/2f,
                    contentWidth/2f,
                    contentWidth/2f*0.9F,
                    ringPaint
            )
        }
    }
}