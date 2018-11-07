package com.qvd.smartswitch.activity.robot;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;
import cn.carbswang.android.numberpickerview.library.NumberPickerView;

public class RobotSettingTimingActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_device_timing_save)
    TextView tvDeviceTimingSave;
    @BindView(R.id.tv_state)
    TextView tvState;
    @BindView(R.id.rl_state)
    RelativeLayout rlState;
    @BindView(R.id.tv_open_time)
    TextView tvOpenTime;
    @BindView(R.id.tv_mode)
    TextView tvMode;
    @BindView(R.id.rl_mode)
    RelativeLayout rlMode;
    @BindView(R.id.picker_hour)
    NumberPickerView pickerHour;
    @BindView(R.id.picker_minute)
    NumberPickerView pickerMinute;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_robot_setting_timing;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.home_list_background).init();
    }

    @OnClick({R.id.iv_common_actionbar_goback, R.id.tv_device_timing_save, R.id.rl_state, R.id.rl_mode})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.tv_device_timing_save:
                break;
            case R.id.rl_state:
                break;
            case R.id.rl_mode:
                break;
        }
    }
}
