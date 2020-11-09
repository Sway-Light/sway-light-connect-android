package com.swaylight.custom_ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.LinearLayout
import com.swaylight.R

/**
 *
 */
class TopLightView : View {

    private var _strokeColor: Int = Color.RED
    private var _strokeBgColor: Int = Color.GRAY
    private var _fillColor: Int = Color.WHITE
    private var _strokeWidth: Float = 0f
    private var _zoomValue: Int = 4
    private var _offsetValue: Int = 0
    private var _startAngle = 0f
    private var _endAngle = 0f

    private var animator: ValueAnimator? = null
    private var currentValue = 0f

    var offsetAngle = 0f

    companion object {
        const val ANGLE_UNIT = 360.div(32.toDouble())
        private val TAG = TopLightView::class.java.simpleName

    }

    private lateinit var arcPaint: Paint
    private lateinit var fillPaint: Paint
    private lateinit var strokeBgPaint: Paint

    /**
     * The stroke color
     */
    var strokeColor: Int
        get() = _strokeColor
        set(value) {
            _strokeColor = value
            invalidatePaintAndMeasurements()
        }

    var strokeBgColor: Int
        get() = _strokeBgColor
        set(value) {
            _strokeBgColor = value
            invalidatePaintAndMeasurements()
        }

    var fillColor: Int
        get() = _fillColor
        set(value) {
            _fillColor = value
            invalidatePaintAndMeasurements()
        }

    /**
     * In the example view, this dimension is the font size.
     */
    var strokeWidth: Float
        get() = _strokeWidth
        set(value) {
            _strokeWidth = value
            invalidatePaintAndMeasurements()
        }

    var zoomValue: Int
        get() = _zoomValue
        set(value) {
            if (value in 4..32) {
                _zoomValue = value
                startAnimation()
                invalidatePaintAndMeasurements()
            }
        }

    var offsetValue: Int
        get() = _offsetValue
        set(value) {
            var v = value
            if (v > 31) {
                while(v !in 0..31) {
                    v -= 32
                }
            }else if(v < 0) {
                while(v !in 0..31) {
                    v += 32
                }
            }
            _offsetValue = v
            startAnimation()
            invalidatePaintAndMeasurements()
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
                attrs, R.styleable.TopLightView, defStyle, 0)

        _strokeColor = a.getColor(
                R.styleable.TopLightView_stroke_color,
                strokeColor
        )
        _strokeBgColor = a.getColor(
                R.styleable.TopLightView_stroke_bg_color,
                strokeBgColor
        )
        _fillColor = a.getColor(
                R.styleable.TopLightView_fill_color,
                fillColor
        )
        _strokeWidth = a.getDimension(
                R.styleable.TopLightView_stroke_width,
                strokeWidth
        )
        _zoomValue = a.getInteger(
                R.styleable.TopLightView_zoom_value,
                zoomValue
        )
        _offsetValue = a.getInteger(
                R.styleable.TopLightView_offset_value,
                offsetValue
        )
        layoutParams = LinearLayout.LayoutParams(R.dimen.color_circle_radius, R.dimen.color_circle_radius)
        arcPaint = Paint()
        fillPaint = Paint()
        strokeBgPaint = Paint()
        a.recycle()
        invalidatePaintAndMeasurements()
    }

    private fun invalidatePaintAndMeasurements() {
        arcPaint.apply {
            isAntiAlias = true
            isFilterBitmap = true
            style = Paint.Style.STROKE
            this.strokeWidth = _strokeWidth
            this.color = _strokeColor
            strokeCap = Paint.Cap.ROUND
        }
        fillPaint.apply {
            isAntiAlias = true
            isFilterBitmap = true
            style = Paint.Style.FILL
            this.color = _fillColor
        }
        strokeBgPaint.apply {
            isAntiAlias = true
            isFilterBitmap = true
            style = Paint.Style.STROKE
            this.strokeWidth = _strokeWidth
            this.color = _strokeBgColor
        }
        offsetAngle = 0f + _offsetValue.times(ANGLE_UNIT + currentValue).toFloat()
        _endAngle = 0f.plus((ANGLE_UNIT+currentValue).times(zoomValue/2).toFloat())
        _startAngle = if(zoomValue.rem(2) == 0) {
            0f.minus((ANGLE_UNIT+currentValue).times(zoomValue/2).toFloat())
        }else {
            0f.minus((ANGLE_UNIT+currentValue).times(zoomValue/2 + 1).toFloat())
        }
    }

    private fun startAnimation() {
        animator?.cancel()
        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 200
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { valueAnimator ->
                currentValue = valueAnimator.animatedValue as Float
                invalidatePaintAndMeasurements()
            }
        }
//        animator?.addListener(object : AnimatorListenerAdapter() {
//            override fun onAnimationEnd(animation: Animator) {
//                startAnimation()
//            }
//        })
        animator?.start()
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

        canvas.rotate(offsetAngle, width.div(2f), width.div(2f))
        canvas.drawCircle(
                width/2f,
                width/2f,
                width.div(2f).minus(_strokeWidth),
                fillPaint)
        canvas.drawArc(
                paddingLeft.toFloat() + _strokeWidth,
                paddingTop.toFloat() + _strokeWidth,
                width - (paddingRight.toFloat() + _strokeWidth),
                height - (paddingBottom.toFloat() + _strokeWidth),
                0f,
                360f,
                false,
                strokeBgPaint
        )
        canvas.drawArc(
                paddingLeft.toFloat() + _strokeWidth,
                paddingTop.toFloat() + _strokeWidth,
                width - (paddingRight.toFloat() + _strokeWidth),
                height - (paddingBottom.toFloat() + _strokeWidth),
                _startAngle,
                _endAngle - _startAngle,
                false,
                arcPaint
        )
        invalidate()
    }
}