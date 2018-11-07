package com.qvd.smartswitch.widget;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by 赵晨璞 on 2016/8/11.
 */
public class MyToast {

    private Toast toast;
    private LinearLayout toastView;

    /**
     * 修改原布局的Toast
     */
    public MyToast() {

    }

    /**
     * 完全自定义布局Toast
     *
     * @param context
     * @param view
     */
    public MyToast(Context context, View view, int duration) {
        toast = new Toast(context);
        toast.setView(view);
        toast.setDuration(duration);
    }

    /**
     * 向Toast中添加自定义view
     *
     * @param view
     * @param postion
     * @return
     */
    public MyToast addView(View view, int postion) {
        toastView = (LinearLayout) toast.getView();
        toastView.addView(view, postion);
        return this;
    }

    /**
     * 设置Toast字体及背景颜色
     *
     * @param messageColor
     * @param backgroundColor
     * @return
     */
    public MyToast setToastColor(int messageColor, int backgroundColor) {
        View view = toast.getView();
        if (view != null) {
            TextView message = view.findViewById(android.R.id.message);
            message.setBackgroundColor(backgroundColor);
            message.setTextColor(messageColor);
        }
        return this;
    }


    /**
     * 设置Toast字体及背景
     *
     * @param messageColor
     * @param background
     * @return
     */
    public MyToast setToastBackground(int messageColor, int background) {
        View view = toast.getView();
        view.setBackgroundColor(Color.TRANSPARENT);
        TextView message = view.findViewById(android.R.id.message);
        message.setBackgroundResource(background);
        message.setTextColor(messageColor);
        message.setPadding(30,30,30,30);
        return this;
    }

    /**
     * 短时间显示Toast
     */
    public MyToast Short(Context context, CharSequence message) {
        if (toast == null || (toastView != null && toastView.getChildCount() > 1)) {
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toastView = null;
        } else {
            toast.setText(message);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        return this;
    }

    /**
     * 短时间显示Toast
     */
    public MyToast Short(Context context, int message) {
        if (toast == null || (toastView != null && toastView.getChildCount() > 1)) {
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toastView = null;
        } else {
            toast.setText(message);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        return this;
    }

    /**
     * 长时间显示Toast
     */
    public MyToast Long(Context context, CharSequence message) {
        if (toast == null || (toastView != null && toastView.getChildCount() > 1)) {
            toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
            toastView = null;
        } else {
            toast.setText(message);
            toast.setDuration(Toast.LENGTH_LONG);
        }
        return this;
    }

    /**
     * 长时间显示Toast
     *
     * @param context
     * @param message
     */
    public MyToast Long(Context context, int message) {
        if (toast == null || (toastView != null && toastView.getChildCount() > 1)) {
            toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
            toastView = null;
        } else {
            toast.setText(message);
            toast.setDuration(Toast.LENGTH_LONG);
        }
        return this;
    }

    /**
     * 自定义显示Toast时间
     *
     * @param context
     * @param message
     * @param duration
     */
    public MyToast Indefinite(Context context, CharSequence message, int duration) {
        if (toast == null || (toastView != null && toastView.getChildCount() > 1)) {
            toast = Toast.makeText(context, message, duration);
            toastView = null;
        } else {
            toast.setText(message);
            toast.setDuration(duration);
        }
        return this;
    }

    /**
     * 自定义显示Toast时间
     *
     * @param context
     * @param message
     * @param duration
     */
    public MyToast Indefinite(Context context, int message, int duration) {
        if (toast == null || (toastView != null && toastView.getChildCount() > 1)) {
            toast = Toast.makeText(context, message, duration);
            toastView = null;
        } else {
            toast.setText(message);
            toast.setDuration(duration);
        }
        return this;
    }

    /**
     * 显示Toast
     *
     * @return
     */
    public void show() {
        toast.show();
    }

    /**
     * 获取Toast
     *
     * @return
     */
    public Toast getToast() {
        return toast;
    }
}
