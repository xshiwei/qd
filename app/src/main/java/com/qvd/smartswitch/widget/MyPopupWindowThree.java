package com.qvd.smartswitch.widget;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.utils.CommonUtils;

public class MyPopupWindowThree extends PopupWindow {
    private TextView tvTitle;
    private EditText etEditText;
    private ImageView ivDelete;
    private TextView tvError;
    private TextView tvCancel;
    private TextView tvConfirm;
    private final View rootView;

    private IPopupWindowListener mListener;

    public MyPopupWindowThree(final Activity activity, String title, String hint, IPopupWindowListener listener) {
        this.mListener = listener;
        LayoutInflater inflater = LayoutInflater.from(activity);
        rootView = inflater.inflate(R.layout.popupwindow_edittext, null, false);
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
        etEditText = rootView.findViewById(R.id.et_edittext);
        ivDelete = rootView.findViewById(R.id.iv_delete);
        tvError = rootView.findViewById(R.id.tv_error);
        tvCancel = rootView.findViewById(R.id.tv_cancel);
        tvConfirm = rootView.findViewById(R.id.tv_confirm);

        tvTitle.setText(title);
        etEditText.setHint(hint);

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.cancel();
            }
        });

        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etEditText.setText("");
                etEditText.setCursorVisible(false);
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

    public EditText getEtEditText() {
        return etEditText;
    }

    public TextView getTvError() {
        return tvError;
    }

    public TextView getTvConfirm() {
        return tvConfirm;
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
