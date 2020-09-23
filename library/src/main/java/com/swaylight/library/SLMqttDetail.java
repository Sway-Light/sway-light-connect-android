package com.swaylight.library;

public class SLMqttDetail {
    public static final String BROKER = "broker";
    public static final String DEVICE_NAME = "device_name";
    public static final String CLIENT_ID = "id";

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
}
