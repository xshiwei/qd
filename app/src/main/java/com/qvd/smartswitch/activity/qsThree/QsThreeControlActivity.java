package com.qvd.smartswitch.activity.qsThree;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.TextOutsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.orhanobut.logger.Logger;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.MainActivity;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.activity.device.DeviceLogActivity;
import com.qvd.smartswitch.activity.device.DeviceSettingActivity;
import com.qvd.smartswitch.activity.device.DeviceSoundControlActivity;
import com.qvd.smartswitch.activity.qsTwo.QsTwoControlActivity;
import com.qvd.smartswitch.model.mqtt.WifiSmartNotifyVo;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.MqttUtils;
import com.qvd.smartswitch.utils.OnMultiClickListener;
import com.qvd.smartswitch.utils.PermissionUtils;
import com.qvd.smartswitch.utils.ToastUtil;
import com.yanzhenjie.permission.Permission;

import butterknife.BindView;
import butterknife.OnClick;
import top.fighter_lee.mqttlibs.connect.MqttManager;
import top.fighter_lee.mqttlibs.mqttv3.IMqttDeliveryToken;
import top.fighter_lee.mqttlibs.mqttv3.MqttCallback;
import top.fighter_lee.mqttlibs.mqttv3.MqttException;
import top.fighter_lee.mqttlibs.mqttv3.MqttMessage;

/**
 * Created by Administrator on 2018/4/3.
 */

public class QsThreeControlActivity extends BaseActivity {

    @BindView(R.id.iv_device_control_goback)
    ImageView ivDeviceControlGoback;
    @BindView(R.id.tv_device_control_title)
    TextView tvDeviceControlTitle;
    @BindView(R.id.iv_device_control_more)
    ImageView ivDeviceControlMore;
    @BindView(R.id.action_bar)
    RelativeLayout actionBar;
    @BindView(R.id.tv_timing_text)
    TextView tvTimingText;
    @BindView(R.id.iv_light_one)
    ImageView ivLightOne;
    @BindView(R.id.iv_switch_one)
    ImageView ivSwitchOne;
    @BindView(R.id.iv_light_two)
    ImageView ivLightTwo;
    @BindView(R.id.iv_switch_two)
    ImageView ivSwitchTwo;
    @BindView(R.id.tv_two)
    TextView tvTwo;
    @BindView(R.id.bmb)
    BoomMenuButton bmb;


    private static String TAG = QsThreeControlActivity.class.getSimpleName();

    /**
     * 灯1未开启
     */
    private boolean isStateOne = false;
    /**
     * 灯2未开启
     */
    private boolean isStatetwo = false;
    /**
     * 是否第一次连接
     */
    private int isFirstConnect;
    /**
     * 设备ID
     */
    private String deviceId;
    private String device_name;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_device_wifi_control;
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    isStateOne = true;
                    ivSwitchOne.setImageResource(R.mipmap.device_switch_on);
                    ivLightOne.setImageResource(R.mipmap.device_light_on);
                    break;
                case 1:
                    isStateOne = false;
                    ivSwitchOne.setImageResource(R.mipmap.device_switch_off);
                    ivLightOne.setImageResource(R.mipmap.device_light_off);
                    break;
                case 2:
                    isStatetwo = true;
                    ivSwitchTwo.setImageResource(R.mipmap.device_switch_on);
                    ivLightTwo.setImageResource(R.mipmap.device_light_on);
                    break;
                case 3:
                    isStatetwo = false;
                    ivSwitchOne.setImageResource(R.mipmap.device_switch_off);
                    ivLightTwo.setImageResource(R.mipmap.device_light_off);
                    break;
            }
        }
    };

    @SuppressLint("CheckResult")
    @Override
    protected void initData() {
        super.initData();
        PermissionUtils.requestPermission(this, Permission.READ_PHONE_STATE);
        isFirstConnect = getIntent().getIntExtra("isFirstConnect", -1);
        deviceId = getIntent().getStringExtra("deviceId");
        device_name = getIntent().getStringExtra("device_name");
        tvDeviceControlTitle.setText(device_name);
        try {
            //获取消息
            MqttManager.getInstance().registerMessageListener(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Logger.e(TAG, cause);
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    if (topic.equals(MqttUtils.TOPIC_ONE)) {
                        return;
                    } else {
                        Gson gson = new Gson();
                        WifiSmartNotifyVo wifiSmartNotifyVo = gson.fromJson(String.valueOf(message), WifiSmartNotifyVo.class);
                        if (wifiSmartNotifyVo.getKey_one().equals("led1")) {
                            if (wifiSmartNotifyVo.getKey_two().equals("11")) {
                                handler.sendEmptyMessage(0);
                                CommonUtils.addDeviceLog(deviceId, "qs03", "手动打开一号灯");
                            } else {
                                handler.sendEmptyMessage(1);
                                CommonUtils.addDeviceLog(deviceId, "qs03", "手动关闭一号灯");
                            }
                        } else if (wifiSmartNotifyVo.getKey_one().equals("led2")) {
                            if (wifiSmartNotifyVo.getKey_two().equals("21")) {
                                handler.sendEmptyMessage(2);
                                CommonUtils.addDeviceLog(deviceId, "qs03", "手动打开二号灯");
                            } else {
                                handler.sendEmptyMessage(3);
                                CommonUtils.addDeviceLog(deviceId, "qs03", "手动关闭二号灯");
                            }
                        } else if (wifiSmartNotifyVo.getKey_one().equals("11")) {
                            handler.sendEmptyMessage(0);
                            if (wifiSmartNotifyVo.getKey_two().equals("21")) {
                                handler.sendEmptyMessage(2);
                            } else {
                                handler.sendEmptyMessage(3);
                            }
                        } else if (wifiSmartNotifyVo.getKey_one().equals("10")) {
                            handler.sendEmptyMessage(1);
                            if (wifiSmartNotifyVo.getKey_two().equals("21")) {
                                handler.sendEmptyMessage(2);
                            } else {
                                handler.sendEmptyMessage(3);
                            }
                        }
                        Logger.e("controlmessageArrived() topic:" + topic + "messageArrived() message:" + message);
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });
            if (!MqttManager.getInstance().isConneect()) {
                //连接
                MqttUtils.getInstance().connect(new MqttUtils.IMqttResultListener() {
                    @Override
                    public void success() {
                        Logger.e("连接success");
                    }

                    @Override
                    public void fail() {

                    }
                });
            }
            //断连重试机制
            MqttManager.getInstance().keepConnect(3000, 3000);
        } catch (MqttException e) {
            e.printStackTrace();
        }

        initMenu();
    }

    /**
     * 初始化菜单
     */
    private void initMenu() {
        TextOutsideCircleButton.Builder builder1 = new TextOutsideCircleButton.Builder()
                .normalImageRes(R.mipmap.device_log_pic)
                .normalText("日志")
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        startActivity(new Intent(QsThreeControlActivity.this, DeviceLogActivity.class)
                                .putExtra("device_id", deviceId)
                                .putExtra("device_type", "qs03"));
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    }
                })
                .textSize(12)
                .normalColor(getResources().getColor(R.color.orange_background))
                .pieceColor(Color.WHITE);
        TextOutsideCircleButton.Builder builder2 = new TextOutsideCircleButton.Builder()
                .normalImageRes(R.mipmap.device_sound_pic)
                .normalText("语音")
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        ToastUtil.showToast("功能正在开发中，敬请期待。。。");
                    }
                })
                .textSize(12)
                .normalColor(getResources().getColor(R.color.app_red_color))
                .pieceColor(Color.WHITE);
        TextOutsideCircleButton.Builder builder3 = new TextOutsideCircleButton.Builder()
                .normalImageRes(R.mipmap.device_timing_pic)
                .normalText("定时")
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        ToastUtil.showToast("功能正在开发中，敬请期待。。。");
                    }
                })
                .textSize(12)
                .normalColor(getResources().getColor(R.color.blue_background))
                .pieceColor(Color.WHITE);
        bmb.addBuilder(builder1);
        bmb.addBuilder(builder2);
        bmb.addBuilder(builder3);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(() -> {
            //订阅
            try {
                MqttUtils.getInstance().sub(MqttUtils.TOPIC_TWO);
                MqttUtils.getInstance().pub("{\"key_one\":\"updateled\",\"key_two\":\"4\"}", MqttUtils.TOPIC_ONE, new MqttUtils.IMqttResultListener() {
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
        }, 500);
        ivSwitchOne.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                if (isStateOne) {
                    //关灯
                    publish("{\"key_one\":\"led1\",\"key_two\":\"10\"}", false, 1);

                } else {
                    //开灯
                    publish("{\"key_one\":\"led1\",\"key_two\":\"11\"}", true, 1);

                }
            }
        });

        ivSwitchTwo.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                if (isStatetwo) {
                    //关灯
                    publish("{\"key_one\":\"led2\",\"key_two\":\"20\"}", false, 2);

                } else {
                    //开灯
                    publish("{\"key_one\":\"led2\",\"key_two\":\"21\"}", true, 2);

                }
            }
        });
    }

    /**
     * 灯发布
     *
     * @param s
     * @param b
     */
    private void publish(String s, boolean b, int i) {
        try {
            MqttUtils.getInstance().pub(s, MqttUtils.TOPIC_ONE, new MqttUtils.IMqttResultListener() {
                @Override
                public void success() {
                    if (i == 1) {
                        isStateOne = b;

                    } else {
                        isStatetwo = b;
                    }
                }

                @Override
                public void fail() {

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(false).transparentBar().statusBarDarkFont(false).init();
    }

    @OnClick({R.id.iv_device_control_goback, R.id.iv_device_control_more})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_device_control_goback:
                startActivity(new Intent(this, MainActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                finish();
                break;
            case R.id.iv_device_control_more:
                startActivity(new Intent(this, DeviceSettingActivity.class)
                        .putExtra("device_id", deviceId)
                        .putExtra("device_No", "qs03"));
                break;
        }
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

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        finish();
        super.onBackPressed();
    }
}
