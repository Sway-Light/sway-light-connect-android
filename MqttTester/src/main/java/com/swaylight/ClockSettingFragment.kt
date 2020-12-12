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
import com.swaylight.library.SLMqttClient
import com.swaylight.library.SLMqttManager
import com.swaylight.library.data.BtModule.SLBtModuleOperation
import com.swaylight.library.data.BtModule.SLBtModuleStatus
import com.swaylight.library.data.SLClockSetting
import com.swaylight.library.data.SLTopic
import java.util.*

class ClockSettingFragment : Fragment() {
    private val TAG = this::class.simpleName.toString()
    private var client: SLMqttClient? = null
    private var deviceName: String? = null
    private var onTimeObj = SLClockSetting()
    private var offTimeObj = SLClockSetting()
    private var btModuleOperation = SLBtModuleOperation()

    var on = true
    private val options = arrayOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    private val enable =  BooleanArray(7)

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

        for(i in options) {
            val cb = CheckBox(v.context)
            cb.setOnCheckedChangeListener { buttonView, isChecked ->
                enable[options.indexOf(i)] = isChecked
                Log.d(TAG, "enable" + Arrays.toString(enable))
                onTimeObj.setEnable(enable)
                offTimeObj.setEnable(enable)
            }
            cb.text = i
            enableGroup.addView(cb)
        }
        onTimeObj.setEnable(enable)
        offTimeObj.setEnable(enable)

        val timePickerDialog = TimePickerDialog(v.context, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            if(on) {
                onTimeObj.setClock(hourOfDay, minute, 0)
                tvOnTime.text = "On time: ${onTimeObj.hour}:${onTimeObj.min}"
            }else {
                offTimeObj.setClock(hourOfDay, minute, 0)
                tvOffTime.text = "Off time: ${offTimeObj.hour}:${offTimeObj.min}"
            }
        }, 0, 0, true)

        tvOnTime.setOnClickListener {
            on = true
            timePickerDialog.show()
        }

        tvOffTime.setOnClickListener {
            on = false
            timePickerDialog.show()
        }

        btSetting.setOnClickListener {
            client!!.publish(SLTopic.POWER_START_TIME, deviceName, onTimeObj.instance)
            client!!.publish(SLTopic.POWER_END_TIME, deviceName, offTimeObj.instance)
        }

        v.findViewById<Button>(R.id.bt_play).setOnClickListener {
            btModuleOperation.setOpCode(SLBtModuleOperation.Code.PLAY)
            client!!.publish(SLTopic.BT_MODULE_OPERATION, deviceName, btModuleOperation.instance)
        }

        v.findViewById<Button>(R.id.bt_pause).setOnClickListener {
            btModuleOperation.setOpCode(SLBtModuleOperation.Code.PAUSE)
            client!!.publish(SLTopic.BT_MODULE_OPERATION, deviceName, btModuleOperation.instance)
        }

        v.findViewById<Button>(R.id.bt_stop).setOnClickListener {
            btModuleOperation.setOpCode(SLBtModuleOperation.Code.STOP)
            client!!.publish(SLTopic.BT_MODULE_OPERATION, deviceName, btModuleOperation.instance)
        }

        v.findViewById<Button>(R.id.bt_forward).setOnClickListener {
            btModuleOperation.setOpCode(SLBtModuleOperation.Code.FORWARD)
            client!!.publish(SLTopic.BT_MODULE_OPERATION, deviceName, btModuleOperation.instance)
        }

        v.findViewById<Button>(R.id.bt_backward).setOnClickListener {
            btModuleOperation.setOpCode(SLBtModuleOperation.Code.BACKWARD)
            client!!.publish(SLTopic.BT_MODULE_OPERATION, deviceName, btModuleOperation.instance)
        }

        v.findViewById<Button>(R.id.bt_vol_up).setOnClickListener {
            btModuleOperation.setOpCode(SLBtModuleOperation.Code.VOL_UP)
            client!!.publish(SLTopic.BT_MODULE_OPERATION, deviceName, btModuleOperation.instance)
        }

        v.findViewById<Button>(R.id.bt_vol_down).setOnClickListener {
            btModuleOperation.setOpCode(SLBtModuleOperation.Code.VOL_DOWN)
            client!!.publish(SLTopic.BT_MODULE_OPERATION, deviceName, btModuleOperation.instance)
        }
        return v
    }
}