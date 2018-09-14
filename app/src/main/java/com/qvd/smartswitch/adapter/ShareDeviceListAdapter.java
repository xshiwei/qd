package com.qvd.smartswitch.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.model.user.UserShareDeviceListVo;
import com.qvd.smartswitch.utils.CommonUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ShareDeviceListAdapter extends BaseQuickAdapter<UserShareDeviceListVo.DataBean, BaseViewHolder> {

    public ShareDeviceListAdapter(@Nullable List<UserShareDeviceListVo.DataBean> data) {
        super(R.layout.item_device_share, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, UserShareDeviceListVo.DataBean item) {
        helper.setText(R.id.tv_name, item.getDevice_name())
                .setText(R.id.tv_content, "[" + item.getRoom_name() + "]   " + item.getShare_object_username());
        if (!CommonUtils.isEmptyString(item.getDevice_pic())) {
            Picasso.with(mContext).load(item.getDevice_pic()).into((ImageView) helper.getView(R.id.iv_device_pic));
        }
    }
}
