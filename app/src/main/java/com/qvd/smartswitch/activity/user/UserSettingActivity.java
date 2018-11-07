package com.qvd.smartswitch.activity.user;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.activity.device.DeviceAllListActivity;
import com.qvd.smartswitch.activity.device.DevicePrivacyActivity;
import com.qvd.smartswitch.activity.home.HomeManageActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/6/14 0014.
 */

public class UserSettingActivity extends BaseActivity {

    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.switch_plug_auto_update)
    Switch switchPlugAutoUpdate;
    @BindView(R.id.rl_check_update)
    RelativeLayout rlCheckUpdate;
    @BindView(R.id.switch_common_voice)
    Switch switchCommonVoice;
    @BindView(R.id.rl_family_manage)
    RelativeLayout rlFamilyManage;
    @BindView(R.id.rl_message_setting)
    RelativeLayout rlMessageSetting;
    @BindView(R.id.tv_region)
    TextView tvRegion;
    @BindView(R.id.rl_region)
    RelativeLayout rlRegion;
    @BindView(R.id.tv_language)
    TextView tvLanguage;
    @BindView(R.id.rl_language)
    RelativeLayout rlLanguage;
    @BindView(R.id.tv_item_privacy)
    TextView tvItemPrivacy;
    @BindView(R.id.tv_current_version)
    TextView tvCurrentVersion;
    @BindView(R.id.rl_device_operation_recode)
    RelativeLayout rlDeviceOperationRecode;
    @BindView(R.id.rl_privacy_setting)
    RelativeLayout rlPrivacySetting;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_user_setting;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }

    @Override
    protected void initData() {
        super.initData();
        tvCommonActionbarTitle.setText(R.string.user_setting_title);
    }

    @OnClick({R.id.iv_common_actionbar_goback, R.id.rl_check_update, R.id.rl_family_manage, R.id.rl_device_operation_recode, R.id.rl_message_setting,
            R.id.rl_region, R.id.rl_language, R.id.tv_item_privacy, R.id.rl_privacy_setting})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.rl_check_update:
                break;
            case R.id.rl_family_manage:
                startActivity(new Intent(this, HomeManageActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.rl_device_operation_recode:
                startActivity(new Intent(this, DeviceAllListActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.rl_message_setting:
                startActivity(new Intent(this, MessageSettingActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.rl_region:
                startActivity(new Intent(this, RegionSeleteActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.rl_language:
                startActivity(new Intent(this, LanguageSeleteActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.tv_item_privacy:
                startActivity(new Intent(this, DevicePrivacyActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.rl_privacy_setting:
                startActivity(new Intent(this, PrivacySettingActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
        }
    }

}
