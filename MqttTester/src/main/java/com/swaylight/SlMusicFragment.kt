package com.swaylight

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.swaylight.custom_ui.CircleView
import com.swaylight.data.GradientColor


class SlMusicFragment : Fragment() {

    private val TAG = SlMusicFragment::class.java.simpleName

    var gradCircleViews: ArrayList<CircleView> = arrayListOf()
    var gradCircleGroup: LinearLayout? = null
    var gradColorList: ArrayList<GradientColor> = arrayListOf(
            GradientColor(Color.BLACK, Color.WHITE, Color.BLUE),
            GradientColor(Color.BLACK, Color.BLUE),
            GradientColor(Color.WHITE, Color.RED),
            GradientColor(Color.GREEN, Color.DKGRAY))

    var gradControlCard: RelativeLayout? = null

    var sbRed: SeekBar? = null
    var sbGreen: SeekBar? = null
    var sbBlue: SeekBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_sl_music, container, false)
        gradControlCard = v.findViewById(R.id.grad_control_card)
        gradCircleGroup = v.findViewById(R.id.grad_circle_group)
        generateGradCircles()

        sbRed = v.findViewById(R.id.sb_red)
        sbGreen = v.findViewById(R.id.sb_green)
        sbBlue = v.findViewById(R.id.sb_blue)
        v.findViewById<TextView>(R.id.tv_grad).setTextAppearance(R.style.tv_mode_selected)
        return v
    }

    private fun generateGradCircles() {

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
