package com.qvd.smartswitch.utils;

import android.graphics.Color;

import com.qvd.smartswitch.MyApplication;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.widget.MyToast;

/**
 * Created by Delete_exe on 2016/5/13.
 */
public class ToastUtil {
    private static final MyToast mToast = new MyToast();

    public static void showToast(String s) {
        mToast.Short(MyApplication.getContext(), s).setToastBackground(Color.WHITE, R.drawable.toast_radius).show();
    }
}
