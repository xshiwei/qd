package com.qvd.smartswitch.activity.device;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.CoordinatorLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleNotifyCallback;
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
import com.qvd.smartswitch.db.DeviceNickNameDaoOpe;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.FucUtil;
import com.qvd.smartswitch.utils.JsonParser;
import com.qvd.smartswitch.utils.RuntimeRationale;
import com.qvd.smartswitch.utils.SnackbarUtils;
import com.qvd.smartswitch.widget.MyProgressDialog;
import com.squareup.picasso.Picasso;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by Administrator on 2018/4/3.
 */

public class DeviceControlTwoActivity extends BaseActivity {


    @BindView(R.id.iv_device_control_goback)
    ImageView ivDeviceControlGoback;
    @BindView(R.id.tv_device_control_title)
    TextView tvDeviceControlTitle;
    @BindView(R.id.iv_device_control_more)
    ImageView ivDeviceControlMore;

    private static String TAG = DeviceControlTwoActivity.class.getSimpleName();
    @BindView(R.id.iv_sound)
    ImageView ivSound;
    @BindView(R.id.tv_light_one)
    TextView tvLightOne;
    @BindView(R.id.tv_light_two)
    TextView tvLightTwo;
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    /**
     * 灯1未开启
     */
    private boolean isStateOne = false;
    /**
     * 灯2未开启
     */
    private boolean isStatetwo = false;
    private BleDevice bledevice;

    private String deviceNickname;
    private Disposable subscribe;

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
    private MyProgressDialog dialog;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_device_control_two;
    }

    @Override
    protected void initData() {
        super.initData();
        bledevice = getIntent().getParcelableExtra("bledevice");
        if (bledevice != null) {
            deviceNickname = DeviceNickNameDaoOpe.queryOne(this, CommonUtils.getMac(bledevice.getMac())).getDeviceNickname();
        }

        // 初始化识别对象
        mAsr = SpeechRecognizer.createRecognizer(this, mInitListener);
        mCloudGrammar = FucUtil.readFile(this, "grammar_sample.abnf", "utf-8");

        mSharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);

        dialog = MyProgressDialog.createProgressDialog(this, 5000, new MyProgressDialog.OnTimeOutListener() {
            @Override
            public void onTimeOut(ProgressDialog dialog) {
                dialog.dismiss();
                SnackbarUtils.Short(coordinatorLayout, "连接超时").show();
            }
        });
    }

    /**
     * 获取通知
     */
    private void getNotify() {
        subscribe = Observable.interval(100, 100, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        BleManager.getInstance().notify(bledevice, "0000fff0-0000-1000-8000-00805f9b34fb", "0000fff6-0000-1000-8000-00805f9b34fb", new BleNotifyCallback() {
                            @Override
                            public void onNotifySuccess() {
                                Logger.e("notify-> success");
                            }

                            @Override
                            public void onNotifyFailure(BleException exception) {
                                Logger.e("notify->" + exception.toString());
                            }

                            @Override
                            public void onCharacteristicChanged(byte[] data) {
                                Logger.e("notify->" + HexUtil.formatHexString(data, false));
                            }
                        });
                        if (!BleManager.getInstance().isConnected(bledevice)) {
                            SnackbarUtils.Short(coordinatorLayout, "蓝牙未连接utils").show();
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        //移动数据统计分析
        FlowerCollector.onResume(this);
        FlowerCollector.onPageStart(TAG);
        super.onResume();
        if (bledevice != null) {
            getNotify();
        }
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.app_color).init();
    }


    @OnClick({R.id.iv_device_control_goback, R.id.iv_device_control_more, R.id.iv_sound, R.id.tv_light_one, R.id.tv_light_two})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_device_control_goback:
                finish();
                break;
            case R.id.iv_device_control_more:
                startActivity(new Intent(this, DeviceControlSettingActivity.class).putExtra("bledevice", bledevice));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.iv_sound:
                //语音控制开关灯
                setSound();
                break;
            case R.id.tv_light_one:
                if (isStateOne) {
                    //关灯
                    writeToBleOne(String.valueOf("fe0100201fffffffffffffffffffffffffffffff"));
                } else {
                    //开灯
                    writeToBleOne(String.valueOf("fe01002120ffffffffffffffffffffffffffffff"));
                }
                break;
            case R.id.tv_light_two:
                if (isStatetwo) {
                    //关灯
                    writeToBleTwo(String.valueOf("fe0100100fffffffffffffffffffffffffffffff"));
                } else {
                    //开灯
                    writeToBleTwo(String.valueOf("fe01001110ffffffffffffffffffffffffffffff"));
                }
                break;
        }
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
     * 向Ble写入数据
     *
     * @param s
     */
    private void writeToBleOne(String s) {
        if (bledevice == null) {
            return;
        }
        BleManager.getInstance().write(bledevice, "0000fff0-0000-1000-8000-00805f9b34fb", "0000fff6-0000-1000-8000-00805f9b34fb", HexUtil.hexStringToBytes(s), true, new BleWriteCallback() {
            @Override
            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                Logger.e("write success, current: " + current
                        + " total: " + total
                        + " justWrite: " + HexUtil.formatHexString(justWrite, true));
                if (!isStateOne) {
                    isStateOne = true;
                    tvLightOne.setText("开");
                } else {
                    isStateOne = false;
                    tvLightOne.setText("关");
                }
            }

            @Override
            public void onWriteFailure(BleException exception) {
                Logger.e("write" + exception.toString());
            }
        });
    }


    /**
     * 向Ble写入数据
     *
     * @param s
     */
    private void writeToBleTwo(String s) {
        if (bledevice == null) {
            return;
        }
        BleManager.getInstance().write(bledevice, "0000fff0-0000-1000-8000-00805f9b34fb", "0000fff6-0000-1000-8000-00805f9b34fb", HexUtil.hexStringToBytes(s), true, new BleWriteCallback() {
            @Override
            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                Logger.e("write success, current: " + current
                        + " total: " + total
                        + " justWrite: " + HexUtil.formatHexString(justWrite, true));
                if (!isStatetwo) {
                    isStatetwo = true;
                    tvLightTwo.setText("开");
                } else {
                    isStatetwo = false;
                    tvLightTwo.setText("关");
                }
            }

            @Override
            public void onWriteFailure(BleException exception) {
                Logger.e("write" + exception.toString());
            }
        });
    }

    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
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
                Picasso.with(DeviceControlTwoActivity.this).load(R.mipmap.voice_empty).into(ivSound);
            } else {
                Picasso.with(DeviceControlTwoActivity.this).load(R.mipmap.voice_full).into(ivSound);
            }
        }

        @Override
        public void onResult(final RecognizerResult result, boolean isLast) {
            if (null != result) {
                Log.d(TAG, "recognizer result：" + result.getResultString());
                String text;
                if ("cloud".equalsIgnoreCase(mEngineType)) {
                    text = JsonParser.parseIatResult(result.getResultString());
                } else {
                    text = JsonParser.parseLocalGrammarResult(result.getResultString());
                }
                Logger.e(text);
                if (text.contains("开") || text.contains("open")) {
                    isStateOne = true;
                    dialog.show();
                    //开灯
                    writeToBleOne(String.valueOf("FE01000100FFFFFFFFFFFFFFFFFFFFFFFFFFFFFF"));
                } else if (text.contains("关") || text.contains("close")) {
                    isStateOne = false;
                    //关灯
                    dialog.show();
                    writeToBleOne(String.valueOf("FE010000FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF"));
                } else {
                    SnackbarUtils.Short(coordinatorLayout, "识别不出来哦").show();
                }
            } else {
                Log.d(TAG, "recognizer result : null");
            }
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            showTip("结束说话");
        }

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            showTip("开始说话");
        }

        @Override
        public void onError(SpeechError error) {
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


    private void requestPermission() {
        AndPermission.with(this)
                .runtime()
                .permission(Permission.RECORD_AUDIO)
                .rationale(new RuntimeRationale())
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        SnackbarUtils.Short(coordinatorLayout, "授权成功").show();
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        SnackbarUtils.Short(coordinatorLayout, "授权失败").show();
                    }
                })
                .start();
    }


    private void showTip(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SnackbarUtils.Short(coordinatorLayout, str).show();
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
    protected void onPause() {
        //移动数据统计分析
        FlowerCollector.onPageEnd(TAG);
        FlowerCollector.onPause(this);
        super.onPause();
        if (subscribe != null) {
            subscribe.dispose();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (subscribe != null) {
            subscribe.dispose();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscribe != null) {
            subscribe.dispose();
        }
        if (null != mAsr) {
            // 退出时释放连接
            mAsr.cancel();
            mAsr.destroy();
        }
    }
}
