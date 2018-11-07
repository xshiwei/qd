package com.qvd.smartswitch.activity.robot;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;

public class RobotRemoteControlActivity extends BaseActivity {
    @Override
    protected int setLayoutId() {
        return R.layout.activity_robot_remote_control;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.home_list_background).init();
    }
}
