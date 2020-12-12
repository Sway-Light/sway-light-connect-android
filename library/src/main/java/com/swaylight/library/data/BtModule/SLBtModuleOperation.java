package com.swaylight.library.data.BtModule;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class SLBtModuleOperation implements Serializable {

    public static final String OPERATION = "operation";
    private Code opCode;
    final private JSONObject jsonObj;

    public SLBtModuleOperation() {
        jsonObj = new JSONObject();
        try {
            jsonObj.put(OPERATION, Code.PLAY.code);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setOpCode(Code opCode) {
        this.opCode = opCode;
        try {
            jsonObj.put(OPERATION, opCode.code);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getInstance() {
        return jsonObj;
    }

    public enum Code {
        PLAY("MA"),
        PAUSE("MA"),
        STOP("MC"),
        FORWARD("MD"),
        BACKWARD("ME"),
        VOL_UP("VU"),
        VOL_DOWN("VD");

        final String code;

        Code(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

}
