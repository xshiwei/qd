package com.qvd.smartswitch.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.model.home.RoomListVo;

import java.util.List;

/**
 * Created by Administrator on 2018/6/13 0013.
 */

public class RoomManageListAdapter extends BaseQuickAdapter<RoomListVo.DataBean, BaseViewHolder> {
    public RoomManageListAdapter(@Nullable List<RoomListVo.DataBean> data) {
        super(R.layout.item_room_manage, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, RoomListVo.DataBean item) {
        helper.setText(R.id.tv_text, item.getRoom_name())
                .setText(R.id.tv_device_num, item.getDevice_count() + "个设备");
        Glide.with(mContext).load(item.getRoom_pic()).into((ImageView) helper.getView(R.id.iv_pic));
    }
}
