package com.adrian.farley.pojo;

import android.text.TextUtils;

import org.json.JSONObject;

/**
 * 设备状态
 * Created by RanQing on 16-9-29 01:26.
 */

public class DevBaseInfo {
    private String camera;  // 设备摄像头地址
    private String id;      // 设备id
    private String line;    // 是否在线。on/off
    private String limit;   // 权限，暂时保留，处理时可忽略此字段

    private LanDev lanDev;  //局域网广播信息

    public DevBaseInfo() {
    }

    public DevBaseInfo(JSONObject obj) {
        parse(obj);
    }

    public DevBaseInfo(String camera, String id, String line, String limit) {
        this.camera = camera;
        this.id = id;
        this.line = line;
        this.limit = limit;
    }

    public String getCamera() {
        return camera;
    }

    public void setCamera(String camera) {
        this.camera = camera;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public LanDev getLanDev() {
        return lanDev;
    }

    public void setLanDev(LanDev lanDev) {
        this.lanDev = lanDev;
    }

    public boolean isOnline() {
        if (TextUtils.isEmpty(line) || !line.equals("on")) {
            return false;
        } else {
            return true;
        }
    }

    public void parse(JSONObject obj) {
        if (obj == null) {
            return;
        }
        camera = obj.optString("camera");
        id = obj.optString("id");
        line = obj.optString("line");
        limit = obj.optString("limit");
    }
}
