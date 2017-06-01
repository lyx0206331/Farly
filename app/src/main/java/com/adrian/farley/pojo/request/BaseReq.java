package com.adrian.farley.pojo.request;

/**
 * 请求基类
 * Created by RanQing on 16-10-13 00:27.
 */

public class BaseReq {
    protected String type;  //请求类型
    protected String userid;// userid在有password时表示user id，否则为设备id
    protected int sessionid;// 会话ID，32位无符数，logon时填0

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public int getSessionid() {
        return sessionid;
    }

    public void setSessionid(int sessionid) {
        this.sessionid = sessionid;
    }
}
