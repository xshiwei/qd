package com.qvd.smartswitch.activity.device;


import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.utils.CommonUtils;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/4/17 0017.
 */

public class DeviceTimingActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.tv_device_timing_save)
    TextView tvDeviceTimingSave;
    @BindView(R.id.tv_device_timing_time)
    TextView tvDeviceTimingTime;
    @BindView(R.id.tp_device_timing)
    TimePicker tpDeviceTiming;
    @BindView(R.id.rl_device_timing_repeat)
    RelativeLayout rlDeviceTimingRepeat;
    @BindView(R.id.rl_device_timing_operation)
    RelativeLayout rlDeviceTimingOperation;


    private PopupWindow popupWindow;
    private PopupWindow popupWindow2;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_device_timing;
    }

    @Override
    protected void initData() {
        super.initData();
        tvCommonActionbarTitle.setText("设备定时");
        tpDeviceTiming.setIs24HourView(true);
        tpDeviceTiming.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                String timing = CommonUtils.getTiming(hourOfDay, minute);
                tvDeviceTimingTime.setText(timing);
            }
        });
        showRepeatPopuwindow();
        showOperationPopuwindow();
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.app_color).init();
    }

    @OnClick({R.id.iv_common_actionbar_goback, R.id.tv_device_timing_save, R.id.rl_device_timing_repeat, R.id.rl_device_timing_operation})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.tv_device_timing_save:
                //实现定时功能
                break;
            case R.id.rl_device_timing_repeat:
                popupWindow.showAtLocation(view,Gravity.CENTER, 0, 0);
                break;
            case R.id.rl_device_timing_operation:
                popupWindow2.showAtLocation(view, Gravity.CENTER, 0, 0);
                break;
        }
    }

    /**
     * 设置重复的popuwindow
     */
    private void showRepeatPopuwindow() {
        LayoutInflater inflater = LayoutInflater.from(this);
        // 引入窗口配置文件
        View view = inflater.inflate(R.layout.popupwindow_repeat, null,false);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, false);
        View ll_popupwindow_repeat = view.findViewById(R.id.ll_popupwindow_repeat);
        TextView tv_popupwindow_repeat_once = view.findViewById(R.id.tv_popupwindow_repeat_once);
        TextView tv_popupwindow_workday = view.findViewById(R.id.tv_popupwindow_workday);
        TextView tv_popupwindow_everyday = view.findViewById(R.id.tv_popupwindow_everyday);
        // 需要设置一下此参数，点击外边可消失
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        // 设置点击窗口外边窗口消失
        popupWindow.setOutsideTouchable(true);
        // 设置此参数获得焦点，否则无法点击
        popupWindow.setFocusable(true);
        ll_popupwindow_repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    /**
     * 设置操作的popuwindow
     */
    private void showOperationPopuwindow() {
        LayoutInflater inflater = LayoutInflater.from(this);
        // 引入窗口配置文件
        View view = inflater.inflate(R.layout.popupwindow_operation, null);
        popupWindow2 = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, false);
        View ll_popupwindow_operation = view.findViewById(R.id.ll_popupwindow_operation);
        TextView tv_popupwindow_open = view.findViewById(R.id.tv_popupwindow_open);
        TextView tv_popupwindow_close = view.findViewById(R.id.tv_popupwindow_close);
        // 需要设置一下此参数，点击外边可消失
        popupWindow2.setBackgroundDrawable(new BitmapDrawable());
        // 设置点击窗口外边窗口消失
        popupWindow2.setOutsideTouchable(true);
        // 设置此参数获得焦点，否则无法点击
        popupWindow2.setFocusable(true);
        ll_popupwindow_operation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow2.dismiss();
            }
        });
    }
}
