package com.qvd.smartswitch.activity.wifi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.isupatches.wisefy.WiseFy;
import com.isupatches.wisefy.callbacks.AddWPA2NetworkCallbacks;
import com.isupatches.wisefy.callbacks.ConnectToNetworkCallbacks;
import com.isupatches.wisefy.callbacks.SearchForAccessPointCallbacks;
import com.orhanobut.logger.Logger;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.model.mqtt.WifiSmartNotifyVo;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.MqttUtils;
import com.qvd.smartswitch.utils.PermissionUtils;
import com.qvd.smartswitch.utils.RxHelper;
import com.qvd.smartswitch.utils.SnackbarUtils;
import com.qvd.smartswitch.utils.ToastUtil;
import com.qvd.smartswitch.widget.MyPopupWindowOne;
import com.yanzhenjie.permission.Permission;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import top.fighter_lee.mqttlibs.connect.MqttManager;
import top.fighter_lee.mqttlibs.mqttv3.IMqttDeliveryToken;
import top.fighter_lee.mqttlibs.mqttv3.MqttCallback;
import top.fighter_lee.mqttlibs.mqttv3.MqttException;
import top.fighter_lee.mqttlibs.mqttv3.MqttMessage;

public class DeviceWifiSettingActivity extends BaseActivity {
    @BindView(R.id.tv_back)
    TextView tvBack;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.tv_wifi_name)
    TextView tvWifiName;
    @BindView(R.id.tv_change_network)
    TextView tvChangeNetwork;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;
    @BindView(R.id.rl_setting_wifi_password)
    RelativeLayout rlSettingWifiPassword;
    @BindView(R.id.tv_text)
    TextView tvText;
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.tv_progress)
    TextView tvProgress;
    @BindView(R.id.tv_find_device)
    TextView tvFindDevice;
    @BindView(R.id.tv_device_register)
    TextView tvDeviceRegister;
    @BindView(R.id.tv_device_init)
    TextView tvDeviceInit;
    @BindView(R.id.rl_device_connect)
    RelativeLayout rlDeviceConnect;

    /**
     * wifi控制实例
     */
    private WiseFy wiseFy;
    /**
     * 当前连接的wifi
     */
    private WifiInfo currentNetwork;
    /**
     * 打开wifi
     */
    private MyPopupWindowOne popupWindowOpenWifi;

    /**
     * 当前路由的密码
     */
    private String password;

    /**
     * 当前路由的SSID
     *
     * @return
     */
    private String wifiSSID;
    /**
     * 展现错误的popupWindow
     */
    private MyPopupWindowOne popupWindowFail;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                case 0:
                    String obj = (String) msg.obj;
                    showfailPopupWindow(obj);
                    popupWindowFail.showPopupWindow(tvBack);
                    break;
                case 1:
                    setProgress(80, 100, 30);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    startActivity(new Intent(DeviceWifiSettingActivity.this, DeviceWifiControlActivity.class));
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    finish();
                    break;
                case 2:
                    int j = (int) msg.obj;
                    tvProgress.setText(j + "%");
                    break;
                case 3:
                    if (wiseFy.isDeviceConnectedToWifiNetwork()) {
                        Logger.e("sssssssssssssssssssssssssssssssssssssssss");
                        initMqtt();
                    }
                    break;
            }
        }
    };

    @Override
    protected int setLayoutId() {
        return R.layout.activity_device_wifi_setting;
    }

    @Override
    protected void initData() {
        super.initData();
        PermissionUtils.requestPermission(this, Permission.Group.LOCATION);
        wiseFy = new WiseFy.brains(this).logging(true).getSmarts();
        //注册接收消息
        MqttManager.getInstance().registerMessageListener(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Logger.e("setting_messageArrived() topic:" + topic + "messageArrived() message:" + message);
                if (topic.equals(MqttUtils.TOPIC_TWO)) {
                    Gson gson = new Gson();
                    WifiSmartNotifyVo wifiSmartNotifyVo = gson.fromJson(String.valueOf(message), WifiSmartNotifyVo.class);
                    if (wifiSmartNotifyVo.getKey_one().equals("mqttsever")) {
                        tvDeviceInit.setTextColor(getResources().getColor(R.color.room_manage_add_text));
                        Message message1 = new Message();
                        message1.what = 1;
                        handler.sendMessage(message1);
                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }


    /**
     * 初始化Mqtt
     */
    private void initMqtt() {
        //连接
        try {
            MqttUtils.getInstance().connect(new MqttUtils.IMqttResultListener() {
                @Override
                public void success() {
                    Logger.e("mqtt连接成功");
                }

                @Override
                public void fail() {

                }
            });
            //断连重试机制
            MqttManager.getInstance().keepConnect(1000, 1000);
        } catch (MqttException e) {
            e.printStackTrace();
        }
        //订阅
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    MqttUtils.getInstance().sub(MqttUtils.TOPIC_TWO);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        }, 500);
    }

    /**
     * 初始化WiFi
     */
    private void initWifi() {
        //检查当前设备是否开启wifi
        if (wiseFy.isWifiEnabled()) {
            //判断当前是否有连接wifi
            isConnectWifi();
        } else {
            new Handler().postDelayed(() -> {
                showOpenWifiPopupWindow();
                popupWindowOpenWifi.showPopupWindow(tvBack);
            }, 2000);
        }
    }

    /**
     * 判断当前是否有连接wifi
     */
    private void isConnectWifi() {
        //判断当前设备是否连接到wifi网络
        if (wiseFy.isDeviceConnectedToWifiNetwork()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ToastUtil.showToast("定位权限未打开");
                return;
            }
            //获取当前连接wifi信息 SSID: Xiaomi_Qvedo, BSSID: 40:31:3c:10:a4:26, MAC: 02:00:00:00:00:00, Supplicant state: COMPLETED, RSSI: -37, Link speed: 144Mbps, Frequency: 2412MHz, Net ID: 3, Metered hint: false, score: 60
            currentNetwork = wiseFy.getCurrentNetwork();
            String ssid = currentNetwork.getSSID();
            //设置当前wifi名称
            tvWifiName.setText("当前Wi-Fi:" + getNewSSID(ssid));
        } else {
            tvWifiName.setText("当前无Wi-Fi连接");
        }
    }

    private String getNewSSID(String ssid) {
        String substring = ssid.substring(1, ssid.length() - 1);
        return substring;
    }

    @Override
    protected void onResume() {
        super.onResume();
        initWifi();
    }

    /**
     * 展示打开Wifi的popouwindow
     */
    private void showOpenWifiPopupWindow() {
        popupWindowOpenWifi = new MyPopupWindowOne(this, "您当前Wi-Fi网络未开启，不能进行连接设备操作,点击确定开启Wi-Fi。", "取消", "确定", new MyPopupWindowOne.IPopupWindowListener() {
            @Override
            public void cancel() {
                popupWindowOpenWifi.dismiss();
                finish();
            }

            @Override
            public void confirm() {
                popupWindowOpenWifi.dismiss();
                boolean b = wiseFy.enableWifi();
                if (b) {
                    ToastUtil.showToast("Wi-Fi开启成功");
                    isConnectWifi();
                } else {
                    ToastUtil.showToast("Wi-Fi开启失败");
                }
            }
        });
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.viewfinder_frame).init();
    }

    @OnClick({R.id.tv_back, R.id.tv_change_network, R.id.tv_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_change_network:
                Intent intent = new Intent();
                ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.wifi.WifiSettings");
                intent.setComponent(cn);
                startActivity(intent);
                break;
            case R.id.tv_confirm:
                if (CommonUtils.isEmptyString(etPassword.getText().toString())) {
                    SnackbarUtils.Short(tvBack, "密码不能为空").show();
                } else if (!wiseFy.isDeviceConnectedToWifiNetwork()) {
                    tvWifiName.setText("当前无Wi-Fi连接");
                    SnackbarUtils.Short(tvBack, "未连接Wi-Fi").show();
                } else {
                    //保存密码
                    password = etPassword.getText().toString();
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ToastUtil.showToast("定位权限未打开");
                        return;
                    }
                    wifiSSID = getNewSSID(wiseFy.getCurrentNetwork().getSSID());
                    rlSettingWifiPassword.setVisibility(View.GONE);
                    tvText.setVisibility(View.GONE);
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    rlDeviceConnect.setVisibility(View.VISIBLE);
                    if (wiseFy.isNetworkSaved("qevdo_qs03")) {
                        searchWifi();
                    } else {
                        addWifi();
                    }
                }
                break;
        }
    }


    /**
     * 添加wifi
     */
    private void addWifi() {
        wiseFy.addWPA2Network("qevdo_qs03", "88888888", new AddWPA2NetworkCallbacks() {
            @Override
            public void addWPA2NetworkWiseFyFailure(int i) {
                showMyFailPopupWindow("当前设备添加失败，是否需要重试？");
            }

            @Override
            public void failureAddingWPA2Network(int i) {
                showMyFailPopupWindow("当前设备添加失败，是否需要重试？");
            }

            @Override
            public void wpa2NetworkAdded(int i, WifiConfiguration wifiConfiguration) {
                Logger.e("添加成功");
                searchWifi();
            }
        });
    }

    /**
     * 开始搜索
     */
    private void searchWifi() {
        //1、找到要连接的设备
        setProgress(0, 15, 50);
        wiseFy.searchForAccessPoint("qevdo_qs03", 3000, true, new SearchForAccessPointCallbacks() {
            @Override
            public void searchForAccessPointWiseFyFailure(int i) {
                showMyFailPopupWindow("当前设备搜索失败，是否需要重试？");
            }

            @Override
            public void accessPointFound(ScanResult scanResult) {
                //2、开始连接Wifi
                Logger.e("开始连接wifi");
                setProgress(15, 25, 100);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        connectWifi(scanResult.SSID);
                    }
                }, 1000);
            }

            @Override
            public void accessPointNotFound() {
                showMyFailPopupWindow("当前未搜索到该类设备，是否需要重试？");
            }
        });
    }

    private void showMyFailPopupWindow(String s) {
        Message message = new Message();
        message.obj = s;
        message.what = 0;
        handler.sendMessage(message);
    }

    /**
     * 连接目标wifi
     */
    private void connectWifi(String ssid) {
        Logger.e("连接成功");
        setProgress(25, 50, 40);
        wiseFy.connectToNetwork(ssid, 3000, new ConnectToNetworkCallbacks() {
            @Override
            public void connectedToNetwork() {
                tvFindDevice.setTextColor(getResources().getColor(R.color.room_manage_add_text));
                //对设备进行延时操作判断
                new Handler().postDelayed(() -> {
                    int count = 0;
                    while (true) {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if (wiseFy.isDeviceConnectedToWifiNetwork()) {
                            if (ActivityCompat.checkSelfPermission(DeviceWifiSettingActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            WifiInfo currentNetwork = wiseFy.getCurrentNetwork();
                            String ssid1 = currentNetwork.getSSID();
                            if (getNewSSID(ssid1).equals("qevdo_qs03")) {
                                //startSocket();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        handler.sendEmptyMessage(3);
                                    }
                                }, 2000);
                            } else {
                                showMyFailPopupWindow("当前设备连接s失败，是否需要重试？");
                            }
                            break;
                        }
                        if (count++ > 5) {
                            showMyFailPopupWindow("当前设备连接s失败，是否需要重试？");
                            break;
                        }
                    }
                }, 1000);
            }

            @Override
            public void connectToNetworkWiseFyFailure(int i) {
                showMyFailPopupWindow("当前设备连接失败，是否需要重试？");
            }

            @Override
            public void failureConnectingToNetwork() {
                showMyFailPopupWindow("当前设备连接失败，是否需要重试？");
            }

            @Override
            public void networkNotFoundToConnectTo() {
                showMyFailPopupWindow("当前未连接到该类设备，是否需要重试？");
            }
        });
    }

    /**
     * 开始wifi模块配网
     */
    private void startSocket() {
        setProgress(50, 80, 50);
        tvDeviceRegister.setTextColor(getResources().getColor(R.color.room_manage_add_text));
        Socket socket = null;
        OutputStream out = null;
        BufferedReader br = null;
        PrintWriter pw = null;
        try {
            socket = new Socket("192.168.4.1", 8266);
            socket.setKeepAlive(true);
            out = socket.getOutputStream();
            pw = new PrintWriter(out);
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            JSONObject object = new JSONObject();
            object.put("state", 1);
            object.put("ssid", wifiSSID);
            object.put("psw", password);
            pw.write(object.toString());
            pw.flush();
            //socket.shutdownOutput();
            char[] b = new char[1024];
            while (br.read(b, 0, b.length) != -1) {
                Logger.e("返回的数据->" + String.valueOf(b));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (br != null) {
                    br.close();
                }
                if (socket != null) {
                    socket.close();
                }
                if (pw != null) {
                    pw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置progress的速率
     *
     * @param start
     * @param end
     * @param time
     */
    private void setProgress(int start, int end, int time) {
        new Thread() {
            @Override
            public void run() {
                int i = start;
                while (i < end) {
                    i++;
                    try {
                        Thread.sleep(time);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    final int j = i;
                    progress.setProgress(i);
                    Message message = new Message();
                    message.obj = j;
                    message.what = 2;
                    handler.sendMessage(message);
                }
            }
        }.start();
    }


    /**
     * 展示失败的popupWindow
     */
    private void showfailPopupWindow(String title) {
        popupWindowFail = new MyPopupWindowOne(DeviceWifiSettingActivity.this, title, "取消", "确定", new MyPopupWindowOne.IPopupWindowListener() {
            @Override
            public void cancel() {
                popupWindowFail.dismiss();
                wiseFy.dump();
                finish();
            }

            @Override
            public void confirm() {
                popupWindowFail.dismiss();
                if (wiseFy.isNetworkSaved("qevdo_qs03")) {
                    searchWifi();
                } else {
                    addWifi();
                }
            }
        });
        popupWindowFail.setFocusable(false);
        popupWindowFail.setOutsideTouchable(false);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (popupWindowFail != null && popupWindowFail.isShowing()) {
            return false;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (MqttManager.getInstance().isConneect()) {
            try {
                MqttUtils.getInstance().unsub(MqttUtils.TOPIC_TWO);
                MqttUtils.getInstance().disconnect(new MqttUtils.IMqttResultListener() {
                    @Override
                    public void success() {

                    }

                    @Override
                    public void fail() {

                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

}
