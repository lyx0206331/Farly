package com.adrian.farley.pojo.request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * 请求设备状态信息
 * Created by RanQing on 16-10-13 14:18.
 */

public class DevInfoReq extends BaseReq {
    private String id;          // 设备id
    private List<String> des;   // 节点描述数组，为空时或不存在此key时表示全读取

    public DevInfoReq(String userid, int sessionid, String id, List<String> des) {
        super.userid = userid;
        super.sessionid = sessionid;
        this.id = id;
        this.des = des;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getDes() {
        return des;
    }

    public void setDes(List<String> des) {
        this.des = des;
    }

    public String conv2JsonString() {
        JSONObject obj = new JSONObject();
        try {
            obj.putOpt("type", "getdevinfos");
            obj.putOpt("userid", userid);
            obj.put("sessionid", sessionid);
            obj.putOpt("id", id);
            JSONArray array = new JSONArray();
            for (String path : des) {
                array.put(path);
            }
            obj.putOpt("des", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj.toString();
    }
}
