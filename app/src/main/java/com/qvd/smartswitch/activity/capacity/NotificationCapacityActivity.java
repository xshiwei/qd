package com.qvd.smartswitch.activity.capacity;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;

/**
 * Created by Administrator on 2018/6/20 0020.
 */

public class NotificationCapacityActivity extends BaseActivity {
    @Override
    protected int setLayoutId() {
        return R.layout.activity_notification_capacity;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }
}
