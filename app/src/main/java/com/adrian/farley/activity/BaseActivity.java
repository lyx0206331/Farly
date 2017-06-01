package com.adrian.farley.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.adrian.farley.R;
import com.adrian.farley.application.MyApplication;
import com.adrian.farley.pojo.request.LogoutReq;
import com.adrian.farley.tools.CommUtils;
import com.adrian.farley.tools.ConnMngr;
import com.adrian.farley.tools.ConnUtils;
import com.adrian.farley.tools.FarleyUtils;

public abstract class BaseActivity extends AppCompatActivity {

    protected PopupWindow popupWindow;
    private Button mQuitBtn;
    private Button mMonitorBtn;
    private Button mUploadBtn;
    private Button mControlBtn;
    private View mLine1, mLine2, mLine3;

    private ProgressDialog mPd;

//    private ConnUtils utils = new ConnUtils(new ConnUtils.ConnCallback() {
//        @Override
//        public void response(String rsp) {
//            Log.e("QUIT", rsp);
//        }
//
//        @Override
//        public void exception(String exception) {
//            Log.e("QUIT", exception);
//        }
//    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVariables();
        initViews();
        loadData();
    }

    protected void startActivity(Class<? extends Activity> dstAct) {
        Intent intent = new Intent(getApplicationContext(), dstAct);
        startActivity(intent);
    }

    protected void startActivity(Class<? extends Activity> dstAct, Bundle bundle) {
        Intent intent = new Intent(getApplicationContext(), dstAct);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    protected void upload(View view) {
        CommUtils.showToast("暂无上传功能");
        popupWindow.dismiss();
    }

    protected void showProgress(boolean show) {
        if (mPd == null) {
            mPd = new ProgressDialog(this);
            mPd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mPd.setMessage("Loading...");
            mPd.setIndeterminate(false);
            mPd.setCancelable(false);
        }
        if (show && !mPd.isShowing()) {
            mPd.show();
        } else if (!show && mPd.isShowing()) {
            mPd.dismiss();
        }
    }

    protected void back(View view) {
        finish();
    }

    protected void more(View view) {
        if (popupWindow == null) {
            View contentView = LayoutInflater.from(this).inflate(R.layout.more_menu_layout, null, false);
            mQuitBtn = (Button) contentView.findViewById(R.id.btn_quit);
            mMonitorBtn = (Button) contentView.findViewById(R.id.btn_monitor);
            mUploadBtn = (Button) contentView.findViewById(R.id.btn_upload);
            mControlBtn = (Button) contentView.findViewById(R.id.btn_ctrl);
            mLine1 = contentView.findViewById(R.id.line1);
            mLine2 = contentView.findViewById(R.id.line2);
            mLine3 = contentView.findViewById(R.id.line3);
            popupWindow = new PopupWindow(contentView, CommUtils.getWindowWidth(this) / 3, LinearLayout.LayoutParams.WRAP_CONTENT, true);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setBackgroundDrawable(new ColorDrawable(0x00ffff));
        }
        if (this instanceof MainActivity) {
            mMonitorBtn.setVisibility(View.GONE);
            mLine2.setVisibility(View.GONE);
            mLine1.setVisibility(View.GONE);
            mLine3.setVisibility(View.GONE);
            mUploadBtn.setVisibility(View.GONE);
            mControlBtn.setVisibility(View.GONE);
        } else if (this instanceof DetailInfoActivity) {
            mMonitorBtn.setVisibility(View.VISIBLE);
            mLine2.setVisibility(View.VISIBLE);
            mLine1.setVisibility(View.VISIBLE);
            mLine3.setVisibility(View.VISIBLE);
            mUploadBtn.setVisibility(View.VISIBLE);
            mControlBtn.setVisibility(View.VISIBLE);
        }
        popupWindow.showAsDropDown(view, 0, 30);
    }

//    protected void control(View view) {
//        popupWindow.dismiss();
//        startActivity(ControlActivity.class);
//    }

    protected void quit(View view) {
        popupWindow.dismiss();
//        utils.sendMsg((new LogoutReq(FarleyUtils.getUserid(), MyApplication.newInstance().getSessionid())).conv2JsonString());
        ConnMngr.getInstance().setCallback(new ConnMngr.IConnMngrCallback() {
            @Override
            public void rsp(String resp) {
                Log.e("QUIT", resp);
            }

            @Override
            public void exc(String exception) {
                Log.e("QUIT", exception);
            }
        });
        ConnMngr.getInstance().sendMsg((new LogoutReq(FarleyUtils.getUserid(), MyApplication.newInstance().getSessionid())).conv2JsonString());
        FarleyUtils.setPassword(null);
        MyApplication.newInstance().setSessionid(0);
        startActivity(LoginActivity.class);
        finish();
    }

//    protected void monitor(View view) {
//        String monitorid = MyApplication.newInstance().getMonitorid();
//        Log.e("monitorid", monitorid);
//        if (TextUtils.isEmpty(monitorid) || monitorid.equals("null")) {
//            CommUtils.showToast("暂无监控");
//        } else {
//            Bundle bundle = new Bundle();
//            bundle.putString("monitorid", monitorid);
//            startActivity(MonitorActivity.class, bundle);
//        }
//        popupWindow.dismiss();
//    }

    public void setTitle(String title) {
        ((TextView)findViewById(R.id.tv_title)).setText(title);
    }

    public void setTitle(int titleid) {
        ((TextView)findViewById(R.id.tv_title)).setText(titleid);
    }

    /**
     * 初始化变量
     */
    protected abstract void initVariables();

    /**
     * 初始化UI
     */
    protected abstract void initViews();

    /**
     * 数据加载
     */
    protected abstract void loadData();
}
