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

import com.swaylight.library.SLMqttClient;
import com.swaylight.library.SLMqttManager;
import com.swaylight.library.data.SLDisplay;
import com.swaylight.library.data.SLMusicColor;
import com.swaylight.library.data.SLTopic;


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
    private SeekBar sbZoom;
    private TextView tvRed;
    private TextView tvGreen;
    private TextView tvBlue;
    private TextView tvOffset;
    private TextView tvZoom;
    private CheckBox cbReleasePublish;
    private SLMusicColor colorObj;
    private SLDisplay displayObj;

    public MusicFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_music, container, false);

        colorObj = new SLMusicColor();
        displayObj = new SLDisplay(0,0, 100);

        this.client = SLMqttManager.getInstance();
        this.deviceName = SLMqttManager.getDeviceName();
        tvModeName = v.findViewById(R.id.textview_mode);
        tvModeName.setText(getString(R.string.musicMode));

        sbRed = v.findViewById(R.id.red_seekbar);
        sbGreen = v.findViewById(R.id.green_seekbar);
        sbBlue = v.findViewById(R.id.blue_seekbar);
        sbOffset = v.findViewById(R.id.offset_seekbar);
        sbZoom = v.findViewById(R.id.zoom_seekbar);

        tvRed = v.findViewById(R.id.tv_red);
        tvGreen = v.findViewById(R.id.tv_green);
        tvBlue = v.findViewById(R.id.tv_blue);
        tvOffset = v.findViewById(R.id.tv_offset);
        tvZoom = v.findViewById(R.id.tv_zoom);
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
                colorObj.setLevel(level);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sbRed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvRed.setText(getString(R.string.r) + ": " + progress);
                colorObj.setRed(progress);
                if(!cbReleasePublish.isChecked()) {
                    client.publish(SLTopic.MUSIC_MODE_COLOR, deviceName, colorObj.getInstance());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                client.publish(SLTopic.MUSIC_MODE_COLOR, deviceName, colorObj.getInstance());
            }
        });

        sbGreen.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvGreen.setText(getString(R.string.g) + ": " + progress);
                colorObj.setGreen(progress);
                if(!cbReleasePublish.isChecked()) {
                    client.publish(SLTopic.MUSIC_MODE_COLOR, deviceName, colorObj.getInstance());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                client.publish(SLTopic.MUSIC_MODE_COLOR, deviceName, colorObj.getInstance());
            }
        });

        sbBlue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvBlue.setText(getString(R.string.b) + ": " + progress);
                colorObj.setBlue(progress);
                if(!cbReleasePublish.isChecked()) {
                    client.publish(SLTopic.MUSIC_MODE_COLOR, deviceName, colorObj.getInstance());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                client.publish(SLTopic.MUSIC_MODE_COLOR, deviceName, colorObj.getInstance());
            }
        });

        sbOffset.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                displayObj.setOffset(progress);
                tvOffset.setText(getString(R.string.offset) + ": " + displayObj.getOffset());
                if(!cbReleasePublish.isChecked()) {
                    client.publish(SLTopic.MUSIC_MODE_OFFSET, deviceName, displayObj.getInstance());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                client.publish(SLTopic.MUSIC_MODE_OFFSET, deviceName, displayObj.getInstance());
            }
        });

        sbZoom.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                displayObj.setZoom(progress);
                tvZoom.setText(getString(R.string.offset) + ": " + displayObj.getZoom());
                if(!cbReleasePublish.isChecked()) {
                    client.publish(SLTopic.MUSIC_MODE_OFFSET, deviceName, displayObj.getInstance());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                client.publish(SLTopic.MUSIC_MODE_OFFSET, deviceName, displayObj.getInstance());
            }
        });
        return v;
    }


    private SeekBar.OnSeekBarChangeListener colorSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
}
