package com.qvd.smartswitch.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.utils.RxHelper;
import com.qvd.smartswitch.utils.SysApplication;
import com.qvd.smartswitch.widget.SimpleButton;

import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2018/4/25 0025.
 */

public class SplashActivity extends AppCompatActivity {

    private boolean mIsSkip = false;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private SimpleButton sbSkip;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        initData();
    }

    protected void initData() {
        SysApplication.getInstance().addActivity(this);
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
        if (!mIsSkip) {
            mIsSkip = true;
            //自动登录，判断跳到主页还是登录页。
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            finish();
        }
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