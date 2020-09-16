package com.swaylight.mqtt.data;

import org.json.JSONException;
import org.json.JSONObject;

public class SLColor {
    public static final String RED = "red";
    public static final String GREEN = "green";
    public static final String BLUE = "blue";

    protected int red;
    protected int green;
    protected int blue;

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

    public SLColor(int red, int green, int blue) {
        jsonObj = new JSONObject();
        setColor(red, green, blue);
    }

    public void setColor(int red, int green, int blue) {
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

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }

    public void setRed(int red) {
        this.red = red;
        setColor(red, green, blue);
    }

    public void setGreen(int green) {
        this.green = green;
        setColor(red, green, blue);
    }

    public void setBlue(int blue) {
        this.blue = blue;
        setColor(red, green, blue);
    }
}
