package com.swaylight.mqtt.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;

public class SLClockSetting {
    public static final String HOUR = "hour";
    public static final String MIN = "min";
    public static final String SEC = "sec";
    public static final String ENABLE = "enable";

    private byte hour;
    private byte min;
    private byte sec;

    private byte[] enable;
    private JSONObject jsonObj;

    public SLClockSetting() {
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

    public SLClockSetting(byte hour, byte min, byte sec, boolean[] enable) {
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

    public void setClock(byte hour, byte min, byte sec) {
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

    public void setClock(byte hour, byte min, byte sec, boolean[] enable) {
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

    public byte getHour() {
        return hour;
    }

    public void setHour(byte hour) {
        this.hour = hour;
        setClock(hour, min, sec);
    }

    public byte getMin() {
        return min;
    }

    public void setMin(byte min) {
        this.min = min;
        setClock(hour, min, sec);
    }

    public byte getSec() {
        return sec;
    }

    public void setSec(byte sec) {
        this.sec = sec;
        setClock(hour, min, sec);
    }
}
