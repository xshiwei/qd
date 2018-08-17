package com.qvd.smartswitch.activity.wifi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class ConfirmLightFlickerActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.tv_confirm_light)
    TextView tvConfirmLight;


    private String wifi_ssid;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_confirm_light_flicker;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }

    @Override
    protected void initData() {
        super.initData();
        wifi_ssid = getIntent().getStringExtra("wifi_ssid");
    }

    @OnClick({R.id.iv_common_actionbar_goback, R.id.tv_confirm_light})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.tv_confirm_light:
                startActivity(new Intent(this, DeviceWifiSettingActivity.class)
                        .putExtra("wifi_ssid", wifi_ssid));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                finish();
                break;
        }
    }
}
