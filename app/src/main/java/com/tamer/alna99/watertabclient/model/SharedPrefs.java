package com.tamer.alna99.watertabclient.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import static android.content.Context.MODE_PRIVATE;

public class SharedPrefs {

    private final static String MY_PREFS_NAME = "USER_INFO";
    private final static String JSON_ARRAY = "jsonArray";

    public static void setUserInfo(Context context, String id, String name, String email, String phone) {
        SharedPreferences.Editor editor = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("id", id);
        editor.putString("name", name);
        editor.putString("email", email);
        editor.putString("phone", phone);
        editor.apply();
    }

    public static void clearData(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
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

    public static void saveOrders(Context context, JsonArray jsonArray) {
        SharedPreferences.Editor editor = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString(JSON_ARRAY, jsonArray.toString());
        editor.apply();
    }

    public static JsonArray getOrders(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String jsonArray = sharedPreferences.getString(JSON_ARRAY, "");
        return new JsonParser().parse(jsonArray).getAsJsonArray();
    }

}
