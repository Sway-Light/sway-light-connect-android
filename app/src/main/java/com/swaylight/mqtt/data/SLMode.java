package com.swaylight.mqtt.data;

public enum SLMode {
    POWER_OFF(0),
    POWER_ON(1),
    LIGHT(2),
    MUSIC(3),
    ;

    private final int modeNum;
    SLMode(int modeNum) {
        this.modeNum = modeNum;
    }

    public int getModeNum() {
        return modeNum;
    }
}
