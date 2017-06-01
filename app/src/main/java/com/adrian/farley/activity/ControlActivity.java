package com.adrian.farley.activity;

import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.adrian.farley.R;
import com.adrian.farley.application.MyApplication;
import com.adrian.farley.pojo.request.DevInfoReq;
import com.adrian.farley.pojo.request.SetInfoReq;
import com.adrian.farley.tools.CommUtils;
import com.adrian.farley.tools.ConnMngr;
import com.adrian.farley.tools.Constants;
import com.adrian.farley.tools.FarleyUtils;
import com.adrian.farley.tools.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ControlActivity extends BaseActivity implements View.OnClickListener, ConnMngr.IConnMngrCallback {

    private TextView mCutFileTV;
    private TextView mParamDbTV;
    private Button mRunBtn;
    private Button mResetBtn;
    private Button mPauseBtn;
    private Button mRefBtn, mAutoBtn, mMdiBtn, mManualBtn;
    private Button mVirtureBtn, mSingleBtn, mBackwardBtn;

    private DevInfoReq devInfoReq;
    private SetInfoReq setInfoReq;
    private String id;

    private boolean[] modeChecked = {false, false, false};

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                ConnMngr.getInstance().sendMsg(devInfoReq.conv2JsonString());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initVariables() {
        id = getIntent().getExtras().getString("id");
    }

    @Override
    protected void initViews() {
        setContentView(R.layout.activity_control);
        findViewById(R.id.ib_more).setVisibility(View.GONE);
        mCutFileTV = (TextView) findViewById(R.id.tv_cut_file);
        mCutFileTV.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        mCutFileTV.getPaint().setAntiAlias(true);
        mParamDbTV = (TextView) findViewById(R.id.tv_param_db);
        mParamDbTV.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        mParamDbTV.getPaint().setAntiAlias(true);
        mRunBtn = (Button) findViewById(R.id.btn_run);
        mResetBtn = (Button) findViewById(R.id.btn_reset);
        mPauseBtn = (Button) findViewById(R.id.btn_pause);
//        mRunStatusRG = (RadioGroup) findViewById(R.id.rg_run_status);
        mRefBtn = (Button) findViewById(R.id.btn_ref);
        mAutoBtn = (Button) findViewById(R.id.btn_auto);
        mMdiBtn = (Button) findViewById(R.id.btn_mdi);
        mManualBtn = (Button) findViewById(R.id.btn_manual);
        mVirtureBtn = (Button) findViewById(R.id.btn_virture);
        mSingleBtn = (Button) findViewById(R.id.btn_single);
        mBackwardBtn = (Button) findViewById(R.id.btn_backward);
        setTitle(R.string.control);

        mRunBtn.setOnClickListener(this);
        mResetBtn.setOnClickListener(this);
        mPauseBtn.setOnClickListener(this);
        mRefBtn.setOnClickListener(this);
        mAutoBtn.setOnClickListener(this);
        mMdiBtn.setOnClickListener(this);
        mManualBtn.setOnClickListener(this);
        mVirtureBtn.setOnClickListener(this);
        mSingleBtn.setOnClickListener(this);
        mBackwardBtn.setOnClickListener(this);

//        mRunBtn.setSelected(true);  //test
    }

    @Override
    protected void loadData() {
        requestData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeMessages(0);
    }

    private void requestData() {
        List<String> des = new ArrayList<>();
        des.add("实时监控/机床状态/机床状态");
        des.add("实时监控/机床状态/运行状态");
        des.add("实时监控/机床状态/机床模式");
        ConnMngr.getInstance().setCallback(this);
        devInfoReq = new DevInfoReq(FarleyUtils.getUserid(), MyApplication.newInstance().getSessionid(), id, des);
        mHandler.sendEmptyMessage(0);
    }

    private void setData(String key, boolean value) {
        if (setInfoReq == null) {
            setInfoReq = new SetInfoReq();
            setInfoReq.setId(id);
            setInfoReq.setUserid(FarleyUtils.getUserid());
            setInfoReq.setSessionid(MyApplication.newInstance().getSessionid());
        }
        setInfoReq.setKey(key);
        setInfoReq.setValue(value);

        showProgress(true);
        mHandler.removeMessages(0);
        ConnMngr.getInstance().sendMsg(setInfoReq.conv2JsonString());
    }

    private void updateMachineStatus(int index) {
        Button[] tvs = new Button[] {mRunBtn, mResetBtn, mPauseBtn};
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
        Button[] tvs = new Button[] {mRefBtn, mAutoBtn, mMdiBtn, mManualBtn};
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

    private boolean isNotNull(JSONObject obj) {
        if (obj == null) {
            return false;
        }
        return true;
    }

    private void refreshUI(String json) {
        showProgress(false);
//        mHandler.sendEmptyMessageDelayed(0, 1000);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
            if (jsonObject.optInt("status") == 18) {    //权限不足
                CommUtils.showToast(R.string.pri_less);
                finish();
            } else if (jsonObject.optInt("status") != 0) {
                CommUtils.showToast(jsonObject.optString("err"));
                return;
            }
//            if (jsonObject.optString("type").equals("setdevinfos_ret")) {
//                LogUtil.e("SET", "setdev");
//                mHandler.sendEmptyMessage(0);
//            } else {
//                LogUtil.e("GET", "getdev");
//                mHandler.sendEmptyMessageDelayed(0, 1000);
//            }
            org.json.JSONObject content = jsonObject.optJSONObject("content");
            if (content != null) {
                JSONObject machine_status = content.optJSONObject("实时监控/机床状态/机床状态");
                if (isNotNull(machine_status)) {
                    if (machine_status.optString(Constants.VALUE).equals("4")) {    //运行
                        updateMachineStatus(0);
                    } else if (machine_status.optString(Constants.VALUE).equals("15")) { //复位
                        updateMachineStatus(1);
                    } else if (machine_status.optString(Constants.VALUE).equals("5")) { //暂停
                        updateMachineStatus(2);
                    }
                } else {
                    updateMachineStatus(-1);
                }
                JSONObject machine_mode = content.optJSONObject("实时监控/机床状态/机床模式");
                if (isNotNull(machine_mode)) {
                    JSONObject virture = machine_mode.optJSONObject("虚拟运行");
                    JSONObject single = machine_mode.optJSONObject("单步");
                    JSONObject backward = machine_mode.optJSONObject("回溯");
                    if (isNotNull(virture) && virture.optString(Constants.VALUE).equals("True")) {
                        mVirtureBtn.setSelected(true);
                        modeChecked[0] = true;
                    } else {
                        mVirtureBtn.setSelected(false);
                        modeChecked[0] = false;
                    }
                    if (isNotNull(single) && single.optString(Constants.VALUE).equals("True")) {
                        mSingleBtn.setSelected(true);
                        modeChecked[1] = true;
                    } else {
                        mSingleBtn.setSelected(false);
                        modeChecked[1] = false;
                    }
                    if (isNotNull(backward) && backward.optString(Constants.VALUE).equals("True")) {
                        mBackwardBtn.setSelected(true);
                        modeChecked[2] = true;
                    } else {
                        mBackwardBtn.setSelected(false);
                        modeChecked[2] = false;
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
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_run:
                setData("实时监控/机床状态/机床状态/运行(W)", true);
                break;
            case R.id.btn_reset:
                setData("实时监控/机床状态/机床状态/复位(W)", true);
                break;
            case R.id.btn_pause:
                setData("实时监控/机床状态/机床状态/暂停(W)", true);
                break;
            case R.id.btn_ref:
                setData("实时监控/机床状态/运行状态/Ref(W)", true);
                break;
            case R.id.btn_auto:
                setData("实时监控/机床状态/运行状态/Auto(W)", true);
                break;
            case R.id.btn_mdi:
                setData("实时监控/机床状态/运行状态/MDI(W)", true);
                break;
            case R.id.btn_manual:
                setData("实时监控/机床状态/运行状态/Manual(W)", true);
                break;
            case R.id.btn_virture:
                setData("实时监控/机床状态/机床模式/虚拟运行", !modeChecked[0]);
                break;
            case R.id.btn_single:
                setData("实时监控/机床状态/机床模式/单步", !modeChecked[1]);
                break;
            case R.id.btn_backward:
                setData("实时监控/机床状态/机床模式/回溯", !modeChecked[2]);
                break;
        }
    }

    @Override
    public void rsp(final String resp) {
//        LogUtil.e("CTRL", resp);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                refreshUI(resp);
                mHandler.sendEmptyMessageDelayed(0, 1000);
            }
        });
    }

    @Override
    public void exc(final String exception) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(exception)) {
                    CommUtils.showToast(exception);
                }
                finish();
            }
        });
    }
}
