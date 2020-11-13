package com.swaylight

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
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.flag.BubbleFlag
import com.skydoves.colorpickerview.flag.FlagMode
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import com.swaylight.custom_ui.CircleView
import com.swaylight.data.GradientColor
import com.swaylight.data.RgbColor
import com.swaylight.library.SLMqttClient
import com.swaylight.library.SLMqttManager
import com.swaylight.library.data.SLColor
import com.swaylight.library.data.SLTopic


class SlLightFragment : Fragment() {

    private val TAG = SwayLightMainActivity::class.java.simpleName
    // UI
    private lateinit var v: View

    private lateinit var gradCircleGroup: LinearLayout
    private lateinit var rgbCircleGroup: LinearLayout
    private lateinit var lightTopConstraint: ViewGroup
    private lateinit var topBgView: FrameLayout
    private lateinit var gradTab: LinearLayout
    private lateinit var rgbTab: LinearLayout
    private lateinit var gradControlCard: RelativeLayout
    private lateinit var rgbControlCard: RelativeLayout
    private lateinit var btStartColor: ImageButton
    private lateinit var btEndColor: ImageButton
    private lateinit var btAddGrad: ImageButton
    private lateinit var btAddRgb: ImageButton

    private lateinit var btRgbColor: ImageButton
    private lateinit var sbGrad: SeekBar
    private lateinit var sbRed: SeekBar
    private lateinit var sbGreen: SeekBar
    private lateinit var sbBlue: SeekBar

    // values
    private var client: SLMqttClient? = null
    private var deviceName: String? = null
    private var currGradIndex = 0
    private var currRgbIndex = 0
    var type: ControlType = ControlType.GRADIENT_COLOR
    var gradCircleViews: ArrayList<CircleView> = arrayListOf()
    var rgbCircleViews: ArrayList<CircleView> = arrayListOf()
    var gradColorList: ArrayList<GradientColor> = arrayListOf()
    var rgbColorList: ArrayList<RgbColor>? = null
    private var mqttColorObj = SLColor(0, 0, 0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_sl_light, container, false)
        client = SLMqttManager.getInstance()
        deviceName = SLMqttManager.getDeviceName()
        rgbColorList = arrayListOf(
                RgbColor(ContextCompat.getColor(requireContext(), R.color.david_green)),
                RgbColor(Color.BLACK),
                RgbColor(Color.BLUE),
                RgbColor(Color.WHITE),
                RgbColor(Color.GREEN))
        initUi()
        gradColorList.addAll(
                arrayOf(
                        GradientColor(
                                ContextCompat.getColor(requireContext(), R.color.light_grad_default_start),
                                Color.WHITE,
                                ContextCompat.getColor(requireContext(), R.color.light_grad_default_end)),
                        GradientColor(
                                ContextCompat.getColor(requireContext(), R.color.light_grad1_start),
                                ContextCompat.getColor(requireContext(), R.color.light_grad1_end)),
                        GradientColor(
                                ContextCompat.getColor(requireContext(), R.color.light_grad2_start),
                                ContextCompat.getColor(requireContext(), R.color.light_grad2_end))
                )
        )
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

        sbGrad.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val currGrad = gradColorList[currGradIndex]
                if (fromUser) {
                    val color = if (currGrad.centerColor == null) {
                        Utils.getColorFromGradient(currGrad.startColor!!, currGrad.endColor!!, progress, sbGrad.max)
                    }else {
                        if (progress < sbGrad.max/2) {
                            Utils.getColorFromGradient(currGrad.startColor!!, currGrad.centerColor!!, progress, sbGrad.max/2)
                        }else {
                            Utils.getColorFromGradient(currGrad.centerColor!!, currGrad.endColor!!, progress.minus(sbGrad.max/2), sbGrad.max/2)
                        }
                    }
                    lightTopConstraint.setBackgroundColor(color)
                    mqttColorObj.setColor(color)
                    client?.publish(SLTopic.LIGHT_MODE_COLOR, deviceName, mqttColorObj.instance)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

        btStartColor.setOnClickListener {
            val builder = ColorPickerDialog.Builder(context)
                    .setTitle("ColorPicker start color")
                    .setPreferenceName("Test")
                    .setPositiveButton(
                            getString(android.R.string.ok),
                            ColorEnvelopeListener { envelope, _ ->
                                btStartColor.drawable.colorFilter = PorterDuffColorFilter(envelope.color, PorterDuff.Mode.SRC)
                                gradCircleViews[currGradIndex].startColor = envelope.color
                                gradColorList[currGradIndex].startColor = envelope.color
                                Utils.setSeekBarColor(sbGrad, gradColorList[currGradIndex])
                                lightTopConstraint.setBackgroundColor(
                                        Utils.getColorFromGradient(
                                                gradColorList[currGradIndex].startColor!!,
                                                gradColorList[currGradIndex].endColor!!,
                                                sbGrad.progress,
                                                sbGrad.max)
                                )
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
                                lightTopConstraint.setBackgroundColor(
                                        Utils.getColorFromGradient(
                                                gradColorList[currGradIndex].startColor!!,
                                                gradColorList[currGradIndex].endColor!!,
                                                sbGrad.progress,
                                                sbGrad.max)
                                )
                            }
                    )
                    .setNegativeButton(
                            getString(android.R.string.cancel)
                    ) { dialogInterface, i -> dialogInterface.dismiss() }
            builder.colorPickerView.flagView = BubbleFlag(context).apply { flagMode = FlagMode.FADE }
            builder.show()
        }

        btAddGrad.setOnClickListener {
            val newGradColor = GradientColor(Color.BLACK, null, Color.WHITE)
            gradColorList.add(newGradColor)
            generateGradCircles()
        }

        btAddRgb.setOnClickListener {
            val newRgbColor = RgbColor(Color.BLACK)
            rgbColorList!!.add(newRgbColor)
            generateRgbCircles()
        }

        sbRed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val color = Utils.getColorFromRgbSeekBar(sbRed, sbGreen, sbBlue)

                if (fromUser) {
                    rgbCircleViews[currRgbIndex].setColor(color)
                    rgbColorList!![currRgbIndex].color = color
                    lightTopConstraint.setBackgroundColor(color)
                    btRgbColor.drawable.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC)
                    mqttColorObj.setColor(color)
                    client?.publish(SLTopic.LIGHT_MODE_COLOR, deviceName, mqttColorObj.instance)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

        sbGreen.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val color = Utils.getColorFromRgbSeekBar(sbRed, sbGreen, sbBlue)
                if (fromUser) {
                    rgbCircleViews[currRgbIndex].setColor(color)
                    rgbColorList!![currRgbIndex].color = color
                    lightTopConstraint.setBackgroundColor(color)
                    btRgbColor.drawable.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC)
                    mqttColorObj.setColor(color)
                    client?.publish(SLTopic.LIGHT_MODE_COLOR, deviceName, mqttColorObj.instance)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

        sbBlue.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val color = Utils.getColorFromRgbSeekBar(sbRed, sbGreen, sbBlue)
                if (fromUser) {
                    rgbCircleViews[currRgbIndex].setColor(color)
                    rgbColorList!![currRgbIndex].color = color
                    lightTopConstraint.setBackgroundColor(color)
                    btRgbColor.drawable.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC)
                    mqttColorObj.setColor(color)
                    client?.publish(SLTopic.LIGHT_MODE_COLOR, deviceName, mqttColorObj.instance)
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
//                    lightTopConstraint.setBackgroundColor(gradColorList[currGradIndex].startColor!!)
                    generateGradCircles()
                }
                ControlType.RGB_COLOR -> {
//                    lightTopConstraint.setBackgroundColor(rgbColorList!![currRgbIndex].color!!)
                    generateRgbCircles()
                }
            }
            topBgView.setBackgroundResource(R.drawable.bg_top_light_view)
        }
    }

    private fun initUi() {
        lightTopConstraint = requireActivity().findViewById(R.id.lightTopConstraint)
        topBgView = requireActivity().findViewById(R.id.light_bg_view)
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
        btAddGrad = v.findViewById(R.id.bt_add_grad)
        btAddRgb = v.findViewById(R.id.bt_add_rgb)
    }

    enum class ControlType(val type: Int) {
        GRADIENT_COLOR(0x00),
        RGB_COLOR(0x01)
    }

    private fun setControlType(type: ControlType) {
        when(type) {
            ControlType.GRADIENT_COLOR -> {
                collapse(rgbControlCard)
                expand(gradControlCard)
                lightTopConstraint.setBackgroundColor(
                        Utils.getColorFromGradient(
                                gradColorList[currGradIndex].startColor!!,
                                gradColorList[currGradIndex].endColor!!,
                                sbGrad.progress,
                                sbGrad.max)
                )
            }
            ControlType.RGB_COLOR -> {
                collapse(gradControlCard)
                expand(rgbControlCard)
                lightTopConstraint.setBackgroundColor(rgbColorList!![currRgbIndex].color!!)
            }
        }
        this.type = type
    }

    private fun generateRgbCircles() {
        rgbCircleViews.clear()
        rgbCircleGroup.removeAllViews()
        for(rgbColor in rgbColorList!!) {
            val circleView = CircleView(requireContext()).apply {
                startColor = rgbColor.color!!
                centerColor = rgbColor.color!!
                endColor = rgbColor.color!!
                gradientType = GradientDrawable.SWEEP_GRADIENT
                isCheck = false
            }
            circleView.setOnClickListener{
                for (gc in rgbCircleViews) {
                    gc.isCheck = false
                }
                circleView.isCheck = true
                btRgbColor.drawable.colorFilter = PorterDuffColorFilter(circleView.startColor, PorterDuff.Mode.SRC)
                Utils.setSeekBarColor(sbRed, sbGreen, sbBlue, rgbColor)
                lightTopConstraint.setBackgroundColor(rgbColor.color!!)
                currRgbIndex = rgbCircleViews.indexOf(circleView)
            }
            circleView.setOnLongClickListener {
                if (rgbCircleViews.size > 1) {
                    val removeIndex = rgbCircleViews.indexOf(circleView)
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setMessage("Delete this color?")
                    builder.setPositiveButton("Yes") { dialog, which ->
                        rgbColorList!!.removeAt(removeIndex)
                        rgbCircleGroup.removeViewAt(removeIndex)
                        rgbCircleViews.removeAt(removeIndex)
                        if (removeIndex == currRgbIndex) {
                            rgbCircleGroup[0].callOnClick()
                        }
                    }
                    builder.setNegativeButton("Cancel") { dialog, which -> }
                    builder.show()
                }
                true
            }
            rgbCircleViews.add(circleView)
            rgbCircleGroup.addView(circleView)
        }
        rgbCircleGroup[currRgbIndex].callOnClick()
    }

    private fun generateGradCircles() {
        gradCircleViews.clear()
        gradCircleGroup.removeAllViews()
        for(gradColor in gradColorList) {
            val circleView = CircleView(requireContext()).apply {
                startColor = gradColor.startColor!!
                centerColor = gradColor.centerColor
                endColor = gradColor.endColor!!
                gradientType = GradientDrawable.SWEEP_GRADIENT
                rotation = 45f
            }
            circleView.setOnClickListener{
                for (gc in gradCircleViews) {
                    gc.isCheck = false
                }
                circleView.isCheck = true
                btStartColor.drawable.colorFilter = PorterDuffColorFilter(circleView.startColor, PorterDuff.Mode.SRC)
                btEndColor.drawable.colorFilter = PorterDuffColorFilter(circleView.endColor, PorterDuff.Mode.SRC)
                Utils.setSeekBarColor(sbGrad, gradColor)
                lightTopConstraint.setBackgroundColor(gradColor.startColor!!)
                currGradIndex = gradCircleViews.indexOf(circleView)
            }
            circleView.setOnLongClickListener {
                if (gradCircleViews.size > 1) {
                    val removeIndex = gradCircleViews.indexOf(circleView)
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setMessage("Delete this color?")
                    builder.setPositiveButton("Yes") { dialog, which ->
                        gradColorList.removeAt(removeIndex)
                        gradCircleGroup.removeViewAt(removeIndex)
                        gradCircleViews.removeAt(removeIndex)
                        if (removeIndex == currGradIndex) {
                            gradCircleGroup[0].callOnClick()
                        }
                    }
                    builder.setNegativeButton("Cancel") { dialog, which -> }
                    builder.show()
                }
                true
            }
            gradCircleViews.add(circleView)
            gradCircleGroup.addView(circleView)
        }
        gradCircleGroup[currGradIndex].callOnClick()
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
