package com.qvd.smartswitch.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.model.device.FamilyDetailsVo;
import com.qvd.smartswitch.utils.CommonUtils;

import java.util.List;

public class FamilyShareDeviceListAdapter extends BaseQuickAdapter<FamilyDetailsVo.DataBean.ShareDevicesDataBean, BaseViewHolder> {


    public FamilyShareDeviceListAdapter(@Nullable List<FamilyDetailsVo.DataBean.ShareDevicesDataBean> data) {
        super(R.layout.item_device_share, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, FamilyDetailsVo.DataBean.ShareDevicesDataBean item) {
        helper.setText(R.id.tv_name, item.getDevice_name())
                .setText(R.id.tv_content, item.getAdd_time().split(" ")[0] + "共享");
        if (!CommonUtils.isEmptyString(item.getDevice_pic())) {
            Glide.with(mContext).load(item.getDevice_pic()).into((ImageView) helper.getView(R.id.iv_device_pic));
        }
    }

}
