package com.qvd.smartswitch.activity.robot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.zcw.togglebutton.ToggleButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RobotSelfSettingActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.rl_robot_time_zone)
    RelativeLayout rlRobotTimeZone;
    @BindView(R.id.rl_volume_setting)
    RelativeLayout rlVolumeSetting;
    @BindView(R.id.rl_disturbance_mode)
    RelativeLayout rlDisturbanceMode;
    @BindView(R.id.tv_time_zone)
    TextView tvTimeZone;

    private ToggleButton tlb_carpet_boost_mode;
    private ToggleButton tlb_mopping_mode;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_robot_self_setting;
    }

    @Override
    protected void initData() {
        super.initData();
        tlb_carpet_boost_mode = findViewById(R.id.tlb_carpet_boost_mode);
        tlb_mopping_mode = findViewById(R.id.tlb_mopping_mode);
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.home_list_background).init();
    }

    @OnClick({R.id.iv_common_actionbar_goback, R.id.rl_robot_time_zone, R.id.rl_volume_setting, R.id.rl_disturbance_mode})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.rl_robot_time_zone:
                //时区设置
                break;
            case R.id.rl_volume_setting:
                //音量设置
                startActivity(new Intent(this, RobotSettingVolumeModeActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.rl_disturbance_mode:
                //勿扰模式
                startActivity(new Intent(this, RobotSettingDisturbanceModeActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
        }
    }


}
