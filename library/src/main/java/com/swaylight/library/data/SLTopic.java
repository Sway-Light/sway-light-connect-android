package com.swaylight.library.data;

import java.io.Serializable;

public enum SLTopic implements Serializable {
    POWER("/power"),
    POWER_START_TIME("/mode/onoff/on_time"),
    POWER_END_TIME("/mode/onoff/off_time"),

    CURR_MODE("/status"),

    LIGHT_MODE_COLOR("/mode/light/color"),
    LIGHT_MODE_OFFSET("/mode/light/offset"),
    LIGHT_MODE_ZOOM("/mode/light/zoom"),
    LIGHT_MODE_DISPLAY("/mode/light/display"),

    MUSIC_MODE_COLOR("/mode/music/color"),
    MUSIC_MODE_OFFSET("/mode/music/offset"),
    MUSIC_MODE_STYLE("/mode/music/style"),
    MUSIC_MODE_DISPLAY("/mode/music/display"),
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