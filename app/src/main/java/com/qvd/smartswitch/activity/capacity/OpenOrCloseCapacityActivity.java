package com.qvd.smartswitch.activity.capacity;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;

/**
 * Created by Administrator on 2018/6/20 0020.
 */

public class OpenOrCloseCapacityActivity extends BaseActivity {
    @Override
    protected int setLayoutId() {
        return R.layout.activity_open_or_close_capacity;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }
}
