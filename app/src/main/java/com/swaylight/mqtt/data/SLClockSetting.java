package com.swaylight.mqtt.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SLClockSetting {
    public static final String HOUR = "hour";
    public static final String MIN = "min";
    public static final String SEC = "sec";
    public static final String ENABLE = "enable";

    private int hour;
    private int min;
    private int sec;

    private byte[] enable = new byte[7];
    private JSONObject jsonObj;

    public SLClockSetting() {
        jsonObj = new JSONObject();
        try {
            for(int i = 0; i < 7; i++) {
                enable[i] = 0;
            }
            jsonObj.put(HOUR, 0);
            jsonObj.put(MIN, 0);
            jsonObj.put(SEC, 0);
            jsonObj.put(ENABLE, new JSONArray(enable));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public SLClockSetting(int hour, int min, int sec, ArrayList<Boolean> enable) {
        jsonObj = new JSONObject();
        this.hour = hour;
        this.min = min;
        this.sec = sec;
        try {
            for(int i = 0; i < 7; i++) {
                if(enable.get(i)) {
                    this.enable[i] = 1;
                }else {
                    this.enable[i] = 0;
                }
            }
            jsonObj.put(HOUR, hour);
            jsonObj.put(MIN, min);
            jsonObj.put(SEC, sec);
            jsonObj.put(ENABLE, new JSONArray(this.enable));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setClock(int hour, int min, int sec) {
        this.hour = hour;
        this.min = min;
        this.sec = sec;
        try {
            jsonObj.put(HOUR, hour);
            jsonObj.put(MIN, min);
            jsonObj.put(SEC, sec);
            jsonObj.put(ENABLE, new JSONArray(this.enable));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setClock(int hour, int min, int sec, boolean[] enable) {
        this.hour = hour;
        this.min = min;
        this.sec = sec;
        try {
            for(int i = 0; i < 7; i++) {
                if(enable[i]) {
                    this.enable[i] = 1;
                }else {
                    this.enable[i] = 0;
                }
            }
            jsonObj.put(HOUR, hour);
            jsonObj.put(MIN, min);
            jsonObj.put(SEC, sec);
            jsonObj.put(ENABLE, new JSONArray(this.enable));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void enableAll(boolean enable) {
        for(int i = 0; i < 7; i++) {
            if(enable) {
                this.enable[i] = 1;
            }else {
                this.enable[i] = 0;
            }
        }
        setClock(hour, min, sec);
    }

    public JSONObject getInstance() {
        return jsonObj;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
        setClock(hour, min, sec);
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
        setClock(hour, min, sec);
    }

    public int getSec() {
        return sec;
    }

    public void setSec(int sec) {
        this.sec = sec;
        setClock(hour, min, sec);
    }

    public void setEnable(boolean[] enable) {
        for(int i = 0; i < 7; i++) {
            if(enable[i]) {
                this.enable[i] = 1;
            }else {
                this.enable[i] = 0;
            }
        }
        setClock(hour, min, sec);
    }
}
