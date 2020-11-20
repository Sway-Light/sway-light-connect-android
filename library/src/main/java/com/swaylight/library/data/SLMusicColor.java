package com.swaylight.library.data;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class SLMusicColor {
    public static final String LEVEL = "level";
    public static final int HIGH_LEV = 1;
    public static final int MEDIUM_LEV = 2;
    public static final int LOW_LEV = 3;

    public static final String HIGH = "h";
    public static final String MEDIUM = "m";
    public static final String LOW = "l";
    private int level;
    private JSONObject jsonObj;
    private SLColor highColorObj;
    private SLColor mediumColorObj;
    private SLColor lowColorObj;

    public SLMusicColor() {
        super();
        try {
            highColorObj = new SLColor(0, 0,0);
            mediumColorObj = new SLColor(0, 0,0);
            lowColorObj = new SLColor(0, 0,0);
            jsonObj = new JSONObject();
            this.jsonObj.put(HIGH, highColorObj.getInstance());
            this.jsonObj.put(MEDIUM, mediumColorObj.getInstance());
            this.jsonObj.put(LOW, lowColorObj.getInstance());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setColor(int red, int green, int blue, String level) {
        try {
            switch (level) {
                case HIGH:
                    highColorObj.setColor(red, green, blue);
                    break;

                case MEDIUM:
                    mediumColorObj.setColor(red, green, blue);
                    break;

                case LOW:
                    lowColorObj.setColor(red, green, blue);
                    break;

                default:
                    break;
            }
            jsonObj.put(HIGH, highColorObj.getInstance());
            jsonObj.put(MEDIUM, mediumColorObj.getInstance());
            jsonObj.put(LOW, lowColorObj.getInstance());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setColor(int color, String level) {
        try {
            switch (level) {
                case HIGH:
                    highColorObj.setColor(color);
                    break;

                case MEDIUM:
                    mediumColorObj.setColor(color);
                    break;

                case LOW:
                    lowColorObj.setColor(color);
                    break;

                default:
                    break;
            }
            jsonObj.put(HIGH, highColorObj.getInstance());
            jsonObj.put(MEDIUM, mediumColorObj.getInstance());
            jsonObj.put(LOW, lowColorObj.getInstance());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getInstance() {
        return jsonObj;
    }
}
