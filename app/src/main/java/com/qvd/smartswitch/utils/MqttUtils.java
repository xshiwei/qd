package com.qvd.smartswitch.utils;

import android.annotation.SuppressLint;

import com.orhanobut.logger.Logger;
import com.qvd.smartswitch.MyApplication;

import top.fighter_lee.mqttlibs.connect.ConnectCommand;
import top.fighter_lee.mqttlibs.connect.DisconnectCommand;
import top.fighter_lee.mqttlibs.connect.MqttManager;
import top.fighter_lee.mqttlibs.connect.PubCommand;
import top.fighter_lee.mqttlibs.connect.SubCommand;
import top.fighter_lee.mqttlibs.connect.UnsubCommand;
import top.fighter_lee.mqttlibs.mqttv3.IMqttActionListener;
import top.fighter_lee.mqttlibs.mqttv3.IMqttToken;
import top.fighter_lee.mqttlibs.mqttv3.MqttException;

public class MqttUtils {

    private static final String TAG = "MqttUtils";
    private static MqttUtils mqttUtils;
    public static final String TOPIC_ONE = "/app/smart-wifi/mqtt";
    public static final String TOPIC_TWO = "/device/smart-wifi/mqtt_notify";

    private IMqttResultListener listener;

    public interface IMqttResultListener {
        void success();

        void fail();
    }

    public void setOnClickListener(IMqttResultListener listener) {
        this.listener = listener;
    }

    private MqttUtils() {
    }

    public static MqttUtils getInstance() {
        if (null == mqttUtils)
            mqttUtils = new MqttUtils();
        return mqttUtils;
    }

    @SuppressLint("MissingPermission")
    public void connect(IMqttResultListener listener) throws MqttException {
        MqttManager.getInstance()
                .connect(new ConnectCommand()
                                .setClientId("qevdo_app" + ConfigUtils.user_id)
                                .setServer("119.29.105.91")
                                .setPort(1883)
                                .setKeepAlive(30)
                                .setTimeout(10)
                                .setCleanSession(true)
                        , new IMqttActionListener() {
                            @Override
                            public void onSuccess(IMqttToken asyncActionToken) {
                                listener.success();
                            }

                            @Override
                            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                                listener.fail();
                            }
                        });
    }

    public void disconnect(IMqttResultListener listener) throws MqttException {
        if (!MqttManager.getInstance().isConneect()) {
            ToastUtil.showToast("当前已断开连接");
            return;
        }
        MqttManager.getInstance().disConnect(new DisconnectCommand()
                .setQuiesceTimeout(10), new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                listener.success();
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                listener.fail();
            }
        });
    }

    public void pub(String message, String topic, IMqttResultListener listener) throws MqttException {
        if (!MqttManager.getInstance().isConneect()) {
            ToastUtil.showToast("当前已断开连接");
            return;
        }
        MqttManager.getInstance().pub(new PubCommand()
                .setMessage(message)
                .setQos(2)
                .setTopic(topic)
                .setRetained(false), new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                listener.success();
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Logger.e(TAG, exception);
                listener.fail();
            }
        });
    }

    public void sub(String topic) throws MqttException {
        if (!MqttManager.getInstance().isConneect()) {
            ToastUtil.showToast("当前已断开连接");
            return;
        }
        MqttManager.getInstance().sub(new SubCommand()
                .setQos(2)
                .setTopic(topic), new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                Logger.e("mqtt订阅成功");
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Logger.e(TAG, exception);
            }
        });
    }

    public void unsub(String topic) throws MqttException {
        if (!MqttManager.getInstance().isConneect()) {
            ToastUtil.showToast("当前已断开连接");
            return;
        }
        MqttManager.getInstance().unSub(new UnsubCommand()
                .setTopic(topic), new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Logger.e(TAG, exception);
            }
        });
    }


}
