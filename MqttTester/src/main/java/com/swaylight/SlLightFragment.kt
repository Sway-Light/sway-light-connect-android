package com.swaylight

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.swaylight.custom_ui.GradientCircle


class SlLightFragment : Fragment() {

    private val TAG = SwayLightMainActivity::class.java.simpleName

    var gradCircle:GradientCircle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_sl_light, container, false)
        gradCircle = v.findViewById(R.id.grad_circle)
        gradCircle!!.setOnClickListener{
            gradCircle!!.isCheck = (!gradCircle!!.isCheck)
            gradCircle!!.rotation = 180
            Log.d(TAG, "isCheck " + gradCircle!!.isCheck)
        }
        return v
    }
}
