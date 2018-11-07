package com.qvd.smartswitch.activity.home;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.utils.MqttAndroidUtils;
import com.qvd.smartswitch.utils.ToastUtil;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import butterknife.BindView;
import butterknife.OnClick;

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

    private MqttAndroidClient androidClient;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_mqtt_test;
    }

    @Override
    protected void initData() {
        super.initData();
        androidClient = MqttAndroidUtils.getInstance().getMqttAndroidClient(MqttAndroidUtils.MqttServerUri);
        androidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, org.eclipse.paho.client.mqttv3.MqttMessage message) throws Exception {

            }

            @Override
            public void deliveryComplete(org.eclipse.paho.client.mqttv3.IMqttDeliveryToken token) {

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
                MqttConnectOptions mqttConnectOptions = MqttAndroidUtils.getInstance().getMqttConnectOptions();
                try {
                    androidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            ToastUtil.showToast("success");
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                            Logger.e("fail" + exception.getMessage() + asyncActionToken.toString());
                        }
                    });
                } catch (org.eclipse.paho.client.mqttv3.MqttException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tv_disconnect:
                break;
            case R.id.tv_publish:
                break;
            case R.id.tv_subscriber:
                break;
            case R.id.tv_unSubscriber:
                break;
        }
    }
}
