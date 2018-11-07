package com.qvd.smartswitch;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.multidex.MultiDex;

import com.clj.fastble.BleManager;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.utils.ActivityManager;
import com.qvd.smartswitch.utils.LanguageUtils;
import com.squareup.leakcanary.LeakCanary;
import com.wenming.library.LogReport;
import com.wenming.library.save.imp.CrashWriter;
import com.wenming.library.upload.email.EmailReporter;

import java.util.Locale;


/**
 * Created by Administrator on 2018/4/2.
 */

public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Locale locale = LanguageUtils.getAppLocale(this);
        LanguageUtils.changeAppLanguage(this, locale, true);
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            return;
//        }
//        LeakCanary.install(this);
        BleManager.getInstance().init(this);
        RetrofitService.init();
        //科大讯飞语音识别初始化
        SpeechUtility.createUtility(context, SpeechConstant.APPID + "=5afcdd8d");
        initCrashReport();

        this.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                ActivityManager.getInstance().addActivity(activity);
                if (LanguageUtils.isSameWithSetting(activity)) {
                    LanguageUtils.changeAppLanguage(activity,
                            LanguageUtils.getAppLocale(activity), true);
                }
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                ActivityManager.getInstance().removeActivity(activity);
            }
        });
    }

    public static Context getContext() {
        return context;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private void initCrashReport() {
        LogReport.getInstance()
                .setCacheSize(30 * 1024 * 1024)//支持设置缓存大小，超出后清空
                .setLogDir(getApplicationContext(), "sdcard/" + this.getString(this.getApplicationInfo().labelRes) + "/")//定义路径为：sdcard/[app name]/
                .setWifiOnly(false)//设置只在Wifi状态下上传，设置为false为Wifi和移动网络都上传
                .setLogSaver(new CrashWriter(getApplicationContext()))//支持自定义保存崩溃信息的样式
                //.setEncryption(new AESEncode()) //支持日志到AES加密或者DES加密，默认不开启
                .init(getApplicationContext());
        initEmailReporter();
    }

    /**
     * 使用EMAIL发送日志
     */
    private void initEmailReporter() {
        EmailReporter email = new EmailReporter(this);
        email.setReceiver("1105943292@qq.com");//收件人
        email.setSender("13631787352@163.com");//发送人邮箱
        email.setSendPassword("qq655988");//邮箱的客户端授权码，注意不是邮箱密码
        email.setSMTPHost("smtp.163.com");//SMTP地址
        email.setPort("465");//SMTP 端口
        LogReport.getInstance().setUploadType(email);
    }

}
