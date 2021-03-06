package com.qvd.smartswitch.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qvd.smartswitch.R;

public class MyErrorLayout extends RelativeLayout {
    private final Context mContext;
    private ImageView iv_pic;
    private TextView textViewMessage;

    public MyErrorLayout(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public MyErrorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    private void initView() {
        LayoutInflater.from(mContext).inflate(R.layout.recycleview_error, this);
        iv_pic = findViewById(R.id.iv_pic);
        textViewMessage = findViewById(R.id.textViewMessage);
    }


    public void setIv_pic(int icon) {
        Drawable img = mContext.getResources().getDrawable(icon);
        iv_pic.setImageDrawable(img);
    }


    public void setTextViewMessage(String text) {
        textViewMessage.setText(text);
    }
}
