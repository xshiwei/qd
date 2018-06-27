package com.qvd.smartswitch.activity.user;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/4/9 0009.
 */

public class AboutCompanyActivity extends BaseActivity {

    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.tv_about_appversion)
    TextView tvAboutAppversion;
    @BindView(R.id.rl_user_about_grade)
    RelativeLayout rlUserAboutGrade;
    @BindView(R.id.rl_user_about_clause)
    RelativeLayout rlUserAboutClause;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_about_company;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.app_color).init();
    }

    @Override
    protected void initData() {
        super.initData();
        tvCommonActionbarTitle.setText(R.string.about_company_title);
    }

    @OnClick({R.id.iv_common_actionbar_goback, R.id.rl_user_about_grade, R.id.rl_user_about_clause})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.rl_user_about_grade:
                break;
            case R.id.rl_user_about_clause:
                break;
        }
    }
}
