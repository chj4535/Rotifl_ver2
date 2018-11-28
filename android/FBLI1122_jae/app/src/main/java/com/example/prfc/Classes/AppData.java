package com.example.prfc.Classes;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

public class AppData {

    /**
     * Save keys
     */
    public enum Key {
        MessageList
    }

    private static String key;//groupid as key

    public AppData(String key){
        this.key = key;
    }

    /**
     * Save object data as json
     * @param context application context
     * @param key save key
     * @param object save object
     */
    public static void putObjectData(Context context, String key, Object object) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson =new Gson();
        String jsonData = gson.toJson(object);
        editor.putString(key, jsonData);
        editor.apply();
    }

    /**
     * Load object data
     * @param context application context
     * @param key saved key
     * @param classOfT saved type
     * @return
     */
    public static Object getObjectData(Context context, String key, Class classOfT) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonData = sharedPreferences.getString(key, "");
        if (jsonData.equals("")) {
            return null;
        } else {
            return gson.fromJson(jsonData, classOfT);
        }
    }

    /**
     * Save Message list
     * @param context application context
     * @param messages receive and sent messages
     *
     */
    public static void putMessageList(Context context, MessageList messages) {
        putObjectData(context, key, messages);
    }

    /**
     * Load saved messages
     * @param context application context
     * @return saved messages
     * 여기 고쳤다!!!!!!!!
     */
    public static MessageList getMessageList(Context context) {
        return (MessageList) getObjectData(context, key, MessageList.class);
    }

    public static void reset(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }

    public void setKey(String key){
        this.key = key;
    }

}

