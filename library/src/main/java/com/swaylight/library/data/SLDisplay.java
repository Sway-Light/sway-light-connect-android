package com.swaylight.library.data;

import org.json.JSONException;
import org.json.JSONObject;

public class SLDisplay {
    public static final String ZOOM = "zoom";
    public static final String OFFSET = "offset";
    public static final String BRIGHT = "brightness";

    private int zoom;
    private int offset;
    private int brightness;

    protected JSONObject jsonObj;

    public SLDisplay(int zoom, int offset, int brightness) {
        jsonObj = new JSONObject();
        try {
            jsonObj.put(ZOOM, zoom);
            jsonObj.put(OFFSET, offset);
            jsonObj.put(BRIGHT, brightness);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.zoom = zoom;
        this.offset = offset;
        this.brightness = brightness;
    }

    public JSONObject getInstance() {
        return jsonObj;
    }

    public int getZoom() {
        return zoom;
    }

    public void setZoom(int zoom) {
        try {
            jsonObj.put(ZOOM, zoom);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.zoom = zoom;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        try {
            jsonObj.put(OFFSET, offset);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.offset = offset;
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        try {
            jsonObj.put(BRIGHT, brightness);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.brightness = brightness;
    }
}
