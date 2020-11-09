package com.swaylight

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
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
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.flag.BubbleFlag
import com.skydoves.colorpickerview.flag.FlagMode
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import com.swaylight.R.styleable.CircleView
import com.swaylight.custom_ui.CircleView
import com.swaylight.data.GradientColor
import com.swaylight.data.RgbColor


class SlLightFragment : Fragment() {

    private val TAG = SwayLightMainActivity::class.java.simpleName

    // UI
    private lateinit var v: View
    private lateinit var gradCircleGroup: LinearLayout
    private lateinit var rgbCircleGroup: LinearLayout
    private lateinit var lightTopConstraint: ViewGroup
    private lateinit var gradTab: LinearLayout
    private lateinit var rgbTab: LinearLayout
    private lateinit var gradControlCard: RelativeLayout
    private lateinit var rgbControlCard: RelativeLayout
    private lateinit var btStartColor: ImageButton
    private lateinit var btEndColor: ImageButton

    private lateinit var btRgbColor: ImageButton
    private lateinit var sbGrad: SeekBar
    private lateinit var sbRed: SeekBar
    private lateinit var sbGreen: SeekBar
    private lateinit var sbBlue: SeekBar

    // values
    private var currGradIndex = 0
    private var currRgbIndex = 0
    var type: ControlType = ControlType.GRADIENT_COLOR
    var gradCircleViews: ArrayList<CircleView> = arrayListOf()
    var rgbCircleViews: ArrayList<CircleView> = arrayListOf()
    var gradColorList: ArrayList<GradientColor> = arrayListOf(
            GradientColor(Color.BLACK, Color.WHITE, Color.BLUE),
            GradientColor(Color.BLACK, Color.BLUE),
            GradientColor(Color.WHITE, Color.RED),
            GradientColor(Color.GREEN, Color.DKGRAY))
    var rgbColorList: ArrayList<RgbColor>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_sl_light, container, false)
        rgbColorList = arrayListOf(
                RgbColor(ContextCompat.getColor(context!!, R.color.david_green)),
                RgbColor(Color.BLACK),
                RgbColor(Color.BLUE),
                RgbColor(Color.WHITE),
                RgbColor(Color.GREEN))
        initUi()
        generateGradCircles()
        generateRgbCircles()
        v.findViewById<TextView>(R.id.tv_grad).setTextAppearance(R.style.tv_mode_selected)
        gradTab.setOnClickListener {
            if(this.type != ControlType.GRADIENT_COLOR) {
                v.findViewById<TextView>(R.id.tv_rgb).setTextAppearance(R.style.tv_mode_unselected)
                v.findViewById<TextView>(R.id.tv_grad).setTextAppearance(R.style.tv_mode_selected)
                setControlType(ControlType.GRADIENT_COLOR)
            }
        }
        rgbTab.setOnClickListener {
            if(this.type != ControlType.RGB_COLOR) {
                v.findViewById<TextView>(R.id.tv_rgb).setTextAppearance(R.style.tv_mode_selected)
                v.findViewById<TextView>(R.id.tv_grad).setTextAppearance(R.style.tv_mode_unselected)
                setControlType(ControlType.RGB_COLOR)
            }
        }

        btStartColor.setOnClickListener {
            val builder = ColorPickerDialog.Builder(context)
                    .setTitle("ColorPicker start color")
                    .setPreferenceName("Test")
                    .setPositiveButton(
                            getString(android.R.string.ok),
                            ColorEnvelopeListener { envelope, _ ->
                                btStartColor.drawable.colorFilter = PorterDuffColorFilter(envelope.color, PorterDuff.Mode.SRC)
                                gradCircleViews[currGradIndex].setColor(envelope.color)
                                gradColorList[currGradIndex].startColor = envelope.color
                                Utils.setSeekBarColor(sbGrad, gradColorList[currGradIndex])
                                Utils.setBgColor(lightTopConstraint,
                                        gradColorList[currGradIndex],
                                        GradientDrawable.Orientation.TOP_BOTTOM)
                            }
                    )
                    .setNegativeButton(
                            getString(android.R.string.cancel)
                    ) { dialogInterface, i -> dialogInterface.dismiss() }
            builder.colorPickerView.flagView = BubbleFlag(context).apply { flagMode = FlagMode.FADE }
            builder.show()
        }

        btEndColor.setOnClickListener {
            val builder = ColorPickerDialog.Builder(context)
                    .setTitle("ColorPicker end color")
                    .setPreferenceName("Test")
                    .setPositiveButton(
                            getString(android.R.string.ok),
                            ColorEnvelopeListener { envelope, _ ->
                                btEndColor.drawable.colorFilter = PorterDuffColorFilter(envelope.color, PorterDuff.Mode.SRC)
                                gradCircleViews[currGradIndex].endColor = envelope.color
                                gradColorList[currGradIndex].endColor = envelope.color
                                Utils.setSeekBarColor(sbGrad, gradColorList[currGradIndex])
                                Utils.setBgColor(lightTopConstraint,
                                        gradColorList[currGradIndex],
                                        GradientDrawable.Orientation.TOP_BOTTOM)
                            }
                    )
                    .setNegativeButton(
                            getString(android.R.string.cancel)
                    ) { dialogInterface, i -> dialogInterface.dismiss() }
            builder.colorPickerView.flagView = BubbleFlag(context).apply { flagMode = FlagMode.FADE }
            builder.show()
        }

        sbRed.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val color = sbRed.progress.shl(16)
                        .plus(sbGreen.progress.shl(8))
                        .plus(sbBlue.progress.shl(0))
                        .plus(255.shl(24))

                if (fromUser) {
                    btRgbColor.drawable.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

        sbGreen.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val color = sbRed.progress.shl(16)
                        .plus(sbGreen.progress.shl(8))
                        .plus(sbBlue.progress.shl(0))
                        .plus(255.shl(24))
                if (fromUser) {
                    btRgbColor.drawable.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

        sbBlue.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val color = sbRed.progress.shl(16)
                        .plus(sbGreen.progress.shl(8))
                        .plus(sbBlue.progress.shl(0))
                        .plus(255.shl(24))
                if (fromUser) {
                    btRgbColor.drawable.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
        setControlType(ControlType.GRADIENT_COLOR)
        return v
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            when(type) {
                ControlType.GRADIENT_COLOR -> {
                    Utils.setBgColor(lightTopConstraint,
                            gradColorList[currGradIndex],
                            GradientDrawable.Orientation.TOP_BOTTOM)
                }
                ControlType.RGB_COLOR -> {
                    Utils.setBgColor(lightTopConstraint,
                            rgbColorList!![currRgbIndex],
                            GradientDrawable.Orientation.TOP_BOTTOM)
                }
            }
        }
    }

    private fun initUi() {
        lightTopConstraint = activity!!.findViewById(R.id.lightTopConstraint)
        gradTab = v.findViewById(R.id.grad_tab)
        rgbTab = v.findViewById(R.id.rgb_tab)
        gradControlCard = v.findViewById(R.id.grad_control_card)
        rgbControlCard = v.findViewById(R.id.rgb_control_card)
        btStartColor = v.findViewById(R.id.bt_start_color)
        btEndColor = v.findViewById(R.id.bt_end_color)
        gradCircleGroup = v.findViewById(R.id.grad_circle_group)
        sbGrad = v.findViewById(R.id.sb_grad)
        btRgbColor = v.findViewById(R.id.bt_rgb_color)
        rgbCircleGroup = v.findViewById(R.id.rgb_circle_group)
        sbRed = v.findViewById(R.id.sb_red)
        sbGreen = v.findViewById(R.id.sb_green)
        sbBlue = v.findViewById(R.id.sb_blue)
    }

    enum class ControlType(val type: Int) {
        GRADIENT_COLOR(0x00),
        RGB_COLOR(0x01)
    }

    private fun setControlType(type: ControlType) {
        when(type) {
            ControlType.GRADIENT_COLOR ->{
                collapse(rgbControlCard)
                expand(gradControlCard)
                Utils.setBgColor(lightTopConstraint,
                        gradColorList[currGradIndex],
                        GradientDrawable.Orientation.TOP_BOTTOM)
            }
            ControlType.RGB_COLOR -> {
                collapse(gradControlCard)
                expand(rgbControlCard)
                Utils.setBgColor(lightTopConstraint,
                        rgbColorList!![currRgbIndex],
                        GradientDrawable.Orientation.TOP_BOTTOM)
            }
        }
        this.type = type
    }

    private fun generateRgbCircles() {
        for(rgbColor in rgbColorList!!) {
            val g = CircleView(requireContext()).apply {
                startColor = rgbColor.color!!
                centerColor = rgbColor.color!!
                endColor = rgbColor.color!!
                gradientType = GradientDrawable.SWEEP_GRADIENT
                isCheck = false
            }
            g.setOnClickListener{
                for (gc in rgbCircleViews) {
                    gc.isCheck = false
                }
                g.isCheck = true
                btRgbColor.drawable.colorFilter = PorterDuffColorFilter(g.startColor, PorterDuff.Mode.SRC)
                Utils.setSeekBarColor(sbRed, sbGreen, sbBlue, rgbColor)
                Utils.setBgColor(lightTopConstraint,
                        rgbColor,
                        GradientDrawable.Orientation.TOP_BOTTOM)
                currRgbIndex = rgbCircleViews.indexOf(g)
            }
            g.setOnLongClickListener {
                TODO("long click to remove color")
            }
            rgbCircleViews.add(g)
            rgbCircleGroup.addView(g)
        }
        currRgbIndex = 0
        rgbCircleViews[currRgbIndex].isCheck = true
        btRgbColor.drawable.colorFilter = PorterDuffColorFilter(rgbCircleViews[currRgbIndex].startColor, PorterDuff.Mode.SRC)
        Utils.setSeekBarColor(sbRed, sbGreen, sbBlue, rgbColorList!![currRgbIndex])

    }

    private fun generateGradCircles() {
        for(gradColor in gradColorList) {
            val g = CircleView(requireContext()).apply {
                startColor = gradColor.startColor!!
                centerColor = gradColor.centerColor
                endColor = gradColor.endColor!!
                gradientType = GradientDrawable.SWEEP_GRADIENT
            }
            g.setOnClickListener{
                for (gc in gradCircleViews) {
                    gc.isCheck = false
                }
                g.isCheck = true
                btStartColor.drawable.colorFilter = PorterDuffColorFilter(g.startColor, PorterDuff.Mode.SRC)
                btEndColor.drawable.colorFilter = PorterDuffColorFilter(g.endColor, PorterDuff.Mode.SRC)
                Utils.setSeekBarColor(sbGrad, gradColor)
                Utils.setBgColor(lightTopConstraint,
                        gradColor,
                        GradientDrawable.Orientation.TOP_BOTTOM)
                currGradIndex = gradCircleViews.indexOf(g)
            }
            g.setOnLongClickListener {
                TODO("long click to remove color")
            }
            gradCircleViews.add(g)
            gradCircleGroup.addView(g)
        }
        currGradIndex = 0
        Utils.setSeekBarColor(sbGrad, gradColorList[currGradIndex])
        gradCircleViews[currGradIndex].isCheck = true
        btStartColor.drawable.colorFilter = PorterDuffColorFilter(gradCircleViews[currGradIndex].startColor, PorterDuff.Mode.SRC)
        btEndColor.drawable.colorFilter = PorterDuffColorFilter(gradCircleViews[currGradIndex].endColor, PorterDuff.Mode.SRC)
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
