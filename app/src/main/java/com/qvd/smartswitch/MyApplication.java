package com.qvd.smartswitch;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.clj.fastble.BleManager;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.qvd.smartswitch.api.RetrofitService;

import top.fighter_lee.mqttlibs.connect.MqttManager;


/**
 * Created by Administrator on 2018/4/2.
 */

public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        BleManager.getInstance().init(this);
        RetrofitService.init();
        //科大讯飞语音识别初始化
        SpeechUtility.createUtility(context, SpeechConstant.APPID + "=5afcdd8d");
        MqttManager.getInstance().setContext(getApplicationContext());
    }

    public static Context getContext() {
        return context;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


}
