package com.adrian.farley.pojo.response;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by adrian on 16-12-6.
 */

public class SetInfoRes extends BaseResp {

    private String key;
    private String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void parse(String resp) {
        JSONObject obj = null;
        try {
            obj = new JSONObject(resp);
            status = obj.optInt("status");
            type = obj.optString("type");
            sessionid = obj.optInt("sessionid");
            err = obj.optString("err");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
