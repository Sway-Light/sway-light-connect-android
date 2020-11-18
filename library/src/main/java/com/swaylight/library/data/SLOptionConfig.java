package com.swaylight.library.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SLOptionConfig {
    public static final String FFT_MAG = "fft_mag";

    private int fftMag;

    private JSONObject jsonObj;

    public SLOptionConfig() {
        jsonObj = new JSONObject();
        try {
            jsonObj.put(FFT_MAG, 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public SLOptionConfig(int fftMag) {
        jsonObj = new JSONObject();
        this.fftMag = fftMag;
        try {
            jsonObj.put(FFT_MAG, fftMag);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getInstance() {
        return jsonObj;
    }

    public void setFftMag(int fftMag) {
        this.fftMag = fftMag;
        try {
            jsonObj.put(FFT_MAG, fftMag);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getFftMag() {
        return fftMag;
    }
}
