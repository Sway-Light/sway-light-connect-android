package com.swaylight

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.swaylight.custom_ui.CircleView
import com.swaylight.data.GradientColor


class SlLightFragment : Fragment() {

    private val TAG = SwayLightMainActivity::class.java.simpleName

    var gradCircleViews: ArrayList<CircleView> = arrayListOf()
    var gradCircleGroup: LinearLayout? = null
    var gradColorList: ArrayList<GradientColor> = arrayListOf(
            GradientColor(Color.BLACK, Color.BLUE),
            GradientColor(Color.WHITE, Color.RED),
            GradientColor(Color.GREEN, Color.DKGRAY))
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
        return v
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun generateGradCircles() {
        for(gradColor in gradColorList) {
            val g = CircleView(context!!, null, gradColor.startColor!!, gradColor.endColor!!)
            g.isCheck = false
            g.ringColor = 0xFFFFFFFF.toInt()
            g.setOnClickListener{
                for (gc in gradCircleViews) {
                    gc.isCheck = false
                }
                g.isCheck = true
                btStartColor!!.drawable.colorFilter = PorterDuffColorFilter(g.startColor, PorterDuff.Mode.SRC)
                btEndColor!!.drawable.colorFilter = PorterDuffColorFilter(g.endColor, PorterDuff.Mode.SRC)
                val colors = intArrayOf(g.startColor, g.endColor)
                val gradDrawable = sbGrad!!.progressDrawable as GradientDrawable
                gradDrawable.colors = colors
                gradDrawable.orientation = GradientDrawable.Orientation.LEFT_RIGHT
            }
            gradCircleViews.add(g)
            gradCircleGroup!!.addView(g)
        }

        val firstCircle = gradCircleViews[0]
        val colors = intArrayOf(firstCircle.startColor, firstCircle.endColor)
        val gradDrawable = sbGrad!!.progressDrawable as GradientDrawable
        firstCircle.isCheck = true
        btStartColor!!.drawable.colorFilter = PorterDuffColorFilter(firstCircle.startColor, PorterDuff.Mode.SRC)
        btEndColor!!.drawable.colorFilter = PorterDuffColorFilter(firstCircle.endColor, PorterDuff.Mode.SRC)
        gradDrawable.colors = colors
        gradDrawable.orientation = GradientDrawable.Orientation.LEFT_RIGHT
    }
}
