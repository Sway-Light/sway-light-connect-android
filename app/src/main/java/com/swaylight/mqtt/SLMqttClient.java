package com.swaylight.mqtt;

import android.content.Context;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

public class SLMqttClient extends MqttAndroidClient {
    public SLMqttClient(Context context, String serverURI, String clientId) {
        super(context, serverURI, clientId);
    }

    public SLMqttClient(Context ctx, String serverURI, String clientId, Ack ackType) {
        super(ctx, serverURI, clientId, ackType);
    }

    public SLMqttClient(Context ctx, String serverURI, String clientId, MqttClientPersistence persistence) {
        super(ctx, serverURI, clientId, persistence);
    }

    public SLMqttClient(Context context, String serverURI, String clientId, MqttClientPersistence persistence, Ack ackType) {
        super(context, serverURI, clientId, persistence, ackType);
    }

    public void publish(String topic, JSONObject jsonObject) {
        MqttMessage msg = new MqttMessage(jsonObject.toString().getBytes());
        try {
            if(SLMqttManager.getInstance().isConnected()){
                SLMqttManager.getInstance().publish(topic, msg);
            }else {
                return;
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publish(String topic, String payload) {
        MqttMessage msg = new MqttMessage(payload.getBytes());
        try {
            if(SLMqttManager.getInstance().isConnected()){
                SLMqttManager.getInstance().publish(topic, msg);
            }else {
                return;
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publish(String topic, int value) {
        MqttMessage msg = new MqttMessage(String.valueOf(value).getBytes());
        try {
            if(SLMqttManager.getInstance().isConnected()){
                SLMqttManager.getInstance().publish(topic, msg);
            }else {
                return;
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publish(String topic, SLMode mode) {
        MqttMessage msg = new MqttMessage(String.valueOf(mode.getModeNum()).getBytes());
        try {
            if(SLMqttManager.getInstance().isConnected()){
                SLMqttManager.getInstance().publish(topic, msg);
            }else {
                return;
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
