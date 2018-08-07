package com.qvd.smartswitch.activity.wifi;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.utils.MqttUtils;
import com.qvd.smartswitch.utils.PermissionUtils;
import com.qvd.smartswitch.utils.ToastUtil;
import com.yanzhenjie.permission.Permission;

import java.util.Observable;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import top.fighter_lee.mqttlibs.connect.MqttManager;
import top.fighter_lee.mqttlibs.mqttv3.IMqttDeliveryToken;
import top.fighter_lee.mqttlibs.mqttv3.MqttCallback;
import top.fighter_lee.mqttlibs.mqttv3.MqttException;
import top.fighter_lee.mqttlibs.mqttv3.MqttMessage;

public class MqttTestActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.tv_connect)
    TextView tvConnect;
    @BindView(R.id.tv_disconnect)
    TextView tvDisconnect;
    @BindView(R.id.tv_publish)
    TextView tvPublish;
    @BindView(R.id.tv_subscriber)
    TextView tvSubscriber;
    @BindView(R.id.tv_unSubscriber)
    TextView tvUnSubscriber;
    @BindView(R.id.tv_text)
    TextView tvText;

    private static final String TAG = "MqttTestActivity";


    @Override
    protected int setLayoutId() {
        return R.layout.activity_mqtt_test;
    }

    @Override
    protected void initData() {
        super.initData();
        PermissionUtils.requestPermission(this, Permission.READ_PHONE_STATE);
        tvCommonActionbarTitle.setText("MQTT控制界面");
        MqttManager.getInstance().registerMessageListener(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Logger.e(TAG, cause);
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                tvText.setText("messageArrived() topic:" + topic + "messageArrived() message:" + message);
                Logger.e("messageArrived() topic:" + topic);
                Logger.e("messageArrived() message:" + message);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }

    @OnClick({R.id.iv_common_actionbar_goback, R.id.tv_connect, R.id.tv_disconnect, R.id.tv_publish, R.id.tv_subscriber, R.id.tv_unSubscriber})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.tv_connect:
                try {
                    MqttUtils.getInstance().connect(new MqttUtils.IMqttResultListener() {
                        @Override
                        public void success() {
                            ToastUtil.showToast("success");
                        }

                        @Override
                        public void fail() {

                        }
                    });
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tv_disconnect:
                try {
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
                break;
            case R.id.tv_publish:
                for (int i = 0;i<1000000;i++){
                    try {
                        MqttUtils.getInstance().pub("xiaomi_8", MqttUtils.TOPIC_ONE, new MqttUtils.IMqttResultListener() {
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
//                io.reactivex.Observable.interval(1, TimeUnit.MICROSECONDS)
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(new Observer<Long>() {
//                            @Override
//                            public void onSubscribe(Disposable d) {
//
//                            }
//
//                            @Override
//                            public void onNext(Long aLong) {
//                                try {
//                                    MqttUtils.getInstance().pub("oppo_A59S", MqttUtils.TOPIC_ONE, new MqttUtils.IMqttResultListener() {
//                                        @Override
//                                        public void success() {
//
//                                        }
//
//                                        @Override
//                                        public void fail() {
//
//                                        }
//                                    });
//                                } catch (MqttException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//
//                            @Override
//                            public void onError(Throwable e) {
//
//                            }
//
//                            @Override
//                            public void onComplete() {
//
//                            }
//                        });
                break;
            case R.id.tv_subscriber:
                try {
                    MqttUtils.getInstance().sub(MqttUtils.TOPIC_TWO);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tv_unSubscriber:
                try {
                    MqttUtils.getInstance().unsub(MqttUtils.TOPIC_TWO);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
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
}
