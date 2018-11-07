package com.qvd.smartswitch.activity.user;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.activity.device.DevicePrivacyActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class PrivacySettingActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.switch_join_experience_explain)
    Switch switchJoinExperienceExplain;
    @BindView(R.id.tv_experience_explain)
    TextView tvExperienceExplain;
    @BindView(R.id.tv_version)
    TextView tvVersion;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_privacy_setting;
    }

    @Override
    protected void initData() {
        super.initData();
        tvCommonActionbarTitle.setText(R.string.privacy_setting_title);
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }

    @OnClick({R.id.iv_common_actionbar_goback, R.id.tv_experience_explain})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.tv_experience_explain:
                startActivity(new Intent(this, DevicePrivacyActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
        }
    }
}
