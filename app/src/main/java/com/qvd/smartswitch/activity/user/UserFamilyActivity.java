package com.qvd.smartswitch.activity.user;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;

/**
 * Created by Administrator on 2018/6/15 0015.
 */

public class UserFamilyActivity extends BaseActivity {
    @Override
    protected int setLayoutId() {
        return R.layout.activity_user_family;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }
}
