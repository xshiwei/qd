package com.qvd.smartswitch.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by xushiwei on 2018/3/8.
 */

public class SharedPreferencesUtil {

    private static final String spFileName = "qevdo_sp";
    public static final String USER_ID = "user_id";
    public static final String IDENTIFIER = "identifier";
    public static final String PASSWORD = "password";
    public static final String IS_HOME = "is_home";
    public static final String KEY_APP_LANGUAGE = "app_language";

    /**
     * 获取是否第一次进入app
     *
     * @param context
     * @param strKey
     * @param strDefault
     * @return
     */
    public static Boolean getBoolean(Context context, String strKey,
                                     Boolean strDefault) {//strDefault	boolean: Value to return if this preference does not exist.
        SharedPreferences setPreferences = context.getSharedPreferences(
                spFileName, Context.MODE_PRIVATE);
        return setPreferences.getBoolean(strKey, strDefault);
    }

    /**
     * 写入是否第一次进入app
     *
     * @param context
     * @param strKey
     * @param strData
     */
    public static void putBoolean(Context context, String strKey,
                                  Boolean strData) {
        SharedPreferences activityPreferences = context.getSharedPreferences(
                spFileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = activityPreferences.edit();
        editor.putBoolean(strKey, strData);
        editor.apply();
    }

    /**
     * 写入String数据
     *
     * @param context
     * @param strKey
     * @param strData
     */
    public static void putString(Context context, String strKey, String strData) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(spFileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(strKey, strData);
        editor.apply();
    }

    /**
     * 获取String数据
     *
     * @param context
     * @param strKey
     * @return
     */
    public static String getString(Context context, String strKey) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(spFileName, Context.MODE_PRIVATE);
        return sharedPreferences.getString(strKey, "");
    }

    public static String getString(Context context, String strKey, String s) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(spFileName, Context.MODE_PRIVATE);
        return sharedPreferences.getString(strKey, s);
    }
}
