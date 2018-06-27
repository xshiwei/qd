package com.qvd.smartswitch.activity.home;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;

/**
 * Created by Administrator on 2018/6/12 0012.
 */

public class DeviceConnectActivity extends BaseActivity {
    @Override
    protected int setLayoutId() {
        return R.layout.activity_device_connect;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).init();
    }
}
