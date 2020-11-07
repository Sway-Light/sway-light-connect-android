package com.swaylight

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.swaylight.custom_ui.CircleView
import com.swaylight.custom_ui.EqualizerView
import com.swaylight.data.GradientColor


class SlMusicFragment : Fragment() {

    private val TAG = SlMusicFragment::class.java.simpleName

    private lateinit var v: View
    var gradCircleViews: ArrayList<CircleView> = arrayListOf()
    private lateinit var lightTopConstraint: ViewGroup
    private lateinit var gradCircleGroup: LinearLayout
    private lateinit var gradColorList: ArrayList<GradientColor>
    private lateinit var gradControlCard: RelativeLayout
    private lateinit var equalizerView: EqualizerView
    private lateinit var sbRed: SeekBar
    private lateinit var sbGreen: SeekBar
    private lateinit var sbBlue: SeekBar
    private var currIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_sl_music, container, false)
        gradColorList = arrayListOf(
                GradientColor(
                        ContextCompat.getColor(context!!, R.color.red),
                        ContextCompat.getColor(context!!, R.color.yellow),
                        ContextCompat.getColor(context!!, R.color.green)),
                GradientColor(
                        ContextCompat.getColor(context!!, R.color.music_grad1_h),
                        ContextCompat.getColor(context!!, R.color.music_grad1_m),
                        ContextCompat.getColor(context!!, R.color.music_grad1_l)),
                GradientColor(
                        ContextCompat.getColor(context!!, R.color.music_grad2_h),
                        ContextCompat.getColor(context!!, R.color.music_grad2_m),
                        ContextCompat.getColor(context!!, R.color.music_grad2_l))
        )
        initUi()
        generateGradCircles()
//        v.findViewById<TextView>(R.id.tv_grad).setTextAppearance(R.style.tv_mode_selected)
        return v
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            Utils.setBgColor(lightTopConstraint,
                    gradColorList[currIndex],
                    GradientDrawable.Orientation.TOP_BOTTOM)
            setEqualizerColor(gradColorList[currIndex])
        }
    }

    private fun initUi() {
        lightTopConstraint = activity!!.findViewById(R.id.lightTopConstraint)
        gradControlCard = v.findViewById(R.id.grad_control_card)
        gradCircleGroup = v.findViewById(R.id.grad_circle_group)
        equalizerView = v.findViewById(R.id.equalizer_view)
        sbRed = v.findViewById(R.id.sb_red)
        sbGreen = v.findViewById(R.id.sb_green)
        sbBlue = v.findViewById(R.id.sb_blue)
    }

    private fun generateGradCircles() {
        for(gradColor in gradColorList) {
            val g = if (gradColor.centerColor == null) {
                CircleView(context!!, null,
                        gradColor.startColor!!,
                        gradColor.endColor!!,
                        GradientDrawable.LINEAR_GRADIENT
                )
            }else {
                CircleView(context!!, null,
                        gradColor.startColor!!,
                        gradColor.endColor!!,
                        gradColor.centerColor!!,
                        GradientDrawable.LINEAR_GRADIENT
                )
            }
            g.setOnClickListener{
                for (gc in gradCircleViews) {
                    gc.isCheck = false
                }
                g.isCheck = true
                currIndex = gradCircleViews.indexOf(g)
//                btStartColor.drawable.colorFilter = PorterDuffColorFilter(g.startColor, PorterDuff.Mode.SRC)
//                btEndColor.drawable.colorFilter = PorterDuffColorFilter(g.endColor, PorterDuff.Mode.SRC)
//                Utils.setSeekBarColor(sbGrad, gradColor)
//                Utils.setBgColor(lightTopConstraint,
//                        gradColor,
//                        GradientDrawable.Orientation.TOP_BOTTOM)
                Utils.setBgColor(lightTopConstraint,
                        gradColor,
                        GradientDrawable.Orientation.TOP_BOTTOM)
                setEqualizerColor(gradColor)
            }
            gradCircleViews.add(g)
            gradCircleGroup.addView(g)
        }
        currIndex = 0
        gradCircleViews[currIndex].isCheck = true
        Utils.setBgColor(lightTopConstraint,
                gradColorList[currIndex],
                GradientDrawable.Orientation.TOP_BOTTOM)
        setEqualizerColor(gradColorList[currIndex])
    }

    private fun setEqualizerColor(g: GradientColor) {
        equalizerView.highColor = g.startColor!!
        equalizerView.mediumColor = g.centerColor!!
        equalizerView.lowColor = g.endColor!!
    }

    private fun expand(v: View) {
        v.isClickable = false
        v.measure(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        val targetHeight = v.measuredHeight
        if (v.isShown) {
            collapse(v)
        } else {
            v.layoutParams.height = 0
            v.visibility = View.VISIBLE
            val a: Animation = object : Animation() {
                override fun applyTransformation(interpolatedTime: Float,
                                                 t: Transformation?) {
                    v.layoutParams.height =
                            if (interpolatedTime == 1f) RelativeLayout.LayoutParams.WRAP_CONTENT
                            else (targetHeight * interpolatedTime).toInt()
                    v.requestLayout()
                }

                override fun willChangeBounds(): Boolean {
                    return true
                }

                override fun hasEnded(): Boolean {
                    v.isClickable = true
                    return super.hasEnded()
                }
            }
            a.duration = (targetHeight + 300).toLong()
            v.startAnimation(a)
        }
    }

    private fun collapse(v: View) {
        v.isClickable = false
        val initialHeight = v.measuredHeight
        val a: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float,
                                             t: Transformation?) {
                if (interpolatedTime == 1f) {
                    v.visibility = View.GONE
                } else {
                    v.layoutParams.height = (initialHeight
                            - (initialHeight * interpolatedTime).toInt())
                    v.requestLayout()
                }
            }

            override fun willChangeBounds(): Boolean {
                return true
            }

            override fun hasEnded(): Boolean {
                v.isClickable = true
                return super.hasEnded()
            }
        }
        a.duration = (v.layoutParams.height + 300).toLong()
        v.startAnimation(a)
    }
}
