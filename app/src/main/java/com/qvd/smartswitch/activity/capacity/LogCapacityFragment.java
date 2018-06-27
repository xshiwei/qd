package com.qvd.smartswitch.activity.capacity;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.melnykov.fab.FloatingActionButton;
import com.orhanobut.logger.Logger;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseFragment;
import com.qvd.smartswitch.adapter.LogCapacityListAdapter;
import com.qvd.smartswitch.model.home.Test2Vo;
import com.qvd.smartswitch.utils.ToastUtil;
import com.vivian.timelineitemdecoration.itemdecoration.DotItemDecoration;
import com.vivian.timelineitemdecoration.itemdecoration.SpanIndexListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2018/6/14 0014.
 */

public class LogCapacityFragment extends BaseFragment {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    private List<Test2Vo> list = new ArrayList<>();
    private LogCapacityListAdapter adapter;
    private DotItemDecoration mItemDecoration;

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
        for (int i = 0; i < 6; i++) {
            list.add(new Test2Vo("2018/6/20 6:23"));
        }
        recyclerview.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mItemDecoration = new DotItemDecoration
                .Builder(getActivity())
                .setOrientation(DotItemDecoration.VERTICAL) //设置方向
                .setItemStyle(DotItemDecoration.STYLE_DRAW)
                .setTopDistance(20)//设置距离顶部高度
                .setItemInterVal(10)
                .setItemPaddingLeft(10)
                .setItemPaddingRight(10)
                .setDotColor(getResources().getColor(R.color.capacity_tablayout_text_two))
                .setDotRadius(5)  //设置圆点的半径
                .setDotPaddingTop(20)//设置第一个点距离上部高度
                .setDotInItemOrientationCenter(false)
                .setLineColor(getResources().getColor(R.color.home_setting_text_two))
                .setLineWidth(1)//设置线的宽度
                .setEndText("END")  //设置结尾的文字
                .setTextColor(getResources().getColor(R.color.home_content_text_two))
                .setTextSize(10)//设置结尾文字大小
                .setDotPaddingText(2)
                .setBottomDistance(40)//结尾线的高度
                .create();

        mItemDecoration.setSpanIndexListener(new SpanIndexListener() {
            @Override
            public void onSpanIndexChange(View view, int spanIndex) {
                view.setBackgroundResource(spanIndex == 0 ? R.drawable.log_left : R.drawable.log_right);
            }
        });
        recyclerview.addItemDecoration(mItemDecoration);
        adapter = new LogCapacityListAdapter(getActivity(), list);
        recyclerview.setAdapter(adapter);
        fab.attachToRecyclerView(recyclerview);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        list.clear();
    }

    @OnClick(R.id.fab)
    public void onViewClicked() {
        ToastUtil.showToast("删除全部");
    }
}
