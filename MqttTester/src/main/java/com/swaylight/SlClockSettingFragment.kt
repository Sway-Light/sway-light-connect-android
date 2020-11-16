package com.swaylight

import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.icu.util.LocaleData
import android.os.Bundle
import android.text.format.Time
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.swaylight.library.SLMqttClient
import com.swaylight.library.SLMqttManager
import com.swaylight.library.data.SLClockSetting
import com.swaylight.library.data.SLTopic
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.Month
import java.time.format.DateTimeFormatter
import java.util.*

class SlClockSettingFragment : Fragment() {
    private val TAG = SwayLightMainActivity::class.java.simpleName
    // UI
    private lateinit var v: View
    private lateinit var tvOnTime: TextView
    private lateinit var tvOffTime:TextView

    // values
    private var client: SLMqttClient? = null
    private var deviceName: String? = null
    var onTimeObj = SLClockSetting()
    var offTimeObj = SLClockSetting()
    private var _onHour: Int = 0
    private var _onMinute: Int = 0
    private var _offHour: Int = 0
    private var _offMinute: Int = 0
    var onHour: Int
        get() = _onHour
        set(value) {
            _onHour = value
            onTimeObj.hour = value
            tvOnTime.text = getTimeString(_onHour, _onMinute)
        }
    var onMinute: Int
        get() = _onMinute
        set(value) {
            _onMinute = value
            onTimeObj.min = value
            tvOnTime.text = getTimeString(_onHour, _onMinute)
        }
    var offHour: Int
        get() = _offHour
        set(value) {
            _offHour = value
            offTimeObj.hour = value
            tvOffTime.text = getTimeString(_offHour, _offMinute)
        }
    var offMinute: Int
        get() = _offMinute
        set(value) {
            _offMinute = value
            offTimeObj.min = value
            tvOffTime.text = getTimeString(_offHour, _offMinute)
        }
    private val enable =  BooleanArray(7)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_sl_clock_setting, container, false)
        initUi()
        client = SLMqttManager.getInstance()
        deviceName = SLMqttManager.getDeviceName()
        for (i in enable.indices) {
            enable[i] = true
        }
        onTimeObj.setEnable(enable)
        offTimeObj.setEnable(enable)
        tvOnTime.setOnClickListener {
            TimePickerDialog(v.context, { view, hourOfDay, minute ->
                onHour = hourOfDay
                onMinute = minute
                client?.publish(SLTopic.POWER_START_TIME, deviceName, onTimeObj.instance)
            }, onHour, onMinute, true).show()
        }
        tvOffTime.setOnClickListener {
            TimePickerDialog(v.context, { view, hourOfDay, minute ->
                offHour = hourOfDay
                offMinute = minute
                client?.publish(SLTopic.POWER_END_TIME, deviceName, offTimeObj.instance)
            }, offHour, offMinute, true).show()
        }
        return v
    }

    private fun initUi() {
        tvOnTime = v.findViewById(R.id.tv_on_time)
        tvOffTime = v.findViewById(R.id.tv_off_time)
    }

    private fun getTimeString(hour: Int, minute: Int): String {
        val time = Date().apply {
            hours = hour
            minutes = minute
        }
        return SimpleDateFormat("HH:mm").format(time)
    }
}