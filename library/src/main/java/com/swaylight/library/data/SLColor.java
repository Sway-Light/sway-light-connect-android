package com.swaylight.library.data;

import android.graphics.Color;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class SLColor {
    public static final String RED = "r";
    public static final String GREEN = "g";
    public static final String BLUE = "b";
    public static final String COLOR = "color";

    protected int red;
    protected int green;
    protected int blue;
    protected int color;

    protected JSONObject jsonObj;

    public SLColor() {
        jsonObj = new JSONObject();
        try {
            jsonObj.put(COLOR, 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public SLColor(int red, int green, int blue) {
        jsonObj = new JSONObject();
        setColor(red, green, blue);
    }

    public void setColor(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        try {
            color = (red << 16) + (green << 8) + blue;
            jsonObj.put(COLOR, color);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setColor(int color) {
        setColor(
                (color >> 16) & 0xFF,
                (color >> 8) & 0xFF,
                (color >> 0) & 0xFF);
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
