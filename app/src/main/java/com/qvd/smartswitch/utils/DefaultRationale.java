package com.qvd.smartswitch.utils;

import android.app.AlertDialog;
import android.content.Context;

import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;

import java.io.File;

/**
 * Created by Administrator on 2018/4/3.
 */

final class DefaultRationale implements Rationale<File> {

    @Override
    public void showRationale(Context context, File data, final RequestExecutor executor) {
        //        List<String> permissionNames = Permission.transformText(context, permissions);
//        String message = context.getString(R.string.message_permission_rationale) + TextUtils.join("\n", permissionNames);
        new AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle("申请权限")
                .setMessage("您需要同意这些权限")
                .setPositiveButton("同意", (dialog, which) -> executor.execute())
                .setNegativeButton("拒绝", (dialog, which) -> executor.cancel())
                .show();
    }
}
