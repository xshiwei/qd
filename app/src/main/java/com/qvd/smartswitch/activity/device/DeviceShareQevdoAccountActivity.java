package com.qvd.smartswitch.activity.device;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.utils.CommonUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeviceShareQevdoAccountActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.iv_device_pic)
    ImageView ivDevicePic;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.et_account)
    EditText etAccount;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;


    private String deviceId;
    private String device_type;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_device_share_qevdo_account;
    }

    @Override
    protected void initData() {
        super.initData();
        deviceId = getIntent().getStringExtra("device_id");
        device_type = getIntent().getStringExtra("device_type");
        tvCommonActionbarTitle.setText("共享给科微多账号");
        tvName.setText(CommonUtils.getDeviceName(device_type));
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }

    @OnClick({R.id.iv_common_actionbar_goback, R.id.tv_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.tv_confirm:
                break;
        }
    }
}
