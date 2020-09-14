package com.swaylight;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.swaylight.mqtt.Topic;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LightFragment extends Fragment {

    private static final String MQTT_TAG = "mqtt";
    // TODO: Rename and change types of parameters
    private TextView tvModeName;

    private MqttAndroidClient client;
    private String deviceName;

    private SeekBar sbBrightness;
    private SeekBar sbRed;
    private SeekBar sbGreen;
    private SeekBar sbBlue;
    private SeekBar sbOffset;
    private SeekBar sbZoom;

    private TextView tvBrightness;
    private TextView tvRed;
    private TextView tvGreen;
    private TextView tvBlue;
    private TextView tvOffset;
    private TextView tvZoom;
    private CheckBox cbReleasePublish;

    private JSONObject colorJsonObj;
    private Map<String, Integer> map;

    public LightFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_light, container, false);
        tvModeName = v.findViewById(R.id.textview_mode);

        this.map = new HashMap<String, Integer>();
        map.put(getContext().getString(R.string.r), 0);
        map.put(getContext().getString(R.string.g), 0);
        map.put(getContext().getString(R.string.b), 0);
        map.put(getContext().getString(R.string.lvl), 1);
        this.colorJsonObj = new JSONObject(map);
        this.client = ((ControlActivity) getActivity()).getClient();
        this.deviceName = ((ControlActivity) getActivity()).getDeviceName();

        tvModeName.setText(getString(R.string.lightMode));

        sbBrightness = v.findViewById(R.id.brightness_seekbar);
        sbRed = v.findViewById(R.id.red_seekbar);
        sbGreen = v.findViewById(R.id.green_seekbar);
        sbBlue = v.findViewById(R.id.blue_seekbar);
        sbOffset = v.findViewById(R.id.offset_seekbar);
        sbZoom = v.findViewById(R.id.zoom_seekbar);

        tvBrightness = v.findViewById(R.id.tv_brightness);
        tvRed = v.findViewById(R.id.tv_red);
        tvGreen = v.findViewById(R.id.tv_green);
        tvBlue = v.findViewById(R.id.tv_blue);
        tvOffset = v.findViewById(R.id.tv_offset);
        tvZoom = v.findViewById(R.id.tv_zoom);
        cbReleasePublish = v.findViewById(R.id.release_publish_checkbox);

        sbBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvBrightness.setText(getString(R.string.brightness) + ": " + progress);
                final String topic = Topic.ROOT + deviceName + Topic.LIGHT_MODE_COLOR;
                updateJsonObj();
                if(!cbReleasePublish.isChecked()) {
                    publishMsg(topic, colorJsonObj);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                final String topic = Topic.ROOT + deviceName + Topic.LIGHT_MODE_COLOR;
                publishMsg(topic, colorJsonObj);
            }
        });

        sbRed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvRed.setText(getString(R.string.r) + ": " + progress);
                final String topic = Topic.ROOT + deviceName + Topic.LIGHT_MODE_COLOR;
                updateJsonObj();
                if(!cbReleasePublish.isChecked()) {
                    publishMsg(topic, colorJsonObj);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                final String topic = Topic.ROOT + deviceName + Topic.LIGHT_MODE_COLOR;
                publishMsg(topic, colorJsonObj);
            }
        });

        sbGreen.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvGreen.setText(getString(R.string.g) + ": " + progress);
                final String topic = Topic.ROOT + deviceName + Topic.LIGHT_MODE_COLOR;
                updateJsonObj();
                if(!cbReleasePublish.isChecked()) {
                    publishMsg(topic, colorJsonObj);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                final String topic = Topic.ROOT + deviceName + Topic.LIGHT_MODE_COLOR;
                publishMsg(topic, colorJsonObj);
            }
        });

        sbBlue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvBlue.setText(getString(R.string.b) + ": " + progress);
                final String topic = Topic.ROOT + deviceName + Topic.LIGHT_MODE_COLOR;
                updateJsonObj();
                if(!cbReleasePublish.isChecked()) {
                    publishMsg(topic, colorJsonObj);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                final String topic = Topic.ROOT + deviceName + Topic.LIGHT_MODE_COLOR;
                publishMsg(topic, colorJsonObj);
            }
        });

        sbOffset.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvOffset.setText(getString(R.string.offset) + ": " + progress);
                final String topic = Topic.ROOT + deviceName + Topic.LIGHT_MODE_OFFSET;
                if(!cbReleasePublish.isChecked()) {
                    publishMsg(topic, Integer.toString(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                final String topic = Topic.ROOT + deviceName + Topic.LIGHT_MODE_OFFSET;
                publishMsg(topic, Integer.toString(seekBar.getProgress()));
            }
        });

        sbZoom.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvZoom.setText(getString(R.string.zoom) + ": " + progress);
                final String topic = Topic.ROOT + deviceName + Topic.LIGHT_MODE_ZOOM;
                if(!cbReleasePublish.isChecked()) {
                    publishMsg(topic, Integer.toString(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                final String topic = Topic.ROOT + deviceName + Topic.LIGHT_MODE_ZOOM;
                publishMsg(topic, Integer.toString(seekBar.getProgress()));
            }
        });

        return v;
    }

    private void updateJsonObj() {
        try {
            colorJsonObj.put(getString(R.string.r), sbRed.getProgress());
            colorJsonObj.put(getString(R.string.g), sbGreen.getProgress());
            colorJsonObj.put(getString(R.string.b), sbBlue.getProgress());
            colorJsonObj.put(getString(R.string.brightness), sbBrightness.getProgress());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void publishMsg(String topic, JSONObject jsonObj) {
        MqttMessage msg = new MqttMessage(jsonObj.toString().getBytes());
        try {
            if(client.isConnected()){
//                ((ControlActivity) getActivity()).appendLog("publish->" + topic + ":" + jsonObj.toString());
                Log.d(MQTT_TAG, "publish->" + topic + ":" + jsonObj.toString());
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
//                ((ControlActivity) getActivity()).appendLog("publish->" + topic + ":" + payload);
                Log.d(MQTT_TAG, "publish->" + topic + ":" + payload);
                client.publish(topic, msg);
            }else {
                return;
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
