package com.qvd.smartswitch.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.model.home.DeviceListVo;

import java.util.List;


/**
 * Created by xushiwei on 2017/11/7.
 */

public class AddRoomDeviceListAdapter extends BaseQuickAdapter<DeviceListVo.DataBean, BaseViewHolder> {

    public AddRoomDeviceListAdapter(@Nullable List<DeviceListVo.DataBean> data) {
        super(R.layout.item_add_room_device, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DeviceListVo.DataBean item) {
        helper.setText(R.id.tv_text, item.getDevice_name())
                .setText(R.id.tv_room, item.getRoom_name());
        if (item.isIs_selete()) {
            helper.setImageResource(R.id.iv_item_device_selete, R.mipmap.device_log_selete);
        } else {
            helper.setImageResource(R.id.iv_item_device_selete, R.mipmap.device_log_not_selete);
        }
        Glide.with(mContext).load(item.getDevice_pic()).into((ImageView) helper.getView(R.id.iv_pic));
    }
}
