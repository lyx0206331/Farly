package com.adrian.farley.tools;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.adrian.farley.R;
import com.adrian.farley.application.MyApplication;
import com.adrian.farley.pojo.DevBaseInfo;
import com.adrian.farley.pojo.request.LoginReq;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

/**
 * Created by RanQing on 16-9-28 16:32.
 */

public class ConnUtils {
    public static String IP = Constants.SERVER_IP;
    public static int PORT = Constants.port;

    private Socket socket;
    private InputStream is;
    private OutputStream os;

    private ConnCallback callback;

//    private boolean isConnected = true;

    public ConnUtils(ConnCallback callback) {
        this.callback = callback;
    }

    public void sendMsg(final String reqStr) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.e("MSG", "reqStr:" + reqStr);
                    StringBuilder sb = new StringBuilder();
                    socket = new Socket(IP, PORT);
                    socket.setSoTimeout(30000);
                    os = socket.getOutputStream();
                    is = socket.getInputStream();
                    os.write(reqStr.getBytes());
                    os.flush();
                    String tmp = null;
                    int count = 0;
                    do {
                        byte[] msg = new byte[1024];
                        count += is.read(msg);
                        tmp = new String(msg);
//                        Log.e("REQ", count + tmp);
                        sb.append(tmp);
                    } while (!tmp.endsWith("\0")/*!tmp.substring(count - 1, count).equals("\0")*/);
                    String rsp = sb.substring(0, count - 2);
                    Log.e("CONN_RSP", count + rsp);
                    callback.response(rsp);
                    is.close();
                    os.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
//                    Log.e("IOERROR", e.getMessage());
                    callback.exception(MyApplication.newInstance().getString(R.string.server_exception));
                } catch (Exception e) {
//                    Log.e("ERROR", e.getMessage());
                    callback.exception(MyApplication.newInstance().getString(R.string.unknown_error));
                }

            }
        }).start();

    }

    public void sendMessage(String msgStr) {
//        if (!isConnected) {
//            return;
//        }
        try {
            msgStr += "\0";
//            Log.e("MSG", "ip:" + IP + " port:" + PORT + "time:" + System.currentTimeMillis() + " msg:" + msgStr);
            if (socket == null || socket.isClosed()) {
                socket = new Socket(IP, PORT);
                socket.setSoTimeout(30000);
//                Log.e("CREATE_SOCKET", "create socket!");
            }
            if (os == null) {
                os = socket.getOutputStream();
            }
            if (is == null) {
                is = socket.getInputStream();
            }
            os.write(msgStr.getBytes());
            os.flush();
            String tmp = null;
            int count = 0;
            boolean end = false;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            do {
                byte[] msg = new byte[512];
                int tmplen = is.read(msg);
                count += tmplen;
                baos.write(msg, 0, tmplen);
                if (msg[tmplen - 1] == 0) {
                    end = true;
                }
            } while (!end);
//            CommUtils.writeToFile(baos.toByteArray(), Environment.getExternalStorageDirectory().getAbsolutePath() + "/farley", "test");
            callback.response(new String(baos.toByteArray()).trim());
        } catch (IOException e) {
            e.printStackTrace();
//                    Log.e("IOERROR", e.getMessage());
            callback.exception(MyApplication.newInstance().getString(R.string.server_exception));
        } catch (Exception e) {
//                    Log.e("ERROR", e.getMessage());
            e.printStackTrace();
            callback.exception(MyApplication.newInstance().getString(R.string.unknown_error));
        }
    }

    public void closeConn() {
//        isConnected = false;
        try {
            if (socket != null && socket.isConnected()) {
                socket.close();
                socket = null;
            }
            if (os != null) {
                os.close();
                os = null;
            }
            if (is != null) {
                is.close();
                is = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * C文件结束符判断
     * @param cStr
     * @return
     */
    private boolean isEndCString(String cStr) {
        if (TextUtils.isEmpty(cStr)) {
            return true;
        }
        char[] chars = cStr.toCharArray();
        for (char c :
                chars) {
            Character ch = c;
            if (0 == ch.hashCode()) {
                return true;
            }
        }
        return false;
    }

    public interface ConnCallback {
        void response(String rsp);
        void exception(String exception);
    }
}
