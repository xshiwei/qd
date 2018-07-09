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

import com.orhanobut.logger.Logger;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.login.MessageVo;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.RegexpUtils;
import com.qvd.smartswitch.utils.ToastUtil;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2018-7-7.
 */

public class SignupTestActivity extends BaseActivity {
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

    private final PhoneCountDownTimer phoneCountDownTimer = new PhoneCountDownTimer(60000, 1000);//60000

    private final EmailCountDownTimer emailCountDownTimer = new EmailCountDownTimer(60000, 1000);


    @Override
    protected int setLayoutId() {
        return R.layout.activity_signup_test;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.add_home_background).init();
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
                    ToastUtil.showToast("手机号或验证码不能为空");
                    return;
                }
                startActivity(new Intent(this, SetPasswordActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.tv_email_code:
                //邮箱验证码
                getEmailCode();
                break;
            case R.id.btn_email_next:
                //邮箱下一步
                if (CommonUtils.isEmptyString(etInputEmail.getText().toString().trim())) {
                    ToastUtil.showToast("邮箱不能为空");
                    return;
                }
                startActivity(new Intent(this, SetPasswordActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
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
     * 获取手机验证码
     */
    private void getPhoneCode() {
        if (!validatePhone()) {
            return;
        } else if (IsSameUserName(etInputPhone.getText().toString().trim())) {
            tvPhoneError.setVisibility(View.VISIBLE);
            tvEmailError.setText("改手机号已被注册");
            return;
        }
        phoneCountDownTimer.start();
    }

    /**
     * 获取邮箱验证码
     */
    private void getEmailCode() {
        if (!validateEmail()) {
            return;
        } else if (IsSameUserName(etInputEmail.getText().toString().trim())) {
            tvEmailError.setVisibility(View.VISIBLE);
            tvEmailError.setText("该邮箱已被注册");
            return;
        }
        emailCountDownTimer.start();
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
     * 验证手机号
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
     * 判断该账号是否被注册
     *
     * @param userName
     * @return
     */
    private boolean IsSameUserName(String userName) {
        final boolean[] t = {false};
        RetrofitService.qdoApi.IsSameUserName(userName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MessageVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MessageVo messageVo) {
                        if (messageVo.getCode() == 200) {
                            t[0] = true;
                        } else {
                            t[0] = false;
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        return t[0];
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
