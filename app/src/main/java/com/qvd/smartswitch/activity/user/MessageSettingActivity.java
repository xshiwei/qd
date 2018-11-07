package com.qvd.smartswitch.activity.user;

import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class MessageSettingActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.switch_device)
    Switch switchDevice;
    @BindView(R.id.switch_device_share)
    Switch switchDeviceShare;
    @BindView(R.id.switch_family_invite)
    Switch switchFamilyInvite;
    @BindView(R.id.switch_message_no_disturbing)
    Switch switchMessageNoDisturbing;
    @BindView(R.id.tv_time)
    TextView tvTime;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_message_setting;
    }

    @Override
    protected void initData() {
        super.initData();
        tvCommonActionbarTitle.setText(R.string.message_setting_title);
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }

    @OnClick({R.id.iv_common_actionbar_goback, R.id.tv_time})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.tv_time:
                break;
        }
    }
}
