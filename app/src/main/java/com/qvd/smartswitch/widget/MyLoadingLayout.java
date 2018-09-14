package com.qvd.smartswitch.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.wang.avi.AVLoadingIndicatorView;

public class MyLoadingLayout extends RelativeLayout {
    private Context mContext;
    private AVLoadingIndicatorView avi_loading;
    private TextView textViewMessage;

    public MyLoadingLayout(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public MyLoadingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    private void initView() {
        LayoutInflater.from(mContext).inflate(R.layout.recycleview_loading, this);
        avi_loading = findViewById(R.id.avi_loading);
        avi_loading.show();
        textViewMessage = findViewById(R.id.textViewMessage);
    }


    public void setTextViewMessage(String text) {
        textViewMessage.setText(text);
    }
}
