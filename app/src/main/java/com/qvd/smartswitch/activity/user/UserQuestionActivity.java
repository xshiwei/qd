package com.qvd.smartswitch.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.adapter.UserQuestionListAdapter;
import com.qvd.smartswitch.model.home.Test2Vo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/6/15 0015.
 */

public class UserQuestionActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.iv_commit_question)
    ImageView ivCommitQuestion;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;

    private UserQuestionListAdapter adapter;
    private List<Test2Vo> list = new ArrayList<>();

    @Override
    protected int setLayoutId() {
        return R.layout.activity_user_question;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }

    @Override
    protected void initData() {
        super.initData();
        for (int i = 0; i < 10; i++) {
            list.add(new Test2Vo("如何更换色环"));
        }
        adapter = new UserQuestionListAdapter(this, list);
        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerview.setAdapter(adapter);
    }

    @OnClick({R.id.iv_common_actionbar_goback, R.id.iv_commit_question})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.iv_commit_question:
                startActivity(new Intent(this, UserFeedbackActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
        }
    }
}
