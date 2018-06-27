package com.qvd.smartswitch.activity.user;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.GrammarListener;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.LexiconListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.iflytek.sunflower.FlowerCollector;
import com.orhanobut.logger.Logger;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.model.DictationResult;
import com.qvd.smartswitch.utils.FucUtil;
import com.qvd.smartswitch.utils.JsonParser;
import com.qvd.smartswitch.utils.RuntimeRationale;
import com.qvd.smartswitch.utils.ToastUtil;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/5/17 0017.
 */

public class SoundControlActivity extends BaseActivity {


    @BindView(R.id.isr_text)
    EditText isrText;
    @BindView(R.id.isr_grammar)
    Button isrGrammar;
    @BindView(R.id.isr_recognize)
    Button isrRecognize;
    @BindView(R.id.isr_stop)
    Button isrStop;
    @BindView(R.id.isr_cancel)
    Button isrCancel;

    private static String TAG = SoundControlActivity.class.getSimpleName();
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

    @Override
    protected int setLayoutId() {
        return R.layout.activity_sound_control;
    }

    @Override
    protected void initData() {
        super.initData();
        // 初始化识别对象
        mAsr = SpeechRecognizer.createRecognizer(this, mInitListener);
        mCloudGrammar = FucUtil.readFile(this, "grammar_sample.abnf", "utf-8");

        mSharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
    }

    @OnClick({R.id.isr_grammar, R.id.isr_recognize, R.id.isr_stop, R.id.isr_cancel})
    public void onViewClicked(View view) {
        if (null == mAsr) {
            // 创建单例失败，与 21001 错误为同样原因，参考 http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=9688
            ToastUtil.showToast("创建对象失败，请确认 libmsc.so 放置正确，且有调用 createUtility 进行初始化");
            return;
        }

        if (null == mEngineType) {
            ToastUtil.showToast("请先选择识别引擎类型");
            return;
        }
        switch (view.getId()) {
            case R.id.isr_grammar:
                ToastUtil.showToast("上传预设关键词/语法文件");
                // 在线-构建语法文件，生成语法id
                isrText.setText(mCloudGrammar);
                mContent = new String(mCloudGrammar);
                //指定引擎类型
                mAsr.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
                mAsr.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
                ret = mAsr.buildGrammar(GRAMMAR_TYPE_ABNF, mContent, mCloudGrammarListener);
                if (ret != ErrorCode.SUCCESS)
                    ToastUtil.showToast("语法构建失败,错误码：" + ret);
                break;
            case R.id.isr_recognize:
                requestPermission();
                isrText.setText(null);// 清空显示内容
                // 设置参数
                if (!setParam()) {
                    showTip("请先构建语法。");
                    return;
                }

                ret = mAsr.startListening(mRecognizerListener);
                if (ret != ErrorCode.SUCCESS) {
                    showTip("识别失败,错误码: " + ret);
                }
                break;
            case R.id.isr_stop:
                mAsr.stopListening();
                showTip("停止识别");
                break;
            case R.id.isr_cancel:
//                mAsr.cancel();
//                showTip("取消识别");
                isrText.setText("");
                showSoundDialog();
                break;
        }
    }

    private void showSoundDialog() {
        RecognizerDialog mDialog = new RecognizerDialog(this, mInitListener);
//        mDialog.setParameter("asr_sch", "1");
//        mDialog.setParameter("nlp_version", "3.0");
        //设置监听，实现听写结果的回调
        mDialog.setListener(new RecognizerDialogListener() {
            String resultJson = "[";//放置在外边做类的变量则报错，会造成json格式不对

            @Override
            public void onResult(RecognizerResult recognizerResult, boolean isLast) {
                Logger.e(recognizerResult.getResultString());
                if (!isLast) {
                    resultJson += recognizerResult.getResultString() + ",";
                } else {
                    resultJson += recognizerResult.getResultString() + "]";
                }
                Logger.e(resultJson);
                if (isLast) {
                    //解析语音识别后返回的json格式的结果
                    Gson gson = new Gson();
                    List<DictationResult> resultList = gson.fromJson(resultJson,
                            new TypeToken<List<DictationResult>>() {
                            }.getType());
                    String result = "";
                    for (int i = 0; i < resultList.size() - 1; i++) {
                        result += resultList.get(i).toString();
                    }
                    isrText.setText(result);
                    //获取焦点
                    isrText.requestFocus();
                    //将光标定位到文字最后，以便修改
                    isrText.setSelection(result.length());
                }
            }

            @Override
            public void onError(SpeechError speechError) {
                //自动生成的方法存根
                speechError.getPlainDescription(true);
            }
        });
        //开始听写，需将sdk中的assets文件下的文件夹拷入项目的assets文件夹下（没有的话自己新建）
        mDialog.show();
    }

    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败,错误码：" + code);
            }
        }
    };

    /**
     * 更新词典监听器。
     */
    private LexiconListener mLexiconListener = new LexiconListener() {
        @Override
        public void onLexiconUpdated(String lexiconId, SpeechError error) {
            if (error == null) {
                showTip("词典更新成功");
            } else {
                showTip("词典更新失败,错误码：" + error.getErrorCode());
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
                showTip("语法构建成功：" + grammarId);
            } else {
                showTip("语法构建失败,错误码：" + error.getErrorCode());
            }
        }
    };

    /**
     * 识别监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            showTip("当前正在说话，音量大小：" + volume);
            Log.d(TAG, "返回音频数据：" + data.length);
        }

        @Override
        public void onResult(final RecognizerResult result, boolean isLast) {
            if (null != result) {
                Log.d(TAG, "recognizer result：" + result.getResultString());
                String text;
                if ("cloud".equalsIgnoreCase(mEngineType)) {
                    text = JsonParser.parseGrammarResult(result.getResultString());
                } else {
                    text = JsonParser.parseLocalGrammarResult(result.getResultString());
                }

                // 显示
                isrText.setText(text);
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
                        ToastUtil.showToast("授权成功");
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        ToastUtil.showToast("授权失败");
                    }
                })
                .start();
    }


    private void showTip(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtil.showToast(str);
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
    }

    @Override
    protected void onResume() {
        //移动数据统计分析
        FlowerCollector.onResume(this);
        FlowerCollector.onPageStart(TAG);
        super.onResume();
    }

    @Override
    protected void onPause() {
        //移动数据统计分析
        FlowerCollector.onPageEnd(TAG);
        FlowerCollector.onPause(this);
        super.onPause();
    }
}