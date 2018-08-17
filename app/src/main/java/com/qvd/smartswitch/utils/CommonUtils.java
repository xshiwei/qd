package com.qvd.smartswitch.utils;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.hardware.input.InputManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;
import com.orhanobut.logger.Logger;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static android.Manifest.permission.READ_PHONE_STATE;


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
     *
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
     *
     * @param hour
     * @param minute
     * @return
     */
    public static List<String> getTiming(int hour, int minute) {
        List<String> date = new ArrayList<>();
        String strHours = "", strMinutes = "";
        String t = "false";
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.setTimeInMillis(calendar.getTimeInMillis() + 24 * 60 * 60 * 1000);
            t = "true";
        }
        long diff = calendar.getTimeInMillis() - System.currentTimeMillis();
        long days = diff / (1000 * 60 * 60 * 24);
        long hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
        if (hours < 10) {
            strHours = "0" + String.valueOf(hours);
        } else {
            strHours = String.valueOf(hours);
        }
        if (minutes < 10) {
            strMinutes = "0" + String.valueOf(minutes);
        } else {
            strMinutes = String.valueOf(minutes);
        }
        date.add(strHours);
        date.add(strMinutes);
        date.add(t);
        return date;
    }

    /**
     * 给图片上色
     *
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
     *
     * @param mContext
     * @param bgAlpha
     */
    public static void setBackgroundAlpha(Context mContext, float bgAlpha) {
        WindowManager.LayoutParams lp = ((Activity) mContext).getWindow().getAttributes();
        lp.alpha = bgAlpha;
        ((Activity) mContext).getWindow().setAttributes(lp);
    }

    /**
     * 获取重连通知
     */
    @SuppressLint("CheckResult")
    public static void getConnectNotify(final RxAppCompatActivity activity, final BleDevice bleDevice, final View view) {
        final ProgressDialog dialog = new ProgressDialog(activity);
        Observable.interval(3, 3, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(activity.<Long>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        if (!BleManager.getInstance().isConnected(bleDevice)) {
                            dialog.show();
                            dialog.setMessage("设备已断开，正在进行重连");
                            BleManager.getInstance().connect(bleDevice.getMac(), new BleGattCallback() {
                                @Override
                                public void onStartConnect() {

                                }

                                @Override
                                public void onConnectFail(BleDevice bleDevice, BleException exception) {
                                    final BluetoothGatt bluetoothGatt = BleManager.getInstance().getBluetoothGatt(bleDevice);
                                    if (bluetoothGatt != null) {
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                bluetoothGatt.disconnect();
                                                bluetoothGatt.close();
                                            }
                                        }, 100);
                                    }
                                }

                                @Override
                                public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                                    dialog.dismiss();
                                    SnackbarUtils.Short(view, "连接成功").show();
                                }

                                @Override
                                public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {

                                }
                            });
                        }
                    }
                });
    }

    /**
     * 关闭软键盘
     */
    public static void closeSoftKeyboard(Activity activity) {
        /**
         * 设置输入法,如果当前页面输入法打开则关闭
         * @param activity
         */
        View a = activity.getCurrentFocus();
        if (a != null) {
            InputMethodManager imm = (InputMethodManager) activity.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            try {
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获得IMEI
     * <p>Must hold
     * {@code <uses-permission android:name="android.permission.READ_PHONE_STATE" />}</p>
     *
     * @return the IMEI
     */
    @SuppressLint({"HardwareIds", "MissingPermission"})
    @RequiresPermission(READ_PHONE_STATE)
    public static String getIMEI(Context context) {
        TelephonyManager tm =
                (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return tm != null ? tm.getImei() : "";
        }
        return tm != null ? tm.getDeviceId() : "";
    }


    /**
     * 根据设备型号返回设备名称
     *
     * @param name
     * @return
     */
    public static String getDeviceName(String name) {
        String newName = "";
        switch (name) {
            case "qs02":
                newName = "蓝牙智能开关";
                break;
            case "qs03":
                newName = "Wi_Fi智能开关";
                break;
        }
        return newName;
    }

    /**
     * 根据设备型号返回设备名称
     *
     * @param name
     * @return
     */
    public static String getTableName(String name) {
        String newName = "";
        switch (name) {
            case "qs02":
                newName = "qs02";
                break;
            case "qs03":
                newName = "qs03";
                break;
        }
        return newName;
    }

}
