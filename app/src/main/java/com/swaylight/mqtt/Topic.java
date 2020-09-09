package com.swaylight.mqtt;

public class Topic {

//    ROOT("feeds/"),
//    POWER("/power"),
//    POWER_START_TIME("/mode/onoff/on_time"),
//    POWER_END_TIME("/mode/onoff/off_time"),
//
//    CURR_MODE("/status"),
//
//    LIGHT_MODE_COLOR("/mode/light/color"),
//    LIGHT_MODE_OFFSET("/mode/light/offset"),
//    LIGHT_MODE_ZOOM("/mode/light/zoom"),
//
//    MUSIC_MODE_COLOR("/mode/music/color"),
//    MUSIC_MODE_OFFSET("/mode/music/offset"),
//    MUSIC_MODE_STYLE("/mode/music/style"),
//    ;

    public static final String ROOT = "feeds/";
    public static final String POWER = "/power";
    public static final String POWER_START_TIME = "/mode/onoff/on_time";
    public static final String POWER_END_TIME = "/mode/onoff/off_time";

    public static final String CURR_MODE = "/status";

    public static final String LIGHT_MODE_COLOR = "/mode/light/color";
    public static final String LIGHT_MODE_OFFSET = "/mode/light/offset";
    public static final String LIGHT_MODE_ZOOM = "/mode/light/zoom";

    public static final String MUSIC_MODE_COLOR = "/mode/music/color";
    public static final String MUSIC_MODE_OFFSET = "/mode/music/offset";
    public static final String MUSIC_MODE_STYLE = "/mode/music/style";

    private String topic;

    Topic(String topic) {
        this.topic = topic;
    }
}
