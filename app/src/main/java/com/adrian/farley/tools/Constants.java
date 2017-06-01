package com.adrian.farley.tools;

import android.os.Environment;

import java.io.File;

public class Constants {
    public static final String SERVER_IP = "61.50.105.142";
    public static final int port = 10004;   //10007
    public static final String SAVE_PATH = Environment.getExternalStorageDirectory().toString() + File.separator + "farley/images/";

    public static final String VALUE = "value";
    public static final String UNIT = "unit";

    public static final String CAMERA_KEY = "29fc2b2c30e04102a863b805a22ead7f";
    public static final String CAMERA_SECRET = "ea8fe5ae4088397ccc2c7f7c02a309fb";
    public static String APP_PUSH_SECRETE = "a88256b1-65d4-4cb3-9ac4-8b08a56ad07d";
    public static String API_URL = "https://open.ys7.com";
    public static String WEB_URL = "https://auth.ys7.com";

    public static final long QUIT_DELAY = 500l;
}
