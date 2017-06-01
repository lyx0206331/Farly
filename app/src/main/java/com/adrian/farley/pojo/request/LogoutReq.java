package com.adrian.farley.pojo.request;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by RanQing on 16-10-13 00:21.
 */

public class LogoutReq extends BaseReq {
    public LogoutReq(String userid, int sessionid) {
        super.userid = userid;
        super.sessionid = sessionid;
    }

    public String conv2JsonString() {
        JSONObject obj = new JSONObject();
        try {
            obj.putOpt("type", "logout");
            obj.putOpt("userid", userid);
            obj.put("sessionid", sessionid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj.toString();
    }
}
