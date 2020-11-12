package com.swaylight

import android.annotation.SuppressLint
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.swaylight.custom_ui.CircleView
import com.swaylight.custom_ui.EqualizerView
import com.swaylight.data.GradientColor


class SlMusicFragment : Fragment() {

    private val TAG = SlMusicFragment::class.java.simpleName

    // UI
    private lateinit var v: View
    private lateinit var lightTopConstraint: ViewGroup
    private lateinit var topBgView: FrameLayout
    private lateinit var gradCircleGroup: LinearLayout
    private lateinit var gradControlCard: RelativeLayout
    private lateinit var equalizerView: EqualizerView
    private lateinit var highCircleView: CircleView
    private lateinit var mediumCircleView: CircleView
    private lateinit var lowCircleView: CircleView
    private lateinit var sbRed: SeekBar
    private lateinit var sbGreen: SeekBar
    private lateinit var sbBlue: SeekBar

    // values
    private var currIndex = 0
    var gradCircleViews: ArrayList<CircleView> = arrayListOf()
    private lateinit var gradColorList: ArrayList<GradientColor>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @SuppressLint("UseRequireInsteadOfGet")
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
            topBgView.setBackgroundResource(R.drawable.bg_top_music_view)
            setEqualizerColor(gradColorList[currIndex])
        }
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun initUi() {
        lightTopConstraint = activity!!.findViewById(R.id.lightTopConstraint)
        topBgView = activity!!.findViewById(R.id.light_bg_view)
        gradControlCard = v.findViewById(R.id.grad_control_card)
        gradCircleGroup = v.findViewById(R.id.grad_circle_group)
        equalizerView = v.findViewById(R.id.equalizer_view)
        highCircleView = v.findViewById(R.id.music_high_circle)
        mediumCircleView = v.findViewById(R.id.music_medium_circle)
        lowCircleView = v.findViewById(R.id.music_low_circle)
        sbRed = v.findViewById(R.id.sb_red)
        sbGreen = v.findViewById(R.id.sb_green)
        sbBlue = v.findViewById(R.id.sb_blue)
    }

    private fun generateGradCircles() {
        for(gradColor in gradColorList) {
            val g = CircleView(requireContext()).apply {
                startColor = gradColor.startColor!!
                centerColor = gradColor.centerColor
                endColor = gradColor.endColor!!
                gradientType = GradientDrawable.LINEAR_GRADIENT
            }
            g.setOnClickListener{
                for (gc in gradCircleViews) {
                    gc.isCheck = false
                }
                g.isCheck = true
                currIndex = gradCircleViews.indexOf(g)

                Utils.setBgColor(lightTopConstraint,
                        gradColor,
                        GradientDrawable.Orientation.TOP_BOTTOM)
                setEqualizerColor(gradColor)
                setCirclesColor(gradColor)
            }
            g.setOnLongClickListener {
                if (gradCircleViews.size > 1) {
                    val removeIndex = gradCircleViews.indexOf(g)
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setMessage("Delete this color?")
                    builder.setPositiveButton("Yes") { dialog, which ->
                        gradColorList.removeAt(removeIndex)
                        gradCircleGroup.removeViewAt(removeIndex)
                        gradCircleViews.removeAt(removeIndex)
                        if (removeIndex == currIndex) {
                            gradCircleGroup[0].callOnClick()
                        }
                    }
                    builder.setNegativeButton("Cancel") { dialog, which -> }
                    builder.show()
                }
                true
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
        setCirclesColor(gradColorList[currIndex])
    }

    private fun setEqualizerColor(g: GradientColor) {
        equalizerView.highColor = g.startColor!!
        equalizerView.mediumColor = g.centerColor!!
        equalizerView.lowColor = g.endColor!!
    }

    private fun setCirclesColor(g: GradientColor) {
        highCircleView.setColor(g.startColor!!)
        mediumCircleView.setColor(g.centerColor!!)
        lowCircleView.setColor(g.endColor!!)
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
