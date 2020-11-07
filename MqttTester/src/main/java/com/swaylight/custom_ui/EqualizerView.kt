package com.swaylight.custom_ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import com.swaylight.R
import kotlin.math.absoluteValue
import kotlin.random.Random


class EqualizerView(context: Context, attrs: AttributeSet?): View(context, attrs) {
    private val TAG = EqualizerView::class.java.simpleName

    var height: Float = 0f
    var width: Float = 0f
    var freqSize: Int = 16
    var levelSize: Int = 10
    var highColor: Int = Color.RED
    var mediumColor: Int = Color.YELLOW
    var lowColor: Int = Color.GREEN

    private val params: ConstraintLayout.LayoutParams = ConstraintLayout.LayoutParams(width.toInt(), height.toInt())
    private var grad: Shader? = null
    private var fillPaint = Paint()
    private var strokePaint = Paint()
    private var animator: ValueAnimator? = null
    private var currentValue = 0f
    private var startValue: IntArray = IntArray(0)
    private var endValue: IntArray = IntArray(0)
    private var deltaValue: IntArray = IntArray(0)

    init {
        context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.EqualizerView,
                0, 0).apply {

            try {
                width = getDimension(R.styleable.EqualizerView_android_layout_width, 0f)
                height = getDimension(R.styleable.EqualizerView_android_layout_height, 0f)
                params.height = height.toInt()
                params.width = width.toInt()
                super.setLayoutParams(params)
                freqSize = getInteger(R.styleable.EqualizerView_freq_size, 16)
                highColor = getInteger(R.styleable.EqualizerView_high_color, 0)
                mediumColor = getInteger(R.styleable.EqualizerView_medium_color, 0)
                lowColor = getInteger(R.styleable.EqualizerView_low_color, 0)
                grad = LinearGradient(
                        width / 2, height, width / 2, 0f,
                        intArrayOf(lowColor, mediumColor, highColor),
                        floatArrayOf(0f, 0.5f, 1f),
                        Shader.TileMode.REPEAT
                )
                startValue = IntArray(freqSize)
                endValue = IntArray(freqSize)
                deltaValue = IntArray(freqSize)
                for (i in 0 until freqSize) {
                    startValue[i] = 100
                    endValue[i] = 100
                }
                generateRandomData()
                startAnimation()
            } finally {
                recycle()
            }
        }
    }

    private fun generateRandomData() {
        for (i in 0 until freqSize) {
            startValue[i] = endValue[i]
            val weight = i.minus(freqSize.div(2)).absoluteValue.toFloat()
                    .div(freqSize.div(2f))
            endValue[i] = Random.nextInt(80.times(weight).toInt(), 50 + 50.times(weight).times(weight).toInt())
            deltaValue[i] = endValue[i] - startValue[i]
        }
    }

    private fun startAnimation() {
        animator?.cancel()
        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 400
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { valueAnimator ->
                currentValue = valueAnimator.animatedValue as Float
                invalidate()
            }
        }
        animator?.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                generateRandomData()
                startAnimation()
            }
        })
        animator?.start()
    }

//    private fun startNegAnimation() {
//        animator?.cancel()
//        animator = ValueAnimator.ofFloat(1f, 0f).apply {
//            duration = 500
//            interpolator = AccelerateDecelerateInterpolator()
//            addUpdateListener { valueAnimator ->
//                currentValue = valueAnimator.animatedValue as Float
//                getRandomData()
//                invalidate()
//            }
//        }
//        animator?.addListener(object : AnimatorListenerAdapter() {
//            override fun onAnimationEnd(animation: Animator) {
//                startPosAnimation()
//            }
//        })
//        animator?.start()
//    }

    override fun draw(canvas: Canvas?) {
        super.setLayoutParams(params)
        super.draw(canvas)
        grad = LinearGradient(
                width / 2, height, width / 2, 0f,
                intArrayOf(lowColor, mediumColor, highColor),
                floatArrayOf(0f, 0.5f, 1f),
                Shader.TileMode.REPEAT
        )

        fillPaint.apply {
            style = Paint.Style.FILL
            shader = grad
        }
        strokePaint.apply {
            style = Paint.Style.STROKE
            strokeWidth = 1f
            color = Color.GRAY
        }

        for (i in 0 until freqSize) {
            canvas?.drawRect(
                    width * (i.toFloat() / freqSize),
                    startValue[i].plus(deltaValue[i]*currentValue).div(100f).times(height),
                    width * ((i.toFloat() + 1) / freqSize),
                    height,
                    fillPaint)
            canvas?.drawRect(
                    width * (i.toFloat() / freqSize),
                    startValue[i].plus(deltaValue[i]*currentValue).div(100f).times(height),
                    width * ((i.toFloat() + 1) / freqSize),
                    height,
                    strokePaint)
        }
    }
}