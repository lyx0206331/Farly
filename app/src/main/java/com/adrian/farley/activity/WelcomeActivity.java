package com.adrian.farley.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.adrian.farley.R;
import com.adrian.farley.application.MyApplication;
import com.adrian.farley.pojo.request.LoginReq;
import com.adrian.farley.pojo.response.LoginRes;
import com.adrian.farley.tools.CommUtils;
import com.adrian.farley.tools.ConnMngr;
import com.adrian.farley.tools.FarleyUtils;

public class WelcomeActivity extends BaseActivity implements ConnMngr.IConnMngrCallback {

//    private ConnUtils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initVariables() {
        if (CommUtils.getNetworkStatus(this) == -1) {
            CommUtils.showToast(R.string.net_error);
            finish();
        }
//        utils = new ConnUtils(this);
    }

    @Override
    protected void initViews() {
        setContentView(R.layout.activity_welcome);

    }

    @Override
    protected void loadData() {
        ConnMngr.getInstance().setCallback(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (!TextUtils.isEmpty(FarleyUtils.getUserid()) && !TextUtils.isEmpty(FarleyUtils.getPassword())) {
//            mHandler.sendEmptyMessage(1);
//        } else {
            mHandler.sendEmptyMessageDelayed(0, 2000);
//        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    startActivity(LoginActivity.class);
                    finish();
                    break;
                case 1:
//                    utils.sendMsg((new LoginReq(FarleyUtils.getUserid(), FarleyUtils.getPassword())).conv2JsonString());
                    ConnMngr.getInstance().sendMsg((new LoginReq(FarleyUtils.getUserid(), FarleyUtils.getPassword())).conv2JsonString());
                    break;
            }
        }
    };

    @Override
    public void rsp(final String resp) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LoginRes loginRes = new LoginRes(resp);
                if (loginRes.getStatus() == 0) {
                    MyApplication.newInstance().setSessionid(loginRes.getSessionid());
                    startActivity(MainActivity.class);
                    finish();
                } else {
                    startActivity(LoginActivity.class);
                    finish();
                }
            }
        });
    }

    @Override
    public void exc(final String exception) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CommUtils.showToast(exception);
                finish();
            }
        });
    }
}
