package com.qvd.smartswitch.activity.base;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.gyf.barlibrary.ImmersionBar;
import com.orhanobut.logger.Logger;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.SplashActivity;
import com.qvd.smartswitch.utils.RuntimeRationale;
import com.qvd.smartswitch.utils.SysApplication;
import com.qvd.smartswitch.utils.ToastUtil;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Setting;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Activity的基类
 */

public abstract class BaseActivity extends RxAppCompatActivity {

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
        mImmersionBar.init();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    protected void initData() {
        requestPermission(Permission.Group.LOCATION);
        requestPermission(Permission.WRITE_EXTERNAL_STORAGE);
        requestPermission(Permission.READ_EXTERNAL_STORAGE);
        requestPermission(Permission.READ_PHONE_STATE);
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

    private void requestPermission(String... permissions) {
        AndPermission.with(this)
                .runtime()
                .permission(permissions)
                .rationale(new RuntimeRationale())
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> permissions) {
                        //Logger.e("授权成功");
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(@NonNull List<String> permissions) {
                        //Logger.e("授权失败");
                        if (AndPermission.hasAlwaysDeniedPermission(getApplicationContext(), permissions)) {
                            showSettingDialog(BaseActivity.this, permissions);
                        }
                    }
                })
                .start();
    }

    /**
     * Display setting dialog.
     */
    public void showSettingDialog(Context context, final List<String> permissions) {
        List<String> permissionNames = Permission.transformText(context, permissions);
        String message = context.getString(R.string.message_permission_always_failed) + TextUtils.join("\n", permissionNames);

        new AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle("申请权限")
                .setMessage(message)
                .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setPermission();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    /**
     * Set permissions.
     */
    private void setPermission() {
        AndPermission.with(this)
                .runtime()
                .setting()
                .onComeback(new Setting.Action() {
                    @Override
                    public void onAction() {
                        ToastUtil.showToast("设置权限");
                    }
                })
                .start();
    }

}
