package com.swaylight;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.swaylight.library.SLMqttClient;
import com.swaylight.library.SLMqttManager;
import com.swaylight.library.data.SLMode;
import com.swaylight.library.data.SLTopic;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ControlActivity extends AppCompatActivity {

    SLMqttManager manager;
    SLMqttClient client;
    public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss.SSS");
    private final static int MAX_LOG_SIZE = 3000;
    private final String MQTT_TAG   = "mqtt";
    private int qos                 = 0;
    private String broker           = "tcp://172.20.10.4";//replace to your broker ip.
    private String clientId;
    private String deviceName;

    final String tag = "David";

    private TextView tvMqttIp;
    private TextView tvConnectStatus;
    private TextView tvLog;
    private Switch swPower;
    private Spinner spMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        broker = "tcp://" + this.getIntent().getStringExtra(getString(R.string.MQTT_BROKER)) + ":1883";
        deviceName = this.getIntent().getStringExtra(getString(R.string.DEVICE_NAME));
        clientId = this.getIntent().getStringExtra(getString(R.string.MQTT_CLIENT_ID));
        tvMqttIp = findViewById(R.id.tv_mqtt_ip);
        tvMqttIp.setText(broker);
        tvConnectStatus = findViewById(R.id.tv_connect_status);
        tvConnectStatus.setText(R.string.disconnected);

        manager = new SLMqttManager(getApplicationContext(), broker, deviceName, clientId);
        client = SLMqttManager.getInstance();
        client.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(final boolean reconnect, String serverURI) {
                try {
                    final String topic = SLTopic.ROOT + deviceName + "/#";
                    client.subscribe(topic, 0);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(reconnect) {
                                appendLog("Reconnect complete");
                            }else {
                                appendLog("Connect complete");
                            }
                            appendLog("subscribe: " + topic);
                            tvConnectStatus.setText(R.string.connected);
                        }
                    });
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                Log.d(MQTT_TAG, "Connected to " + broker);
            }

            @Override
            public void connectionLost(Throwable cause) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvConnectStatus.setText(R.string.disconnected);
                        appendLog("Connection LOST");
                    }
                });
                Log.d(MQTT_TAG, "Disconnect to " + broker);
            }

            @Override
            public void messageArrived(final String topic, final MqttMessage message) throws Exception {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        appendLog(topic + ":\n" + message.toString());
                    }
                });

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
        manager.setMqttActionListener(new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvConnectStatus.setText(R.string.connected);
                        appendLog("Connect to " + broker + " success");
                    }
                });
                Log.d(MQTT_TAG, "Connect to " + broker + " success");
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvConnectStatus.setText(R.string.disconnected);
                        appendLog("Connect to " + broker + " fail");
                    }
                });
                Log.d(MQTT_TAG, "Connect to " + broker + " fail");
            }
        });

        try {
            manager.connect();
        } catch (MqttException ex){
            ex.printStackTrace();
        }

        final FragmentManager fragmentManager = getSupportFragmentManager();
        final MusicFragment musicFragment = new MusicFragment();
        final LightFragment lightFragment = new LightFragment();
        final ClockSettingFragment clockSettingFragment = new ClockSettingFragment();
        if(!musicFragment.isAdded()) {
            Log.d(tag, "add musicFragment");
            fragmentManager.beginTransaction().add(R.id.fragment_container, musicFragment).commit();
        }
        if(!lightFragment.isAdded()) {
            Log.d(tag, "add lightFragment");
            fragmentManager.beginTransaction().add(R.id.fragment_container, lightFragment).commit();
        }

        if(!clockSettingFragment.isAdded()) {
            Log.d(tag, "add lightFragment");
            fragmentManager.beginTransaction().add(R.id.fragment_container, clockSettingFragment).commit();
        }

        tvLog = findViewById(R.id.tv_log);
        tvLog.setMovementMethod(new ScrollingMovementMethod());
        tvLog.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ControlActivity.this);
                builder.setMessage("Clear log?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tvLog.scrollTo(0,0);
                        tvLog.setText("");
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
                return false;
            }
        });
        swPower = findViewById(R.id.sw_power);
        swPower.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    client.publish(SLTopic.POWER, deviceName, SLMode.POWER_ON);
                }else {
                    client.publish(SLTopic.POWER, deviceName, SLMode.POWER_OFF);
                }
            }
        });

        spMode = findViewById(R.id.sp_mode);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.modes, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spMode.setAdapter(adapter);
        spMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String modeName = (String) parent.getItemAtPosition(position);
                Log.d(tag, "select: " + parent.getItemAtPosition(position));
                if(modeName.equals("Light")) {
                    fragmentManager.beginTransaction().hide(clockSettingFragment).commit();
                    fragmentManager.beginTransaction().hide(musicFragment).commit();
                    fragmentManager.beginTransaction().show(lightFragment).commit();
                    client.publish(SLTopic.CURR_MODE, deviceName, SLMode.LIGHT);
                }else if(modeName.equals("Music")) {
                    fragmentManager.beginTransaction().hide(clockSettingFragment).commit();
                    fragmentManager.beginTransaction().hide(lightFragment).commit();
                    fragmentManager.beginTransaction().show(musicFragment).commit();
                    client.publish(SLTopic.CURR_MODE, deviceName, SLMode.MUSIC);
                }else if(modeName.equals("ClockSetting")) {
                    fragmentManager.beginTransaction().show(clockSettingFragment).commit();
                    fragmentManager.beginTransaction().hide(lightFragment).commit();
                    fragmentManager.beginTransaction().hide(musicFragment).commit();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(client != null) {
            try {
                client.disconnect();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void appendLog(final String str) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                String strFull = DATE_FORMAT.format(new Date()) + " " + str + "\n" + tvLog.getText().toString();
                if(strFull.length() > MAX_LOG_SIZE) {
                    strFull = strFull.substring(0, MAX_LOG_SIZE);
                }
                tvLog.setText(strFull);
                while(tvLog.canScrollVertically(-1)) {
                    tvLog.scrollBy(0,-1);
                }
            }
        });
    }
}
