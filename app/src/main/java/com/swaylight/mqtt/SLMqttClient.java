package com.swaylight.mqtt;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

public class SLMqttClient extends MqttAndroidClient {
    private final String tag = getClass().getSimpleName();

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

    public void publish(SLTopic swayLightTopic, String deviceName, JSONObject jsonObject) {
        try {
            jsonObject.put("id", this.getClientId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MqttMessage msg = new MqttMessage(jsonObject.toString().getBytes());
        String topic = SLTopic.ROOT + deviceName + swayLightTopic.getTopic();
        try {
            Log.d(tag, "pub " + topic + ":" + jsonObject.toString());
            if(SLMqttManager.getInstance().isConnected()){
                SLMqttManager.getInstance().publish(topic, msg);
            }else {
                return;
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publish(SLTopic swayLightTopic, String deviceName, String payload) {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("id", this.getClientId());
            jsonObj.put("value", payload);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MqttMessage msg = new MqttMessage(jsonObj.toString().getBytes());
        String topic = SLTopic.ROOT + deviceName + swayLightTopic.getTopic();
        try {
            Log.d(tag, "pub " + topic + ":" + payload);
            if(SLMqttManager.getInstance().isConnected()){
                SLMqttManager.getInstance().publish(topic, msg);
            }else {
                return;
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publish(SLTopic swayLightTopic, String deviceName, int value) {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("id", this.getClientId());
            jsonObj.put("value", value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MqttMessage msg = new MqttMessage(jsonObj.toString().getBytes());
        String topic = SLTopic.ROOT + deviceName + swayLightTopic.getTopic();
        try {
            Log.d(tag, "pub " + topic + ":" + value);
            if(SLMqttManager.getInstance().isConnected()){
                SLMqttManager.getInstance().publish(topic, msg);
            }else {
                return;
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publish(SLTopic swayLightTopic, String deviceName, SLMode mode) {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("id", this.getClientId());
            jsonObj.put("value", mode.getModeNum());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MqttMessage msg = new MqttMessage(jsonObj.toString().getBytes());
        String topic = SLTopic.ROOT + deviceName + swayLightTopic.getTopic();
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
