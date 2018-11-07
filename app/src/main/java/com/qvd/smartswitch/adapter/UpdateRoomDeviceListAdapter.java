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
 * Created by xushiwei on 2017/11/7.
 */

public class UpdateRoomDeviceListAdapter extends BaseQuickAdapter<RoomDeviceListVo.DataBean, BaseViewHolder> {
    private final String room_name;

    public UpdateRoomDeviceListAdapter(@Nullable List<RoomDeviceListVo.DataBean> data, String room_name) {
        super(R.layout.item_add_room_device, data);
        this.room_name = room_name;
    }

    @Override
    protected void convert(BaseViewHolder helper, RoomDeviceListVo.DataBean item) {
        helper.setText(R.id.tv_text, item.getDevice_name())
                .setText(R.id.tv_room, room_name + "");
        if (item.getIs_selete()) {
            helper.setImageResource(R.id.iv_item_device_selete, R.mipmap.device_log_selete);
        } else {
            helper.setImageResource(R.id.iv_item_device_selete, R.mipmap.device_log_not_selete);
        }
        Glide.with(mContext).load(item.getDevice_pic()).into((ImageView) helper.getView(R.id.iv_pic));
    }
}
