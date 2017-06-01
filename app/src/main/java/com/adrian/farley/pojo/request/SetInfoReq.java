package com.adrian.farley.pojo.request;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by adrian on 16-12-6.
 */

public class SetInfoReq extends BaseReq {
    private String id;
    private String key;
    private boolean value;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public String conv2JsonString() {
        JSONObject obj = new JSONObject();
        try {
            obj.putOpt("type", "setdevinfos");
            obj.putOpt("userid", userid);
            obj.put("sessionid", sessionid);
            obj.putOpt("id", id);
            JSONObject des = new JSONObject();
            des.put(key, value);
            obj.putOpt("des", des);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj.toString();
    }
}
