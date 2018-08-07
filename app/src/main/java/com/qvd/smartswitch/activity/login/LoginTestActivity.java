package com.qvd.smartswitch.activity.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.MainActivity;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.login.LoginVo;
import com.qvd.smartswitch.model.login.MessageVo;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.SnackbarUtils;
import com.qvd.smartswitch.widget.MyProgressDialog;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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
        mImmersionBar.fitsSystemWindows(false).transparentBar().init();
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
        if (!validate()) {
            return;
        }

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
        RetrofitService.qdoApi.login(etAccount.getText().toString().trim(), etPassword.getText().toString().trim())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LoginVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(LoginVo messageVo) {
                        if (messageVo.getCode() == 200) {
                            onLoginSuccess();
                            progressDialog.dismiss();
                        } else if (messageVo.getCode() == 400) {
                            SnackbarUtils.Short(btnLogin, "登录失败");
                        } else {
                            SnackbarUtils.Short(btnLogin, "连接超时");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e("login->" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 验证账号密码
     *
     * @return
     */
    private boolean validate() {
        boolean valid = true;
        if (CommonUtils.isEmptyString(etAccount.getText().toString().trim()) || CommonUtils.isEmptyString(etPassword.getText().toString().trim())) {
            valid = false;
            SnackbarUtils.Short(btnLogin, "密码或账号不能为空").show();
        } else if (etPassword.length() < 6 || etPassword.length() > 16) {
            valid = false;
            SnackbarUtils.Short(btnLogin, "密码长度不规范").show();
        }
        return valid;
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
        SnackbarUtils.Short(btnLogin, "登录失败").show();
        btnLogin.setEnabled(true);
    }
}
