package com.swaylight.mqtt.data;

import org.json.JSONException;
import org.json.JSONObject;

public class SLLightColor extends SLColor{
    public static final String BRIGHTNESS = "brightness";
    private byte brightness;

    public SLLightColor() {
        super();
        try {
            jsonObj.put(BRIGHTNESS, 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public SLLightColor(byte red, byte green, byte blue) {
        super(red, green, blue);
        this.brightness = 0;
        setColor(red, green, blue, brightness);
    }

    public SLLightColor(byte red, byte green, byte blue, byte brightness) {
        super(red, green, blue);
        this.brightness = brightness;
        setColor(red, green, blue, brightness);
    }

    public void setColor(byte red, byte green, byte blue, byte brightness) {
        super.setColor(red, green, blue);
        try {
            this.jsonObj.put(BRIGHTNESS, brightness);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public byte getBrightness() {
        return brightness;
    }

    public void setBrightness(byte brightness) {
        this.brightness = brightness;
        setColor(red, green, blue, brightness);
    }
}
