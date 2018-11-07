package com.qvd.smartswitch.utils;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.qvd.smartswitch.MyApplication;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.login.LoginActivity;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.login.MessageVo;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
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
     * 定时将时间转换
     *
     * @param hour
     * @param minute
     * @return
     */
    public static List<String> getTiming(int hour, int minute) {
        List<String> date = new ArrayList<>();
        String strHours, strMinutes;
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
                .compose(activity.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(aLong -> {
                    if (!BleManager.getInstance().isConnected(bleDevice)) {
                        dialog.show();
                        dialog.setMessage("设备已断开，正在进行重连");
                        BleManager.getInstance().connect(bleDevice.getMac(), new BleGattCallback() {
                            @Override
                            public void onStartConnect() {

                            }

                            @Override
                            public void onConnectFail(BleDevice bleDevice1, BleException exception) {
                                final BluetoothGatt bluetoothGatt = BleManager.getInstance().getBluetoothGatt(bleDevice1);
                                if (bluetoothGatt != null) {
                                    new Handler().postDelayed(() -> {
                                        bluetoothGatt.disconnect();
                                        bluetoothGatt.close();
                                    }, 100);
                                }
                            }

                            @Override
                            public void onConnectSuccess(BleDevice bleDevice1, BluetoothGatt gatt, int status) {
                                dialog.dismiss();
                                SnackbarUtils.Short(view, "连接成功").show();
                            }

                            @Override
                            public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {

                            }
                        });
                    }
                });
    }

    /**
     * 关闭软键盘
     */
    public static void closeSoftKeyboard(Activity activity) {
        View a = activity.getCurrentFocus();
        if (a != null) {
            InputMethodManager imm = (InputMethodManager) activity.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            try {
                Objects.requireNonNull(imm).hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
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
            case "ae01":
                newName = "应用体验";
                break;
            case "other01":
                newName = "其他";
                break;
        }
        return newName;
    }

    /**
     * 给设备添加操作日志
     *
     * @param device_id
     * @param device_type
     * @param log_content
     */
    public static void addDeviceLog(String device_id, String device_type, String log_content) {
        RetrofitService.qdoApi.addDeviceLog(device_id, device_type, log_content)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MessageVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MessageVo messageVo) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    /**
     * 获取今天星期几
     *
     * @return
     */
    public static String getWeek() {
        Calendar cal = Calendar.getInstance();
        int i = cal.get(Calendar.DAY_OF_WEEK);
        switch (i) {
            case 1:
                return "星期日";
            case 2:
                return "星期一";
            case 3:
                return "星期二";
            case 4:
                return "星期三";
            case 5:
                return "星期四";
            case 6:
                return "星期五";
            case 7:
                return "星期六";
            default:
                return "";
        }
    }

    /**
     * 获取分享后对象是否接受的状态
     *
     * @param i
     * @return
     */
    public static String getShareState(int i) {
        if (i == 0) {
            return "未接受";
        } else {
            return "已接受";
        }
    }

    /**
     * 截取字符串
     *
     * @param s
     * @return
     */
    public static String cutDeviceName(String s) {
        String[] split = s.split("-");
        return split[0];
    }

    /**
     * 跳转页面，如果没有登录就跳出弹窗提示用户登录
     * @param userId
     * @param packageContext
     * @param cls
     */
    public static void startIntentLogin(String userId, Context packageContext, Class<?> cls) {
        if (CommonUtils.isEmptyString(userId)) {
            new MaterialDialog.Builder(packageContext)
                    .content("需要登录才能使用，是否现在登录？")
                    .negativeText("取消")
                    .positiveText("确定")
                    .onPositive((dialog, which) -> {
                        packageContext.startActivity(new Intent(packageContext, LoginActivity.class));
                        Objects.requireNonNull((Activity) packageContext).overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    }).show();
        } else {
            packageContext.startActivity(new Intent(packageContext, cls));
            Objects.requireNonNull((Activity) packageContext).overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }
    }

    /**
     * 跳转页面动画
     * @param packageContext
     */
    public static void startIntentAnim(Context packageContext){
        Objects.requireNonNull((Activity) packageContext).overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }
}
