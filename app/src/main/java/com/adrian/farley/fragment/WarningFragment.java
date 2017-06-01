package com.adrian.farley.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.adrian.farley.R;
import com.adrian.farley.activity.BaseActivity;
import com.adrian.farley.activity.DetailInfoActivity;
import com.adrian.farley.adapter.WarningAdapter;
import com.adrian.farley.application.MyApplication;
import com.adrian.farley.pojo.WarningInfo;
import com.adrian.farley.pojo.request.DevInfoReq;
import com.adrian.farley.tools.CommUtils;
import com.adrian.farley.tools.ConnMngr;
import com.adrian.farley.tools.Constants;
import com.adrian.farley.tools.FarleyUtils;
import com.videogo.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class WarningFragment extends BaseFragment implements ConnMngr.IConnMngrCallback {

//    private ConnUtils utils;

    private RecyclerView mWarningListRV;
    private TextView mEmptyTV;
    private Button mRefreshBtn;

    private WarningAdapter adapter;

    public WarningFragment() {
        // Required empty public constructor
//        utils = new ConnUtils(this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mLayout = inflater.inflate(R.layout.fragment_warning, container, false);
        mWarningListRV = (RecyclerView) mLayout.findViewById(R.id.rv_warning_list);
        mWarningListRV.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutMngr = new LinearLayoutManager(getContext());
        mWarningListRV.setLayoutManager(layoutMngr);
        adapter = new WarningAdapter();
        mWarningListRV.setAdapter(adapter);
        mEmptyTV = (TextView) mLayout.findViewById(R.id.tv_no_warning);
        mRefreshBtn = (Button) mLayout.findViewById(R.id.btn_refresh);
        mRefreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWarningInfo();
                v.setVisibility(View.GONE);
            }
        });
        getActivity().setTitle(R.string.warning_info);
        getWarningInfo();
        // Inflate the layout for this fragment
        return mLayout;
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        getWarningInfo();
    }

    @Override
    protected void lazyLoad() {
        getActivity().setTitle(R.string.warning_info);
    }

    private void getWarningInfo() {
        String id = ((DetailInfoActivity)getActivity()).getId();
        List<String> des = new ArrayList<>();
        des.add("报警");
        DevInfoReq devInfoReq = new DevInfoReq(FarleyUtils.getUserid(), MyApplication.newInstance().getSessionid(), id, des);
//        utils.sendMsg(devInfoReq.conv2JsonString());
        ConnMngr.getInstance().setCallback(this);
        ConnMngr.getInstance().sendMsg(devInfoReq.conv2JsonString());
    }

    private void hasContent(int flag) {
        LogUtil.e("WARNING", "has content:" + flag);
        if (flag == 1) {    //有警报
            mEmptyTV.setVisibility(View.GONE);
            mRefreshBtn.setVisibility(View.GONE);
            mWarningListRV.setVisibility(View.VISIBLE);
        } else if (flag == 0) { //无警报
            mEmptyTV.setVisibility(View.VISIBLE);
            mRefreshBtn.setVisibility(View.GONE);
            mWarningListRV.setVisibility(View.GONE);
        } else if (flag == -1) {    //异常
            mEmptyTV.setVisibility(View.GONE);
            mWarningListRV.setVisibility(View.GONE);
            mRefreshBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void rsp(final String resp) {
//        LogUtil.e("WARNING", resp);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    org.json.JSONObject jsonObject = new org.json.JSONObject(resp);
                    if (jsonObject.optInt("status") != 0) {
                        CommUtils.showToast(jsonObject.optString("err"));
                        hasContent(-1);
                        return;
                    }
//                    parseJson(jsonObject);
                    org.json.JSONObject content = jsonObject.optJSONObject("content");
                    if (content != null) {
                        org.json.JSONObject warningInfo = content.optJSONObject("报警");
                        if (warningInfo == null || warningInfo.length() == 0) {
                            hasContent(0);
                        } else {
                            hasContent(1);
                            List<WarningInfo> list = new ArrayList<WarningInfo>();
                            Iterator<String> keys = warningInfo.keys();
                            while (keys.hasNext()) {
                                String index = keys.next();
                                JSONObject value = warningInfo.optJSONObject(index);
                                WarningInfo info = new WarningInfo(index, value.optString(Constants.VALUE));
                                list.add(info);
                            }
                            adapter.setList(list);
                        }
                    } else {
                        hasContent(0);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    hasContent(-1);
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
