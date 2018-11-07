package com.qvd.smartswitch.activity.robot;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.adapter.RobotTimingListAdapter;
import com.qvd.smartswitch.model.home.TestVo;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class RobotTimingSweepActivity extends BaseActivity {

    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.smart_refresh)
    SmartRefreshLayout smartRefresh;
    @BindView(R.id.tv_add_timing)
    TextView tvAddTiming;

    private final List<TestVo> list = new ArrayList<>();
    private RobotTimingListAdapter adapter;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_robot_timing_sweep;
    }

    @Override
    protected void initData() {
        super.initData();
        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new RobotTimingListAdapter(list);
        adapter.openLoadAnimation();
        adapter.isFirstOnly(false);
        recyclerview.setAdapter(adapter);

        smartRefresh.setHeaderHeight(100);
        smartRefresh.setEnableHeaderTranslationContent(true);
        smartRefresh.setRefreshHeader(new ClassicsHeader(this));
        smartRefresh.setOnRefreshListener(refreshlayout -> {
            getData();
            smartRefresh.finishRefresh(1000, true);
        });

        myErrorLayout.setOnClickListener(v -> getData());

        myEmptyLayout.setOnClickListener(v -> getData());
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    /**
     * 获取数据
     */
    private void getData() {
        list.clear();
        list.add(new TestVo("今天 12：01"));
        list.add(new TestVo("今天 09：01"));
        list.add(new TestVo("今天 11：01"));
        adapter.notifyDataSetChanged();
    }


    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.home_list_background).init();
    }

    @OnClick({R.id.iv_common_actionbar_goback, R.id.tv_add_timing})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.tv_add_timing:
                startActivity(new Intent(this, RobotSettingTimingActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
        }
    }
}
