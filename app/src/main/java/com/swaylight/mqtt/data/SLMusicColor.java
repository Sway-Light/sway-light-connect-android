package com.swaylight.mqtt.data;

import org.json.JSONException;

public class SLMusicColor extends SLColor{
    public static final String LEVEL = "level";
    private byte level;

    public SLMusicColor() {
        super();
        try {
            this.jsonObj.put(LEVEL, 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public SLMusicColor(byte red, byte green, byte blue) {
        super(red, green, blue);
    }

    public SLMusicColor(byte red, byte green, byte blue, byte level) {
        super(red, green, blue);
        this.level = level;
        setColor(red, green, blue, level);
    }

    public void setColor(byte red, byte green, byte blue) {
        super.setColor(red, green, blue);
    }

    public void setColor(byte red, byte green, byte blue, byte level) {
        super.setColor(red, green, blue);
        try {
            jsonObj.put(LEVEL, level);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public byte getLevel() {
        return level;
    }

    public void setLevel(byte level) {
        this.level = level;
        setColor(red, green, blue, level);
    }
}
