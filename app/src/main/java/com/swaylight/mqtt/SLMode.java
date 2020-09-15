package com.swaylight.mqtt;

public enum SLMode {
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
