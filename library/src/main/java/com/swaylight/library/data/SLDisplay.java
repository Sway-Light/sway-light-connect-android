package com.swaylight.library.data;

import org.json.JSONException;
import org.json.JSONObject;

public class SLDisplay {
    public static final String ZOOM = "zoom";
    public static final String OFFSET = "offset";

    private int zoom;
    private int offset;

    protected JSONObject jsonObj;

    public SLDisplay(int zoom, int offset) {
        jsonObj = new JSONObject();
        try {
            jsonObj.put(ZOOM, 0);
            jsonObj.put(OFFSET, 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.zoom = zoom;
        this.offset = offset;
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
}
