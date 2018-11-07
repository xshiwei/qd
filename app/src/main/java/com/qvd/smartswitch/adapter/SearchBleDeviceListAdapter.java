package com.qvd.smartswitch.adapter;

import android.support.annotation.Nullable;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.clj.fastble.data.BleDevice;
import com.qvd.smartswitch.R;


import java.util.List;

/**
 * Created by Administrator on 2018/4/2.
 */

public class SearchBleDeviceListAdapter extends BaseQuickAdapter<BleDevice, BaseViewHolder> {

    public SearchBleDeviceListAdapter(@Nullable List<BleDevice> data) {
        super(R.layout.item_search_ble_device, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, BleDevice item) {
        helper.setText(R.id.tv_decive_name, item.getName())
                .setText(R.id.tv_decive_rssi, item.getRssi() + "");
    }
}
