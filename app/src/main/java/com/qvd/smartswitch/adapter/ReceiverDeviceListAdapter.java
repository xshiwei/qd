package com.qvd.smartswitch.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.model.user.UserReceiverDeviceListVo;
import com.qvd.smartswitch.utils.CommonUtils;

import java.util.List;

public class ReceiverDeviceListAdapter extends BaseQuickAdapter<UserReceiverDeviceListVo.DataBean, BaseViewHolder> {

    public ReceiverDeviceListAdapter(@Nullable List<UserReceiverDeviceListVo.DataBean> data) {
        super(R.layout.item_device_share_receiver, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, UserReceiverDeviceListVo.DataBean item) {
        helper.setText(R.id.tv_name, item.getDevice_name())
                .setText(R.id.tv_content, "来自 " + item.getUser_name())
                .addOnClickListener(R.id.tv_receiver);
        if (!CommonUtils.isEmptyString(item.getDevice_pic())) {
            Glide.with(mContext).load(item.getDevice_pic()).into((ImageView) helper.getView(R.id.civ_portrait));
        }
        if (item.getIs_share() == 0) {
            helper.setGone(R.id.tv_receiver, true);
            helper.setGone(R.id.tv_already_receiver, false);
        } else {
            helper.setGone(R.id.tv_receiver, false);
            helper.setGone(R.id.tv_already_receiver, true);
        }
    }
}
