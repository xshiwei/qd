package com.qvd.smartswitch.activity.device;

import android.widget.ImageView;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class DeviceNetworkMessageActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.tv_wifi_name)
    TextView tvWifiName;
    @BindView(R.id.tv_wifi_strength)
    TextView tvWifiStrength;
    @BindView(R.id.tv_rssi)
    TextView tvRssi;
    @BindView(R.id.tv_mode)
    TextView tvMode;
    @BindView(R.id.tv_ip_address)
    TextView tvIpAddress;
    @BindView(R.id.tv_mac_address)
    TextView tvMacAddress;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_device_network_message;
    }

    @Override
    protected void initData() {
        super.initData();
        tvCommonActionbarTitle.setText(R.string.device_network_message_title);
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }

    @OnClick(R.id.iv_common_actionbar_goback)
    public void onViewClicked() {
        finish();
    }
}
