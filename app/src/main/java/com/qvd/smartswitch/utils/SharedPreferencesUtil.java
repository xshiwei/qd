package com.qvd.smartswitch.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by xushiwei on 2018/3/8.
 */

public class SharedPreferencesUtil {

    private static final String spFileName = "qevdo_sp";
    public static final String FIRST_OPEN = "first_open";
    public static final String TIMG_TIME = "timg_time";

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
        editor.commit();
    }

    /**
     * 提交保存定时的信息
     *
     * @param context
     * @param strKey
     * @param strTime
     */
    public static void putTimgTime(Context context, String strKey, String strTime) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(spFileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(strKey, strTime);
        editor.commit();
    }


    /**
     * 获取保存的定时的消息
     *
     * @param context
     * @param strKey
     * @param strDefault
     * @return
     */
    public static String getTimgTime(Context context, String strKey, String strDefault) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(spFileName, Context.MODE_PRIVATE);
        return sharedPreferences.getString(strKey, strDefault);
    }
}
