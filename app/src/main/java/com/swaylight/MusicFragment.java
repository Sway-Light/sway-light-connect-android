package com.swaylight;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.swaylight.mqtt.SLMqttClient;
import com.swaylight.mqtt.SLMqttManager;
import com.swaylight.mqtt.SLTopic;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class MusicFragment extends Fragment {

    // TODO: Rename and change types of parameters
    private final String tag = this.getTag();

    private final String MQTT_TAG   = "mqtt";
    private SLMqttClient client;
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
    private CheckBox cbReleasePublish;

    private JSONObject colorJsonObj;
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
        this.colorJsonObj = new JSONObject(map);

        this.client = SLMqttManager.getInstance();
        this.deviceName = SLMqttManager.getDeviceName();
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
        cbReleasePublish = v.findViewById(R.id.release_publish_checkbox);

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
                updateJsonObj();
                if(!cbReleasePublish.isChecked()) {
                    client.publish(SLTopic.MUSIC_MODE_COLOR, deviceName, colorJsonObj);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                client.publish(SLTopic.MUSIC_MODE_COLOR, deviceName, colorJsonObj);
            }
        });

        sbGreen.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvGreen.setText(getString(R.string.g) + ": " + progress);
                updateJsonObj();
                if(!cbReleasePublish.isChecked()) {
                    client.publish(SLTopic.MUSIC_MODE_COLOR, deviceName, colorJsonObj);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                client.publish(SLTopic.MUSIC_MODE_COLOR, deviceName, colorJsonObj);
            }
        });

        sbBlue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvBlue.setText(getString(R.string.b) + ": " + progress);
                updateJsonObj();
                if(!cbReleasePublish.isChecked()) {
                    client.publish(SLTopic.MUSIC_MODE_COLOR, deviceName, colorJsonObj);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                client.publish(SLTopic.MUSIC_MODE_COLOR, deviceName, colorJsonObj);
            }
        });

        sbOffset.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvOffset.setText(getString(R.string.offset) + ": " + progress);
                if(!cbReleasePublish.isChecked()) {
                    client.publish(SLTopic.MUSIC_MODE_OFFSET, deviceName, progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                client.publish(SLTopic.MUSIC_MODE_OFFSET, deviceName, seekBar.getProgress());
            }
        });
        return v;
    }

    private void updateJsonObj() {
        try {
            colorJsonObj.put(getString(R.string.r), sbRed.getProgress());
            colorJsonObj.put(getString(R.string.g), sbGreen.getProgress());
            colorJsonObj.put(getString(R.string.b), sbBlue.getProgress());
            colorJsonObj.put(getString(R.string.lvl), level);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
