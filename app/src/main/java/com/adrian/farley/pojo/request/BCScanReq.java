package com.adrian.farley.pojo.request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by adrian on 16-11-21.
 */

public class BCScanReq extends BaseReq {
    public BCScanReq(String userid, int sessionid) {
        super.userid = userid;
        super.sessionid = sessionid;
    }

    public String conv2JsonString() {
        JSONObject obj = new JSONObject();
        try {
            obj.putOpt("type", "broadcast");
            obj.putOpt("userid", userid);
            obj.put("sessionid", sessionid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj.toString();
    }
}
