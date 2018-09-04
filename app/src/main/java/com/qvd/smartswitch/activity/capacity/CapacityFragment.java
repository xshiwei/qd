package com.qvd.smartswitch.activity.capacity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseFragment;
import com.qvd.smartswitch.adapter.TabLayoutAdapter;
import com.qvd.smartswitch.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/6/14 0014.
 */

public class CapacityFragment extends BaseFragment {

    @BindView(R.id.tbl_capacity)
    TabLayout tblCapacity;
    @BindView(R.id.iv_capacity_add)
    ImageView ivCapacityAdd;
    @BindView(R.id.vp_capacity)
    ViewPager vpCapacity;

    private TabLayoutAdapter adapter;
    /**
     * 定义要装fragment的列表
     */
    private List<Fragment> list_fragment;
    /**
     * 定义tablayout的title集合
     */
    private final List<String> list_title = new ArrayList<>();

    private RecommendCapacityFragment recommendCapacityFragment;
    private MyCapacityFragment myCapacityFragment;
    private LogCapacityFragment logCapacityFragment;


    public static CapacityFragment newInstance(String param1) {
        CapacityFragment fragment = new CapacityFragment();
        Bundle args = new Bundle();
        args.putString("agrs1", param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_capacity;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }

    @Override
    protected void initData() {
        super.initData();
        recommendCapacityFragment = RecommendCapacityFragment.newInstance("recommend");
        myCapacityFragment = MyCapacityFragment.newInstance("my");
        logCapacityFragment = LogCapacityFragment.newInstance("log");

        list_fragment = new ArrayList<>();
        list_fragment.add(recommendCapacityFragment);
        list_fragment.add(myCapacityFragment);
        list_fragment.add(logCapacityFragment);

        list_title.add("推荐");
        list_title.add("我的");
        list_title.add("日志");

        tblCapacity.setTabMode(View.FOCUSABLES_TOUCH_MODE);
        //TabLayout加载viewpager
        tblCapacity.setupWithViewPager(vpCapacity);
        //为TabLayout添加tab名称
        tblCapacity.addTab(tblCapacity.newTab().setText(list_title.get(0)));
        tblCapacity.addTab(tblCapacity.newTab().setText(list_title.get(1)));
        tblCapacity.addTab(tblCapacity.newTab().setText(list_title.get(2)));

        adapter = new TabLayoutAdapter(getChildFragmentManager(), list_fragment, list_title);
        //viewpager加载adapter
        vpCapacity.setAdapter(adapter);
        vpCapacity.setCurrentItem(0);
    }

    @OnClick(R.id.iv_capacity_add)
    public void onViewClicked() {
        ToastUtil.showToast("此功能正在开发中，请期待。。。");
//        startActivity(new Intent(getActivity(),AddCapacityActivity.class));
//        getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }
}
