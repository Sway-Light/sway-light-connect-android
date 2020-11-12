package com.swaylight.library;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

public class SLMqttManager {

    static SLMqttClient client = null;
    private String broker;
    private String clientId;
    static private String deviceName;
    private MqttCallbackExtended mqttCallbackExtended;
    private IMqttActionListener mqttActionListener;

    private MqttConnectOptions mqttConnectOptions;

    public SLMqttManager(Context context, String broker, String deviceName, String clientId) {
        this.broker = broker;
        SLMqttManager.deviceName = deviceName;
        this.clientId = clientId;
        client = new SLMqttClient(context, broker, clientId);

        mqttConnectOptions = new MqttConnectOptions();
    }

    static public SLMqttClient getInstance() {
        if(client == null) {
            Log.e(SLMqttManager.class.getSimpleName(), "get mqtt instance failed!");
        }
        return client;
    }

    static public String getDeviceName() {
        return deviceName;
    }

    public String getClientId() {
        return clientId;
    }

    public void setCallback(MqttCallbackExtended mqttCallbackExtended) {
        this.mqttCallbackExtended = mqttCallbackExtended;
        client.setCallback(mqttCallbackExtended);
    }

    public void setMqttActionListener(IMqttActionListener mqttActionListener) {
        this.mqttActionListener = mqttActionListener;
    }

    public void connect() throws MqttException {
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);
        client.connect(mqttConnectOptions, null, mqttActionListener);
    }

    public void connect(int timeout) throws MqttException {
        mqttConnectOptions.setConnectionTimeout(timeout);
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);
        client.connect(mqttConnectOptions, null, mqttActionListener);
    }
}
