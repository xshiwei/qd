package com.qvd.smartswitch.activity.robot;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.adapter.RobotSweepRecodeAdapter;
import com.qvd.smartswitch.model.home.TestVo;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class RobotSweepRecodeActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_total_time)
    TextView tvTotalTime;
    @BindView(R.id.tv_total_area)
    TextView tvTotalArea;
    @BindView(R.id.tv_total_num)
    TextView tvTotalNum;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.smart_refresh)
    SmartRefreshLayout smartRefresh;

    private RobotSweepRecodeAdapter adapter;
    private final List<TestVo> list = new ArrayList<>();

    @Override
    protected int setLayoutId() {
        return R.layout.activity_robot_sweep_recode;
    }

    @Override
    protected void initData() {
        super.initData();
        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new RobotSweepRecodeAdapter(list);
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
        list.add(new TestVo("09月10日  15:00"));
        list.add(new TestVo("09月11日  14:00"));
        list.add(new TestVo("09月12日  16:00"));
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.bottom_text).init();
    }

    @OnClick(R.id.iv_common_actionbar_goback)
    public void onViewClicked() {
        finish();
    }
}
