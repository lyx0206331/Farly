package com.adrian.farley.pojo.response;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 登录返回
 * Created by RanQing on 16-10-12 18:14.
 */

public class LoginRes extends BaseResp {

    public LoginRes(String resp) {
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

    @Override
    public String toString() {
        return "LoginRes{" +
                "type='" + type + '\'' +
                ", sessionid=" + sessionid +
                ", status=" + status +
                ", err='" + err + '\'' +
                '}';
    }
}
