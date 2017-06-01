package com.adrian.farley.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.adrian.farley.R;
import com.adrian.farley.application.MyApplication;
import com.adrian.farley.pojo.DevBaseInfo;
import com.adrian.farley.pojo.LanDev;
import com.adrian.farley.pojo.request.LoginReq;
import com.adrian.farley.pojo.response.LoginRes;
import com.adrian.farley.tools.CommUtils;
import com.adrian.farley.tools.ConnMngr;
import com.adrian.farley.tools.search_dev.DevSearchClientUtil;
import com.adrian.farley.tools.FarleyUtils;
import com.adrian.farley.tools.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends BaseActivity implements ConnMngr.IConnMngrCallback, DevSearchClientUtil.IResultCallback {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    private RadioGroup mNetworkTypeRG;
    // UI references.
    private AutoCompleteTextView mAccountView;
    private EditText mPasswordView;
    private View mLoginFormView;

    private String account;
    private String password;
    DevSearchClientUtil searchClientUtil;

    private List<DevBaseInfo> baseInfos;
    private List<LanDev> lanDevs;
    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initVariables() {
    }

    @Override
    protected void initViews() {
        setContentView(R.layout.activity_login);
        mNetworkTypeRG = (RadioGroup) findViewById(R.id.rg_network);
        if (FarleyUtils.isRemote()) {
            mNetworkTypeRG.check(R.id.rb_remote);
        } else {
            mNetworkTypeRG.check(R.id.rb_lan);
        }
        mNetworkTypeRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                ConnMngr.getInstance().closeConn();
                switch (checkedId) {
                    case R.id.rb_remote:
                        FarleyUtils.setIfRemote(true);
                        break;
                    case R.id.rb_lan:
                        FarleyUtils.setIfRemote(false);
                        break;
                }
            }
        });
        // Set up the login form.
        mAccountView = (AutoCompleteTextView) findViewById(R.id.account);
//        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);

    }

    @Override
    protected void loadData() {
        if (!TextUtils.isEmpty(FarleyUtils.getUserid())) {
            mAccountView.setText(FarleyUtils.getUserid());
            mPasswordView.requestFocus();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        searchClientUtil = null;
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        mAccountView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        account = mAccountView.getText().toString();
        password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(account)) {
            mAccountView.setError(getString(R.string.error_field_required));
            focusView = mAccountView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
//            showProgress();
            if (FarleyUtils.isRemote()) {
                login();
            } else {
                if (searchClientUtil == null) {
                    searchClientUtil = new DevSearchClientUtil(this, this);
                }
                searchClientUtil.searchDev();
            }
        }
    }

    private void login() {
        ConnMngr.getInstance().setCallback(this);
        ConnMngr.getInstance().sendMsg((new LoginReq(account, password)).conv2JsonString());
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    @Override
    public void rsp(final String resp) {
        if (!FarleyUtils.isRemote()) {//局域网登录
            if (index < lanDevs.size()) {
                LoginRes loginRes = new LoginRes(resp);
                if (loginRes.getType().equals("logon_ret")) {
                    if (loginRes.getStatus() == 0) {
                        DevBaseInfo info = baseInfos.get(index - 1);
                        info.setLine("on");
                    }
                }
                lanLogin();
            } else {    //局域网内搜索到的最后一个设备登录成功
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LoginRes loginRes = new LoginRes(resp);
                        if (loginRes.getType().equals("logon_ret")) {
                            if (loginRes.getStatus() == 0) {
                                DevBaseInfo info = baseInfos.get(index - 1);
                                info.setLine("on");
                                MyApplication.newInstance().setDevBaseInfos(baseInfos);
                            }
                        }
                        if (MyApplication.newInstance().isDevNull()) {
                            CommUtils.showToast(R.string.lan_none_dev);
                            showProgress(false);
                        } else {
                            FarleyUtils.setUseridPwd(account, password);
                            CommUtils.showToast(R.string.login_success);
                            showProgress(false);
                            startActivity(MainActivity.class);
                            finish();
                        }
                    }
                });
            }

        } else {    //远程登录
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LoginRes loginRes = new LoginRes(resp);
                    if (loginRes.getType().equals("logon_ret")) {
                        if (loginRes.getStatus() == 0) {
                            MyApplication.newInstance().setSessionid(loginRes.getSessionid());
                            FarleyUtils.setUseridPwd(account, password);
                            CommUtils.showToast(R.string.login_success);
                            showProgress(false);
                            startActivity(MainActivity.class);
                            finish();
                        } else {
                            showProgress(false);
                            CommUtils.showToast(R.string.login_failed);
                        }
                    }
                }
            });
        }
    }

    private void lanLogin() {
        LanDev dev0 = lanDevs.get(index++);
        ConnMngr.getInstance().setIpPort(dev0.getIp(), dev0.getPort());
        login();
    }

    @Override
    public void exc(final String exception) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CommUtils.showToast(exception);
                showProgress(false);
            }
        });
    }

    @Override
    public void getLanDevs(List<LanDev> devs) {
        if (devs != null && devs.size() > 0) {
            index = 0;
            lanDevs = devs;
            baseInfos = new ArrayList();
            for (LanDev dev :
                    devs) {
                LogUtil.e("DEV", dev.toString());
                DevBaseInfo info = new DevBaseInfo();
                info.setId(dev.getId());
                info.setLanDev(dev);
                baseInfos.add(info);
//                ConnMngr.getInstance().setIpPort(dev.getIp(), dev.getPort());
//                login();
            }
            lanLogin();
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    CommUtils.showToast(R.string.lan_none_dev);
                    showProgress(false);
                }
            });
        }
    }
}

