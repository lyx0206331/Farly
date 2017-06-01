package com.adrian.farley.tools;

import android.os.Environment;

import com.adrian.farley.application.MyApplication;
import com.videogo.util.LogUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Created by adrian on 16-11-9.
 */
public class ConnMngr implements ConnUtils.ConnCallback {
    private static ConnMngr instance;
    private ConnUtils utils;
    private IConnMngrCallback callback;

    public static ConnMngr getInstance() {
        if (instance == null) {
            instance = new ConnMngr();
        }
        return instance;
    }

    private ConnMngr() {
        utils = new ConnUtils(this);
    }

    public void setIpPort(String ip, int port) {
        if (FarleyUtils.isRemote()) {
            ConnUtils.IP = Constants.SERVER_IP;
            ConnUtils.PORT = Constants.port;
        } else {
            ConnUtils.IP = ip;
            ConnUtils.PORT = port;
        }
    }

    public void sendMsg(final String msg) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                utils.sendMessage(msg);
            }
        }).start();
    }

    public void closeConn() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                utils.closeConn();
                utils = null;
                instance = null;
                LogUtil.e("CLOSE_CONN", "close conn");
            }
        }).start();
    }

    public void setCallback(IConnMngrCallback callback) {
        this.callback = callback;
    }

    public String getAssetFile(String fileName) {
        InputStream is = null;
        try {
            is = MyApplication.newInstance().getAssets().open(fileName);
            int len = is.available();
//            LogUtil.e("ASSET_LEN", len + "");
            StringBuilder sb = new StringBuilder();
            byte[] buff = new byte[512];
            byte[] cotent = new byte[len];
            int count = 0;
            int s = 0;
            while ((count = is.read(buff)) != -1) {
//                sb.append(new String(buff, 0, count));
                System.arraycopy(buff, 0, cotent, s, count);
                s += count;
//                LogUtil.e("ASSET_LEN", s + "");
            }
            is.close();
            return new String(cotent);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void response(String rsp) {
//        LogUtil.e("CONNMNGR_RSP", rsp);
        if (callback != null) {
            callback.rsp(rsp);
        }
    }

    @Override
    public void exception(String exception) {
//        LogUtil.e("ONNMNGR_EXC", exception);
        if (callback != null) {
            callback.exc(exception);
        }
    }

    public interface IConnMngrCallback{
        void rsp(String resp);
        void exc(String exception);
    }
}
