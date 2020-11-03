package com.swaylight

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.swaylight.custom_ui.GradientCircle
import com.swaylight.data.GradientColor


class SlLightFragment : Fragment() {

    private val TAG = SwayLightMainActivity::class.java.simpleName

    var gradCircles: ArrayList<GradientCircle> = arrayListOf()
    var gradCircleGroup: LinearLayout? = null
    var gradColorList: ArrayList<GradientColor> = arrayListOf(
            GradientColor(Color.BLACK, Color.BLUE),
            GradientColor(Color.WHITE, Color.RED),
            GradientColor(Color.GREEN, Color.DKGRAY))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_sl_light, container, false)
        gradCircleGroup = v.findViewById(R.id.grad_circle_group)
        generateGradCircles()
        return v
    }

    private fun generateGradCircles() {
        for(gradColor in gradColorList) {
            val g = GradientCircle(context!!, null, gradColor.startColor!!, gradColor.endColor!!)
            g.isCheck = false
            g.ringColor = 0xFFFFFFFF.toInt()
            g.setOnClickListener{
                for (gc in gradCircles) {
                    gc.isCheck = false
                }
                g.isCheck = true
            }
            gradCircles.add(g)
            gradCircleGroup!!.addView(g)
        }
    }
}
