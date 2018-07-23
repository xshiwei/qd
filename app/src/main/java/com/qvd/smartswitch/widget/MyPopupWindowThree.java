package com.qvd.smartswitch.widget;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
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
    private final TextView tvTitle;
    private final EditText etEditText;
    private final ImageView ivDelete;
    private final TextView tvError;
    private final TextView tvCancel;
    private final TextView tvConfirm;
    private View rootView;

    private IPopupWindowListener mListener;

    public MyPopupWindowThree(final Activity activity, String title, String hint, final int length, IPopupWindowListener listener) {
        this.mListener = listener;
        LayoutInflater inflater = LayoutInflater.from(activity);
        rootView = inflater.inflate(R.layout.popupwindow_edittext, null, false);
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
        etEditText = rootView.findViewById(R.id.et_edittext);
        ivDelete = rootView.findViewById(R.id.iv_delete);
        tvError = rootView.findViewById(R.id.tv_error);
        tvCancel = rootView.findViewById(R.id.tv_cancel);
        tvConfirm = rootView.findViewById(R.id.tv_confirm);

        tvTitle.setText(title);
        etEditText.setHint(hint);

        if (etEditText.getText().toString().equals("")) {
            tvConfirm.setEnabled(false);
            tvConfirm.setTextColor(activity.getResources().getColor(R.color.home_setting_text_three));
        }

        etEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (length == 0) {
                    if (s.toString().length() > 20) {
                        tvError.setVisibility(View.VISIBLE);
                        tvError.setText("长度超过最大");
                        tvConfirm.setEnabled(false);
                        tvConfirm.setTextColor(activity.getResources().getColor(R.color.home_setting_text_three));
                    } else if (s.toString().length() == 0) {
                        tvConfirm.setEnabled(false);
                        tvConfirm.setTextColor(activity.getResources().getColor(R.color.home_setting_text_three));
                        etEditText.setCursorVisible(false);
                    } else {
                        tvError.setVisibility(View.GONE);
                        tvConfirm.setEnabled(true);
                        tvConfirm.setTextColor(activity.getResources().getColor(R.color.popupwindow_confirm_text));
                    }
                } else {
                    if (s.toString().length() > length) {
                        tvError.setVisibility(View.VISIBLE);
                        tvError.setText("长度超过最大");
                        tvConfirm.setEnabled(false);
                        tvConfirm.setTextColor(activity.getResources().getColor(R.color.home_setting_text_three));
                    } else if (s.toString().length() == 0) {
                        tvConfirm.setEnabled(false);
                        tvConfirm.setTextColor(activity.getResources().getColor(R.color.home_setting_text_three));
                        etEditText.setCursorVisible(false);
                    } else if (s.length() == length) {
                        tvError.setVisibility(View.GONE);
                        tvConfirm.setEnabled(true);
                        tvConfirm.setTextColor(activity.getResources().getColor(R.color.popupwindow_confirm_text));
                    } else {
                        tvConfirm.setEnabled(false);
                        tvConfirm.setTextColor(activity.getResources().getColor(R.color.home_setting_text_three));
                    }
                }

            }
        });

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
