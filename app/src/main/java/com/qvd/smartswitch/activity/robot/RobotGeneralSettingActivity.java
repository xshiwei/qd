package com.qvd.smartswitch.activity.robot;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RobotGeneralSettingActivity extends BaseActivity {

    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.rl_rename)
    RelativeLayout rlRename;
    @BindView(R.id.rl_robot_share)
    RelativeLayout rlRobotShare;
    @BindView(R.id.rl_check_update)
    RelativeLayout rlCheckUpdate;
    @BindView(R.id.rl_delete_robot)
    RelativeLayout rlDeleteRobot;
    @BindView(R.id.rl_network_message)
    RelativeLayout rlNetworkMessage;
    @BindView(R.id.rl_robot_time_zone)
    RelativeLayout rlRobotTimeZone;
    @BindView(R.id.rl_robot_feedback)
    RelativeLayout rlRobotFeedback;
    @BindView(R.id.rl_robot_item)
    RelativeLayout rlRobotItem;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_robot_general_setting;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }

    @OnClick({R.id.iv_common_actionbar_goback, R.id.rl_rename, R.id.rl_robot_share, R.id.rl_check_update, R.id.rl_delete_robot, R.id.rl_network_message, R.id.rl_robot_time_zone, R.id.rl_robot_feedback, R.id.rl_robot_item})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.rl_rename:
                break;
            case R.id.rl_robot_share:
                break;
            case R.id.rl_check_update:
                break;
            case R.id.rl_delete_robot:
                break;
            case R.id.rl_network_message:
                break;
            case R.id.rl_robot_time_zone:
                break;
            case R.id.rl_robot_feedback:
                break;
            case R.id.rl_robot_item:
                break;
        }
    }
}
