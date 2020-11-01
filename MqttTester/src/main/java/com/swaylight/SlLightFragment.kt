package com.swaylight

import android.R
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat.getDrawable
import androidx.fragment.app.Fragment


class SlLightFragment : Fragment() {

    var ivGradientCircle:ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_sl_light, container, false)
        ivGradientCircle = v.findViewById(R.id.iv_gradient_circle)
//        val drawable: Drawable? = getDrawable(R.drawable.gradient_circle)

        return v
    }
}
