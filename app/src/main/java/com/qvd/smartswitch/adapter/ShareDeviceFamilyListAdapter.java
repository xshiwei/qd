package com.qvd.smartswitch.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.model.home.DeviceListVo;
import com.qvd.smartswitch.utils.CommonUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ShareDeviceFamilyListAdapter extends BaseQuickAdapter<DeviceListVo.DataBean, BaseViewHolder> {


    public ShareDeviceFamilyListAdapter(@Nullable List<DeviceListVo.DataBean> data) {
        super(R.layout.item_device_share, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DeviceListVo.DataBean item) {
        helper.setText(R.id.tv_name, item.getDevice_name());
        helper.setText(R.id.tv_content, item.getDevice_mac());
        if (!CommonUtils.isEmptyString(item.getDevice_pic())) {
            Picasso.with(mContext).load(item.getDevice_pic()).into((ImageView) helper.getView(R.id.iv_device_pic));
        }
    }

}
