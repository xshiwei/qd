package com.qvd.smartswitch.activity.home;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;

public class SeleteRoomToSetActivity extends BaseActivity {
    @Override
    protected int setLayoutId() {
        return R.layout.activity_selete_room_to_set;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }
}
