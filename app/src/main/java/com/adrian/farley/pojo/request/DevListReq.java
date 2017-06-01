package com.adrian.farley.pojo.request;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 设备列表请求
 * Created by RanQing on 16-10-13 01:07.
 */

public class DevListReq extends BaseReq {
    public DevListReq(String userid, int sessionid) {
        super.userid = userid;
        super.sessionid = sessionid;
    }

    public String conv2JsonString() {
        JSONObject obj = new JSONObject();
        try {
            obj.putOpt("type", "getdevlist");
            obj.putOpt("userid", userid);
            obj.put("sessionid", sessionid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj.toString();
    }
}
