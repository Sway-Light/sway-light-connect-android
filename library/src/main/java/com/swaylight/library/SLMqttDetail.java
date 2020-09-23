package com.swaylight.library;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;

public class SLMqttDetail implements Serializable {

    private String name;
    private String broker;
    private String deviceName;
    private String clientId;

    public SLMqttDetail(String name, String broker, String deviceName, String clientId) {
        this.name = name;
        this.broker = broker;
        this.deviceName = deviceName;
        this.clientId = clientId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBroker() {
        return broker;
    }

    public void setBroker(String broker) {
        this.broker = broker;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return this.name.equals(((SLMqttDetail)obj).name);
    }

    @NonNull
    @Override
    public String toString() {
        return "SLMqttDetail:{" +
                "name:" + name  +
                ", broker:" + broker +
                ", deviceName:" + deviceName +
                ", clientId:" + clientId + "}\n";
    }
}
