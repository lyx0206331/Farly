package com.adrian.farley.tools;

import android.content.Context;
import android.content.SharedPreferences;

import com.adrian.farley.application.MyApplication;

/**
 * Created by RanQing on 16-10-13 09:52.
 */

public class FarleyUtils {
    public static SharedPreferences getPref() {
        SharedPreferences shared_pref = MyApplication.newInstance().getSharedPreferences("shared_pref", Context.MODE_PRIVATE);
        return shared_pref;
    }

    public static void setUseridPwd(String userid, String pwd) {
        SharedPreferences preferences = getPref();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userid", userid);
        editor.putString("password", pwd);
        editor.commit();
    }

    public static void setPassword(String password) {
        SharedPreferences preferences = getPref();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("password", password);
        editor.commit();
    }

    public static String getUserid() {
        SharedPreferences preferences = getPref();
        return preferences.getString("userid", "");
    }

    public static String getPassword() {
        SharedPreferences preferences = getPref();
        return preferences.getString("password", "");
    }

    public static void setIfRemote(boolean remote) {
        SharedPreferences preferences = getPref();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("remote", remote);
        editor.commit();
    }

    public static boolean isRemote() {
        SharedPreferences preferences = getPref();
        return preferences.getBoolean("remote", true);
    }
}
