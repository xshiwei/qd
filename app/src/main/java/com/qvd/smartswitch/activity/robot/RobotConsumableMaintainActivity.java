package com.qvd.smartswitch.activity.robot;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.andexert.library.RippleView;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.adapter.RobotConsumableMaintainAdapter;
import com.qvd.smartswitch.model.home.TestVo;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class RobotConsumableMaintainActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.smart_refresh)
    SmartRefreshLayout smartRefresh;
    @BindView(R.id.riv_immediately_buy)
    RippleView rivImmediatelyBuy;

    private RobotConsumableMaintainAdapter adapter;
    private final List<TestVo> list = new ArrayList<>();

    @Override
    protected int setLayoutId() {
        return R.layout.activity_robot_consumable_maintain;
    }

    @Override
    protected void initData() {
        super.initData();
        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new RobotConsumableMaintainAdapter(list);
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
        list.add(new TestVo("93%"));
        list.add(new TestVo("87%"));
        list.add(new TestVo("62%"));
        list.add(new TestVo("62%"));
        list.add(new TestVo("62%"));
        adapter.notifyDataSetChanged();
    }


    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.home_list_background).init();
    }

    @OnClick({R.id.iv_common_actionbar_goback, R.id.riv_immediately_buy})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.riv_immediately_buy:
                break;
        }
    }
}
