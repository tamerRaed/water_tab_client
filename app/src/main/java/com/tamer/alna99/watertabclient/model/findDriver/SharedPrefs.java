package com.tamer.alna99.watertabclient.model.findDriver;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class SharedPrefs {

    private final static String MY_PREFS_NAME = "SETTING";

    public static void setUserInfo(Context context, String id) {
        SharedPreferences.Editor editor = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("id", id);
        editor.apply();
    }

    public static String getUserInfo(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        return sharedPreferences.getString("id", "-1");
    }

}
