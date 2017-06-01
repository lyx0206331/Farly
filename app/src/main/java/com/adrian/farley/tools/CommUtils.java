package com.adrian.farley.tools;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.adrian.farley.application.MyApplication;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class CommUtils {

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * for API 23+
     *
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static boolean verifyStoragePermissions(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        } else {
            return true;
        }
        return false;
    }

    public static void showToast(Context ctx, String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(Context ctx, int msgId) {
        Toast.makeText(ctx, msgId, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(String msg) {
        Toast.makeText(MyApplication.newInstance(), msg, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(int msgId) {
        Toast.makeText(MyApplication.newInstance(), msgId, Toast.LENGTH_SHORT).show();
    }

    public static String getVersionName(Context ctx) {
        PackageManager pm = ctx.getPackageManager();
        try {
            PackageInfo pInfo = pm.getPackageInfo(ctx.getPackageName(), PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
            return "v" + pInfo.versionName;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static void requestPicList() {

        try {
            byte[] cmd = {(byte) 0xA5, 0x5A, 0x00, 0x00, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x5A, (byte) 0xA5, 0x00, 0x00};
            Socket socket = new Socket("192.168.1.110", 7986);
            System.out.println("Connected to server ..... Sending echo String");

            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            // for(int i = 0 ; i < cmd.length; i++){
            // Sending encode string to server
            out.write(cmd);
            byte[] pre = new byte[12];
            byte[] lengthB = new byte[4];
            int len = 0;
            if (in.read(pre, 0, 12) != -1) {
                System.arraycopy(pre, 8, lengthB, 0, 4);
                len = bytes2int(lengthB);
            }
            if (len > 0) {
                byte[] content = new byte[len];
                if (in.read(content, 12, len) != -1) {
                    byte[] byte4num = new byte[4];
                    System.arraycopy(content, 0, byte4num, 0, 4);
                    int fileNum = bytes2int(byte4num);
                    byte[] byte4name = new byte[content.length - 4];
                    System.arraycopy(content, 4, byte4name, 0, content.length - 4);
                    String[] nameArray = bytes2string(byte4name).split("#");
                    for (String name : nameArray) {
                        System.out.println("pic name--->" + name);
                    }
                }
            }

            // }
            Thread.sleep(1000);

            socket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void requestPicListTest() {

        try {
            byte[] cmd = {(byte) 0xA5, 0x5A, 0x00, 0x00, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x5A, (byte) 0xA5, 0x00, 0x00};
            Socket socket = new Socket("192.168.1.110", 7986);
            System.out.println("Connected to server ..... Sending echo String");

            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            // for(int i = 0 ; i < cmd.length; i++){
            // Sending encode string to server
            out.write(cmd);
            byte[] pre = new byte[12];
            byte[] lengthB = new byte[4];
            int len = 0;
            if (in.read(pre) != -1) {
                // System.arraycopy(pre, 8, lengthB, 0, 4);
                // len = bytes2int(lengthB);
                System.out.println("response data : " + pre[0]);
            }
            // if (len > 0) {
            // byte[] content = new byte[len];
            // if (in.read(content, 12, len) != -1) {
            // byte[] byte4num = new byte[4];
            // System.arraycopy(content, 0, byte4num, 0, 4);
            // int fileNum = bytes2int(byte4num);
            // byte[] byte4name = new byte[content.length - 4];
            // System.arraycopy(content, 4, byte4name, 0, content.length - 4);
            // String[] nameArray = bytes2string(byte4name).split("#");
            // for (String name : nameArray) {
            // System.out.println("pic name--->" + name);
            // }
            // }
            // }
            //

            // }
            Thread.sleep(1000);

            socket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static int bytes2int(byte[] b) {
        int a = 0;
        for (int i = 0; i < b.length; i++) {
            a += b[i];
        }
        return a;
    }

    public static String bytes2string(byte[] b) {
        String result = null;
        String str = new String(b);
        result = String.copyValueOf(str.toCharArray());
        return result;
    }

    public static String unicode2utf8(String unicode) {
        try {
            byte[] utf8 = unicode.getBytes("UTF-8");
            return new String(utf8, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static void store2sdcard(Bitmap bitmap, String name) {
        File file = new File(Constants.SAVE_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        File imageFile = new File(file, name);
        try {
            imageFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void copyDrawable2sdcard(Context ctx, int drawableId, String name) {
        File f = new File(Constants.SAVE_PATH + name);
        if (!f.exists()) {
            Bitmap bmp = BitmapFactory.decodeResource(ctx.getResources(), drawableId);
            store2sdcard(bmp, name);
        }
    }

    public static String[] getFilesArrayByType(String dirPath, final String type) {
        File dir = new File(dirPath);
        if (!dir.exists() || !dir.isDirectory()) {
            return null;
        }
        FilenameFilter filter = new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                // TODO Auto-generated method stub
                return name.endsWith(type);
            }
        };
        String[] files = dir.list(filter);
        for (int i = 0; i < files.length; i++) {
            files[i] = dirPath + files[i];
        }
        return files;
    }

    public static List<Bitmap> getBitmapList(String[] picNames) {
        List<Bitmap> list = new ArrayList<>();
        for (String name : picNames) {
            Bitmap bmp = BitmapFactory.decodeFile(name);
            list.add(bmp);
        }
        return list;
    }

    /**
     * 获取bitmap图片，有损压缩，防止内存溢出
     *
     * @param path
     * @return
     */
    public static Bitmap getBitmap(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = options.outWidth / 200;
        options.inPreferredConfig = Bitmap.Config.ARGB_4444;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.outWidth /= 4;
        options.outHeight /= 4;
        options.inJustDecodeBounds = false;
        Bitmap bmp = BitmapFactory.decodeFile(path, options);
        return bmp;
    }

    /**
     * 获取屏幕信息
     *
     * @param act
     * @return
     */
    public static DisplayMetrics getScreenInfo(Activity act) {
        DisplayMetrics dm = new DisplayMetrics();
        act.getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm;
    }

    /**
     * 获取网络状态
     *
     * @param ctx
     * @return -1:无网络;0:移动网络;1:wifi网络;2:以太网
     */
    public static int getNetworkStatus(Context ctx) {
        int status = -1;
        ConnectivityManager manager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        // NetworkInfo mobileInfo =
        // manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        // NetworkInfo wifiInfo =
        // manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo netInfo = manager.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isAvailable()) {
            if (netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                ///// WiFi网络
                status = 1;
            } else if (netInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {
                ///// 有线网络
                status = 2;
            } else if (netInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                ///////// 3g网络
                status = 0;
            }
        } else {
            status = -1;
        }
        return status;
    }

    /**
     * 获取wifi状态
     *
     * @param ctx
     * @return
     */
    public static boolean getWifiStatus(Context ctx) {
        WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int state = wifiInfo.getNetworkId();
        return state != -1 ? true : false;
    }

    /**
     * 获取wifi名称（SSID）
     *
     * @param ctx
     * @return
     */
    public static String getWifiName(Context ctx) {
        WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        Log.d("wifiInfo", wifiInfo.toString());
        Log.d("SSID", wifiInfo.getSSID());
        return wifiInfo.getSSID();
    }

    /**
     * 根据wifi获取ip地址
     *
     * @param ctx
     * @return
     */
    public static String getIp4Wifi(Context ctx) {
        // 获取wifi服务
        WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        // 判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String ip = (ipAddress & 0xFF) + "." + ((ipAddress >> 8) & 0xFF) + "." + ((ipAddress >> 16) & 0xFF) + "."
                + (ipAddress >> 24 & 0xFF);
        // Log.e("Test", "wifi ip---->" + ip);
        return ip;
    }

    /**
     * 获取屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getWindowWidth(Context context) {
        WindowManager wm = (WindowManager) (context.getSystemService(Context.WINDOW_SERVICE));
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    public static String readFromFile(String path) {
        FileInputStream fis = null;
        StringBuilder sb = new StringBuilder();
        try {
            fis = new FileInputStream(new File(path));
            byte[] bytes = new byte[1024];
            int count = 0;
            while ((count = fis.read(bytes)) != -1) {
                sb.append(new String(bytes, 0, count));
            }
            fis.close();
            String str = sb.toString();
            Log.e("localfile", str);
            return str;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void writeToFile(byte[] bytes, String dir, String name) {
        File parent = new File(dir);
        if (!parent.exists()) {
            parent.mkdirs();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FileOutputStream fos = null;
        try {
            File desFile = new File(parent, name);
            if (!desFile.exists()) {
                desFile.createNewFile();
            }
            fos = new FileOutputStream(desFile);
//            baos.write(bytes);
//            baos.writeTo(fos);
//            baos.flush();
            fos.write(bytes);
            fos.flush();
            fos.close();
//            baos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
