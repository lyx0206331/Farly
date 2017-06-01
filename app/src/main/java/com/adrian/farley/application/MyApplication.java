package com.adrian.farley.application;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import com.adrian.farley.pojo.DevBaseInfo;
import com.adrian.farley.tools.Constants;
import com.videogo.errorlayer.ErrorInfo;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.openapi.EZOpenSDKListener;
import com.videogo.openapi.EzvizAPI;
import com.videogo.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by RanQing on 16-9-21 10:58.
 */

public class MyApplication extends Application {

    private static MyApplication newInstance;

    private int sessionid;
    private String monitorid = "652869920";

    private String acToken;

    private List<DevBaseInfo> devBaseInfos = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        newInstance = this;

        /**********国内版本初始化EZOpenSDK**************/
        {
            /**
             * sdk日志开关，正式发布需要去掉
             */
            EZOpenSDK.showSDKLog(true);

            /**
             * 设置是否支持P2P取流,详见api
             */
            EZOpenSDK.enableP2P(true);

            /**
             * APP_KEY请替换成自己申请的
             */
            EZOpenSDK.initLib(this, Constants.CAMERA_KEY, "");
        }

        /**********海外版本初始化EZGlobalSDK**************/
        {
            /**
             * sdk日志开关，正式发布需要去掉
             */
//        EZGlobalSDK.showSDKLog(true);

            /**
             * 设置是否支持P2P取流,详见api
             */
//        EZGlobalSDK.enableP2P(true);
            /**
             * APP_KEY请替换成自己申请的
             */
//        EZGlobalSDK.initLib(this, APP_KEY, "");
        }

        EzvizAPI.getInstance().setServerUrl(Constants.API_URL, Constants.WEB_URL);

        /**
         * 如果需要推送服务，需要再初始化EZOpenSDK后，调用以下方法初始化推送服务
         * push_secret_key 推送服务secret_key需要单独申请
         */
        {
            EZOpenSDK.getInstance().initPushService(this.getApplicationContext(), Constants.APP_PUSH_SECRETE, pushServerListener);
        }


        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
                throwable.printStackTrace();
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                requestAcToken();
            }
        }).start();
    }

    public static EZOpenSDK getOpenSDK() {
        return EZOpenSDK.getInstance();
    }

//    public static EZGlobalSDK getOpenSDK() {
//        return EZGlobalSDK.getInstance();
//    }


    public EZOpenSDKListener.EZPushServerListener pushServerListener = new EZOpenSDKListener.EZPushServerListener() {
        @Override
        public void onStartPushServerSuccess(boolean bSuccess, ErrorInfo errorInfo) {
            LogUtil.debugLog("PUSH", "start push server " + bSuccess);
        }
    };

    public static MyApplication newInstance() {
        return newInstance;
    }

    public int getSessionid() {
        return sessionid;
    }

    public void setSessionid(int sessionid) {
        this.sessionid = sessionid;
    }

    public String getMonitorid() {
        return monitorid;
    }

    public void setMonitorid(String monitorid) {
        this.monitorid = monitorid;
    }

    public String getAcToken() {
        return acToken;
    }

    public void setAcToken(String acToken) {
        this.acToken = acToken;
    }

    public List<DevBaseInfo> getDevBaseInfos() {
        List<DevBaseInfo> devs = new ArrayList<>();
        if (devBaseInfos != null && devBaseInfos.size() > 0) {
            for (DevBaseInfo info :
                    devBaseInfos) {
                if (info.isOnline()) {
                    devs.add(info);
                }
            }
        }
        return devs;
    }

    public void setDevBaseInfos(List<DevBaseInfo> devBaseInfos) {
        this.devBaseInfos = devBaseInfos;
    }

    public boolean isDevNull() {
        if (getDevBaseInfos().size() <= 0) {
            return true;
        }
        return false;
    }

    private void requestAcToken() {
        String urlStr = "https://open.ys7.com/api/lapp/token/get";
        URL url = null;
        try {
            url = new URL(urlStr);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setDoInput(true);
            urlConn.setDoOutput(true);
            urlConn.setRequestMethod("POST");
            urlConn.setUseCaches(false);
            urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConn.setRequestProperty("Charset", "utf-8");

            urlConn.connect();

            DataOutputStream dos = new DataOutputStream(urlConn.getOutputStream());
            dos.writeBytes("appKey=" + Constants.CAMERA_KEY + "&appSecret=" + Constants.CAMERA_SECRET);
            dos.flush();
            dos.close();

            BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
            String result = "";
            String readLine = null;
            while ((readLine = br.readLine()) != null) {
                result += readLine;
            }
            br.close();
            urlConn.disconnect();

            Log.e("accessTocken", result);

            AcToken token = new AcToken();
            token.parse(result);
            acToken = token.accessToken;
            getOpenSDK().setAccessToken(acToken);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class AcToken {
        private String accessToken;
        private long expireTime;
        private int code;
        private String msg;

        public void parse(String jsonStr) {
            if (TextUtils.isEmpty(jsonStr)) {
                return;
            }
            try {
                JSONObject json = new JSONObject(jsonStr);
                code = json.optInt("code");
                String msg = json.optString("msg");
                if (code == 200) {
                    JSONObject acJson = json.optJSONObject("data");
                    accessToken = acJson.optString("accessToken");
                    expireTime = acJson.optLong("expireTime");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
