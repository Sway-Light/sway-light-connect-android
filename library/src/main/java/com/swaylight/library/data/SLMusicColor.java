package com.swaylight.library.data;

import org.json.JSONException;

public class SLMusicColor extends SLColor{
    public static final String LEVEL = "level";
    private int level;

    public SLMusicColor() {
        super();
        try {
            this.jsonObj.put(LEVEL, 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public SLMusicColor(int red, int green, int blue) {
        super(red, green, blue);
    }

    public SLMusicColor(int red, int green, int blue, int level) {
        super(red, green, blue);
        this.level = level;
        setColor(red, green, blue, level);
    }

    public void setColor(int red, int green, int blue) {
        super.setColor(red, green, blue);
    }

    public void setColor(int red, int green, int blue, int level) {
        super.setColor(red, green, blue);
        try {
            jsonObj.put(LEVEL, level);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
        setColor(red, green, blue, level);
    }
}
