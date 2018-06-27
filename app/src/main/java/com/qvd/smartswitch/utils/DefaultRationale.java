package com.qvd.smartswitch.utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;

import com.qvd.smartswitch.R;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;

import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2018/4/3.
 */

public final class DefaultRationale implements Rationale<File> {

    @Override
    public void showRationale(Context context, File data, final RequestExecutor executor) {
        //        List<String> permissionNames = Permission.transformText(context, permissions);
//        String message = context.getString(R.string.message_permission_rationale) + TextUtils.join("\n", permissionNames);
        new AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle("申请权限")
                .setMessage("您需要同意这些权限")
                .setPositiveButton("同意", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        executor.execute();
                    }
                })
                .setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        executor.cancel();
                    }
                })
                .show();
    }
}
