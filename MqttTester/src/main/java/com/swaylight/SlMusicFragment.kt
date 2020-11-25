package com.swaylight

import android.annotation.SuppressLint
import android.graphics.Color
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
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.flag.BubbleFlag
import com.skydoves.colorpickerview.flag.FlagMode
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import com.swaylight.custom_ui.CircleView
import com.swaylight.custom_ui.EqualizerView
import com.swaylight.data.FilePath
import com.swaylight.data.GradientColor
import com.swaylight.library.SLMqttClient
import com.swaylight.library.SLMqttManager
import com.swaylight.library.data.SLMusicColor
import com.swaylight.library.data.SLTopic


class SlMusicFragment : Fragment() {

    private val TAG = SlMusicFragment::class.java.simpleName

    // UI
    private lateinit var v: View
    private lateinit var lightTopConstraint: ViewGroup
    private lateinit var topBgView: FrameLayout
    private lateinit var gradCircleGroup: LinearLayout
    private lateinit var gradControlCard: RelativeLayout
    private lateinit var btAddGrad: ImageButton
    private lateinit var equalizerView: EqualizerView
    private lateinit var highCircleView: CircleView
    private lateinit var mediumCircleView: CircleView
    private lateinit var lowCircleView: CircleView
    private lateinit var sbRed: SeekBar
    private lateinit var sbGreen: SeekBar
    private lateinit var sbBlue: SeekBar

    // values
    private var client: SLMqttClient? = null
    private var deviceName: String? = null
    private var currIndex = 0
    var gradCircleViews: ArrayList<CircleView> = arrayListOf()
    private var gradColorList: ArrayList<GradientColor> = arrayListOf()
    private var mqttMusicColorObj = SLMusicColor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_sl_music, container, false)
        client = SLMqttManager.getInstance()
        deviceName = SLMqttManager.getDeviceName()

        // file is empty, add init color.
        gradColorList = Utils.readFromFile(context?.filesDir.toString(), FilePath.MUSIC_RGB_COLOR) as ArrayList<GradientColor>
        if(gradColorList.isEmpty()) {
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
            updateColorAndFile()
        }
        initUi()
        generateGradCircles()
        btAddGrad.setOnClickListener {
            val newGradColor = GradientColor(
                    Color.RED,
                    Color.YELLOW,
                    Color.GREEN
            )
            gradColorList.add(newGradColor)
            updateColorAndFile()
            generateGradCircles()
        }
        highCircleView.setOnClickListener {
            val builder = ColorPickerDialog.Builder(context)
                    .setTitle("ColorPicker HIGH color")
                    .setPreferenceName("Test")
                    .setPositiveButton(
                            getString(android.R.string.ok),
                            ColorEnvelopeListener { envelope, _ ->
                                gradCircleViews[currIndex].startColor = envelope.color
                                gradColorList[currIndex].startColor = envelope.color
                                updateColorAndFile()
                                generateGradCircles()
                            }
                    )
                    .setNegativeButton(
                            getString(android.R.string.cancel)
                    ) { dialogInterface, i -> dialogInterface.dismiss() }
            builder.colorPickerView.flagView = BubbleFlag(context).apply { flagMode = FlagMode.FADE }
            builder.show()
        }

        mediumCircleView.setOnClickListener {
            val builder = ColorPickerDialog.Builder(context)
                    .setTitle("ColorPicker MIDDLE color")
                    .setPreferenceName("Test")
                    .setPositiveButton(
                            getString(android.R.string.ok),
                            ColorEnvelopeListener { envelope, _ ->
                                gradCircleViews[currIndex].centerColor = envelope.color
                                gradColorList[currIndex].centerColor = envelope.color
                                updateColorAndFile()
                                generateGradCircles()
                            }
                    )
                    .setNegativeButton(
                            getString(android.R.string.cancel)
                    ) { dialogInterface, i -> dialogInterface.dismiss() }
            builder.colorPickerView.flagView = BubbleFlag(context).apply { flagMode = FlagMode.FADE }
            builder.show()
        }

        lowCircleView.setOnClickListener {
            val builder = ColorPickerDialog.Builder(context)
                    .setTitle("ColorPicker LOW color")
                    .setPreferenceName("Test")
                    .setPositiveButton(
                            getString(android.R.string.ok),
                            ColorEnvelopeListener { envelope, _ ->
                                gradCircleViews[currIndex].endColor = envelope.color
                                gradColorList[currIndex].endColor = envelope.color
                                updateColorAndFile()
                                generateGradCircles()
                            }
                    )
                    .setNegativeButton(
                            getString(android.R.string.cancel)
                    ) { dialogInterface, i -> dialogInterface.dismiss() }
            builder.colorPickerView.flagView = BubbleFlag(context).apply { flagMode = FlagMode.FADE }
            builder.show()
        }
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
            generateGradCircles()
        }
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun initUi() {
        lightTopConstraint = activity!!.findViewById(R.id.lightTopConstraint)
        topBgView = activity!!.findViewById(R.id.light_bg_view)
        gradControlCard = v.findViewById(R.id.grad_control_card)
        gradCircleGroup = v.findViewById(R.id.grad_circle_group)
        btAddGrad = v.findViewById(R.id.bt_add_grad)
        equalizerView = v.findViewById(R.id.equalizer_view)
        highCircleView = v.findViewById(R.id.music_high_circle)
        mediumCircleView = v.findViewById(R.id.music_medium_circle)
        lowCircleView = v.findViewById(R.id.music_low_circle)
        sbRed = v.findViewById(R.id.sb_red)
        sbGreen = v.findViewById(R.id.sb_green)
        sbBlue = v.findViewById(R.id.sb_blue)
    }

    private fun generateGradCircles() {
        gradCircleViews.clear()
        gradCircleGroup.removeAllViews()
        for(gradColor in gradColorList) {
            val circleView = CircleView(requireContext()).apply {
                startColor = gradColor.startColor!!
                centerColor = gradColor.centerColor
                endColor = gradColor.endColor!!
                setColor(
                        gradColor.endColor!!,
                        gradColor.centerColor,
                        gradColor.startColor!!)
                gradientType = GradientDrawable.LINEAR_GRADIENT
            }
            circleView.setOnClickListener{
                for (gc in gradCircleViews) {
                    gc.isCheck = false
                }
                circleView.isCheck = true
                currIndex = gradCircleViews.indexOf(circleView)

                Utils.setBgColor(lightTopConstraint,
                        gradColor,
                        GradientDrawable.Orientation.TOP_BOTTOM)
                setEqualizerColor(gradColor)
                setCirclesColor(gradColor)
                mqttMusicColorObj.setColor(gradColor.startColor!!, SLMusicColor.HIGH)
                mqttMusicColorObj.setColor(gradColor.centerColor!!, SLMusicColor.MEDIUM)
                mqttMusicColorObj.setColor(gradColor.endColor!!, SLMusicColor.LOW)
                client?.publish(
                        SLTopic.MUSIC_MODE_COLOR,
                        deviceName,
                        mqttMusicColorObj.instance
                )
                Log.d(TAG, "color:${mqttMusicColorObj.instance.toString()}")
            }
            circleView.setOnLongClickListener {
                if (gradCircleViews.size > 0) {
                    val removeIndex = gradCircleViews.indexOf(circleView)
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setMessage("Delete this color?")
                    builder.setPositiveButton("Yes") { dialog, which ->
                        gradColorList.removeAt(removeIndex)
                        gradCircleGroup.removeViewAt(removeIndex)
                        gradCircleViews.removeAt(removeIndex)
                        updateColorAndFile()
                        if (removeIndex == currIndex) {
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
        for(i in 0..2) {
            try {
                gradCircleViews[i].setOnLongClickListener(null)
            }catch (e: Exception) {
                Log.e(e.toString(), e.message.toString())
            }
        }
        gradCircleGroup[currIndex].callOnClick()
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

    private fun updateColorAndFile() {
        Utils.writeToFile(context?.filesDir.toString(), FilePath.MUSIC_RGB_COLOR, gradColorList)
        gradColorList.clear()
        gradColorList = Utils.readFromFile(context?.filesDir.toString(), FilePath.MUSIC_RGB_COLOR) as ArrayList<GradientColor>
    }
}
