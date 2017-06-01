package com.adrian.farley.pojo.response;

/**
 * 返回基类
 * Created by RanQing on 16-10-13 00:30.
 */

public class BaseResp {
    protected String type;  // 返回类型
    protected int sessionid;// status有错时回复无意义的0，无错时回复分配的值
    protected int status;   // 验证状态，0为成功，非0不成功
    protected String err;   // 调试阶段的辅助信息，程序可以不处理

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSessionid() {
        return sessionid;
    }

    public void setSessionid(int sessionid) {
        this.sessionid = sessionid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getErr() {
        return err;
    }

    public void setErr(String err) {
        this.err = err;
    }
}
