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

import com.clj.fastble.BleManager;
import com.google.gson.Gson;
import com.nightonke.boommenu.BoomButtons.TextOutsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.orhanobut.logger.Logger;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.MainActivity;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.activity.base.BaseHandler;
import com.qvd.smartswitch.activity.base.BaseRunnable;
import com.qvd.smartswitch.activity.device.DeviceLogActivity;
import com.qvd.smartswitch.activity.qsTwo.QsTwoSettingActivity;
import com.qvd.smartswitch.model.device.ScanResultVo;
import com.qvd.smartswitch.model.mqtt.WifiSmartNotifyVo;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.MqttAndroidUtils;
import com.qvd.smartswitch.utils.OnMultiClickListener;
import com.qvd.smartswitch.utils.ToastUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;


import butterknife.BindView;
import butterknife.OnClick;

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
    @BindView(R.id.tv_connect_text)
    TextView tvConnectText;
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
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout smartRefreshLayout;

    private static final String TAG = QsThreeControlActivity.class.getSimpleName();

    /**
     * 灯1未开启
     */
    private boolean isStateOne = false;

    /**
     * 灯2未开启
     */
    private boolean isStatetwo = false;

    private ScanResultVo resultVo;
    private MqttAndroidClient mqttAndroidClient;
    private MqttConnectOptions mqttConnectOptions;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_qsthree_control;
    }

    private final MyHandler handler = new MyHandler(this);

    private class MyHandler extends BaseHandler<QsThreeControlActivity> {

        protected MyHandler(QsThreeControlActivity reference) {
            super(reference);
        }

        @Override
        protected void handleMessage(QsThreeControlActivity reference, Message msg) {
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
                    ivSwitchTwo.setImageResource(R.mipmap.device_switch_off);
                    ivLightTwo.setImageResource(R.mipmap.device_light_off);
                    break;
            }
        }
    }

    @SuppressLint("CheckResult")
    @Override
    protected void initData() {
        super.initData();
        mqttAndroidClient = MqttAndroidUtils.getInstance().getMqttAndroidClient(MqttAndroidUtils.MqttServerUri);
        mqttConnectOptions = MqttAndroidUtils.getInstance().getMqttConnectOptions();
        resultVo = (ScanResultVo) getIntent().getSerializableExtra("scanResult");
        tvDeviceControlTitle.setText(resultVo.getDeviceName());
        //获取消息
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                if (topic.equals(MqttAndroidUtils.TOPIC_ONE)) {
                } else {
                    Gson gson = new Gson();
                    WifiSmartNotifyVo wifiSmartNotifyVo = gson.fromJson(String.valueOf(message), WifiSmartNotifyVo.class);
                    switch (wifiSmartNotifyVo.getKey_one()) {
                        case "led1":
                            if (wifiSmartNotifyVo.getKey_two().equals("11")) {
                                handler.sendEmptyMessage(0);
                                CommonUtils.addDeviceLog(resultVo.getDeviceId(), "qs03", "手动打开一号灯");
                            } else {
                                handler.sendEmptyMessage(1);
                                CommonUtils.addDeviceLog(resultVo.getDeviceId(), "qs03", "手动关闭一号灯");
                            }
                            break;
                        case "led2":
                            if (wifiSmartNotifyVo.getKey_two().equals("21")) {
                                handler.sendEmptyMessage(2);
                                CommonUtils.addDeviceLog(resultVo.getDeviceId(), "qs03", "手动打开二号灯");
                            } else {
                                handler.sendEmptyMessage(3);
                                CommonUtils.addDeviceLog(resultVo.getDeviceId(), "qs03", "手动关闭二号灯");
                            }
                            break;
                        case "11":
                            handler.sendEmptyMessage(0);
                            if (wifiSmartNotifyVo.getKey_two().equals("21")) {
                                handler.sendEmptyMessage(2);
                            } else {
                                handler.sendEmptyMessage(3);
                            }
                            break;
                        case "10":
                            handler.sendEmptyMessage(1);
                            if (wifiSmartNotifyVo.getKey_two().equals("21")) {
                                handler.sendEmptyMessage(2);
                            } else {
                                handler.sendEmptyMessage(3);
                            }
                            break;
                    }
                    Logger.e("controlmessageArrived() topic:" + topic + "messageArrived() message:" + message);
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        //设置刷新控件头部高度
        smartRefreshLayout.setHeaderHeight(100);
        smartRefreshLayout.setFooterHeight(1);
        smartRefreshLayout.setEnableRefresh(true);
        smartRefreshLayout.setEnableHeaderTranslationContent(true);
        smartRefreshLayout.setEnableFooterTranslationContent(false);
        //设置头部样式
        smartRefreshLayout.setRefreshHeader(new ClassicsHeader(this).setAccentColor(getResources().getColor(R.color.white)));
        smartRefreshLayout.setOnRefreshListener(refreshlayout -> {
            if (!BleManager.getInstance().isConnected(resultVo.getDeviceMac())) {
                if (!mqttAndroidClient.isConnected()) {
                    tvConnectText.setVisibility(View.VISIBLE);
                    //连接
                    try {
                        mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                            @Override
                            public void onSuccess(IMqttToken asyncActionToken) {
                                tvConnectText.setVisibility(View.GONE);
                            }

                            @Override
                            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                            }
                        });
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            }
            smartRefreshLayout.finishRefresh(1000, true);
        });
        initMenu();
    }

    /**
     * 初始化菜单
     */
    private void initMenu() {
        TextOutsideCircleButton.Builder builder1 = new TextOutsideCircleButton.Builder()
                .normalImageRes(R.mipmap.device_log_pic)
                .normalText("操作记录")
                .listener(index -> {
                    startActivity(new Intent(QsThreeControlActivity.this, DeviceLogActivity.class)
                            .putExtra("device_id", resultVo.getDeviceId())
                            .putExtra("device_type", "qs03"));
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                })
                .textSize(12)
                .normalColor(getResources().getColor(R.color.orange_background))
                .pieceColor(Color.WHITE);
        TextOutsideCircleButton.Builder builder2 = new TextOutsideCircleButton.Builder()
                .normalImageRes(R.mipmap.device_sound_pic)
                .normalText("语音")
                .listener(index -> {
                    startActivity(new Intent(QsThreeControlActivity.this, QsThreeSoundControlActivity.class)
                            .putExtra("device_id", resultVo.getDeviceId()));
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                })
                .textSize(12)
                .normalColor(getResources().getColor(R.color.app_red_color))
                .pieceColor(Color.WHITE);
        TextOutsideCircleButton.Builder builder3 = new TextOutsideCircleButton.Builder()
                .normalImageRes(R.mipmap.device_timing_pic)
                .normalText("定时")
                .listener(index -> ToastUtil.showToast("功能正在开发中，敬请期待。。。"))
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
        if (!mqttAndroidClient.isConnected()) {
            tvConnectText.setVisibility(View.VISIBLE);
            //连接
            try {
                mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        tvConnectText.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                    }
                });
            } catch (org.eclipse.paho.client.mqttv3.MqttException e) {
                e.printStackTrace();
            }
        }
        try {
            mqttAndroidClient.subscribe(MqttAndroidUtils.TOPIC_TWO, 2);
        } catch (MqttException e) {
            e.printStackTrace();
        }
        handler.postDelayed(new BaseRunnable(() -> {
            //订阅
            MqttMessage message = new MqttMessage();
            message.setPayload("{\"key_one\":\"updateled\",\"key_two\":\"4\"}".getBytes());
            message.setQos(2);
            try {
                mqttAndroidClient.publish(MqttAndroidUtils.TOPIC_ONE, message, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {

                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }), 500);
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
        MqttMessage message = new MqttMessage();
        message.setPayload(s.getBytes());
        message.setQos(2);
        try {
            mqttAndroidClient.publish(MqttAndroidUtils.TOPIC_ONE, message, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Logger.e("发布成功");
                    if (i == 1) {
                        isStateOne = b;
                    } else {
                        isStatetwo = b;
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.light_background_end).statusBarDarkFont(false).init();
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
                startActivity(new Intent(this, QsTwoSettingActivity.class)
                        .putExtra("device_id", resultVo.getDeviceId()));
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mqttAndroidClient.isConnected()) {
            try {
                mqttAndroidClient.unsubscribe(MqttAndroidUtils.TOPIC_TWO);
                mqttAndroidClient.disconnect();
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
