package com.qvd.smartswitch.activity.base;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.gyf.barlibrary.ImmersionBar;
import com.qvd.smartswitch.activity.SplashActivity;
import com.qvd.smartswitch.widget.MyEmptyLayout;
import com.qvd.smartswitch.widget.MyErrorLayout;
import com.qvd.smartswitch.widget.MyLoadingLayout;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Activity的基类
 */

public abstract class BaseNoTipActivity extends RxAppCompatActivity {

    private InputMethodManager imm;
    protected ImmersionBar mImmersionBar;
    private Unbinder unbinder;

    protected MyEmptyLayout myEmptyLayout;
    private MyErrorLayout myErrorLayout;
    private MyLoadingLayout myLoadingLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //判断当前页面是否被销毁，如果是，则跳转到启动页。
        if (savedInstanceState != null) {
            Intent intent = new Intent(this, SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        super.onCreate(savedInstanceState);
        setContentView(setLayoutId());
        //绑定控件
        unbinder = ButterKnife.bind(this);
        //初始化沉浸式
        myEmptyLayout = new MyEmptyLayout(this);
        myErrorLayout = new MyErrorLayout(this);
        myLoadingLayout = new MyLoadingLayout(this);
        initImmersionBar();
        //初始化数据
        initData();
        //view与数据绑定
        initView();
        //网络请求加载数据
        getData();
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

    private void initView() {
    }

    private void getData() {
    }

    public void finish() {
        super.finish();
        hideSoftKeyBoard();
    }

    private void hideSoftKeyBoard() {
        View localView = getCurrentFocus();
        if (this.imm == null) {
            this.imm = ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
        }
        if ((localView != null) && (this.imm != null)) {
            this.imm.hideSoftInputFromWindow(localView.getWindowToken(), 2);
        }
    }


}
