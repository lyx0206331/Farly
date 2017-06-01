package com.adrian.farley.tools.search_dev;

import android.content.Context;
import android.text.TextUtils;

import com.adrian.farley.application.MyApplication;
import com.adrian.farley.pojo.LanDev;
import com.adrian.farley.pojo.request.BCScanReq;
import com.adrian.farley.tools.FarleyUtils;
import com.adrian.farley.tools.LogUtil;
import com.adrian.farley.tools.NetTool;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by adrian on 16-4-3.
 */
public class DevSearchClientUtil {

    private static final int LOCAL_PORT = 7789;

    private DatagramSocket detectSocket = null;
    private DatagramPacket outPacket = null;

    private ReceiveThread receiveThread;
    private SendThread sendThread;

    private NetTool netTool;
    private Context ctx;
    private List<LanDev> devs;

    private IResultCallback callback;

    private boolean canReceived = true;

    public DevSearchClientUtil(Context context, IResultCallback callback) {
        ctx = context;
        this.callback = callback;
        devs = new ArrayList<>();
    }

    public void searchDev() {
        if (sendThread != null) {
            sendThread.cancel();
            sendThread = null;
            sendThread = new SendThread();
            sendThread.start();
        } else {
            sendThread = new SendThread();
            sendThread.start();
        }
        if (receiveThread == null) {
            canReceived = true;
            receiveThread = new ReceiveThread();
            receiveThread.start();
            stopDelay(5000l);
        } else {
            canReceived = true;
            receiveThread.cancel();
            receiveThread = null;
            receiveThread = new ReceiveThread();
            receiveThread.start();
            stopDelay(5000l);
        }
        if (netTool == null) {
            netTool = new NetTool(ctx);
        }
    }

    private void stopDelay(long delay) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                LogUtil.e("SEARCH", "stop search");
                canReceived = false;
                if (callback != null) {
                    callback.getLanDevs(devs);
                }
                sendThread.cancel();
                sendThread = null;
                receiveThread.cancel();
                receiveThread = null;
            }
        };
        Timer timer = new Timer(true);
        timer.schedule(task, delay);
    }

    public void stopReceive() {
        canReceived = false;
    }

    class SendThread extends Thread {
        @Override
        public void run() {
            LogUtil.e("SEARCH", "Send thread started.");
            try {
                if (detectSocket == null) {
//                    detectSocket = new DatagramSocket(LOCAL_PORT);
                    detectSocket = new DatagramSocket(null);
                    detectSocket.setReuseAddress(true);
                    detectSocket.bind(new InetSocketAddress(LOCAL_PORT));
                }

                int packetPort = 7788;

                // Broadcast address
                InetAddress hostAddress = InetAddress.getByName("255.255.255.255");
                String outMessage = new BCScanReq(FarleyUtils.getUserid(), MyApplication.newInstance().getSessionid()).conv2JsonString();
                byte[] buf = outMessage.getBytes();
                LogUtil.e("SEARCH", "Send " + outMessage + " to " + hostAddress);
                // Send packet to hostAddress:9999, server that listen
                // 9999 would reply this packet
                outPacket = new DatagramPacket(buf,
                        buf.length, hostAddress, packetPort);
                detectSocket.send(outPacket);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (outPacket != null) {
                    outPacket = null;
                }
//                if (detectSocket != null) {
//                    detectSocket.close();
//                    detectSocket = null;
//                }
            }
        }

        public void cancel() {
            if (outPacket != null) {
                outPacket = null;
            }
//            if (detectSocket != null) {
//                detectSocket.close();
//                detectSocket = null;
//            }
        }
    }

    class ReceiveThread extends Thread {
        byte[] buf = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        DatagramSocket socket;
        @Override
        public void run() {

            LogUtil.e("SEARCH", "Receive thread started.");
            while (canReceived) {
                try {
                    if (socket == null) {
//                        socket = new DatagramSocket(10435);
                        socket = new DatagramSocket(null);
                        socket.setReuseAddress(true);
                        socket.bind(new InetSocketAddress(10435));
                    }
                    detectSocket.receive(packet);
                    if (packet != null && !TextUtils.isEmpty(packet.getAddress().getHostAddress())) {
                        String rcvd = "Received from " + packet.getSocketAddress() + ", Data="
                                + new String(packet.getData(), 0, packet.getLength());
                        LogUtil.e("SEARCH", rcvd);
//                        netTool.sendMsg(packet.getAddress().getHostAddress(), "scan" + netTool.getLocAddress() + " ( " + android.os.Build.MODEL + " ) ");
                        LanDev dev = parseBC(new String(packet.getData(), 0, packet.getLength()));
                        if (dev != null) {
                            devs.add(dev);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public void cancel() {
            if (packet != null) {
                packet = null;
            }
        }

    }

    private LanDev parseBC(String json) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        try {
            JSONObject obj = new JSONObject(json);
            if (obj.optInt("status") != 0) {
                return null;
            }
            LanDev lanDev = new LanDev(obj.optString("ip"), obj.optInt("port"), obj.optString("id"));
            return lanDev;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    public interface IResultCallback {
        void getLanDevs(List<LanDev> devs);
    }
}
