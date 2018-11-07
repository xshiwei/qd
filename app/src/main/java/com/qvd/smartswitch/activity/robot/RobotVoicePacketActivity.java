package com.qvd.smartswitch.activity.robot;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.adapter.RobotVoicePacketAdapter;
import com.qvd.smartswitch.model.home.TestVo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class RobotVoicePacketActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;

    private RobotVoicePacketAdapter adapter;
    private final List<TestVo> list = new ArrayList<>();

    @Override
    protected int setLayoutId() {
        return R.layout.activity_robot_voice_packet;
    }

    @Override
    protected void initData() {
        super.initData();
        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new RobotVoicePacketAdapter(list);
        adapter.openLoadAnimation();
        adapter.isFirstOnly(false);
        recyclerview.setAdapter(adapter);
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
        list.add(new TestVo("标准普通话版（默认）"));
        list.add(new TestVo("机器人版"));
        list.add(new TestVo("动漫儿童版"));
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.home_list_background).init();
    }

    @OnClick(R.id.iv_common_actionbar_goback)
    public void onViewClicked() {
        finish();
    }
}
