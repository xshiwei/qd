package com.qvd.smartswitch.activity.user;

import android.widget.ImageView;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.model.user.UserFeedbackListVo;
import com.qvd.smartswitch.utils.CommonUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class UserFeedBackDetailsActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_text)
    TextView tvText;


    private UserFeedbackListVo.DataBean dataBean;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_user_feedback_details;
    }

    @Override
    protected void initData() {
        super.initData();
        dataBean = (UserFeedbackListVo.DataBean) getIntent().getSerializableExtra("data");
        tvCommonActionbarTitle.setText("反馈详情");
        tvContent.setText(dataBean.getFeedback_content());
        tvName.setText(CommonUtils.getDeviceName(dataBean.getCategory_type()) + "  |");
        tvTime.setText("  " + dataBean.getFeedback_time());
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
