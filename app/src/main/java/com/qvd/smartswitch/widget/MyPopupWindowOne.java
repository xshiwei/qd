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

public class MyPopupWindowOne extends PopupWindow {
    private final View rootView;

    private TextView tvTitle;
    private TextView tvCancel;
    private TextView tvConfirm;

    private IPopupWindowListener mListener;


    public MyPopupWindowOne(final Activity activity, String title, String cancel, String confirm, IPopupWindowListener listener) {
        this.mListener = listener;
        LayoutInflater inflater = LayoutInflater.from(activity);
        rootView = inflater.inflate(R.layout.popupwindow_dialog, null, false);
        this.setContentView(rootView);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setBackgroundDrawable(new ColorDrawable());
        this.setAnimationStyle(R.style.AnimBottom);
        this.setOutsideTouchable(true);
        this.setFocusable(true);
        CommonUtils.setBackgroundAlpha(activity, 0.5f);
        this.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                CommonUtils.setBackgroundAlpha(activity, 1.0f);
            }
        });

        tvTitle = rootView.findViewById(R.id.tv_title);
        tvCancel = rootView.findViewById(R.id.tv_cancel);
        tvConfirm = rootView.findViewById(R.id.tv_confirm);

        tvTitle.setText(title);
        tvCancel.setText(cancel);
        tvConfirm.setText(confirm);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.cancel();
            }
        });

        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.confirm();
            }
        });
    }

    public interface IPopupWindowListener {
        void cancel();

        void confirm();
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
