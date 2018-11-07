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
import com.qvd.smartswitch.utils.ToastUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
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
                    ToastUtil.showToast(getString(R.string.signup_phone_vaild_not_empty));
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
                if (CommonUtils.isEmptyString(etInputEmail.getText().toString().trim()) || CommonUtils.isEmptyString(etInputEmailCode.getText().toString().trim())) {
                    ToastUtil.showToast(getString(R.string.signup_email_not_empty));
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
                                ToastUtil.showToast(getString(R.string.signup_vaild_account_not_vaild));
                            }
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
                .doOnNext(messageVo -> {
                    if (messageVo.getCode() == 200) {
                        if (type == 1) {
                            tvEmailError.setVisibility(View.VISIBLE);
                            tvEmailError.setText(R.string.common_email_be_registered);
                        } else {
                            tvPhoneError.setVisibility(View.VISIBLE);
                            tvPhoneError.setText(R.string.common_phone_be_registered);
                        }
                    }
                })
                .filter(messageVo -> messageVo.getCode() != 200)
                .observeOn(Schedulers.io())
                .concatMap((Function<MessageVo, ObservableSource<MessageVo>>) messageVo -> {
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
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MessageVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MessageVo messageVo) {
                        switch (messageVo.getCode()) {
                            case 200:
                                if (type == 1) {
                                    emailCountDownTimer.start();
                                } else {
                                    phoneCountDownTimer.start();
                                }
                                ToastUtil.showToast(getString(R.string.common_code_send_success));
                                break;
                            case 400:
                                ToastUtil.showToast(getString(R.string.common_code_send_fail));
                                break;
                            case 405:
                                ToastUtil.showToast(getString(R.string.common_vaild_not_repeat_send));
                                break;
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
     * 验证手机号
     */
    private boolean validatePhone() {
        boolean valid = true;
        String phone = etInputPhone.getText().toString().trim();
        if (CommonUtils.isEmptyString(phone)) {
            valid = false;
            tvPhoneError.setVisibility(View.VISIBLE);
            tvPhoneError.setText(R.string.common_phone_not_empty);
        } else if (!RegexpUtils.isMobileNO(phone)) {
            valid = false;
            tvPhoneError.setVisibility(View.VISIBLE);
            tvPhoneError.setText(R.string.common_phone_type_error);
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
            tvEmailError.setText(R.string.common_email_not_empty);
        } else if (!RegexpUtils.isEmailNO(email)) {
            valid = false;
            tvEmailError.setVisibility(View.VISIBLE);
            tvEmailError.setText(R.string.common_email_type_error);
        } else {
            tvEmailError.setVisibility(View.GONE);
        }
        return valid;
    }

    /**
     * 创建手机号验证码定时器
     */
    class PhoneCountDownTimer extends CountDownTimer {

        PhoneCountDownTimer(long millisInFuture, long countDownInterval) {
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
            tvPhoneCode.setText(R.string.reset_password_three_send_code);
            //设置可点击
            tvPhoneCode.setClickable(true);
        }
    }

    /**
     * 创建邮箱验证码定时器
     */
    class EmailCountDownTimer extends CountDownTimer {

        EmailCountDownTimer(long millisInFuture, long countDownInterval) {
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
            tvEmailCode.setText(R.string.reset_password_three_send_code);
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
