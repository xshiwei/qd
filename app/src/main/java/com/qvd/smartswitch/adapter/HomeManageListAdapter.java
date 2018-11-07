package com.qvd.smartswitch.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.model.home.HomeListVo;

import java.util.List;

/**
 * Created by Administrator on 2018/6/13 0013.
 */

public class HomeManageListAdapter extends BaseQuickAdapter<HomeListVo.DataBean, BaseViewHolder> {

    public HomeManageListAdapter(@Nullable List<HomeListVo.DataBean> data) {
        super(R.layout.item_home_manage, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, HomeListVo.DataBean item) {
        helper.setText(R.id.tv_home, item.getFamily_name())
                .setText(R.id.tv_room_num, item.getRoom_count() + "个房间")
                .setText(R.id.tv_device_num, item.getDevice_count() + "个设备");
    }
}
