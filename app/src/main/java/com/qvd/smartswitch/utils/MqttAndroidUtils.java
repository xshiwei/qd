package com.qvd.smartswitch.utils;

import com.qvd.smartswitch.MyApplication;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

public class MqttAndroidUtils {

    private static final String TAG = "MqttUtils";
    public static final String TOPIC_ONE = "/app/smart-wifi/mqtt";
    public static final String TOPIC_TWO = "/device/smart-wifi/mqtt_notify";
    public static final String MqttServerUri = "tcp://119.29.105.91:1883";
    public static final String MqttRobotServerUri = "tcp://119.29.105.91:1889";

    private static class MqttAndroidUtilsHolder {
        private static final MqttAndroidUtils INSTANCE = new MqttAndroidUtils();
    }

    private MqttAndroidUtils() {
    }

    public static MqttAndroidUtils getInstance() {
        return MqttAndroidUtilsHolder.INSTANCE;
    }

    public MqttAndroidClient getMqttAndroidClient(String uri) {
        MqttAndroidClient mqttAndroidClient = new MqttAndroidClient(MyApplication.getContext(), uri, "qevdo_app" + ConfigUtils.user_id);
        return mqttAndroidClient;
    }

    public MqttConnectOptions getMqttConnectOptions() {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);
        return mqttConnectOptions;
    }


}
