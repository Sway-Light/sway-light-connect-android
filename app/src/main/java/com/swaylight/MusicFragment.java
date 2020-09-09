package com.swaylight;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.swaylight.mqtt.Topic;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class MusicFragment extends Fragment {

    // TODO: Rename and change types of parameters
    private final String tag = this.getTag();

    private final String MQTT_TAG   = "mqtt";
    private MqttAndroidClient client;
    private String deviceName;

    private TextView tvModeName;
    private Spinner spLevel;
    private int level;
    private SeekBar sbRed;
    private SeekBar sbGreen;
    private SeekBar sbBlue;
    private SeekBar sbOffset;
    private TextView tvRed;
    private TextView tvGreen;
    private TextView tvBlue;
    private TextView tvOffset;

    private JSONObject mJsonObj;
    private Map<String, Integer> map;

    public MusicFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_music, container, false);

        this.map = new HashMap<String, Integer>();
        map.put(getContext().getString(R.string.r), 0);
        map.put(getContext().getString(R.string.g), 0);
        map.put(getContext().getString(R.string.b), 0);
        map.put(getContext().getString(R.string.lvl), 1);
        this.mJsonObj = new JSONObject(map);

        this.client = ((ControlActivity) getActivity()).getClient();
        this.deviceName = ((ControlActivity) getActivity()).getDeviceName();
        tvModeName = v.findViewById(R.id.textview_mode);
        tvModeName.setText(getString(R.string.musicMode));

        sbRed = v.findViewById(R.id.red_seekbar);
        sbGreen = v.findViewById(R.id.green_seekbar);
        sbBlue = v.findViewById(R.id.blue_seekbar);
        sbOffset = v.findViewById(R.id.offset_seekbar);

        tvRed = v.findViewById(R.id.tv_red);
        tvGreen = v.findViewById(R.id.tv_green);
        tvBlue = v.findViewById(R.id.tv_blue);
        tvOffset = v.findViewById(R.id.tv_offset);

        spLevel = v.findViewById(R.id.level_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(), R.array.level, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spLevel.setAdapter(adapter);
        spLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                level = Integer.valueOf((String) parent.getItemAtPosition(position));
                Log.d(tag, "select " + level);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sbRed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvRed.setText(getString(R.string.r) + ": " + progress);
                final String topic = Topic.ROOT + deviceName + Topic.MUSIC_MODE_COLOR;
                updateJsonObj();
                publishMsg(topic, mJsonObj);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sbGreen.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvGreen.setText(getString(R.string.g) + ": " + progress);
                final String topic = Topic.ROOT + deviceName + Topic.MUSIC_MODE_COLOR;
                updateJsonObj();
                publishMsg(topic, mJsonObj);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sbBlue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvBlue.setText(getString(R.string.b) + ": " + progress);
                final String topic = Topic.ROOT + deviceName + Topic.MUSIC_MODE_COLOR;
                updateJsonObj();
                publishMsg(topic, mJsonObj);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sbOffset.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvOffset.setText(getString(R.string.offset) + ": " + progress);
                final String topic = Topic.ROOT + deviceName + Topic.MUSIC_MODE_OFFSET;
                publishMsg(topic, Integer.toString(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        return v;
    }

    private void updateJsonObj() {
        try {
            mJsonObj.put(getString(R.string.r), sbRed.getProgress());
            mJsonObj.put(getString(R.string.g), sbGreen.getProgress());
            mJsonObj.put(getString(R.string.b), sbBlue.getProgress());
            mJsonObj.put(getString(R.string.lvl), level);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void publishMsg(String topic, JSONObject jsonObject) {
        MqttMessage msg = new MqttMessage(jsonObject.toString().getBytes());
        try {
            if(client.isConnected()){
                Log.d(MQTT_TAG, "publish to topic: " + topic + ":" + jsonObject.toString());
                client.publish(topic, msg);
            }else {
                return;
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void publishMsg(String topic, String payload) {
        MqttMessage msg = new MqttMessage(payload.getBytes());
        try {
            if(client.isConnected()){
                Log.d(MQTT_TAG, "publish to topic: " + topic + ":" + payload);
                client.publish(topic, msg);
            }else {
                return;
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
