package com.qvd.smartswitch.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.model.device.DeviceTimingVo;

import java.util.List;

/**
 * Created by Administrator on 2018/6/13 0013.
 */

public class BleTimingAdapter extends BaseQuickAdapter<DeviceTimingVo.DataBean, BaseViewHolder> {

    public BleTimingAdapter(@Nullable List<DeviceTimingVo.DataBean> data) {
        super(R.layout.item_ble_timing, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DeviceTimingVo.DataBean item) {
        helper.setText(R.id.tv_time,item.getStart_time())
                .setText(R.id.tv_content,item.getTiming_contetnt());
        if (item.getTiming_state().equals("1")){
            Glide.with(mContext).load(R.mipmap.device_ble_timing_one).into((ImageView) helper.getView(R.id.iv_state));
        }else {
            Glide.with(mContext).load(R.mipmap.device_ble_timing_two).into((ImageView) helper.getView(R.id.iv_state));
        }
    }
}
