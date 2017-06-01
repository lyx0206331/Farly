package com.adrian.farley.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adrian.farley.R;
import com.adrian.farley.activity.DetailInfoActivity;
import com.adrian.farley.application.MyApplication;
import com.adrian.farley.pojo.request.DevInfoReq;
import com.adrian.farley.tools.CommUtils;
import com.adrian.farley.tools.ConnMngr;
import com.adrian.farley.tools.Constants;
import com.adrian.farley.tools.FarleyUtils;
import com.adrian.farley.tools.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class OverviewFragment extends BaseFragment implements ConnMngr.IConnMngrCallback {

    private TextView mRunningStatusTV;
    private TextView mReadyStatusTV;
    private TextView mWarningStatusTV;
    private TextView mPauseStatusTV;
    private TextView mRefStatusTV;
    private TextView mAutoStatusTV;
    private TextView mMdiStatusTV;
    private TextView mManualStatusTV;
    private TextView mVirtureStatusTV;
    private TextView mSingleStatusTV;
    private TextView mBackwardStatusTV;
    private TextView mXRealPosTV;
    private TextView mYRealPosTV;
    private TextView mZRealPosTV;
    private TextView mLaserPowerTV;
    private ProgressBar mFeedVelPB;

    private DevInfoReq devInfoReq;
    private String id;

    public OverviewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(R.string.overview);
        id = ((DetailInfoActivity)getActivity()).getId();
        getOverviewInfo();
        View mLayout = inflater.inflate(R.layout.fragment_overview, container, false);
        mRunningStatusTV = (TextView) mLayout.findViewById(R.id.tv_running_status);
        mReadyStatusTV = (TextView) mLayout.findViewById(R.id.tv_ready_status);
        mWarningStatusTV = (TextView) mLayout.findViewById(R.id.tv_warning_status);
        mPauseStatusTV = (TextView) mLayout.findViewById(R.id.tv_pause_status);
        mRefStatusTV = (TextView) mLayout.findViewById(R.id.tv_ref_status);
        mAutoStatusTV = (TextView) mLayout.findViewById(R.id.tv_auto_status);
        mMdiStatusTV = (TextView) mLayout.findViewById(R.id.tv_mdi_status);
        mManualStatusTV = (TextView) mLayout.findViewById(R.id.tv_manual_status);
        mVirtureStatusTV = (TextView) mLayout.findViewById(R.id.tv_virture_status);
        mSingleStatusTV = (TextView) mLayout.findViewById(R.id.tv_single_status);
        mBackwardStatusTV = (TextView) mLayout.findViewById(R.id.tv_backward_status);
        mXRealPosTV = (TextView) mLayout.findViewById(R.id.tv_real_pos_x);
        mYRealPosTV = (TextView) mLayout.findViewById(R.id.tv_real_pos_y);
        mZRealPosTV = (TextView) mLayout.findViewById(R.id.tv_real_pos_z);
        mLaserPowerTV = (TextView) mLayout.findViewById(R.id.tv_laser_power);
        mFeedVelPB = (ProgressBar) mLayout.findViewById(R.id.pb_feed_vel);
//        mRunningStatusTV.setSelected(true);
        return mLayout;
    }

    @Override
    protected void lazyLoad() {
        getActivity().setTitle(R.string.overview);
    }

    private void updateMachineStatus(int index) {
        TextView[] tvs = new TextView[] {mReadyStatusTV, mRunningStatusTV, mPauseStatusTV, mWarningStatusTV};
        if (index >= 0 && index < tvs.length) {
            for (int i = 0; i < tvs.length; i++) {
                if (i == index) {
                    tvs[i].setSelected(true);
                } else {
                    tvs[i].setSelected(false);
                }
            }
        } else {
            for (TextView tv :
                    tvs) {
                tv.setSelected(false);
            }
        }
    }
    private void updateRunStatus(int index) {
        TextView[] tvs = new TextView[] {mRefStatusTV, mAutoStatusTV, mMdiStatusTV, mManualStatusTV};
        if (index >= 0 && index < tvs.length) {
            for (int i = 0; i < tvs.length; i++) {
                if (i == index) {
                    tvs[i].setSelected(true);
                } else {
                    tvs[i].setSelected(false);
                }
            }
        } else {
            for (TextView tv :
                    tvs) {
                tv.setSelected(false);
            }
        }
    }
    private void updateMachineMode(int index) {
        TextView[] tvs = new TextView[] {mVirtureStatusTV, mSingleStatusTV, mBackwardStatusTV};
        if (index >= 0 && index < tvs.length) {
            for (int i = 0; i < tvs.length; i++) {
                if (i == index) {
                    tvs[i].setSelected(true);
                } else {
                    tvs[i].setSelected(false);
                }
            }
        } else {
            for (TextView tv :
                    tvs) {
                tv.setSelected(false);
            }
        }
    }


    @Override
    protected void onVisible() {
        super.onVisible();
        mHandler.removeMessages(0);
//        mHandler.removeMessages(1);
//        mHandler.sendEmptyMessage(0);
        getOverviewInfo();
    }

    @Override
    protected void onInvisible() {
        super.onInvisible();
        mHandler.removeMessages(0);
//        mHandler.removeMessages(1);
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeMessages(0);
//        mHandler.removeMessages(1);
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.e("OVERVIEW", "onResume");
    }

    private void getOverviewInfo() {
        List<String> des = new ArrayList<>();
        des.add("实时监控/机床状态/机床状态");
        des.add("实时监控/机床状态/运行状态");
        des.add("实时监控/机床状态/机床模式");
        des.add("实时监控/机床状态/激光功率");
        des.add("实时监控/X轴信息/实际位置");
        des.add("实时监控/Y轴信息/实际位置");
        des.add("实时监控/Z轴信息/实际位置");
        des.add("工艺数据库/标准切割/切割速度");
        ConnMngr.getInstance().setCallback(this);
        devInfoReq = new DevInfoReq(FarleyUtils.getUserid(), MyApplication.newInstance().getSessionid(), id, des);
        mHandler.sendEmptyMessage(0);
    }

    private boolean isNotNull(JSONObject obj) {
        if (obj == null) {
            return false;
        }
        return true;
    }

    private void setData(String json) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
            if (jsonObject.optInt("status") != 0) {
                CommUtils.showToast(jsonObject.optString("err"));
                return;
            }
            org.json.JSONObject content = jsonObject.optJSONObject("content");
            if (content != null) {
                JSONObject machine_status = content.optJSONObject("实时监控/机床状态/机床状态");
                if (isNotNull(machine_status)) {
                    if (machine_status.optString(Constants.VALUE).equals("4")) {    //运行
                        updateMachineStatus(1);
                    } else if (machine_status.optString(Constants.VALUE).equals("5")) { //暂停
                        updateMachineStatus(2);
                    } else if (machine_status.optString(Constants.VALUE).equals("6")) { //报警
                        updateMachineStatus(3);
                    } else {    //准备
                        updateMachineStatus(0);
                    }
                } else {
                    updateMachineStatus(0);
                }
                JSONObject machine_mode = content.optJSONObject("实时监控/机床状态/机床模式");
                if (isNotNull(machine_mode)) {
                    JSONObject virture = machine_mode.optJSONObject("虚拟运行");
                    JSONObject single = machine_mode.optJSONObject("单步");
                    JSONObject backward = machine_mode.optJSONObject("回溯");
                    if (isNotNull(virture) && virture.optString(Constants.VALUE).equals("True")) {
                        mVirtureStatusTV.setSelected(true);
                    } else {
                        mVirtureStatusTV.setSelected(false);
                    }
                    if (isNotNull(single) && single.optString(Constants.VALUE).equals("True")) {
                        mSingleStatusTV.setSelected(true);
                    } else {
                        mSingleStatusTV.setSelected(false);
                    }
                    if (isNotNull(backward) && backward.optString(Constants.VALUE).equals("True")) {
                        mBackwardStatusTV.setSelected(true);
                    } else {
                        mBackwardStatusTV.setSelected(false);
                    }
                }
                JSONObject run_status = content.optJSONObject("实时监控/机床状态/运行状态");
                if (isNotNull(run_status)) {
                    JSONObject ref = run_status.optJSONObject("Ref");
                    JSONObject auto = run_status.optJSONObject("Auto");
                    JSONObject mdi = run_status.optJSONObject("MDI");
                    JSONObject manual = run_status.optJSONObject("Manual");
                    if (isNotNull(ref) && ref.optString(Constants.VALUE).equals("True")) {
                        updateRunStatus(0);
                    } else if (isNotNull(auto) && auto.optString(Constants.VALUE).equals("True")) {
                        updateRunStatus(1);
                    } else if (isNotNull(mdi) && mdi.optString(Constants.VALUE).equals("True")) {
                        updateRunStatus(2);
                    } else if (isNotNull(manual) && manual.optString(Constants.VALUE).equals("True")) {
                        updateRunStatus(3);
                    } else {
                        updateRunStatus(-1);
                    }
                }
                org.json.JSONObject x_real_pos = content.optJSONObject("实时监控/X轴信息/实际位置");
                if (isNotNull(x_real_pos))  {
                    mXRealPosTV.setText(x_real_pos.optString(Constants.VALUE) + x_real_pos.optString(Constants.UNIT));
                } else {
                    mXRealPosTV.setText(R.string.null_content);
                }
                org.json.JSONObject y_real_pos = content.optJSONObject("实时监控/Y轴信息/实际位置");
                if (isNotNull(y_real_pos)) {
                    mYRealPosTV.setText(y_real_pos.optString(Constants.VALUE) + y_real_pos.optString(Constants.UNIT));
                } else {
                    mYRealPosTV.setText(R.string.null_content);
                }
                org.json.JSONObject z_real_pos = content.optJSONObject("实时监控/Z轴信息/实际位置");
                if (isNotNull(z_real_pos)) {
                    mXRealPosTV.setText(z_real_pos.optString(Constants.VALUE) + z_real_pos.optString(Constants.UNIT));
                } else {
                    mZRealPosTV.setText(R.string.null_content);
                }
                JSONObject laser_power = content.optJSONObject("实时监控/机床状态/激光功率");
                if (isNotNull(laser_power)) {
                    mLaserPowerTV.setText(laser_power.optString(Constants.VALUE) + laser_power.optString(Constants.UNIT));
                } else {
                    mLaserPowerTV.setText(R.string.null_content);
                }
                mFeedVelPB.setProgress((int) (Math.random() * 100));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                ConnMngr.getInstance().sendMsg(devInfoReq.conv2JsonString());
            } else if (msg.what == 1) { //test progress,just a demo
                mFeedVelPB.setProgress((int) (Math.random() * 100));
//                mHandler.sendEmptyMessageDelayed(1, 1000);
            }
        }
    };

    @Override
    public void rsp(final String resp) {
        LogUtil.e("OVERVIEW", resp);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setData(resp);
                mHandler.sendEmptyMessageDelayed(0, 1000);
//                mHandler.sendEmptyMessageDelayed(1, 1000);
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
