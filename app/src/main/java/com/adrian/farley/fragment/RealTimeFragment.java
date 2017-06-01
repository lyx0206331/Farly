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
import android.widget.TextView;

import com.adrian.farley.R;
import com.adrian.farley.activity.DetailInfoActivity;
import com.adrian.farley.application.MyApplication;
import com.adrian.farley.pojo.request.DevInfoReq;
import com.adrian.farley.tools.CommUtils;
import com.adrian.farley.tools.ConnMngr;
import com.adrian.farley.tools.Constants;
import com.adrian.farley.tools.FarleyUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class RealTimeFragment extends BaseFragment implements ConnMngr.IConnMngrCallback {

//    private ConnUtils utils;

    private DevInfoReq devInfoReq;

    private TextView mRealPosXTV;
    private TextView mSetPosXTV;
    private TextView mSetVeloXTV;
    private TextView mPosDiffXTV;
    private TextView mRealPosYTV;
    private TextView mSetPosYTV;
    private TextView mSetVeloYTV;
    private TextView mPosDiffYTV;

    private TextView mJerkTV;
    private TextView mRspSlowTV;
    private TextView mReachWindowTV;
    private TextView mAccLimitTV;
    private TextView mRealHeightTV;
    private TextView mNormalRspTV;
    private TextView mSetHeightTV;
    private TextView mMaxVeloTV;

    private TextView mCutTimeTV;
    private TextView mCutDistanceTV;
    private TextView mLineTimeTV;
    private TextView mLineDistanceTV;
    private TextView mNullTimeTV;
    private TextView mNullDistanceTV;
    private TextView mAllCutTimeTV;
    private TextView mAllCutDistanceTV;
    private TextView mAllLineTimeTV;
    private TextView mAllLineDistanceTV;
    private TextView mAllNullTimeTV;
    private TextView mAllNullDistanceTV;

    private TextView mSingleStepTV;
    private TextView mBackwardTV;
    private TextView mVirtureRunTV;
    private TextView mMachineStatusTV;
//    private TextView mResetTV;
//    private TextView mPauseTV;
//    private TextView mRunTV;
//    private TextView mReadyTV;

    private TextView mGasPressureTV;
    private TextView mGasTypeTV;
    private TextView mLaserPowerTV;
    private TextView mLaserDutyTV;
    private TextView mLaserFreqTV;

    private TextView mAutoTV;
    private TextView mMDITV;
    private TextView mManualTV;
    private TextView mRefTV;

    public RealTimeFragment() {
        // Required empty public constructor
//        utils = new ConnUtils(this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(R.string.real_time_monitor);
        getRealTimeInfo();
        View mLayout = inflater.inflate(R.layout.fragment_real_time, container, false);
        initViews(mLayout);
//        initData();
        return mLayout;
    }

    @Override
    protected void lazyLoad() {
        getActivity().setTitle(R.string.real_time_monitor);
    }

    @Override
    protected void onInvisible() {
        super.onInvisible();
//        Log.e("REALTIME", "invisible");
        mHandler.removeMessages(0);
    }

    @Override
    protected void onVisible() {
        super.onVisible();
//        Log.e("REALTIME", "visible");
        mHandler.removeMessages(0);
//        mHandler.sendEmptyMessage(0);
        getRealTimeInfo();
    }

    private void initViews(View parentView) {
        mRealPosXTV = (TextView) parentView.findViewById(R.id.tv_real_pos_x);
        mSetPosXTV = (TextView) parentView.findViewById(R.id.tv_set_pos_x);
        mSetVeloXTV = (TextView) parentView.findViewById(R.id.tv_set_velo_x);
        mPosDiffXTV = (TextView) parentView.findViewById(R.id.tv_pos_diff_x);
        mRealPosYTV = (TextView) parentView.findViewById(R.id.tv_real_pos_y);
        mSetPosYTV = (TextView) parentView.findViewById(R.id.tv_set_pos_y);
        mSetVeloYTV = (TextView) parentView.findViewById(R.id.tv_set_velo_y);
        mPosDiffYTV = (TextView) parentView.findViewById(R.id.tv_pos_diff_y);

        mJerkTV = (TextView) parentView.findViewById(R.id.tv_jerk_value);
        mRspSlowTV = (TextView) parentView.findViewById(R.id.tv_rsp_slow);
        mReachWindowTV = (TextView) parentView.findViewById(R.id.tv_reach_window);
        mAccLimitTV = (TextView) parentView.findViewById(R.id.tv_acc_limit);
        mRealHeightTV = (TextView) parentView.findViewById(R.id.tv_real_height);
        mNormalRspTV = (TextView) parentView.findViewById(R.id.tv_normal_rsp);
        mSetHeightTV = (TextView) parentView.findViewById(R.id.tv_set_height);
        mMaxVeloTV = (TextView) parentView.findViewById(R.id.tv_max_velo);

        mCutTimeTV = (TextView) parentView.findViewById(R.id.tv_cut_time);
        mCutDistanceTV = (TextView) parentView.findViewById(R.id.tv_cut_distance);
        mLineTimeTV = (TextView) parentView.findViewById(R.id.tv_line_time);
        mLineDistanceTV = (TextView) parentView.findViewById(R.id.tv_line_distance);
        mNullTimeTV = (TextView) parentView.findViewById(R.id.tv_null_time);
        mNullDistanceTV = (TextView) parentView.findViewById(R.id.tv_null_distance);
        mAllCutTimeTV = (TextView) parentView.findViewById(R.id.tv_all_cut_time);
        mAllCutDistanceTV = (TextView) parentView.findViewById(R.id.tv_all_cut_distance);
        mAllLineTimeTV = (TextView) parentView.findViewById(R.id.tv_all_line_time);
        mAllLineDistanceTV = (TextView) parentView.findViewById(R.id.tv_all_line_distance);
        mAllNullTimeTV = (TextView) parentView.findViewById(R.id.tv_all_null_time);
        mAllNullDistanceTV = (TextView) parentView.findViewById(R.id.tv_all_null_distance);

        mSingleStepTV = (TextView) parentView.findViewById(R.id.tv_single_step);
        mBackwardTV = (TextView) parentView.findViewById(R.id.tv_backward);
        mVirtureRunTV = (TextView) parentView.findViewById(R.id.tv_virture_run);
        mMachineStatusTV = (TextView) parentView.findViewById(R.id.tv_machine_status);
//        mResetTV = (TextView) parentView.findViewById(R.id.tv_reset);
//        mPauseTV = (TextView) parentView.findViewById(R.id.tv_pause);
//        mRunTV = (TextView) parentView.findViewById(R.id.tv_run);

        mGasPressureTV = (TextView) parentView.findViewById(R.id.tv_gas_pressure);
        mGasTypeTV = (TextView) parentView.findViewById(R.id.tv_gas_type);
        mLaserPowerTV = (TextView) parentView.findViewById(R.id.tv_laser_power);
        mLaserDutyTV = (TextView) parentView.findViewById(R.id.tv_laser_duty);
        mLaserFreqTV = (TextView) parentView.findViewById(R.id.tv_laser_freq);

        mAutoTV = (TextView) parentView.findViewById(R.id.tv_auto);
        mMDITV = (TextView) parentView.findViewById(R.id.tv_mdi);
        mManualTV = (TextView) parentView.findViewById(R.id.tv_manual);
        mRefTV = (TextView) parentView.findViewById(R.id.tv_ref);
    }

    private void initData(String json) {
//        String json = CommUtils.readFromFile(Environment.getExternalStorageDirectory().getPath() + "/farley/realtime");
//        Log.e("initdata", json);

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
            if (jsonObject.optInt("status") != 0) {
                CommUtils.showToast(jsonObject.optString("err"));
                return;
            }
            org.json.JSONObject content = jsonObject.optJSONObject("content");
            if (content != null) {
                org.json.JSONObject realtime = content.optJSONObject("实时监控");
                if (realtime != null) {
                    JSONObject x_info = realtime.optJSONObject("X轴信息");
                    if (x_info != null) {
                        JSONObject real_pos = x_info.optJSONObject("实际位置");
                        if (isNotNull(real_pos)) mRealPosXTV.setText(getString(R.string.real_pos) + real_pos.optString(Constants.VALUE) + real_pos.optString(Constants.UNIT));
                        JSONObject set_pos = x_info.optJSONObject("设定位置");
                        if (isNotNull(set_pos)) mSetPosXTV.setText(getString(R.string.set_pos) + set_pos.optString(Constants.VALUE) + set_pos.optString(Constants.UNIT));
                        JSONObject set_velo = x_info.optJSONObject("设定速度");
                        if (isNotNull(set_velo)) mSetVeloXTV.setText(getString(R.string.set_velo) + set_velo.optString(Constants.VALUE) + set_velo.optString(Constants.UNIT));
                        JSONObject pos_diff = x_info.optJSONObject("跟随误差");
                        if (isNotNull(pos_diff)) mPosDiffXTV.setText(getString(R.string.pos_diff) + pos_diff.optString(Constants.VALUE) + pos_diff.optString(Constants.UNIT));
                    }
                    JSONObject y_info = realtime.optJSONObject("Y轴信息");
                    if (y_info != null) {
                        JSONObject real_pos = y_info.optJSONObject("实际位置");
                        if (isNotNull(real_pos)) mRealPosYTV.setText(getString(R.string.real_pos) + real_pos.optString(Constants.VALUE) + real_pos.optString(Constants.UNIT));
                        JSONObject set_pos = y_info.optJSONObject("设定位置");
                        if (isNotNull(set_pos)) mSetPosYTV.setText(getString(R.string.set_pos) + set_pos.optString(Constants.VALUE) + set_pos.optString(Constants.UNIT));
                        JSONObject set_velo = y_info.optJSONObject("设定速度");
                        if (isNotNull(set_velo)) mSetVeloYTV.setText(getString(R.string.set_velo) + set_velo.optString(Constants.VALUE) + set_velo.optString(Constants.UNIT));
                        JSONObject pos_diff = y_info.optJSONObject("跟随误差");
                        if (isNotNull(pos_diff)) mPosDiffYTV.setText(getString(R.string.pos_diff) + pos_diff.optString(Constants.VALUE) + pos_diff.optString(Constants.UNIT));
                    }
                    JSONObject z_info = realtime.optJSONObject("Z轴信息");
                    if (z_info != null) {
                        JSONObject jerk_value = z_info.optJSONObject("随动Jerk值");
                        if (isNotNull(jerk_value)) mJerkTV.setText(getString(R.string.jerk_value) + jerk_value.optString(Constants.VALUE) + jerk_value.optString(Constants.UNIT));
                        JSONObject rsp_slow = z_info.optJSONObject("随动低响应值");
                        if (isNotNull(rsp_slow)) mRspSlowTV.setText(getString(R.string.rsp_slow) + rsp_slow.optString(Constants.VALUE) + rsp_slow.optString(Constants.UNIT));
                        JSONObject reach_window = z_info.optJSONObject("随动到位窗口");
                        if (isNotNull(reach_window)) mReachWindowTV.setText(getString(R.string.reach_window) + reach_window.optString(Constants.VALUE) + reach_window.optString(Constants.UNIT));
                        JSONObject acc_limit = z_info.optJSONObject("随动加速度限制值");
                        if (isNotNull(acc_limit)) mAccLimitTV.setText(getString(R.string.acc_limit) + acc_limit.optString(Constants.VALUE) + acc_limit.optString(Constants.UNIT));
                        JSONObject real_height = z_info.optJSONObject("随动实际高度");
                        if (isNotNull(real_height)) mRealHeightTV.setText(getString(R.string.real_height) + real_height.optString(Constants.VALUE) + real_height.optString(Constants.UNIT));
                        JSONObject rsp_normal = z_info.optJSONObject("随动正常响应值");
                        if (isNotNull(rsp_normal)) mNormalRspTV.setText(getString(R.string.normal_rsp) + rsp_normal.optString(Constants.VALUE) + rsp_normal.optString(Constants.UNIT));
                        JSONObject set_height = z_info.optJSONObject("随动设定高度");
                        if (isNotNull(set_height)) mSetHeightTV.setText(getString(R.string.set_height) + set_height.optString(Constants.VALUE) + set_height.optString(Constants.UNIT));
                        JSONObject max_velo = z_info.optJSONObject("随动速度");
                        if (isNotNull(max_velo)) mMaxVeloTV.setText(getString(R.string.max_velo) + max_velo.optString(Constants.VALUE) + max_velo.optString(Constants.UNIT));
                    }
                    JSONObject other = realtime.optJSONObject("其他");
//                    Log.e("other", other != null ? other.toString() : "other is null");
                    if (other != null) {
                        JSONObject cut_time = other.optJSONObject("当日切割时间");
                        if (isNotNull(cut_time)) mCutTimeTV.setText(getString(R.string.cut_time) + cut_time.optString(Constants.VALUE) + cut_time.optString(Constants.UNIT));
                        JSONObject cut_distance = other.optJSONObject("当日切割距离");
                        if (isNotNull(cut_distance)) mCutDistanceTV.setText(getString(R.string.cut_distance) + cut_distance.optString(Constants.VALUE) + cut_distance.optString(Constants.UNIT));
                        JSONObject line_time = other.optJSONObject("当日划线时间");
                        if (isNotNull(line_time)) mLineTimeTV.setText(getString(R.string.line_time) + line_time.optString(Constants.VALUE) + line_time.optString(Constants.UNIT));
                        JSONObject line_distance = other.optJSONObject("当日划线距离");
                        if (isNotNull(line_distance)) mLineDistanceTV.setText(getString(R.string.line_distance) + line_distance.optString(Constants.VALUE) + line_distance.optString(Constants.UNIT));
                        JSONObject null_time = other.optJSONObject("当日空走时间");
                        if (isNotNull(null_time)) mNullTimeTV.setText(getString(R.string.null_time) + null_time.optString(Constants.VALUE) + null_time.optString(Constants.UNIT));
                        JSONObject null_distance = other.optJSONObject("当日空走距离");
                        if (isNotNull(null_distance)) mNullDistanceTV.setText(getString(R.string.null_distance) + null_distance.optString(Constants.VALUE) + null_distance.optString(Constants.UNIT));
                        JSONObject all_cut_time = other.optJSONObject("累计切割时间");
                        if (isNotNull(all_cut_time)) mAllCutTimeTV.setText(getString(R.string.all_cut_time) + all_cut_time.optString(Constants.VALUE) + all_cut_time.optString(Constants.UNIT));
                        JSONObject all_cut_distance = other.optJSONObject("累计切割距离");
                        if (isNotNull(all_cut_distance)) mAllCutDistanceTV.setText(getString(R.string.all_cut_distance) + all_cut_distance.optString(Constants.VALUE) + all_cut_distance.optString(Constants.UNIT));
                        JSONObject all_line_time = other.optJSONObject("累计划线时间");
                        if (isNotNull(all_line_time)) mAllLineTimeTV.setText(getString(R.string.all_line_time) + all_line_time.optString(Constants.VALUE) + all_line_time.optString(Constants.UNIT));
                        JSONObject all_line_distance = other.optJSONObject("累计划线距离");
                        if (isNotNull(all_line_distance)) mAllLineDistanceTV.setText(getString(R.string.all_line_distance) + all_line_distance.optString(Constants.VALUE) + all_line_distance.optString(Constants.UNIT));
                        JSONObject all_null_time = other.optJSONObject("累计空走时间");
                        if (isNotNull(all_null_time)) mAllNullTimeTV.setText(getString(R.string.all_null_time) + all_null_time.optString(Constants.VALUE) + all_null_time.optString(Constants.UNIT));
                        JSONObject all_null_distance = other.optJSONObject("累计空走距离");
                        if (isNotNull(all_null_distance)) mAllNullDistanceTV.setText(getString(R.string.all_null_distance) + all_null_distance.optString(Constants.VALUE) + all_null_distance.optString(Constants.UNIT));
                    }
                    JSONObject machine_status = realtime.optJSONObject("机床状态");
                    Log.e("machine_status", machine_status != null ? machine_status.toString() : "machine_status is null");
                    if (machine_status != null) {
                        JSONObject machine_mode = machine_status.optJSONObject("机床模式");
                        if (machine_mode != null) {
//                            Log.e("machine_mode", machine_mode != null ? machine_mode.toString() : "machine_mode is null");
                            JSONObject single_step = machine_mode.optJSONObject("单步");
                            if (isNotNull(single_step)) mSingleStepTV.setText(getString(R.string.single_step) + single_step.optString(Constants.VALUE) + single_step.optString(Constants.UNIT));
                            JSONObject backward = machine_mode.optJSONObject("回溯");
                            if (isNotNull(backward)) mBackwardTV.setText(getString(R.string.backward) + backward.optString(Constants.VALUE) + backward.optString(Constants.UNIT));
                            JSONObject virture_run = machine_mode.optJSONObject("虚拟运行");
                            if (isNotNull(virture_run)) mVirtureRunTV.setText(getString(R.string.virture_run) + virture_run.optString(Constants.VALUE) + virture_run.optString(Constants.UNIT));
                        }
                        JSONObject machine_state = machine_status.optJSONObject("机床状态");
                        if (machine_state !=  null) {
//                            JSONObject reset = machine_state.optJSONObject("复位");
//                            if (isNotNull(reset)) mResetTV.setText(getString(R.string.reset) + reset.optString(Constants.VALUE) + reset.optString(Constants.UNIT));
//                            JSONObject pause = machine_state.optJSONObject("暂停");
//                            if (isNotNull(pause)) mPauseTV.setText(getString(R.string.pause) + pause.optString(Constants.VALUE) + pause.optString(Constants.UNIT));
//                            JSONObject run = machine_state.optJSONObject("运行");
//                            if (isNotNull(run)) mRunTV.setText(getString(R.string.run) + run.optString(Constants.VALUE) + run.optString(Constants.UNIT));
                            String status = null;
                            String s = machine_state.optString(Constants.VALUE);
                            if (s.equals("4")) {    //运行
                                status = "Active";
                            } else if (s.equals("5")) { //暂停
                                status = "Hold";
                            } else if (s.equals("6")) { //报警
                                status = "Error";
                            } else {    //准备
                                status = "Ready";
                            }
                            mMachineStatusTV.setText(getString(R.string.machine_status0) + status);
                        }
                        JSONObject gas_pressure = machine_status.optJSONObject("气体气压");
                        if (isNotNull(gas_pressure)) mGasPressureTV.setText(getString(R.string.gas_pressure) + gas_pressure.optString(Constants.VALUE) + gas_pressure.optString(Constants.UNIT));
                        JSONObject gas_type = machine_status.optJSONObject("气体类型");
                        if (isNotNull(gas_type)) mGasTypeTV.setText(getString(R.string.gas_type) + gas_type.optString(Constants.VALUE) + gas_type.optString(Constants.UNIT));
                        JSONObject laser_power = machine_status.optJSONObject("激光功率");
                        if (isNotNull(laser_power)) mLaserPowerTV.setText(getString(R.string.laser_power) + laser_power.optString(Constants.VALUE) + laser_power.optString(Constants.UNIT));
                        JSONObject laser_duty = machine_status.optJSONObject("激光占空比");
                        if (isNotNull(laser_duty)) mLaserDutyTV.setText(getString(R.string.laser_duty) + laser_duty.optString(Constants.VALUE) + laser_duty.optString(Constants.UNIT));
                        JSONObject laser_freq = machine_status.optJSONObject("激光频率");
                        if (isNotNull(laser_freq)) mLaserFreqTV.setText(getString(R.string.laser_freq) + laser_freq.optString(Constants.VALUE) + laser_freq.optString(Constants.UNIT));
                        JSONObject run_status = machine_status.optJSONObject("运行状态");
                        if (run_status !=  null) {
                            JSONObject auto = run_status.optJSONObject("Auto");
                            if (isNotNull(auto)) mAutoTV.setText(getString(R.string.auto) + auto.optString(Constants.VALUE) + auto.optString(Constants.UNIT));
                            JSONObject mdi = run_status.optJSONObject("MDI");
                            if (isNotNull(mdi)) mMDITV.setText(getString(R.string.mdi) + mdi.optString(Constants.VALUE) + mdi.optString(Constants.UNIT));
                            JSONObject manual = run_status.optJSONObject("Manual");
                            if (isNotNull(manual)) mManualTV.setText(getString(R.string.manual) + manual.optString(Constants.VALUE) + manual.optString(Constants.UNIT));
                            JSONObject ref = run_status.optJSONObject("Ref");
                            if (isNotNull(ref)) mRefTV.setText(getString(R.string.ref) + ref.optString(Constants.VALUE) + ref.optString(Constants.UNIT));
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean isNotNull(JSONObject obj) {
        if (obj == null) {
            return false;
        }
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeMessages(0);
    }

    private void getRealTimeInfo() {
        String id = ((DetailInfoActivity)getActivity()).getId();
        List<String> des = new ArrayList<>();
        des.add("实时监控");
        ConnMngr.getInstance().setCallback(this);
        devInfoReq = new DevInfoReq(FarleyUtils.getUserid(), MyApplication.newInstance().getSessionid(), id, des);
        mHandler.sendEmptyMessage(0);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
//                utils.sendMsg(devInfoReq.conv2JsonString());
                ConnMngr.getInstance().sendMsg(devInfoReq.conv2JsonString());
            }
        }
    };

    @Override
    public void rsp(final String resp) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initData(resp);
                mHandler.sendEmptyMessageDelayed(0, 1000);
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
