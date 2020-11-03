package com.swaylight

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.SeekBar
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

    var btStartColor: ImageButton? = null
    var btEndColor: ImageButton? = null
    var sbGrad: SeekBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_sl_light, container, false)
        btStartColor = v.findViewById(R.id.bt_start_color)
        btEndColor = v.findViewById(R.id.bt_end_color)
        gradCircleGroup = v.findViewById(R.id.grad_circle_group)
        sbGrad = v.findViewById(R.id.sb_grad)
        generateGradCircles()

        rgbColorList = arrayListOf(
                RgbColor(ContextCompat.getColor(context!!, R.color.david_green)),
                RgbColor(Color.BLACK),
                RgbColor(Color.BLUE),
                RgbColor(Color.WHITE),
                RgbColor(Color.GREEN))
        rgbCircleGroup = v.findViewById(R.id.rgb_circle_group)
        generateRgbCircles()
        return v
    }

    private fun generateRgbCircles() {
        for(rgbColor in rgbColorList!!) {
            val g = CircleView(context!!, null, rgbColor.color!!)
            g.setOnClickListener{
                for (gc in rgbCircleViews) {
                    gc.isCheck = false
                }
                g.isCheck = true
            }
            rgbCircleViews.add(g)
            rgbCircleGroup!!.addView(g)
        }
        val firstCircle = rgbCircleViews[0]
        firstCircle.isCheck = true
    }

    @RequiresApi(Build.VERSION_CODES.Q)
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
}
