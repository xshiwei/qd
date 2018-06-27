package com.qvd.smartswitch.activity.user;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;

/**
 * Created by Administrator on 2018/6/14 0014.
 */

public class UserSettingActivity extends BaseActivity {

    @Override
    protected int setLayoutId() {
        return R.layout.activity_user_setting;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }
}
