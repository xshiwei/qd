package com.qvd.smartswitch.activity.user;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.adapter.RegionSeleteAdapter;
import com.qvd.smartswitch.model.home.TestVo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class RegionSeleteActivity extends BaseActivity {


    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;

    private RegionSeleteAdapter adapter;
    private final List<TestVo> list = new ArrayList<>();

    @Override
    protected int setLayoutId() {
        return R.layout.activity_region_selete;
    }

    @Override
    protected void initData() {
        super.initData();
        tvCommonActionbarTitle.setText("请选择您要使用的地区");
        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new RegionSeleteAdapter(list);
        adapter.openLoadAnimation();
        adapter.isFirstOnly(false);
        adapter.setHasStableIds(true);
        recyclerview.setAdapter(adapter);

        myErrorLayout.setOnClickListener(v -> getData());
        getData();
    }

    private void getData() {
        list.clear();
        list.add(new TestVo("大陆地区（默认）"));
        list.add(new TestVo("美国"));
        list.add(new TestVo("欧洲"));
        list.add(new TestVo("印度"));
        list.add(new TestVo("韩国"));
        list.add(new TestVo("日本"));
        adapter.notifyDataSetChanged();
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
