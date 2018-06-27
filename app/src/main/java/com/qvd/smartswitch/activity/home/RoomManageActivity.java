package com.qvd.smartswitch.activity.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.adapter.AddRoomListAdapter;
import com.qvd.smartswitch.adapter.RoomManageListAdapter;
import com.qvd.smartswitch.model.home.Test2Vo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/6/13 0013.
 */

public class RoomManageActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.tv_add_room)
    TextView tvAddRoom;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;

    private List<Test2Vo> list = new ArrayList<>();
    private RoomManageListAdapter adapter;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_room_manage;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }

    @Override
    protected void initData() {
        super.initData();
        for (int i = 0; i < 3; i++) {
            list.add(new Test2Vo("客厅"));
        }
        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new RoomManageListAdapter(this, list);
        recyclerview.setAdapter(adapter);
        adapter.setOnItemClickListener(new RoomManageListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                startActivity(new Intent(RoomManageActivity.this, UpdateRoomDetailsActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }

            @Override
            public void onItemLongClickListener(View view, int position) {

            }
        });
    }

    @OnClick({R.id.iv_common_actionbar_goback, R.id.tv_add_room})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.tv_add_room:
                startActivity(new Intent(this, AddRoomListActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
        }
    }
}
