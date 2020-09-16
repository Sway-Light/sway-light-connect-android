package com.swaylight.mqtt.data;

import org.json.JSONException;
import org.json.JSONObject;

public class SLColor {
    public static final String RED = "red";
    public static final String GREEN = "green";
    public static final String BLUE = "blue";

    protected byte red;
    protected byte green;
    protected byte blue;

    protected JSONObject jsonObj;

    public SLColor() {
        jsonObj = new JSONObject();
        try {
            jsonObj.put(RED, 0);
            jsonObj.put(GREEN, 0);
            jsonObj.put(BLUE, 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public SLColor(byte red, byte green, byte blue) {
        jsonObj = new JSONObject();
        setColor(red, green, blue);
    }

    public void setColor(byte red, byte green, byte blue) {
        try {
            jsonObj.put(RED, red);
            jsonObj.put(GREEN, green);
            jsonObj.put(BLUE, blue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getInstance() {
        return jsonObj;
    }

    public byte getRed() {
        return red;
    }

    public byte getGreen() {
        return green;
    }

    public byte getBlue() {
        return blue;
    }

    public void setRed(byte red) {
        this.red = red;
        setColor(red, green, blue);
    }

    public void setGreen(byte green) {
        this.green = green;
        setColor(red, green, blue);
    }

    public void setBlue(byte blue) {
        this.blue = blue;
        setColor(red, green, blue);
    }
}
