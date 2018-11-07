package com.qvd.smartswitch.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;

import com.qvd.smartswitch.R;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;

import java.util.List;

/**
 * Created by Administrator on 2018/4/3.
 */

public final class RuntimeRationale implements Rationale<List<String>> {

    @Override
    public void showRationale(Context context, List<String> permissions, final RequestExecutor executor) {
        List<String> permissionNames = Permission.transformText(context, permissions);
        String message = context.getString(R.string.message_permission_rationale) + TextUtils.join("\n", permissionNames);

        new AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle("申请权限")
                .setMessage(message)
                .setPositiveButton("同意", (dialog, which) -> executor.execute())
                .setNegativeButton("取消", (dialog, which) -> executor.cancel())
                .show();
    }
}
