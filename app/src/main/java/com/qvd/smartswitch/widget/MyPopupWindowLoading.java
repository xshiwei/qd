package com.qvd.smartswitch.widget;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.utils.CommonUtils;
import com.wang.avi.AVLoadingIndicatorView;

public class MyPopupWindowLoading extends PopupWindow {
    private final View rootView;

    private AVLoadingIndicatorView avi_loading;
    private TextView tv_text;


    public MyPopupWindowLoading(final Activity activity, String text) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        rootView = inflater.inflate(R.layout.popupwindow_loading, null, false);
        this.setContentView(rootView);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setBackgroundDrawable(new ColorDrawable());
        this.setAnimationStyle(R.style.AnimBottom);
        this.setFocusable(false);
        this.setOutsideTouchable(false);
        CommonUtils.setBackgroundAlpha(activity, 0.5f);

        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                CommonUtils.setBackgroundAlpha(activity, 1.0f);
            }
        });

        avi_loading = rootView.findViewById(R.id.avi_loading);
        tv_text = rootView.findViewById(R.id.tv_text);
        tv_text.setText(text);
    }

    /**
     * 显示popupWindow
     */
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            this.showAtLocation(parent, Gravity.BOTTOM, 0, 30);
        } else {
            this.dismiss();
        }
    }

}
