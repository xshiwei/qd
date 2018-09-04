package com.qvd.smartswitch.activity.base;


import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.gyf.barlibrary.ImmersionBar;
import com.qvd.smartswitch.activity.SplashActivity;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.login.LoginVo;
import com.qvd.smartswitch.receiver.NetBroadcastReceiver;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.ConfigUtils;
import com.qvd.smartswitch.utils.NetUtils;
import com.qvd.smartswitch.utils.SharedPreferencesUtil;
import com.qvd.smartswitch.utils.SysApplication;
import com.qvd.smartswitch.utils.ToastUtil;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Activity的基类
 */

public abstract class BaseNoTipActivity extends RxAppCompatActivity  {


    private InputMethodManager imm;
    protected ImmersionBar mImmersionBar;
    private Unbinder unbinder;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //判断当前页面是否被销毁，如果是，则跳转到启动页。
        if (savedInstanceState != null) {
            Intent intent = new Intent(this, SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        SysApplication.getInstance().addActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(setLayoutId());
        //绑定控件
        unbinder = ButterKnife.bind(this);
        //初始化沉浸式
        if (isImmersionBarEnabled())
            initImmersionBar();
        //初始化数据
        initData();
        //view与数据绑定
        initView();
        //网络请求加载数据
        getData();
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
                                    SharedPreferencesUtil.putString(BaseNoTipActivity.this, SharedPreferencesUtil.USER_ID, loginVo.getData().getUser_id());
                                    SharedPreferencesUtil.putString(BaseNoTipActivity.this, SharedPreferencesUtil.IDENTIFIER, loginVo.getData().getIdentifier());
                                    SharedPreferencesUtil.putString(BaseNoTipActivity.this, SharedPreferencesUtil.PASSWORD, loginVo.getData().getPassword());
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        this.imm = null;
        if (mImmersionBar != null)
            mImmersionBar.destroy();  //在BaseActivity里销毁
    }


    protected abstract int setLayoutId();

    protected void initImmersionBar() {
        //在BaseActivity里初始化
        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.statusBarDarkFont(true, 1).init();
    }

    protected void initData() {

    }

    protected void initView() {
    }

    protected void getData() {
    }


    /**
     * 是否可以使用沉浸式
     * Is immersion bar enabled boolean.
     *
     * @return the boolean
     */
    protected boolean isImmersionBarEnabled() {
        return true;
    }

    public void finish() {
        super.finish();
        hideSoftKeyBoard();
    }

    public void hideSoftKeyBoard() {
        View localView = getCurrentFocus();
        if (this.imm == null) {
            this.imm = ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
        }
        if ((localView != null) && (this.imm != null)) {
            this.imm.hideSoftInputFromWindow(localView.getWindowToken(), 2);
        }
    }


}
