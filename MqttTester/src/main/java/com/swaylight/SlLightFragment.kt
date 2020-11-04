package com.swaylight

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.swaylight.custom_ui.CircleView
import com.swaylight.data.GradientColor
import com.swaylight.data.RgbColor


class SlLightFragment : Fragment() {

    private val TAG = SwayLightMainActivity::class.java.simpleName

    var gradCircleViews: ArrayList<CircleView> = arrayListOf()
    var gradCircleGroup: LinearLayout? = null
    var gradColorList: ArrayList<GradientColor> = arrayListOf(
            GradientColor(Color.BLACK, Color.WHITE, Color.BLUE),
            GradientColor(Color.BLACK, Color.BLUE),
            GradientColor(Color.WHITE, Color.RED),
            GradientColor(Color.GREEN, Color.DKGRAY))

    var rgbCircleViews: ArrayList<CircleView> = arrayListOf()
    var rgbCircleGroup: LinearLayout? = null
    var rgbColorList: ArrayList<RgbColor>? = null

    var gradTab: LinearLayout? = null
    var rgbTab: LinearLayout? = null
    var gradControlCard: RelativeLayout? = null
    var rgbControlCard: RelativeLayout? = null

    var btStartColor: ImageButton? = null
    var btEndColor: ImageButton? = null
    var btRgbColor: ImageButton? = null
    var sbGrad: SeekBar? = null
    var sbRed: SeekBar? = null
    var sbGreen: SeekBar? = null
    var sbBlue: SeekBar? = null

    var type: ControlType = ControlType.GRADIENT_COLOR

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_sl_light, container, false)
        gradTab = v.findViewById(R.id.grad_tab)
        rgbTab = v.findViewById(R.id.rgb_tab)
        gradControlCard = v.findViewById(R.id.grad_control_card)
        rgbControlCard = v.findViewById(R.id.rgb_control_card)
        btStartColor = v.findViewById(R.id.bt_start_color)
        btEndColor = v.findViewById(R.id.bt_end_color)
        gradCircleGroup = v.findViewById(R.id.grad_circle_group)
        sbGrad = v.findViewById(R.id.sb_grad)
        generateGradCircles()

        btRgbColor = v.findViewById(R.id.bt_rgb_color)
        rgbColorList = arrayListOf(
                RgbColor(ContextCompat.getColor(context!!, R.color.david_green)),
                RgbColor(Color.BLACK),
                RgbColor(Color.BLUE),
                RgbColor(Color.WHITE),
                RgbColor(Color.GREEN))
        rgbCircleGroup = v.findViewById(R.id.rgb_circle_group)
        sbRed = v.findViewById(R.id.sb_red)
        sbGreen = v.findViewById(R.id.sb_green)
        sbBlue = v.findViewById(R.id.sb_blue)
        generateRgbCircles()
        v.findViewById<TextView>(R.id.tv_grad).setTextAppearance(R.style.tv_mode_selected)
        gradTab!!.setOnClickListener {
            if(this.type != ControlType.GRADIENT_COLOR) {
                v.findViewById<TextView>(R.id.tv_rgb).setTextAppearance(R.style.tv_mode_unselected)
                v.findViewById<TextView>(R.id.tv_grad).setTextAppearance(R.style.tv_mode_selected)
                setControlType(ControlType.GRADIENT_COLOR)
            }
        }
        rgbTab!!.setOnClickListener {
            if(this.type != ControlType.RGB_COLOR) {
                v.findViewById<TextView>(R.id.tv_rgb).setTextAppearance(R.style.tv_mode_selected)
                v.findViewById<TextView>(R.id.tv_grad).setTextAppearance(R.style.tv_mode_unselected)
                setControlType(ControlType.RGB_COLOR)
            }
        }
        setControlType(ControlType.GRADIENT_COLOR)
        return v
    }

    enum class ControlType(val type: Int) {
        GRADIENT_COLOR(0x00),
        RGB_COLOR(0x01)
    }

    private fun setControlType(type: ControlType) {
        when(type) {
            ControlType.GRADIENT_COLOR ->{

                collapse(rgbControlCard!!)
                expand(gradControlCard!!)
            }
            ControlType.RGB_COLOR -> {
                collapse(gradControlCard!!)
                expand(rgbControlCard!!)
            }
        }
        this.type = type
    }

    private fun generateRgbCircles() {
        for(rgbColor in rgbColorList!!) {
            val g = CircleView(context!!, null, rgbColor.color!!)
            g.setOnClickListener{
                for (gc in rgbCircleViews) {
                    gc.isCheck = false
                }
                g.isCheck = true
                btRgbColor!!.drawable.colorFilter = PorterDuffColorFilter(g.startColor, PorterDuff.Mode.SRC)
                Utils.setSeekBarColor(sbRed!!, sbGreen!!, sbBlue!!, rgbColor)
            }
            rgbCircleViews.add(g)
            rgbCircleGroup!!.addView(g)
        }
        val firstCircle = rgbCircleViews[0]
        firstCircle.isCheck = true
        btRgbColor!!.drawable.colorFilter = PorterDuffColorFilter(firstCircle.startColor, PorterDuff.Mode.SRC)
        Utils.setSeekBarColor(sbRed!!, sbGreen!!, sbBlue!!, rgbColorList!![0])

    }

    private fun generateGradCircles() {
        for(gradColor in gradColorList) {
            val g = if (gradColor.centerColor == null) {
                CircleView(context!!, null, gradColor.startColor!!, gradColor.endColor!!)
            }else {
                CircleView(context!!, null, gradColor.startColor!!, gradColor.endColor!!, gradColor.centerColor!!)
            }
            g.setOnClickListener{
                for (gc in gradCircleViews) {
                    gc.isCheck = false
                }
                g.isCheck = true
                btStartColor!!.drawable.colorFilter = PorterDuffColorFilter(g.startColor, PorterDuff.Mode.SRC)
                btEndColor!!.drawable.colorFilter = PorterDuffColorFilter(g.endColor, PorterDuff.Mode.SRC)
                Utils.setSeekBarColor(sbGrad!!, gradColor)
            }
            gradCircleViews.add(g)
            gradCircleGroup!!.addView(g)
        }

        val firstCircle = gradCircleViews[0]
        Utils.setSeekBarColor(sbGrad!!, gradColorList[0])
        firstCircle.isCheck = true
        btStartColor!!.drawable.colorFilter = PorterDuffColorFilter(firstCircle.startColor, PorterDuff.Mode.SRC)
        btEndColor!!.drawable.colorFilter = PorterDuffColorFilter(firstCircle.endColor, PorterDuff.Mode.SRC)
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
