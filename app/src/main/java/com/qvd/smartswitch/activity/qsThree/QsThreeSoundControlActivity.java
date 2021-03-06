package com.qvd.smartswitch.activity.qsThree;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.GrammarListener;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.sunflower.FlowerCollector;
import com.orhanobut.logger.Logger;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.FucUtil;
import com.qvd.smartswitch.utils.JsonParser;
import com.qvd.smartswitch.utils.MqttAndroidUtils;
import com.qvd.smartswitch.utils.RuntimeRationale;
import com.qvd.smartswitch.utils.SnackbarUtils;
import com.qvd.smartswitch.utils.ToastUtil;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import butterknife.BindView;
import butterknife.OnClick;

public class QsThreeSoundControlActivity extends BaseActivity {
    @BindView(R.id.tv_text)
    TextView tvText;
    @BindView(R.id.ll_topic_text)
    LinearLayout llTopicText;
    @BindView(R.id.iv_topic)
    ImageView ivTopic;
    @BindView(R.id.iv_sound)
    ImageView ivSound;
    @BindView(R.id.iv_close)
    ImageView ivClose;

    //语音控制变量
    // 语音识别对象
    private SpeechRecognizer mAsr;
    // 缓存
    private SharedPreferences mSharedPreferences;
    // 云端语法文件
    private String mCloudGrammar = null;

    private static final String KEY_GRAMMAR_ABNF_ID = "grammar_abnf_id";
    private static final String GRAMMAR_TYPE_ABNF = "abnf";
    private static final String GRAMMAR_TYPE_BNF = "bnf";

    private final String mEngineType = SpeechConstant.TYPE_CLOUD;
    // 语法、词典临时变量
    private String mContent;
    // 函数调用返回值
    private int ret = 0;
    private static final String TAG = QsThreeSoundControlActivity.class.getSimpleName();
    private String device_id;
    private MqttAndroidClient mqttAndroidClient;
    private MqttConnectOptions mqttConnectOptions;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_qsthree_sound_control;
    }

    @Override
    protected void initData() {
        super.initData();
        // 初始化识别对象
        mqttAndroidClient = MqttAndroidUtils.getInstance().getMqttAndroidClient(MqttAndroidUtils.MqttServerUri);
        mqttConnectOptions = MqttAndroidUtils.getInstance().getMqttConnectOptions();
        device_id = getIntent().getStringExtra("device_id");
        mAsr = SpeechRecognizer.createRecognizer(this, mInitListener);
        mCloudGrammar = FucUtil.readFile(this, "grammar_sample.abnf", "utf-8");
        mSharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.statusBarDarkFont(false);
    }

    @OnClick({R.id.iv_topic, R.id.iv_sound, R.id.iv_close})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_topic:
                ToastUtil.showToast("功能开发中。。。");
                break;
            case R.id.iv_sound:
                tvText.setText("你可以这样说：");
                setSound();
                break;
            case R.id.iv_close:
                finish();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mqttAndroidClient.isConnected()) {
            //连接
            try {
                mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {

                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                    }
                });
            } catch (org.eclipse.paho.client.mqttv3.MqttException e) {
                e.printStackTrace();
            }

        }
        FlowerCollector.onResume(this);
        FlowerCollector.onPageStart(TAG);
    }

    /**
     * 设置语音控制
     */
    private void setSound() {
        if (null == mAsr) {
            // 创建单例失败，与 21001 错误为同样原因，参考 http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=9688
            Logger.e("创建对象失败，请确认 libmsc.so 放置正确，且有调用 createUtility 进行初始化");
            return;
        }

        mContent = mCloudGrammar;
        //指定引擎类型
        mAsr.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        mAsr.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
        ret = mAsr.buildGrammar(GRAMMAR_TYPE_ABNF, mContent, mCloudGrammarListener);
        if (ret != ErrorCode.SUCCESS) {
            Logger.e("语法构建失败,错误码：" + ret);
            return;
        }
        requestPermission();
        // 设置参数
        if (!setParam()) {
            Logger.e("请先构建语法。");
            return;
        }
        ret = mAsr.startListening(mRecognizerListener);
        if (ret != ErrorCode.SUCCESS) {
            Logger.e("识别失败,错误码: " + ret);
        }
    }

    /**
     * 初始化监听器。
     */
    private final InitListener mInitListener = code -> {
        if (code != ErrorCode.SUCCESS) {
            Logger.e("初始化失败,错误码：" + code);
        }
    };

    /**
     * 云端构建语法监听器。
     */
    private final GrammarListener mCloudGrammarListener = new GrammarListener() {
        @Override
        public void onBuildFinish(String grammarId, SpeechError error) {
            if (error == null) {
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                if (!TextUtils.isEmpty(grammarId))
                    editor.putString(KEY_GRAMMAR_ABNF_ID, grammarId);
                editor.apply();
            } else {
                Logger.e("语法构建失败,错误码：" + error.getErrorCode());
            }
        }
    };

    /**
     * 识别监听器。
     */
    private final RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            if (volume < 10) {
                Glide.with(QsThreeSoundControlActivity.this).load(R.mipmap.voice_empty).into(ivSound);
            } else {
                Glide.with(QsThreeSoundControlActivity.this).load(R.mipmap.voice_full).into(ivSound);
            }
        }

        @Override
        public void onResult(final RecognizerResult result, boolean isLast) {
            if (null != result) {
                String text;
                text = JsonParser.parseIatResult(result.getResultString());
                Logger.e(text);
                tvText.setText(text);
                if (text.contains("一")) {
                    if (text.contains("开") || text.contains("open")) {
                        //开灯
                        publish("{\"key_one\":\"led1\",\"key_two\":\"11\"}");
                        CommonUtils.addDeviceLog(device_id, "qs03", "语音打开一号灯");
                    } else if (text.contains("关") || text.contains("close")) {
                        //关灯
                        publish("{\"key_one\":\"led1\",\"key_two\":\"10\"}");
                        CommonUtils.addDeviceLog(device_id, "qs03", "语音关闭一号灯");
                    }
                } else if (text.contains("二")) {
                    if (text.contains("开") || text.contains("open")) {
                        //开灯
                        publish("{\"key_one\":\"led2\",\"key_two\":\"21\"}");
                        CommonUtils.addDeviceLog(device_id, "qs03", "语音打开二号灯");
                    } else if (text.contains("关") || text.contains("close")) {
                        //关灯
                        publish("{\"key_one\":\"led2\",\"key_two\":\"20\"}");
                        CommonUtils.addDeviceLog(device_id, "qs03", "语音关闭二号灯");
                    }
                } else if (text.contains("全部")) {
                    if (text.contains("开") || text.contains("open")) {
                        //开灯
                        publish("{\"key_one\":\"led1\",\"key_two\":\"11\"}");
                        CommonUtils.addDeviceLog(device_id, "qs03", "语音打开一号灯");
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //开灯
                        publish("{\"key_one\":\"led2\",\"key_two\":\"21\"}");
                        CommonUtils.addDeviceLog(device_id, "qs03", "语音打开二号灯");
                    } else if (text.contains("关") || text.contains("close")) {
                        //关灯
                        publish("{\"key_one\":\"led1\",\"key_two\":\"10\"}");
                        CommonUtils.addDeviceLog(device_id, "qs03", "语音关闭一号灯");
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //关灯
                        publish("{\"key_one\":\"led2\",\"key_two\":\"20\"}");
                        CommonUtils.addDeviceLog(device_id, "qs03", "语音关闭二号灯");
                    }
                } else {
                    tvText.setText("识别不出来哦");
                }
            }
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            showTip("结束说话");
            llTopicText.setVisibility(View.GONE);
        }

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            showTip("开始说话");
            llTopicText.setVisibility(View.VISIBLE);
        }

        @Override
        public void onError(SpeechError error) {
            llTopicText.setVisibility(View.VISIBLE);
            showTip("onError Code：" + error.getErrorCode());
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {

        }

    };

    /**
     * 灯发布
     *
     * @param s
     */
    private void publish(String s) {
        MqttMessage message = new MqttMessage();
        message.setQos(2);
        message.setPayload(s.getBytes());
        try {
            mqttAndroidClient.publish(MqttAndroidUtils.TOPIC_ONE, message, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                }
            });
        } catch (org.eclipse.paho.client.mqttv3.MqttException e) {
            e.printStackTrace();
        }
    }

    private void requestPermission() {
        AndPermission.with(this)
                .runtime()
                .permission(Permission.RECORD_AUDIO)
                .rationale(new RuntimeRationale())
                .onGranted(data -> SnackbarUtils.Short(tvText, "授权成功").show())
                .onDenied(data -> SnackbarUtils.Short(tvText, "授权失败").show())
                .start();
    }


    private void showTip(final String str) {
        runOnUiThread(() -> SnackbarUtils.Short(tvText, str).show());
    }

    /**
     * 参数设置
     *
     * @return
     */
    private boolean setParam() {
        boolean result;
        //设置识别引擎
        mAsr.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        //设置返回结果为json格式
        mAsr.setParameter(SpeechConstant.RESULT_TYPE, "json");

        String grammarId = mSharedPreferences.getString(KEY_GRAMMAR_ABNF_ID, null);
        if (TextUtils.isEmpty(grammarId)) {
            result = false;
        } else {
            //设置云端识别使用的语法id
            mAsr.setParameter(SpeechConstant.CLOUD_GRAMMAR, grammarId);
            result = true;
        }

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mAsr.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mAsr.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/asr.wav");
        return result;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mAsr) {
            // 退出时释放连接
            mAsr.cancel();
            mAsr.destroy();
        }
        //移动数据统计分析
        FlowerCollector.onPageEnd(TAG);
        FlowerCollector.onPause(this);
    }
}
