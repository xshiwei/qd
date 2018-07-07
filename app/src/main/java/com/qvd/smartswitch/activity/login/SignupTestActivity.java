package com.qvd.smartswitch.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
    @BindView(R.id.tv_phone_error)
    TextView tvPhoneError;
    @BindView(R.id.tv_email_error)
    TextView tvEmailError;


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
                startActivity(new Intent(this, SetPasswordActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.tv_email_code:
                //邮箱验证码
                emailCountDownTimer.start();
                getEmailCode();
                break;
            case R.id.btn_email_next:
                //邮箱下一步
                startActivity(new Intent(this, SetPasswordActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.tv_phone_code:
                //手机验证码
                phoneCountDownTimer.start();
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

    }

    /**
     * 获取邮箱验证码
     */
    private void getEmailCode() {

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
            tvEmailCode.setText("发送验证码");
            //设置可点击
            tvEmailCode.setClickable(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        phoneCountDownTimer.cancel();
        emailCountDownTimer.cancel();
    }
}
