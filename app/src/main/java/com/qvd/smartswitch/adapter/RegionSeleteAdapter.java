package com.qvd.smartswitch.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.model.home.TestVo;

import java.util.List;

public class RegionSeleteAdapter extends BaseQuickAdapter<TestVo, BaseViewHolder> {
    public RegionSeleteAdapter(@Nullable List<TestVo> data) {
        super(R.layout.item_region_selete, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, TestVo item) {
        helper.setText(R.id.tv_region, item.getTest());
        if (helper.getLayoutPosition() == 0) {
            helper.setVisible(R.id.iv_selete, true);
            helper.setTextColor(R.id.tv_region, mContext.getResources().getColor(R.color.add_home_background));
        } else {
            helper.setVisible(R.id.iv_selete, false);
            helper.setTextColor(R.id.tv_region, mContext.getResources().getColor(R.color.home_setting_text));
        }
    }
}
