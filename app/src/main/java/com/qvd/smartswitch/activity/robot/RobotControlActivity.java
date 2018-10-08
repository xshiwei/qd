package com.qvd.smartswitch.activity.robot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RobotControlActivity extends BaseActivity {
    @BindView(R.id.iv_device_control_goback)
    ImageView ivDeviceControlGoback;
    @BindView(R.id.tv_device_control_title)
    TextView tvDeviceControlTitle;
    @BindView(R.id.tv_state)
    TextView tvState;
    @BindView(R.id.iv_device_control_more)
    ImageView ivDeviceControlMore;
    @BindView(R.id.tv_area)
    TextView tvArea;
    @BindView(R.id.tv_electric_quantity)
    TextView tvElectricQuantity;
    @BindView(R.id.tv_clear_time)
    TextView tvClearTime;
    @BindView(R.id.ll_recharge)
    LinearLayout llRecharge;
    @BindView(R.id.ll_sweep)
    LinearLayout llSweep;
    @BindView(R.id.ll_standard)
    LinearLayout llStandard;
    @BindView(R.id.ll_map)
    LinearLayout llMap;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_robot_control;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.bottom_text).statusBarDarkFont(false).init();
    }

    @OnClick({R.id.iv_device_control_goback, R.id.iv_device_control_more, R.id.ll_recharge, R.id.ll_sweep, R.id.ll_standard, R.id.ll_map})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_device_control_goback:
                finish();
                break;
            case R.id.iv_device_control_more:
                startActivity(new Intent(this, RobotSettingActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.ll_recharge:
                break;
            case R.id.ll_sweep:
                break;
            case R.id.ll_standard:
                break;
            case R.id.ll_map:
                break;
        }
    }
}
