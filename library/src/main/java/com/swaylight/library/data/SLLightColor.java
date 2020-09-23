package com.swaylight.library.data;

import org.json.JSONException;

public class SLLightColor extends SLColor{
    public static final String BRIGHTNESS = "brightness";
    private int brightness;

    public SLLightColor() {
        super();
        try {
            jsonObj.put(BRIGHTNESS, 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public SLLightColor(int red, int green, int blue) {
        super(red, green, blue);
        this.brightness = 0;
        setColor(red, green, blue, brightness);
    }

    public SLLightColor(int red, int green, int blue, int brightness) {
        super(red, green, blue);
        this.brightness = brightness;
        setColor(red, green, blue, brightness);
    }

    public void setColor(int red, int green, int blue, int brightness) {
        super.setColor(red, green, blue);
        try {
            this.jsonObj.put(BRIGHTNESS, brightness);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
        setColor(red, green, blue, brightness);
    }
}
