package com.swaylight

import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.swaylight.mqtt.SLMqttClient
import com.swaylight.mqtt.SLMqttManager
import com.swaylight.mqtt.SLTopic
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONStringer
import java.util.*

class ClockSettingFragment : Fragment() {
    private val TAG = this::class.simpleName.toString()
    private var client: SLMqttClient? = null
    private var deviceName: String? = null
    private var onTimeJsonObj: JSONObject? = null
    private var offTimeJsonObj: JSONObject? = null
    private var map: Map<String, Int>? = null

    var on = true
    val options = arrayOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val enable = arrayOf(0, 0, 0, 0, 0, 0, 0)
    var onHour = 0
    var onMin = 0
    var offHour = 0
    var offMin = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_clock_setting, container, false)
        client = SLMqttManager.getInstance()
        deviceName = SLMqttManager.getDeviceName()
        val tvOnTime = v.findViewById<TextView>(R.id.on_time_textview)
        val tvOffTime = v.findViewById<TextView>(R.id.off_time_textview)
        tvOnTime.text = "On time: 0:0"
        tvOffTime.text = "Off time: 0:0"
        val enableGroup = v.findViewById<LinearLayout>(R.id.enable_container)
        val btSetting = v.findViewById<Button>(R.id.setting_button)

        initJson()

        for(i in options) {
            val cb = CheckBox(v.context)
            cb.setOnCheckedChangeListener { buttonView, isChecked ->
                if(isChecked) {
                    enable[options.indexOf(i)] = 1
                }else {
                    enable[options.indexOf(i)] = 0
                }
                updateJsonObj()
                Log.d(TAG, "enable" + Arrays.toString(enable))
            }
            cb.text = i
            enableGroup.addView(cb)
        }

        val timePickerDialog = TimePickerDialog(v.context, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            if(on) {
                onHour = hourOfDay
                onMin = minute
                tvOnTime.text = "On time: $onHour:$onMin"
                updateJsonObj()
            }else {
                offHour = hourOfDay
                offMin = minute
                tvOffTime.text = "Off time: $offHour:$offMin"
                updateJsonObj()
            }
        }, onHour, onMin, true)

        tvOnTime.setOnClickListener {
            on = true
            timePickerDialog.show()
        }

        tvOffTime.setOnClickListener {
            on = false
            timePickerDialog.show()
        }

        btSetting.setOnClickListener {
            client!!.publish(SLTopic.POWER_START_TIME, deviceName, onTimeJsonObj)
            client!!.publish(SLTopic.POWER_END_TIME, deviceName, offTimeJsonObj)
        }

        return v
    }

    private fun initJson() {
        map = HashMap()
        (map as HashMap<String, Int>)[context!!.getString(R.string.hour)] = 0
        (map as HashMap<String, Int>)[context!!.getString(R.string.min)] = 0
        (map as HashMap<String, Int>)[context!!.getString(R.string.sec)] = 0
        (map as HashMap<String, Array<Int>>)[context!!.getString(R.string.enable)] = enable
        onTimeJsonObj = JSONObject(map)
        offTimeJsonObj = JSONObject(map)
    }

    private fun updateJsonObj() {
        onTimeJsonObj!!.put(context!!.getString(R.string.hour), onHour)
        onTimeJsonObj!!.put(context!!.getString(R.string.min), onMin)
        onTimeJsonObj!!.put(context!!.getString(R.string.enable), JSONArray(enable))
        offTimeJsonObj!!.put(context!!.getString(R.string.hour), offHour)
        offTimeJsonObj!!.put(context!!.getString(R.string.min), offMin)
        offTimeJsonObj!!.put(context!!.getString(R.string.enable), JSONArray(enable))
        Log.d(TAG, "onTimeJsonObj:" + onTimeJsonObj)
        Log.d(TAG, "offTimeJsonObj:" + offTimeJsonObj)
    }
}