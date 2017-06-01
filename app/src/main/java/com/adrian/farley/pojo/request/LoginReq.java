package com.adrian.farley.pojo.request;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 登录请求
 * Created by RanQing on 16-10-12 17:20.
 */

public class LoginReq extends BaseReq {
    private String password;// 用户登录需要password，转发程序连服务器不需要
    private String platform;// 登陆平台，”pc为pc客户端，”android”为安卓端

    public LoginReq() {
    }

    public LoginReq(String userid, String password) {
        this.password = password;
        super.userid = userid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String conv2JsonString() {
        JSONObject obj = new JSONObject();
        try {
            obj.putOpt("type", "logon");
            obj.putOpt("userid", userid);
            obj.putOpt("password", password);
            obj.put("sessionid", 0);
            obj.putOpt("platform", "android");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj.toString();
    }
}
