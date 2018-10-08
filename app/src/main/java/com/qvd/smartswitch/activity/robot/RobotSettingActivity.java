package com.qvd.smartswitch.activity.robot;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.zcw.togglebutton.ToggleButton;

import butterknife.BindView;
import butterknife.OnClick;

public class RobotSettingActivity extends BaseActivity {

    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.rl_robot_setting)
    RelativeLayout rlRobotSetting;
    @BindView(R.id.rl_timing_sweep)
    RelativeLayout rlTimingSweep;
    @BindView(R.id.rl_voice_packet)
    RelativeLayout rlVoicePacket;
    @BindView(R.id.rl_message_reminding)
    RelativeLayout rlMessageReminding;
    @BindView(R.id.rl_robot_general_setting)
    RelativeLayout rlRobotGeneralSetting;
    @BindView(R.id.rl_sweep_recode)
    RelativeLayout rlSweepRecode;
    @BindView(R.id.rl_consumable_maintain)
    RelativeLayout rlConsumableMaintain;
    @BindView(R.id.rl_product_guide_service)
    RelativeLayout rlProductGuideService;
    @BindView(R.id.rl_remote_control)
    RelativeLayout rlRemoteControl;
    @BindView(R.id.rl_locate_robot)
    RelativeLayout rlLocateRobot;


    private ToggleButton tlb_message_switch;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_robot_setting;
    }

    @Override
    protected void initData() {
        super.initData();
        tlb_message_switch = findViewById(R.id.tlb_message_switch);
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.home_list_background).init();
    }

    @OnClick({R.id.iv_common_actionbar_goback, R.id.rl_robot_setting, R.id.rl_timing_sweep, R.id.rl_voice_packet, R.id.rl_message_reminding, R.id.rl_robot_general_setting, R.id.rl_sweep_recode, R.id.rl_consumable_maintain, R.id.rl_product_guide_service, R.id.rl_remote_control, R.id.rl_locate_robot})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.rl_robot_setting:
                startActivity(new Intent(this, RobotSelfSettingActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.rl_timing_sweep:
                startActivity(new Intent(this, RobotTimingSweepActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.rl_voice_packet:
                break;
            case R.id.rl_message_reminding:
                break;
            case R.id.rl_robot_general_setting:
                startActivity(new Intent(this, RobotGeneralSettingActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.rl_sweep_recode:
                break;
            case R.id.rl_consumable_maintain:
                break;
            case R.id.rl_product_guide_service:
                break;
            case R.id.rl_remote_control:
                break;
            case R.id.rl_locate_robot:
                break;
        }
    }
}
