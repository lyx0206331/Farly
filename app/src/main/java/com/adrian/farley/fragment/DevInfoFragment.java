package com.adrian.farley.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.adrian.farley.R;
import com.adrian.farley.activity.DetailInfoActivity;
import com.adrian.farley.adapter.SimpleTreeAdapter;
import com.adrian.farley.adapter.TreeListViewAdapter;
import com.adrian.farley.application.MyApplication;
import com.adrian.farley.pojo.Bean;
import com.adrian.farley.pojo.FileBean;
import com.adrian.farley.pojo.Node;
import com.adrian.farley.pojo.request.DevInfoReq;
import com.adrian.farley.tools.CommUtils;
import com.adrian.farley.tools.ConnMngr;
import com.adrian.farley.tools.Constants;
import com.adrian.farley.tools.FarleyUtils;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DevInfoFragment extends BaseFragment implements ConnMngr.IConnMngrCallback {

    private List<Bean> mDatas = new ArrayList<Bean>();
    private List<FileBean> mDatas2 = new ArrayList<FileBean>();
    private ListView mTree;
    private TreeListViewAdapter mAdapter;
    private Button mRefreshBtn;

//    private ConnUtils utils;

    public DevInfoFragment() {
        // Required empty public constructor
//        utils = new ConnUtils(this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(R.string.dev_info);
//        initDatas();
        View mLayout = inflater.inflate(R.layout.fragment_dev_info, container, false);
        mTree = (ListView) mLayout.findViewById(R.id.lv_dev_info);
        mRefreshBtn = (Button) mLayout.findViewById(R.id.btn_refresh);
        mRefreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDevInfo();
                v.setVisibility(View.GONE);
            }
        });
//        initDataTree();
        getDevInfo();
        return mLayout;
    }

    private void initDataTree() {
        try
        {
            mAdapter = new SimpleTreeAdapter<Bean>(mTree, getContext(), mDatas, 1);
            mAdapter.setOnTreeNodeClickListener(new TreeListViewAdapter.OnTreeNodeClickListener()
            {
                @Override
                public void onClick(Node node, int position)
                {
                    if (node.isLeaf())
                    {
                        CommUtils.showToast(node.getName());
                    }
                }

            });

        } catch (Exception e)
        {
            e.printStackTrace();
        }
        mTree.setAdapter(mAdapter);
    }

    @Override
    protected void lazyLoad() {
        getActivity().setTitle(R.string.dev_info);
//        getDevInfo();
    }

    @Override
    protected void onVisible() {
        super.onVisible();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void initDatas()
    {
//        mDatas.add(new Bean(1, 0, "根目录1"));
//        mDatas.add(new Bean(2, 0, "根目录2"));
//        mDatas.add(new Bean(3, 0, "根目录3"));
//        mDatas.add(new Bean(4, 0, "根目录4"));
//        mDatas.add(new Bean(5, 1, "子目录1-1"));
//        mDatas.add(new Bean(6, 1, "子目录1-2"));
//
//        mDatas.add(new Bean(7, 5, "子目录1-1-1"));
//        mDatas.add(new Bean(8, 2, "子目录2-1"));
//
//        mDatas.add(new Bean(9, 4, "子目录4-1"));
//        mDatas.add(new Bean(10, 4, "子目录4-2"));
//
//        mDatas.add(new Bean(11, 10, "子目录4-2-1"));
//        mDatas.add(new Bean(12, 10, "子目录4-2-3"));
//        mDatas.add(new Bean(13, 10, "子目录4-2-2"));
//        mDatas.add(new Bean(14, 9, "子目录4-1-1"));
//        mDatas.add(new Bean(15, 9, "子目录4-1-2"));
//        mDatas.add(new Bean(16, 9, "子目录4-1-3"));
//
//        mDatas2.add(new FileBean(1, 0, "文件管理系统"));
//        mDatas2.add(new FileBean(2, 1, "游戏"));
//        mDatas2.add(new FileBean(3, 1, "文档"));
//        mDatas2.add(new FileBean(4, 1, "程序"));
//        mDatas2.add(new FileBean(5, 2, "war3"));
//        mDatas2.add(new FileBean(6, 2, "刀塔传奇"));
//
//        mDatas2.add(new FileBean(7, 4, "面向对象"));
//        mDatas2.add(new FileBean(8, 4, "非面向对象"));
//
//        mDatas2.add(new FileBean(9, 7, "C++"));
//        mDatas2.add(new FileBean(10, 7, "JAVA"));
//        mDatas2.add(new FileBean(11, 7, "Javascript"));
//        mDatas2.add(new FileBean(12, 8, "C"));

    }


    private void getDevInfo() {
        String id = ((DetailInfoActivity)getActivity()).getId();
        List<String> des = new ArrayList<>();
        des.add("设备信息");
        DevInfoReq devInfoReq = new DevInfoReq(FarleyUtils.getUserid(), MyApplication.newInstance().getSessionid(), id, des);
//        utils.sendMsg(devInfoReq.conv2JsonString());
        ConnMngr.getInstance().setCallback(this);
        ConnMngr.getInstance().sendMsg(devInfoReq.conv2JsonString());
//        String local = CommUtils.readFromFile(Environment.getExternalStorageDirectory().getPath() + "/farley/device_info");
//        Log.e("LOCAL", local);
//        try {
//            parseJson(new org.json.JSONObject(local));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    private int addData(int id, int pid, org.json.JSONObject jsonObject) {
        if (jsonObject == null) {
            return 0;
        }
        Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            mDatas.add(new Bean(++id, pid, key + ":" + jsonObject.optString(key)));
//            Log.e("ID_INFO", key + ":" + id + "--" + pid);
        }
        return id;
    }

    private void parseJson(org.json.JSONObject jsonObject) {
        Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            Log.e("key0", key);
            Object obj = jsonObject.opt(key);
            if (obj instanceof org.json.JSONObject) {
                org.json.JSONObject json = (org.json.JSONObject) obj;
                parseJson(json);
            } else {
                Log.e("value", obj.toString());
            }
        }
    }

    @Override
    public void rsp(final String resp) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    org.json.JSONObject jsonObject = new org.json.JSONObject(resp);
                    if (jsonObject.optInt("status") != 0) {
                        CommUtils.showToast(jsonObject.optString("err"));
                        mTree.setVisibility(View.GONE);
                        mRefreshBtn.setVisibility(View.VISIBLE);
                        return;
                    } else {
                        mTree.setVisibility(View.VISIBLE);
                        mRefreshBtn.setVisibility(View.GONE);
                    }
//                    parseJson(jsonObject);
                    org.json.JSONObject content = jsonObject.optJSONObject("content");
                    if (content != null) {
                        org.json.JSONObject devInfo = content.optJSONObject("设备信息");
                        if (devInfo != null) {
                            org.json.JSONObject baseInfo = devInfo.optJSONObject("基本信息");
                            if (baseInfo != null) {
                                int index = 0;
                                mDatas.add(new Bean(++index, 0, "基本信息"));
                                org.json.JSONObject guestInfo = baseInfo.optJSONObject("客户信息");
                                if (guestInfo != null) {
                                    mDatas.add(new Bean(++index, 1, "客户信息:" + guestInfo.optString(Constants.VALUE) + guestInfo.optString(Constants.UNIT)));
//                                    index = addData(2, 2, guestInfo);
                                }
                                org.json.JSONObject machineType = baseInfo.optJSONObject("机床型号");
                                if (machineType != null) {
                                    mDatas.add(new Bean(++index, 1, "机床型号:" + machineType.optString(Constants.VALUE) + machineType.optString(Constants.UNIT)));
//                                    index = addData(index, index, machineType);
                                }
                                org.json.JSONObject devNum = baseInfo.optJSONObject("设备编号");
                                if (devNum != null) {
                                    mDatas.add(new Bean(++index, 1, "设备编号:" + devNum.optString(Constants.VALUE) + devNum.optString(Constants.UNIT)));
//                                    index = addData(index, index, devNum);
                                }
                            }
                        }
                    }
                    initDataTree();
                } catch (JSONException e) {
                    e.printStackTrace();
                    mTree.setVisibility(View.GONE);
                    mRefreshBtn.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void exc(final String exception) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(exception)) {
                    CommUtils.showToast(exception);
                }
                getActivity().finish();
            }
        });
    }
}
