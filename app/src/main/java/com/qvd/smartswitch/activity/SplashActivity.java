package com.qvd.smartswitch.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Window;
import android.view.WindowManager;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.login.LoginVo;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.ConfigUtils;
import com.qvd.smartswitch.utils.PermissionUtils;
import com.qvd.smartswitch.utils.RxHelper;
import com.qvd.smartswitch.utils.SharedPreferencesUtil;
import com.qvd.smartswitch.utils.SysApplication;
import com.qvd.smartswitch.widget.SimpleButton;
import com.wenming.library.LogReport;
import com.yanzhenjie.permission.Permission;

import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/4/25 0025.
 */

public class SplashActivity extends BaseActivity {


    private SimpleButton sbSkip;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        super.onCreate(savedInstanceState);
        SysApplication.getInstance().addActivity(this);
        AutoLogin();
    }

    @Override
    protected int setLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    /**
     * 自动登录
     */
    private void AutoLogin() {
        String password = SharedPreferencesUtil.getString(this, SharedPreferencesUtil.PASSWORD);
        String identifier = SharedPreferencesUtil.getString(this, SharedPreferencesUtil.IDENTIFIER);
        if (!CommonUtils.isEmptyString(password) && !CommonUtils.isEmptyString(identifier)) {
            RetrofitService.qdoApi.login(identifier, password)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<LoginVo>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(LoginVo loginVo) {
                            if (loginVo != null) {
                                if (loginVo.getData() != null && loginVo.getCode() == 200) {
                                    SharedPreferencesUtil.putString(SplashActivity.this, SharedPreferencesUtil.USER_ID, loginVo.getData().getUser_id());
                                    SharedPreferencesUtil.putString(SplashActivity.this, SharedPreferencesUtil.IDENTIFIER, loginVo.getData().getIdentifier());
                                    SharedPreferencesUtil.putString(SplashActivity.this, SharedPreferencesUtil.PASSWORD, loginVo.getData().getPassword());
                                    ConfigUtils.user_id = loginVo.getData().getUser_id();
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
    }


    protected void initData() {
        sbSkip = findViewById(R.id.sb_skip);
        RxHelper.countdown(3)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onError(Throwable e) {
                        _doSkip();
                    }

                    @Override
                    public void onComplete() {
                        _doSkip();
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Integer integer) {
                        sbSkip.setText("跳过 " + integer);
                    }
                });
    }

    private void _doSkip() {
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        finish();
    }

    @Override
    public void onBackPressed() {
        // 不响应后退键
        return;
    }

    @OnClick(R.id.sb_skip)
    public void onViewClicked() {
        _doSkip();
    }

}
