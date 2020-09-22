package com.swaylight.mqtt.data;

public enum SLTopic {
    POWER("/power"),
    POWER_START_TIME("/mode/onoff/on_time"),
    POWER_END_TIME("/mode/onoff/off_time"),

    CURR_MODE("/status"),

    LIGHT_MODE_COLOR("/mode/light/color"),
    LIGHT_MODE_OFFSET("/mode/light/offset"),
    LIGHT_MODE_ZOOM("/mode/light/zoom"),

    MUSIC_MODE_COLOR("/mode/music/color"),
    MUSIC_MODE_OFFSET("/mode/music/offset"),
    MUSIC_MODE_STYLE("/mode/music/style"),
    ;

    static public final String ROOT = "feeds/";

    String topic;

    SLTopic(String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }
}