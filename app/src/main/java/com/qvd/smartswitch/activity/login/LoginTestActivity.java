package com.qvd.smartswitch.activity.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.MainActivity;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.utils.ToastUtil;
import com.qvd.smartswitch.widget.MyProgressDialog;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018-7-7.
 */

public class LoginTestActivity extends BaseActivity {
    @BindView(R.id.tv_country)
    TextView tvCountry;
    @BindView(R.id.iv_user)
    ImageView ivUser;
    @BindView(R.id.et_account)
    EditText etAccount;
    @BindView(R.id.iv_password)
    ImageView ivPassword;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.btn_login)
    TextView btnLogin;
    @BindView(R.id.tv_forget_password)
    TextView tvForgetPassword;
    @BindView(R.id.view_line)
    View viewLine;
    @BindView(R.id.tv_register)
    TextView tvRegister;
    @BindView(R.id.iv_wechat)
    ImageView ivWechat;
    @BindView(R.id.iv_qq)
    ImageView ivQq;
    @BindView(R.id.iv_weibo)
    ImageView ivWeibo;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_login_test;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.add_home_background).init();
    }

    @Override
    protected void initData() {
        super.initData();
        etAccount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ivUser.setBackgroundResource(R.mipmap.login_user_selete);
                } else {
                    ivUser.setBackgroundResource(R.mipmap.login_user_unselete);
                }
            }
        });

        etPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ivPassword.setBackgroundResource(R.mipmap.login_password_selete);
                } else {
                    ivPassword.setBackgroundResource(R.mipmap.login_password_unselete);
                }
            }
        });
    }

    @OnClick({R.id.tv_country, R.id.btn_login, R.id.tv_forget_password, R.id.tv_register, R.id.iv_wechat, R.id.iv_qq, R.id.iv_weibo})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_country:
                //国家地区
                break;
            case R.id.btn_login:
                //登录
                login();
                break;
            case R.id.tv_forget_password:
                //忘记密码
                break;
            case R.id.tv_register:
                //立刻注册
                startActivity(new Intent(this, SignupTestActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.iv_wechat:
                //微信登录
                break;
            case R.id.iv_qq:
                //qq登录
                break;
            case R.id.iv_weibo:
                //微博登录
                break;
        }
    }

    /**
     * 登录
     */
    private void login() {
        //        if (!validate()) {
//            onLoginFailed();
//            return;
//        }

        btnLogin.setEnabled(false);

        final MyProgressDialog progressDialog = MyProgressDialog.createProgressDialog(this, 5000,
                new MyProgressDialog.OnTimeOutListener() {
                    @Override
                    public void onTimeOut(ProgressDialog dialog) {
                        dialog.dismiss();
                        onLoginFailed();
                    }
                });
        progressDialog.setMessage("登录中");
        progressDialog.show();

        //调用接口登录，登陆成功关闭progressDialog
    }


    /**
     * 登录成功
     */
    public void onLoginSuccess() {
        btnLogin.setEnabled(true);
        startActivity(new Intent(this, MainActivity.class));
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    /**
     * 登录失败
     */
    private void onLoginFailed() {
        ToastUtil.showToast("登录失败");
        btnLogin.setEnabled(true);
    }
}
