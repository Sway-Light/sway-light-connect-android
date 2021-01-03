package com.swaylight

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import com.swaylight.R.drawable.bg_basic_music_control
import com.swaylight.R.drawable.bg_visual_music_control
import com.swaylight.library.SLMqttClient
import com.swaylight.library.SLMqttManager
import com.swaylight.library.data.BtModule.SLBtModuleOperation
import com.swaylight.library.data.SLMode
import com.swaylight.library.data.SLTopic


/**
 * A simple [Fragment] subclass.
 * Use the [SlMusicControlFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SlMusicControlFragment : Fragment() {
    private lateinit var v: View
    private lateinit var btPrev: ImageButton
    private lateinit var btPlayAndPause: ImageButton
    private lateinit var btNext: ImageButton
    private lateinit var btVolDown: ImageButton
    private lateinit var btVolUp: ImageButton
    private lateinit var btBtStatus: ImageButton
    private lateinit var tvVolume: TextView

    // values
    private var client: SLMqttClient? = null
    private var deviceName: String? = null
    private var curMode: SLMode = SLMode.LIGHT
    private var isBtConnect: Boolean = false
    private var isMusicPlaying: Boolean = false
    private var volume: Int = 0

    // MQTT Object
    private var btModuleOpObj = SLBtModuleOperation()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_sl_music_control, container, false)

        client = SLMqttManager.getInstance()
        deviceName = SLMqttManager.getDeviceName()
        initUi()
        refreshUi()

        btPrev.setOnClickListener {
            btModuleOpObj.setOpCode(SLBtModuleOperation.Code.BACKWARD)
            client?.publish(SLTopic.BT_MODULE_OPERATION, deviceName, btModuleOpObj.instance)
        }

        btPlayAndPause.setOnClickListener {
            btModuleOpObj.setOpCode(SLBtModuleOperation.Code.PLAY)
            client?.publish(SLTopic.BT_MODULE_OPERATION, deviceName, btModuleOpObj.instance)
        }

        btNext.setOnClickListener {
            btModuleOpObj.setOpCode(SLBtModuleOperation.Code.FORWARD)
            client?.publish(SLTopic.BT_MODULE_OPERATION, deviceName, btModuleOpObj.instance)
        }

        btVolUp.setOnClickListener {
            btModuleOpObj.setOpCode(SLBtModuleOperation.Code.VOL_UP)
            client?.publish(SLTopic.BT_MODULE_OPERATION, deviceName, btModuleOpObj.instance)
        }

        btVolDown.setOnClickListener {
            btModuleOpObj.setOpCode(SLBtModuleOperation.Code.VOL_DOWN)
            client?.publish(SLTopic.BT_MODULE_OPERATION, deviceName, btModuleOpObj.instance)
        }
        return v
    }

    fun setMode(mode: SLMode) {
        curMode = mode
        refreshUi()
    }

    fun getBtStatus(): Boolean {
        return isBtConnect
    }

    fun setBtStatus(isConnect: Boolean) {
        isBtConnect = isConnect
        refreshUi()
    }

    fun getIsMusicPlaying(): Boolean {
        return isMusicPlaying
    }

    fun setIsMusicPlaying(isPlaying: Boolean) {
        isMusicPlaying = isPlaying
        refreshUi()
    }

    fun getVolume(): Int {
        return volume
    }

    fun setVolume(volume: Int) {
        if(volume !in 0..15) {
            return
        }
        this.volume = volume
        refreshUi()
    }

    private fun refreshUi() {
        tvVolume.apply {
            if(isBtConnect) {
                text = volume.toString()
                setTextAppearance(R.style.bt_connected)
            }else {
                text = "-"
                setTextAppearance(R.style.bt_disconnected)
            }
        }
        if (curMode == SLMode.MUSIC) {
            v.findViewById<FrameLayout>(R.id.root_frame).setBackgroundResource(bg_visual_music_control)
            if (isBtConnect) {
                if(isMusicPlaying) {
                    btPlayAndPause.setImageResource(R.mipmap.ic_visual_enable_music_pause)
                }else {
                    btPlayAndPause.setImageResource(R.mipmap.ic_visual_enable_music_play)
                }
                btBtStatus.setImageResource(R.mipmap.ic_visual_bt_connected)
                btPrev.setImageResource(R.mipmap.ic_visual_enable_music_prev)
                btNext.setImageResource(R.mipmap.ic_visual_enable_music_next)
                btVolDown.setImageResource(R.mipmap.ic_visual_enable_music_volume_down)
                btVolUp.setImageResource(R.mipmap.ic_visual_enable_music_volume_up)
            }else {
                btBtStatus.setImageResource(R.mipmap.ic_visual_bt_disconnected)
                btPrev.setImageResource(R.mipmap.ic_visual_disable_music_prev)
                btPlayAndPause.setImageResource(R.mipmap.ic_visual_disable_music_play)
                btNext.setImageResource(R.mipmap.ic_visual_disable_music_next)
                btVolDown.setImageResource(R.mipmap.ic_visual_disable_music_volume_down)
                btVolUp.setImageResource(R.mipmap.ic_visual_disable_music_volume_up)
            }
        }else if(curMode == SLMode.LIGHT) {
            v.findViewById<FrameLayout>(R.id.root_frame).setBackgroundResource(bg_basic_music_control)
            if (isBtConnect) {
                if (isMusicPlaying) {
                    btPlayAndPause.setImageResource(R.mipmap.ic_basic_enable_music_pause)
                }else {
                    btPlayAndPause.setImageResource(R.mipmap.ic_basic_enable_music_play)
                }
                btBtStatus.setImageResource(R.mipmap.ic_basic_bt_connected)
                btPrev.setImageResource(R.mipmap.ic_basic_enable_music_prev)
                btNext.setImageResource(R.mipmap.ic_basic_enable_music_next)
                btVolDown.setImageResource(R.mipmap.ic_basic_enable_music_volume_down)
                btVolUp.setImageResource(R.mipmap.ic_basic_enable_music_volume_up)
            }else {
                btBtStatus.setImageResource(R.mipmap.ic_basic_bt_disconnected)
                btPrev.setImageResource(R.mipmap.ic_basic_disable_music_prev)
                btPlayAndPause.setImageResource(R.mipmap.ic_basic_disable_music_play)
                btNext.setImageResource(R.mipmap.ic_basic_disable_music_next)
                btVolDown.setImageResource(R.mipmap.ic_basic_disable_music_volume_down)
                btVolUp.setImageResource(R.mipmap.ic_basic_disable_music_volume_up)
            }
        }
    }

    private fun initUi() {
        btPrev = v.findViewById(R.id.bt_music_prev)
        btPlayAndPause = v.findViewById(R.id.bt_music_play_and_pause)
        btNext = v.findViewById(R.id.bt_music_next)
        btVolDown = v.findViewById(R.id.bt_music_volume_down)
        btVolUp = v.findViewById(R.id.bt_music_volume_up)
        btBtStatus = v.findViewById(R.id.bt_bt_status)
        tvVolume = v.findViewById(R.id.tv_volume)
    }
}