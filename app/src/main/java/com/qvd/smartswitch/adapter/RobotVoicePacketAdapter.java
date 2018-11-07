package com.qvd.smartswitch.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.model.home.TestVo;

import java.util.List;

public class RobotVoicePacketAdapter extends BaseQuickAdapter<TestVo, BaseViewHolder> {
    public RobotVoicePacketAdapter(@Nullable List<TestVo> data) {
        super(R.layout.item_robot_voice_packet, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, TestVo item) {
        helper.setText(R.id.tv_name, item.getTest());
    }
}
