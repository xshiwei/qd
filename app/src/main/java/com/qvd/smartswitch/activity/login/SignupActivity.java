package com.qvd.smartswitch.activity.login;

import android.content.Intent;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.login.MessageVo;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.RegexpUtils;
import com.qvd.smartswitch.utils.SnackbarUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2018-7-7.
 */

public class SignupActivity extends BaseActivity {
    @BindView(R.id.rl_actionbar)
    RelativeLayout rlActionbar;
    @BindView(R.id.tv_phone_register)
    TextView tvPhoneRegister;
    @BindView(R.id.tv_email_register)
    TextView tvEmailRegister;
    @BindView(R.id.et_input_phone)
    EditText etInputPhone;
    @BindView(R.id.et_input_code)
    EditText etInputCode;
    @BindView(R.id.tv_phone_code)
    TextView tvPhoneCode;
    @BindView(R.id.rl_input_code)
    RelativeLayout rlInputCode;
    @BindView(R.id.btn_phone_next)
    TextView btnPhoneNext;
    @BindView(R.id.et_input_email)
    EditText etInputEmail;
    @BindView(R.id.tv_email_code)
    TextView tvEmailCode;
    @BindView(R.id.rl_input_email)
    RelativeLayout rlInputEmail;
    @BindView(R.id.btn_email_next)
    TextView btnEmailNext;
    @BindView(R.id.rl_phone)
    RelativeLayout rlPhone;
    @BindView(R.id.rl_email)
    RelativeLayout rlEmail;
    @BindView(R.id.iv_goback)
    ImageView ivGoback;
    @BindView(R.id.tv_country)
    TextView tvCountry;
    @BindView(R.id.tv_email_error)
    TextView tvEmailError;
    @BindView(R.id.tv_phone_error)
    TextView tvPhoneError;
    @BindView(R.id.et_input_email_code)
    EditText etInputEmailCode;

    private final PhoneCountDownTimer phoneCountDownTimer = new PhoneCountDownTimer(60000, 1000);//60000

    private final EmailCountDownTimer emailCountDownTimer = new EmailCountDownTimer(60000, 1000);


    /**
     * 表示是手机号注册还是邮箱注册（1表示邮箱，2表示手机号）
     */
    private String identity_type;


    @Override
    protected int setLayoutId() {
        return R.layout.activity_signup;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(false).transparentBar().init();
    }

    @Override
    protected void initData() {
        super.initData();
        etInputPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //当edittext文字改变时将tvPhoneError设为不可见
                tvPhoneError.setVisibility(View.GONE);
            }
        });

        etInputEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //当edittext文字改变时将tvEmailError设为不可见
                tvEmailError.setVisibility(View.GONE);
            }
        });
    }

    @OnClick({R.id.tv_phone_register, R.id.tv_email_register, R.id.btn_phone_next, R.id.tv_email_code, R.id.btn_email_next,
            R.id.tv_phone_code, R.id.iv_goback, R.id.tv_country})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_phone_register:
                //手机号注册
                rlPhone.setVisibility(View.VISIBLE);
                rlEmail.setVisibility(View.GONE);
                tvPhoneRegister.setTextColor(getResources().getColor(R.color.capacity_tablayout_text_two));
                tvEmailRegister.setTextColor(getResources().getColor(R.color.capacity_tablayout_text));
                break;
            case R.id.tv_email_register:
                //邮箱注册
                rlPhone.setVisibility(View.GONE);
                rlEmail.setVisibility(View.VISIBLE);
                tvPhoneRegister.setTextColor(getResources().getColor(R.color.capacity_tablayout_text));
                tvEmailRegister.setTextColor(getResources().getColor(R.color.capacity_tablayout_text_two));
                break;
            case R.id.btn_phone_next:
                //手机号下一步
                if (CommonUtils.isEmptyString(etInputPhone.getText().toString().trim()) ||
                        CommonUtils.isEmptyString(etInputCode.getText().toString().trim())) {
                    SnackbarUtils.Short(btnPhoneNext, "手机号或验证码不能为空").show();
                    return;
                }
                isVerificationCode(2, etInputPhone.getText().toString(), etInputCode.getText().toString());
                break;
            case R.id.tv_email_code:
                //邮箱验证码
                getEmailCode();
                break;
            case R.id.btn_email_next:
                //邮箱下一步
                if (CommonUtils.isEmptyString(etInputEmail.getText().toString().trim())) {
                    SnackbarUtils.Short(btnEmailNext, "邮箱不能为空").show();
                    return;
                }
                isVerificationCode(1, etInputEmail.getText().toString(), etInputEmailCode.getText().toString());
                break;
            case R.id.tv_phone_code:
                //手机验证码
                getPhoneCode();
                break;
            case R.id.iv_goback:
                finish();
                break;
            case R.id.tv_country:
                //选择国家地区
                break;
        }
    }


    /**
     * 判断账号和验证码是否匹配
     */
    private void isVerificationCode(int type, String username, String code) {
        RetrofitService.qdoApi.isVerificationCode(username, code)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MessageVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MessageVo messageVo) {
                        if (messageVo != null) {
                            if (messageVo.getCode() == 200) {
                                if (type == 1) {
                                    startActivity(new Intent(SignupActivity.this, SetPasswordActivity.class).putExtra("userName", etInputEmail.getText().toString().trim())
                                            .putExtra("identity_type", "1"));
                                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                } else {
                                    startActivity(new Intent(SignupActivity.this, SetPasswordActivity.class).putExtra("userName", etInputPhone.getText().toString().trim())
                                            .putExtra("identity_type", "2"));
                                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                }
                            } else {
                                SnackbarUtils.Short(btnEmailNext, "验证码与账号不匹配");
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        SnackbarUtils.Short(btnEmailNext, "网络连接失败");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 获取手机验证码
     */
    private void getPhoneCode() {
        if (!validatePhone()) {
            return;
        }
        isEmailValidation(2, etInputPhone.getText().toString());
    }

    /**
     * 获取邮箱验证码
     */
    private void getEmailCode() {
        if (!validateEmail()) {
            return;
        }
        isEmailValidation(1, etInputEmail.getText().toString());
    }

    /**
     * 判断验证短信是否发送
     */
    private void isEmailValidation(int type, String username) {
        RetrofitService.qdoApi.isSameUserName(username)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<MessageVo>() {
                    @Override
                    public void accept(MessageVo messageVo) throws Exception {
                        if (messageVo.getCode() == 200) {
                            if (type == 1) {
                                tvEmailError.setVisibility(View.VISIBLE);
                                tvEmailError.setText("该邮箱已被注册");
                            } else {
                                tvPhoneError.setVisibility(View.VISIBLE);
                                tvPhoneError.setText("该手机号已被注册");
                            }
                        }
                    }
                })
                .filter(new Predicate<MessageVo>() {
                    @Override
                    public boolean test(MessageVo messageVo) throws Exception {
                        return messageVo.getCode() != 200;
                    }
                })
                .observeOn(Schedulers.io())
                .concatMap(new Function<MessageVo, ObservableSource<MessageVo>>() {
                    @Override
                    public ObservableSource<MessageVo> apply(MessageVo messageVo) throws Exception {
                        if (type == 1) {
                            //表示邮箱获取验证码
                            Map<String, Object> map = new HashMap<>();
                            map.put("username", username);
                            map.put("identity_type", "1");
                            return RetrofitService.qdoApi.isUsernameValidation(map);
                        } else {
                            //表示手机号获取验证码
                            Map<String, Object> map = new HashMap<>();
                            map.put("username", username);
                            map.put("identity_type", "2");
                            map.put("sms_type", 0);
                            return RetrofitService.qdoApi.isUsernameValidation(map);
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MessageVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MessageVo messageVo) {
                        if (messageVo.getCode() == 200) {
                            if (type == 1) {
                                emailCountDownTimer.start();
                            } else {
                                phoneCountDownTimer.start();
                            }
                            SnackbarUtils.Short(btnEmailNext, "验证码发送成功").show();
                        } else if (messageVo.getCode() == 400) {
                            SnackbarUtils.Short(btnEmailNext, "验证码发送失败").show();
                        } else if (messageVo.getCode() == 405) {
                            SnackbarUtils.Short(btnEmailNext, "验证码不能重复发送").show();
                        } else {
                            SnackbarUtils.Short(btnEmailNext, "网络连接超时").show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        SnackbarUtils.Short(btnEmailNext, "网络连接失败");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 验证手机号
     */
    private boolean validatePhone() {
        boolean valid = true;
        String phone = etInputPhone.getText().toString().trim();
        if (CommonUtils.isEmptyString(phone)) {
            valid = false;
            tvPhoneError.setVisibility(View.VISIBLE);
            tvPhoneError.setText("手机号码不能为空");
        } else if (!RegexpUtils.isMobileNO(phone)) {
            valid = false;
            tvPhoneError.setVisibility(View.VISIBLE);
            tvPhoneError.setText("手机号码格式不正确");
        } else {
            tvPhoneError.setVisibility(View.GONE);
        }
        return valid;
    }

    /**
     * 验证验证码
     */
    private boolean validateCode() {
        boolean valid = true;
        String code = etInputCode.getText().toString().trim();
        if (CommonUtils.isEmptyString(code) || code.length() != 6) {
            valid = false;
        }
        return valid;
    }

    /**
     * 验证邮箱
     */
    private boolean validateEmail() {
        boolean valid = true;
        String email = etInputEmail.getText().toString().trim();
        if (CommonUtils.isEmptyString(email)) {
            valid = false;
            tvEmailError.setVisibility(View.VISIBLE);
            tvEmailError.setText("邮箱不能为空");
        } else if (!RegexpUtils.isEmailNO(email)) {
            valid = false;
            tvEmailError.setVisibility(View.VISIBLE);
            tvEmailError.setText("邮箱格式不正确");
        } else {
            tvEmailError.setVisibility(View.GONE);
        }
        return valid;
    }

    /**
     * 创建手机号验证码定时器
     */
    public class PhoneCountDownTimer extends CountDownTimer {

        public PhoneCountDownTimer(long millisInFuture, long countDownInterval) {
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

    /**
     * 创建邮箱验证码定时器
     */
    public class EmailCountDownTimer extends CountDownTimer {

        public EmailCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
            //防止计时过程中重复点击
            tvEmailCode.setClickable(false);
            tvEmailCode.setText("" + l / 1000);
        }

        @Override
        public void onFinish() {
            //重新给Button设置文字
            tvEmailCode.setText("发送邮箱验证码");
            //设置可点击
            tvEmailCode.setClickable(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //页面关闭时计数器也关闭
        phoneCountDownTimer.cancel();
        emailCountDownTimer.cancel();
    }
}
