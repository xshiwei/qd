package com.qvd.smartswitch.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.model.device.HomeShareDeviceListVo;

import java.util.List;

/**
 * Created by Administrator on 2018/6/6 0006.
 */

public class HomeShareListAdapter extends BaseQuickAdapter<HomeShareDeviceListVo.DataBean, BaseViewHolder> {

    public HomeShareListAdapter(@Nullable List<HomeShareDeviceListVo.DataBean> data) {
        super(R.layout.item_home_device, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, HomeShareDeviceListVo.DataBean item) {
        helper.setText(R.id.tv_device_name, item.getDevice_name());
    }
}

