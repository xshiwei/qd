package com.qvd.smartswitch.activity.capacity;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.melnykov.fab.FloatingActionButton;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseFragment;
import com.qvd.smartswitch.adapter.LogCapacityListAdapter;
import com.qvd.smartswitch.model.home.Test2Vo;
import com.qvd.smartswitch.utils.SnackbarUtils;
import com.qvd.smartswitch.widget.EmptyLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/6/14 0014.
 */

public class LogCapacityFragment extends BaseFragment {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.emptylayout)
    EmptyLayout emptylayout;

    private final List<Test2Vo> list = new ArrayList<>();

    public static LogCapacityFragment newInstance(String param1) {
        LogCapacityFragment fragment = new LogCapacityFragment();
        Bundle args = new Bundle();
        args.putString("agrs1", param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_log_capacity;
    }

    @Override
    protected void initData() {
        super.initData();
        emptylayout.showEmpty(R.mipmap.home_empty,"此功能正在开发中...");
        for (int i = 0; i < 6; i++) {
            list.add(new Test2Vo("2018/6/20 6:23"));
        }
        recyclerview.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        LogCapacityListAdapter adapter = new LogCapacityListAdapter(getActivity(), list);
        recyclerview.setAdapter(adapter);
        fab.attachToRecyclerView(recyclerview);
    }

    @OnClick(R.id.fab)
    public void onViewClicked() {
        SnackbarUtils.Short(coordinatorLayout, "删除全部").show();
    }
}
