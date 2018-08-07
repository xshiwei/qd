package com.qvd.smartswitch.activity.device;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;
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
import com.qvd.smartswitch.utils.RuntimeRationale;
import com.qvd.smartswitch.utils.SnackbarUtils;
import com.squareup.picasso.Picasso;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class DeviceSoundControlActivity extends BaseActivity {
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

    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    // 语法、词典临时变量
    String mContent;
    // 函数调用返回值
    int ret = 0;
    private BleDevice bledevice;
    private static String TAG = DeviceSoundControlActivity.class.getSimpleName();

    @Override
    protected int setLayoutId() {
        return R.layout.activity_sound_control;
    }

    @Override
    protected void initData() {
        super.initData();
        // 初始化识别对象
        bledevice = getIntent().getParcelableExtra("bledevice");
        mAsr = SpeechRecognizer.createRecognizer(this, mInitListener);
        mCloudGrammar = FucUtil.readFile(this, "grammar_sample.abnf", "utf-8");
        mSharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        //CommonUtils.getNotify(this, bledevice, "0000fff0-0000-1000-8000-00805f9b34fb", "0000fff6-0000-1000-8000-00805f9b34fb");
        CommonUtils.getConnectNotify(this, bledevice, tvText);
    }

    @OnClick({R.id.iv_topic, R.id.iv_sound, R.id.iv_close})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_topic:
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

        if (null == mEngineType) {
            Logger.e("请先选择识别引擎类型");
            return;
        }
        mContent = new String(mCloudGrammar);
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
            return;
        }
    }

    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            if (code != ErrorCode.SUCCESS) {
                Logger.e("初始化失败,错误码：" + code);
            }
        }
    };

    /**
     * 云端构建语法监听器。
     */
    private GrammarListener mCloudGrammarListener = new GrammarListener() {
        @Override
        public void onBuildFinish(String grammarId, SpeechError error) {
            if (error == null) {
                String grammarID = new String(grammarId);
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                if (!TextUtils.isEmpty(grammarId))
                    editor.putString(KEY_GRAMMAR_ABNF_ID, grammarID);
                editor.commit();
            } else {
                Logger.e("语法构建失败,错误码：" + error.getErrorCode());
            }
        }
    };

    /**
     * 识别监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            if (volume < 10) {
                Picasso.with(DeviceSoundControlActivity.this).load(R.mipmap.voice_empty).into(ivSound);
            } else {
                Picasso.with(DeviceSoundControlActivity.this).load(R.mipmap.voice_full).into(ivSound);
            }
        }

        @Override
        public void onResult(final RecognizerResult result, boolean isLast) {
            if (null != result) {
                String text;
                if ("cloud".equalsIgnoreCase(mEngineType)) {
                    text = JsonParser.parseIatResult(result.getResultString());
                } else {
                    text = JsonParser.parseLocalGrammarResult(result.getResultString());
                }
                Logger.e(text);
                tvText.setText(text);
                if (text.contains("一")) {
                    if (text.contains("开") || text.contains("open")) {
                        //开灯
                        writeToBle(String.valueOf("fe010011ffffffffffffffffffffffffffffffff"));
                    } else if (text.contains("关") || text.contains("close")) {
                        //关灯
                        writeToBle(String.valueOf("fe010010ffffffffffffffffffffffffffffffff"));
                    }
                } else if (text.contains("二")) {
                    if (text.contains("开") || text.contains("open")) {
                        //开灯
                        writeToBle(String.valueOf("fe010021ffffffffffffffffffffffffffffffff"));
                    } else if (text.contains("关") || text.contains("close")) {
                        //关灯
                        writeToBle(String.valueOf("fe010020ffffffffffffffffffffffffffffffff"));
                    }
                } else if (text.contains("全部")) {
                    if (text.contains("开") || text.contains("open")) {
                        //开灯
                        writeToBle(String.valueOf("fe010011ffffffffffffffffffffffffffffffff"));
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //开灯
                        writeToBle(String.valueOf("fe010021ffffffffffffffffffffffffffffffff"));
                    } else if (text.contains("关") || text.contains("close")) {
                        //关灯
                        writeToBle(String.valueOf("fe010010ffffffffffffffffffffffffffffffff"));
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //关灯
                        writeToBle(String.valueOf("fe010020ffffffffffffffffffffffffffffffff"));
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
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }

    };

    /**
     * 向Ble写入数据
     *
     * @param s
     */
    private void writeToBle(final String s) {
        if (bledevice == null) {
            return;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                BleManager.getInstance().write(bledevice, "0000fff0-0000-1000-8000-00805f9b34fb", "0000fff6-0000-1000-8000-00805f9b34fb", HexUtil.hexStringToBytes(s), true, new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        Logger.e("write success, current: " + current
                                + " total: " + total
                                + " justWrite: " + HexUtil.formatHexString(justWrite, true));
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {
                        Logger.e("write" + exception.toString());
                    }
                });
            }
        }, 100);
    }

    private void requestPermission() {
        AndPermission.with(this)
                .runtime()
                .permission(Permission.RECORD_AUDIO)
                .rationale(new RuntimeRationale())
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        SnackbarUtils.Short(tvText, "授权成功").show();
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        SnackbarUtils.Short(tvText, "授权失败").show();
                    }
                })
                .start();
    }


    private void showTip(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SnackbarUtils.Short(tvText, str).show();
            }
        });
    }

    /**
     * 参数设置
     *
     * @return
     */
    public boolean setParam() {
        boolean result = false;
        //设置识别引擎
        mAsr.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        //设置返回结果为json格式
        mAsr.setParameter(SpeechConstant.RESULT_TYPE, "json");

        if ("cloud".equalsIgnoreCase(mEngineType)) {
            String grammarId = mSharedPreferences.getString(KEY_GRAMMAR_ABNF_ID, null);
            if (TextUtils.isEmpty(grammarId)) {
                result = false;
            } else {
                //设置云端识别使用的语法id
                mAsr.setParameter(SpeechConstant.CLOUD_GRAMMAR, grammarId);
                result = true;
            }
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
        BleManager.getInstance().stopNotify(bledevice, "0000fff0-0000-1000-8000-00805f9b34fb", "0000fff6-0000-1000-8000-00805f9b34fb");
    }
}
