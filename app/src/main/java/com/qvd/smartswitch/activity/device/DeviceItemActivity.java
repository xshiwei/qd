package com.qvd.smartswitch.activity.device;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.utils.FucUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeviceItemActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.tv_text)
    TextView tvText;
    private String text;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_device_item;
    }

    @Override
    protected void initData() {
        super.initData();
        tvCommonActionbarTitle.setText("使用条款");
        text = FucUtil.readFile(this, "device_use_item", "utf-8");
        tvText.setText(text);
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
