package com.qvd.smartswitch.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.model.device.RoomDeviceListVo;

import java.util.List;

/**
 * Created by Administrator on 2018/6/6 0006.
 */

public class HomeDeviceListAdapter extends BaseQuickAdapter<RoomDeviceListVo.DataBean, BaseViewHolder> {

    public HomeDeviceListAdapter(@Nullable List<RoomDeviceListVo.DataBean> data) {
        super(R.layout.item_home_device, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, RoomDeviceListVo.DataBean item) {
        helper.setText(R.id.tv_device_name, item.getDevice_name());
        Glide.with(mContext).load(item.getDevice_pic()).into((ImageView) helper.getView(R.id.iv_device_pic));
    }

}

