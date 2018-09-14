package com.qvd.smartswitch.activity.device;

import android.widget.ImageView;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.model.device.DeviceCommonQuestionVo;
import com.qvd.smartswitch.utils.CommonUtils;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeviceCommonQuestionDetailsActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.iv_image)
    ImageView ivImage;

    private DeviceCommonQuestionVo.DataBean data;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_device_common_question_detail;
    }

    @OnClick(R.id.iv_common_actionbar_goback)
    public void onViewClicked() {
        finish();
    }

    @Override
    protected void initData() {
        super.initData();
        data = (DeviceCommonQuestionVo.DataBean) getIntent().getSerializableExtra("data");
        tvCommonActionbarTitle.setText(CommonUtils.getDeviceName(data.getDevice_no()));
        tvTitle.setText(data.getQuestion_title());
        tvContent.setText(data.getQuestion_content());
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }
}
