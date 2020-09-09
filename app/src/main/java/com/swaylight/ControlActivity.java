package com.swaylight;

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

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.swaylight.mqtt.Topic;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ControlActivity extends AppCompatActivity {

    MqttAndroidClient client;
    public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss.SSS");
    private final static int MAX_LOG_SIZE = 8000;
    private final String MQTT_TAG   = "mqtt";
    private int qos                 = 0;
    private String broker           = "tcp://172.20.10.4";//replace to your broker ip.
    private String clientId         = "sway-light";
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
        broker = "tcp://" + this.getIntent().getExtras().getString(getString(R.string.MQTT_BROKER)) + ":1883";
        deviceName = this.getIntent().getExtras().getString(getString(R.string.DEVICE_NAME));
        tvMqttIp = findViewById(R.id.tv_mqtt_ip);
        tvMqttIp.setText(broker);
        tvConnectStatus = findViewById(R.id.tv_connect_status);
        tvConnectStatus.setText(getString(R.string.disconnected));

        final FragmentManager fragmentManager = getSupportFragmentManager();
        final MusicFragment musicFragment = new MusicFragment();
        final LightFragment lightFragment = new LightFragment();
        if(!musicFragment.isAdded()) {
            Log.d(tag, "add musicFragment");
            fragmentManager.beginTransaction().add(R.id.fragment_container, musicFragment).commit();
        }
        if(!lightFragment.isAdded()) {
            Log.d(tag, "add lightFragment");
            fragmentManager.beginTransaction().add(R.id.fragment_container, lightFragment).commit();
        }

        tvLog = findViewById(R.id.tv_log);
        tvLog.setMovementMethod(new ScrollingMovementMethod());
        tvLog.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                tvLog.scrollTo(0,0);
                tvLog.setText("");
                return false;
            }
        });
        swPower = findViewById(R.id.sw_power);
        swPower.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                final String topic = Topic.ROOT + deviceName + Topic.POWER;
                if(isChecked){
                    publishMsg(topic, String.valueOf(1));
                }else {
                    publishMsg(topic, String.valueOf(0));
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
                final String topic = Topic.ROOT + deviceName + Topic.CURR_MODE;
                String modeName = (String) parent.getItemAtPosition(position);
                if(modeName.equals("Light")) {
                    Log.d(tag, "select: " + parent.getItemAtPosition(position));
                    fragmentManager.beginTransaction().hide(musicFragment).commit();
                    fragmentManager.beginTransaction().show(lightFragment).commit();
                    publishMsg(topic, String.valueOf(2));
                }else if(modeName.equals("Music")) {
                    Log.d(tag, "select: " + parent.getItemAtPosition(position));
                    fragmentManager.beginTransaction().hide(lightFragment).commit();
                    fragmentManager.beginTransaction().show(musicFragment).commit();
                    publishMsg(topic, String.valueOf(3));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        client = new MqttAndroidClient(getApplicationContext(), broker, clientId);
        client.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                tvConnectStatus.setText(getString(R.string.connected));
                appendLog("connectComplete");
                try {
                    final String topic = Topic.ROOT + deviceName + "/#";
                    client.subscribe(topic, 0);
                    appendLog("subscribe: " + topic);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                Log.d(MQTT_TAG, "Connected to " + broker);
            }

            @Override
            public void connectionLost(Throwable cause) {
                tvConnectStatus.setText(getString(R.string.disconnected));
                appendLog("connectionLost");
                Log.d(MQTT_TAG, "Disconnect to " + broker);
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                appendLog(topic + ":" + message.toString());
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);

        try {
            client.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    tvConnectStatus.setText(getString(R.string.connected));
                    appendLog("Connect to " + broker + " success");
                    Log.d(MQTT_TAG, "Connect to " + broker + " success");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    tvConnectStatus.setText(getString(R.string.disconnected));
                    appendLog("Connect to " + broker + " fail");
                    Log.d(MQTT_TAG, "Connect to " + broker + " fail");
                }
            });
        } catch (MqttException ex){
            ex.printStackTrace();
        }

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

    private void publishMsg(String topic, JSONObject jsonObject) {
        MqttMessage msg = new MqttMessage(jsonObject.toString().getBytes());
        try {
            if(client.isConnected()){
//                appendLog("publish->" + topic + ":" + jsonObject.toString());
                Log.d(MQTT_TAG, "publish->" + topic + ":" + jsonObject.toString());
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
//                appendLog("publish->" + topic + ":" + payload);
                Log.d(MQTT_TAG, "publish->" + topic + ":" + payload);
                client.publish(topic, msg);
            }else {
                return;
            }
        } catch (MqttException e) {
            e.printStackTrace();
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

    public MqttAndroidClient getClient() {
        return client;
    }

    public String getDeviceName() {
        return deviceName;
    }
}
