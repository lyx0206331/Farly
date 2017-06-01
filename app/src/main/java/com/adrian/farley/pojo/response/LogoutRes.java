package com.adrian.farley.pojo.response;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by RanQing on 16-10-13 01:00.
 */

public class LogoutRes extends BaseResp {
    public LogoutRes(String resp) {
        JSONObject obj = null;
        try {
            obj = new JSONObject(resp);
            status = obj.optInt("status");
            type = obj.optString("type");
            sessionid = obj.optInt("sessionid");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
