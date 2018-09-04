package com.qvd.smartswitch.activity.base;


import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
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

public abstract class BaseActivity extends RxAppCompatActivity implements NetBroadcastReceiver.NetEvevt {

    private NetBroadcastReceiver broadcastReceiver;
    private InputMethodManager imm;
    protected ImmersionBar mImmersionBar;
    private Unbinder unbinder;

    public static NetBroadcastReceiver.NetEvevt evevt;
    /**
     * 网络类型
     */
    private int netMobile;


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
        evevt = this;
        inspectNet();
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

    @Override
    protected void onResume() {
        super.onResume();
        broadcastReceiver = new NetBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(broadcastReceiver, filter);
    }

    /**
     * 初始化时判断有没有网络
     */
    public boolean inspectNet() {
        this.netMobile = NetUtils.getNetWorkState(BaseActivity.this);
        return isNetConnect();
    }

    /**
     * 网络变化之后的类型
     */
    @Override
    public void onNetChange(int netMobile) {
        this.netMobile = netMobile;
        isNetConnect();
        if (netMobile == -1) {
            ToastUtil.showToast("网络未连接，请检查网络后再试");
        } else {
            if (!CommonUtils.isEmptyString(ConfigUtils.user_id)) {
                AutoLogin();
            }
        }
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
                                    SharedPreferencesUtil.putString(BaseActivity.this, SharedPreferencesUtil.USER_ID, loginVo.getData().getUser_id());
                                    SharedPreferencesUtil.putString(BaseActivity.this, SharedPreferencesUtil.IDENTIFIER, loginVo.getData().getIdentifier());
                                    SharedPreferencesUtil.putString(BaseActivity.this, SharedPreferencesUtil.PASSWORD, loginVo.getData().getPassword());
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

    /**
     * 判断有无网络 。
     *
     * @return true 有网, false 没有网络.
     */
    public boolean isNetConnect() {
        if (netMobile == 1) {
            return true;
        } else if (netMobile == 0) {
            return true;
        } else if (netMobile == -1) {
            return false;

        }
        return false;
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

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
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
