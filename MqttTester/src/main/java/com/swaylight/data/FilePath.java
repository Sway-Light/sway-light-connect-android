package com.swaylight.data;

public enum FilePath {
    MQTT_LIST("mqtt_list.txt"),
    LIGHT_RGB_COLOR("light_rgb_color"),
    LIGHT_GRA_COLOR("light_gra_color"),
    MUSIC_RGB_COLOR("music_color"),
    ;

    String file;

    FilePath(String file) {
        this.file = file;
    }

    public String getFile() {
        return file;
    }
}
