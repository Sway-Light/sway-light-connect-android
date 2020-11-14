package com.swaylight.library.data;

import java.io.Serializable;

public enum SLMode implements Serializable {
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
