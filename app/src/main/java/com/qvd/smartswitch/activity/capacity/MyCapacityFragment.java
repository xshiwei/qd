package com.qvd.smartswitch.activity.capacity;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseFragment;
import com.qvd.smartswitch.widget.EmptyLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2018/6/14 0014.
 */

public class MyCapacityFragment extends BaseFragment {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.emptylayout)
    EmptyLayout emptylayout;

    public static MyCapacityFragment newInstance(String param1) {
        MyCapacityFragment fragment = new MyCapacityFragment();
        Bundle args = new Bundle();
        args.putString("agrs1", param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_my_capacity;
    }

    @Override
    protected void initData() {
        super.initData();
        emptylayout.showEmpty(R.mipmap.home_empty, "此功能正在开发中...");
    }
}
