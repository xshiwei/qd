package com.qvd.smartswitch.activity.login;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.login.MessageVo;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.ToastUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ResetPasswordThreeActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.tv_text)
    TextView tvText;
    @BindView(R.id.et_input_code)
    EditText etInputCode;
    @BindView(R.id.tv_phone_code)
    TextView tvPhoneCode;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;
    @BindView(R.id.tv_other_vaild)
    TextView tvOtherVaild;

    private int type;
    private String account;

    private final CountDownTimer countDownTimer = new CountDownTimer(60000, 1000);//60000
    private HashMap<String, Object> map;


    @Override
    protected int setLayoutId() {
        return R.layout.activity_reset_password_three;
    }

    @Override
    protected void initData() {
        super.initData();
        type = getIntent().getIntExtra("type", -1);
        account = getIntent().getStringExtra("account");
        tvCommonActionbarTitle.setText("科微多账号-科微多安全验证");
        map = new HashMap<>();
        if (type == 1) {
            map.put("username", account);
            map.put("identity_type", "1");
            tvText.setText("请使用安全邮箱" + account + "获取验证码短信");
        } else if (type == 2) {
            map.put("username", account);
            map.put("identity_type", "2");
            map.put("sms_type", 0);
            tvText.setText("请使用安全手机" + account + "获取验证码短信");
        }
        sendCode();
    }

    /**
     * 发送验证码
     */
    private void sendCode() {
        RetrofitService.qdoApi.userForgetPasswordValidation(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MessageVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MessageVo messageVo) {
                        if (messageVo.getCode() == 200) {
                            countDownTimer.start();
                            ToastUtil.showToast("发送成功");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.add_room_background).init();
    }

    @OnClick({R.id.iv_common_actionbar_goback, R.id.tv_phone_code, R.id.tv_confirm, R.id.tv_other_vaild})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.tv_phone_code:
                //获取验证码
                sendCode();
                break;
            case R.id.tv_confirm:
                //确认
                if (CommonUtils.isEmptyString(etInputCode.getText().toString())) {
                    ToastUtil.showToast("验证码不能为空");
                } else if (etInputCode.getText().toString().length() != 6) {
                    ToastUtil.showToast("验证码长度错误");
                } else {
                    vaildCode();
                }
                break;
            case R.id.tv_other_vaild:
                //其他验证方式
                break;
        }
    }

    /**
     * 验证验证码是否正确
     */
    private void vaildCode() {
        RetrofitService.qdoApi.isVerificationCode(account, etInputCode.getText().toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MessageVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MessageVo messageVo) {
                        if (messageVo.getCode() == 200) {
                            startActivity(new Intent(ResetPasswordThreeActivity.this, ResetPasswordFourActivity.class)
                                    .putExtra("type", type)
                                    .putExtra("account", account));
                            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 创建手机号验证码定时器
     */
    public class CountDownTimer extends android.os.CountDownTimer {

        public CountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
            //防止计时过程中重复点击
            tvPhoneCode.setClickable(false);
            tvPhoneCode.setText("" + l / 1000);
        }

        @Override
        public void onFinish() {
            //重新给Button设置文字
            tvPhoneCode.setText("发送验证码");
            //设置可点击
            tvPhoneCode.setClickable(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
    }
}
