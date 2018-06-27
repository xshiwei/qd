package com.qvd.smartswitch.utils;



import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.WindowManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class CommonUtils {
    /**
     * Check whether the input string is empty
     *
     * @param
     * @return boolean
     */
    public static boolean isEmptyString(String... obj) {
        for (String s : obj) {
            if (s == null || "".equals(s)) {
                return true;
            }
        }
        return false;
    }

    public static int getStringLength(int number) {
        return String.valueOf(number).length();
    }


    public static String getDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }

    public static String getPicDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }


    /**
     * 获取MAC地址
     * @param s
     * @return
     */
    public static String getMac(String s) {
        String[] str = s.split(":");
        String mac = "";
        for (int i = 0; i < str.length; i++) {
            mac += String.valueOf(Integer.parseInt(str[i], 16));
        }
        return mac;
    }


    /**
     * 定时将时间转换
     * @param hour
     * @param minute
     * @return
     */
    public static String getTiming(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.setTimeInMillis(calendar.getTimeInMillis() + 24 * 60 * 60 * 1000);
        }
        long diff = calendar.getTimeInMillis() - System.currentTimeMillis();
        long days = diff / (1000 * 60 * 60 * 24);
        long hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
        return hours + "小时" + minutes + "分钟后执行";
    }


    /**
     * 给图片上色
     * @param drawable
     * @param color
     * @return
     */
    public static Drawable tintDrawable(@NonNull Drawable drawable, int color) {
        Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(wrappedDrawable, color);
        return wrappedDrawable;
    }


    /**
     * 设置背景透明度
     * @param mContext
     * @param bgAlpha
     */
    public static void setBackgroundAlpha(Context mContext, float bgAlpha) {
        WindowManager.LayoutParams lp = ((Activity) mContext).getWindow().getAttributes();
        lp.alpha = bgAlpha;
        ((Activity) mContext).getWindow().setAttributes(lp);
    }

}
