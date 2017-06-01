package com.adrian.farley.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.adrian.farley.R;
import com.adrian.farley.application.MyApplication;
import com.adrian.farley.fragment.DevInfoFragment;
import com.adrian.farley.fragment.OverviewFragment;
import com.adrian.farley.fragment.RealTimeFragment;
import com.adrian.farley.fragment.SraftDBFragment;
import com.adrian.farley.fragment.WarningFragment;
import com.adrian.farley.tools.CommUtils;
import com.adrian.farley.tools.Constants;
import com.adrian.farley.tools.FarleyUtils;
import com.adrian.farley.tools.ftp.Ftp;
import com.adrian.farley.tools.ftp.FtpUtil;
import com.videogo.constant.IntentConsts;
import com.videogo.errorlayer.ErrorInfo;
import com.videogo.exception.BaseException;
import com.videogo.exception.ErrorCode;
import com.videogo.openapi.bean.EZCameraInfo;
import com.videogo.openapi.bean.EZDeviceInfo;
import com.videogo.util.ConnectionDetector;
import com.videogo.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class DetailInfoActivity extends BaseFragmentActivity implements RadioGroup.OnCheckedChangeListener {
    private static final int GET_CAMERA_LIST_MSG = 0;

    private static final int FRAGMENT_COUNT = 5;

    private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];

    private RadioGroup mTabRG;
    private View mIndicatorView;

    private int curPos = 0;
    private int stepW;
    private String id;
    private String ip;
    private int port;

    private FtpUtil ftpUtil;

    private Bundle bundle;

    private List<EZDeviceInfo> result;
    private MyHandler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initVariables() {
        bundle = getIntent().getExtras();
        id = bundle.getString("id");
        ip = bundle.getString("ip");
        port = bundle.getInt("port");
        initHandler();
    }

    @Override
    protected void initViews() {
        setContentView(R.layout.activity_detail_info);
        fragments[0] = new DevInfoFragment();
        fragments[1] = new OverviewFragment();
        fragments[2] = new RealTimeFragment();
        fragments[3] = new WarningFragment();
        fragments[4] = new SraftDBFragment();
        switchFragment(fragments[0], R.id.fragment_container);
        mIndicatorView = findViewById(R.id.indicator_bottom);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mIndicatorView.getLayoutParams();
        stepW = CommUtils.getWindowWidth(this) / FRAGMENT_COUNT;
        lp.width = stepW;
        mTabRG = (RadioGroup) findViewById(R.id.rg_tab);
        mTabRG.check(R.id.rb_dev_info);

        mTabRG.setOnCheckedChangeListener(this);
    }

    @Override
    protected void loadData() {
    }

    public String getId() {
        return id;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_dev_info:
                switchFragment(fragments[0], R.id.fragment_container);
                startAnim(0);
                break;
            case R.id.rb_overview:
                switchFragment(fragments[1], R.id.fragment_container);
                startAnim(1);
                break;
            case R.id.rb_real_time:
                switchFragment(fragments[2], R.id.fragment_container);
                startAnim(2);
                break;
            case R.id.rb_warning:
                switchFragment(fragments[3], R.id.fragment_container);
                startAnim(3);
                break;
            case R.id.rb_sraft_database:
                switchFragment(fragments[4], R.id.fragment_container);
                startAnim(4);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fragments[0] = null;
        fragments[1] = null;
        fragments[2] = null;
        fragments[3] = null;
        fragments[4] = null;
        fragments = null;
        if (ftpUtil != null) {
            ftpUtil.closeFtp();
        }
    }

    private void startAnim(int pos) {
        TranslateAnimation anim = new TranslateAnimation(curPos * stepW, pos * stepW, 0, 0);
        anim.setDuration(200);
        anim.setFillAfter(true);
        mIndicatorView.startAnimation(anim);
        curPos = pos;
    }

    protected void control(View view) {
        popupWindow.dismiss();
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        startActivity(ControlActivity.class, bundle);
    }

    protected void openFileExp(View view) {
        popupWindow.dismiss();
        Intent intent = new Intent(this, FileExpActivity.class);
        startActivityForResult(intent, 0);
    }

    private void upload(final String path) {
//        CommUtils.showToast("暂无上传功能");
        popupWindow.dismiss();
//        openFileBrowser();
        if (ftpUtil == null) {
            ftpUtil = new FtpUtil();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                ftpUtil.closeFtp();
                try {
                    boolean connected = ftpUtil.connectFtp(new Ftp(ip, port, FarleyUtils.getUserid(), FarleyUtils.getPassword(), ""));
                    if (connected) {
                        CommUtils.showToast("连接FTP成功");
                        ftpUtil.upload(new File(path));
                    } else {
                        CommUtils.showToast("FTP连接失败");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void openFileBrowser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("file/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "请选择一个要上传的文件"),
                1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {//是否选择，没选择就不会继续
//            Uri uri = data.getData();//得到uri，后面就是将uri转化成file的过程。
////            String[] proj = {MediaStore.Images.Media.DATA};
////            Cursor actualimagecursor = managedQuery(uri, proj, null, null, null);
////            int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
////            actualimagecursor.moveToFirst();
////            String img_path = actualimagecursor.getString(actual_image_column_index);
////            File file = new File(img_path);
//            File file = new File(uri.getPath());
//            Toast.makeText(this, file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            Toast.makeText(this, data.getStringExtra("file"), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Gets the corresponding path to a file from the given content:// URI
     *
     * @param selectedVideoUri The content:// URI to find the file path from
     * @param contentResolver  The content resolver to use to perform the query.
     * @return the file path as a string
     */
    public static String getFilePathFromContentUri(Uri selectedVideoUri,
                                                   ContentResolver contentResolver) {
        String filePath;
        String[] filePathColumn = {MediaStore.MediaColumns.DATA};

        Cursor cursor = contentResolver.query(selectedVideoUri, filePathColumn, null, null, null);
//      也可用下面的方法拿到cursor
//      Cursor cursor = this.context.managedQuery(selectedVideoUri, filePathColumn, null, null, null);

        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }

    private void getCameraList() {
        if (isFinishing()) {
            return;
        }
        if (!ConnectionDetector.isNetworkAvailable(this)) {
            return;
        }
        try {
            result = MyApplication.getOpenSDK().getDeviceList(0, 20);
            LogUtil.e("CAMERA_LIST", "camera count:" + result.size());
        } catch (BaseException e) {
            e.printStackTrace();
            ErrorInfo errorInfo = (ErrorInfo) e.getObject();
            LogUtil.debugLog("CameraList_EXC", errorInfo.toString());
        }
    }

    public void play() {
        if (result == null || result.size() == 0) {
            return;
        }
        EZDeviceInfo deviceInfo = result.get(0);
        if (deviceInfo.getCameraNum() <= 0 || deviceInfo.getCameraInfoList() == null || deviceInfo.getCameraInfoList().size() <= 0) {
            LogUtil.d("PLAY", "cameralist is null or cameralist size is 0");
            return;
        }
        if (deviceInfo.getCameraNum() == 1 && deviceInfo.getCameraInfoList() != null && deviceInfo.getCameraInfoList().size() == 1) {
            LogUtil.d("PLAY", "cameralist have one camera");
            final EZCameraInfo cameraInfo = getCameraInfoFromDevice(deviceInfo, 0);
            if (cameraInfo == null) {
                return;
            }

            Intent intent = new Intent(this, MonitorActivity.class);
            intent.putExtra(IntentConsts.EXTRA_CAMERA_INFO, cameraInfo);
            intent.putExtra(IntentConsts.EXTRA_DEVICE_INFO, deviceInfo);
            startActivity(intent);
        }
    }

    protected void monitor(View view) {
        play();
        popupWindow.dismiss();
    }

    /**
     * 通过ezdevice 得到其中通道信息
     *
     * @param deviceInfo
     * @return
     */
    public static EZCameraInfo getCameraInfoFromDevice(EZDeviceInfo deviceInfo, int camera_index) {
        if (deviceInfo == null) {
            return null;
        }
        if (deviceInfo.getCameraNum() > 0 && deviceInfo.getCameraInfoList() != null && deviceInfo.getCameraInfoList().size() > camera_index) {
            return deviceInfo.getCameraInfoList().get(camera_index);
        }
        return null;
    }

    private void initHandler() {
        HandlerThread ht = new HandlerThread("ht");
        ht.start();
        mHandler = new MyHandler(ht.getLooper());
        mHandler.sendEmptyMessage(GET_CAMERA_LIST_MSG);
    }

    class MyHandler extends Handler {
        public MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_CAMERA_LIST_MSG:
                    getCameraList();
                    break;
                default:
                    break;
            }
        }
    }
}
