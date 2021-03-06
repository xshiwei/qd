package com.qvd.smartswitch.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.model.user.UserShareDeviceListVo;
import com.qvd.smartswitch.utils.CommonUtils;

import java.util.List;

public class ShareDeviceListAdapter extends BaseQuickAdapter<UserShareDeviceListVo.DataBean, BaseViewHolder> {


    public ShareDeviceListAdapter(@Nullable List<UserShareDeviceListVo.DataBean> data) {
        super(R.layout.item_device_share, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, UserShareDeviceListVo.DataBean item) {
        helper.setText(R.id.tv_name, item.getDevice_name());
        if (item.getShare_object_username() != null) {
            helper.setText(R.id.tv_content, "[" + item.getRoom_name() + "]   共享至" + item.getShare_object_username());
        } else {
            helper.setText(R.id.tv_content, "[" + item.getRoom_name() + "]   " + "未共享");
        }
        if (!CommonUtils.isEmptyString(item.getDevice_pic())) {
            Glide.with(mContext).load(item.getDevice_pic()).into((ImageView) helper.getView(R.id.iv_device_pic));
        }
    }

}
