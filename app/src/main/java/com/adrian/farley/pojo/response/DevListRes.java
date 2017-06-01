package com.adrian.farley.pojo.response;

import android.util.Log;

import com.adrian.farley.pojo.DevBaseInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 设备列表返回
 * Created by RanQing on 16-10-13 01:19.
 */

public class DevListRes extends BaseResp {
    private ArrayList<DevBaseInfo> list;

    public DevListRes() {
    }

    public DevListRes(String resp) {
        parse(resp);
    }

    private void parse(String resp) {
        if (list == null) {
            list = new ArrayList<>();
        }
        JSONObject obj = null;
        try {
            obj = new JSONObject(resp);
            status = obj.optInt("status");
            type = obj.optString("type");
            sessionid = obj.optInt("sessionid");
            JSONArray array = obj.optJSONArray("devlist");
            if (array == null || array.length() == 0) {
                return;
            }
            Log.e("DEV", "dev json count:" + array.length());
            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.optJSONObject(i);
                list.add(new DevBaseInfo(item));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<DevBaseInfo> getList() {
        return list;
    }

    public void setList(ArrayList<DevBaseInfo> list) {
        this.list = list;
    }
}
