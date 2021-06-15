package com.tamer.alna99.watertabclient.model;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class SharedPrefs {

    private final static String MY_PREFS_NAME = "USER_INFO";

    public static void setUserInfo(Context context, String id, String name, String email, String phone) {
        SharedPreferences.Editor editor = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("id", id);
        editor.putString("name", name);
        editor.putString("email", email);
        editor.putString("phone", phone);
        editor.apply();
    }

    public static String getUserId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        return sharedPreferences.getString("id", "-1");
    }

    public static String getUserName(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        return sharedPreferences.getString("name", "-1");
    }

    public static String getUserEmail(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        return sharedPreferences.getString("email", "-1");
    }

    public static String getUserPhone(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        return sharedPreferences.getString("phone", "-1");
    }

    public static String getUserPassword(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        return sharedPreferences.getString("password", "-1");
    }

}
