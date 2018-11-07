package com.qvd.smartswitch.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.model.home.RoomPicListVo;

import java.util.List;

/**
 * Created by Administrator on 2018/6/13 0013.
 */

public class AddRoomListAdapter extends BaseQuickAdapter<RoomPicListVo.DataBean, BaseViewHolder> {

    public AddRoomListAdapter(@Nullable List<RoomPicListVo.DataBean> data) {
        super(R.layout.item_add_room, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, RoomPicListVo.DataBean item) {
        helper.setText(R.id.tv_text, item.getRoom_name());
        Glide.with(mContext).load(item.getRoom_max_pic()).into((ImageView) helper.getView(R.id.iv_pic));
    }
}
