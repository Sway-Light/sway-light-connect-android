package com.swaylight.library.data.BtModule;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class SLBtModuleStatus implements Serializable {

    public static final String CONNECT = "cnt";
    public static final String IS_PLAY = "IS_PLAY";
    public static final String VOLUME = "vol";

    private final JSONObject jsonObj;

    public boolean connect = false;
    public boolean isPlay = false;
    public int volume = 0;

    public SLBtModuleStatus() {
        jsonObj = new JSONObject();
        try {
            jsonObj.put(CONNECT, connect);
            jsonObj.put(IS_PLAY, isPlay);
            jsonObj.put(VOLUME, volume);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getInstance() {
        return jsonObj;
    }
}