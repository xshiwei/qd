package com.qvd.smartswitch.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.model.home.TestVo;

import java.util.List;

public class RobotSweepRecodeAdapter extends BaseQuickAdapter<TestVo, BaseViewHolder> {
    public RobotSweepRecodeAdapter(@Nullable List<TestVo> data) {
        super(R.layout.item_robot_sweep_recode, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, TestVo item) {
        helper.setText(R.id.tv_time, item.getTest());
    }
}
