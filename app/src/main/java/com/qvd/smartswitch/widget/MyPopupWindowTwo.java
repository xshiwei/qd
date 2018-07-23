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

public class MyPopupWindowTwo extends PopupWindow {

    private final TextView tvTitle;
    private final TextView tvCancel;
    private final TextView tvConfirm;
    private final TextView tvText;
    private View rootView;

    private IPopupWindowListener mListener;

    public MyPopupWindowTwo(final Activity activity, String title, String text, String cancel, String confirm, IPopupWindowListener listener) {
        this.mListener = listener;
        LayoutInflater inflater = LayoutInflater.from(activity);
        rootView = inflater.inflate(R.layout.popupwindow_dialog_two, null, false);
        this.setContentView(rootView);
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
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
        tvText = rootView.findViewById(R.id.tv_text);
        tvTitle.setText(title);
        tvText.setText(text);
        tvCancel.setText(cancel);
        tvConfirm.setText(confirm);

        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.confirm();
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.cancel();
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
            this.showAtLocation(parent, Gravity.BOTTOM, 0, 20);
        } else {
            this.dismiss();
        }
    }

}
