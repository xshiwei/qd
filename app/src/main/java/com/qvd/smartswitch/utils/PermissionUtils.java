package com.qvd.smartswitch.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.qvd.smartswitch.R;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Setting;

import java.util.List;

public class PermissionUtils {

    /**
     * 申请权限
     *
     * @param permissions
     */
    public static void requestPermission(final Context context, String... permissions) {
        AndPermission.with(context)
                .runtime()
                .permission(permissions)
                .rationale(new RuntimeRationale())
                .onGranted(permissions12 -> {

                })
                .onDenied(permissions1 -> {
                    if (AndPermission.hasAlwaysDeniedPermission(context, permissions1)) {
                        showSettingDialog(context, permissions1);
                    }
                })
                .start();
    }

    /**
     * Display setting dialog.
     */
    private static void showSettingDialog(final Context context, final List<String> permissions) {
        List<String> permissionNames = Permission.transformText(context, permissions);
        String message = context.getString(R.string.message_permission_always_failed) + TextUtils.join("\n", permissionNames) + "权限才能使用此功能";

        new MaterialDialog.Builder(context)
                .autoDismiss(false)
                .title("我们需要申请以下权限")
                .content(message)
                .positiveText("去设置")
                .negativeText("取消")
                .onPositive((dialog, which) -> setPermission(context))
                .show();
    }

    /**
     * Set permissions.
     */
    private static void setPermission(Context context) {
        AndPermission.with(context)
                .runtime()
                .setting()
                .onComeback(new Setting.Action() {
                    @Override
                    public void onAction() {
                    }
                })
                .start();
    }
}
