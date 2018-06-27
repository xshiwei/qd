package com.qvd.smartswitch.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
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
 * Created by Administrator on 2018/6/15 0015.
 */

public class UserHelpActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.iv_message)
    ImageView ivMessage;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.rl_experience)
    RelativeLayout rlExperience;
    @BindView(R.id.rl_capacity)
    RelativeLayout rlCapacity;
    @BindView(R.id.rl_other)
    RelativeLayout rlOther;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_user_help;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }


    @OnClick({R.id.iv_common_actionbar_goback, R.id.iv_message, R.id.rl_experience, R.id.rl_capacity, R.id.rl_other})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.iv_message:
                startActivity(new Intent(this, UserFeedbackListActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.rl_experience:
                startActivity(new Intent(this, UserQuestionActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.rl_capacity:
                break;
            case R.id.rl_other:
                break;
        }
    }
}
