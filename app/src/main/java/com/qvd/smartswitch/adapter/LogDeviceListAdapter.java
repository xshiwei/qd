package com.qvd.smartswitch.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.github.vipulasri.timelineview.TimelineView;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.model.device.DeviceLogVo;

import java.util.List;

/**
 * Created by Administrator on 2018/6/12 0012.
 */

public class LogDeviceListAdapter extends BaseQuickAdapter<DeviceLogVo.DataBean, DeviceLogHolder> {

    public LogDeviceListAdapter(@Nullable List<DeviceLogVo.DataBean> data) {
        super(data);
    }

    @Override
    protected void convert(DeviceLogHolder helper, DeviceLogVo.DataBean item) {
        if (item.getLog_content() != null) {
            helper.tvText.setText(item.getLog_content());
        }
        if (item.getCreate_time() != null) {
            helper.tvTime.setText(item.getCreate_time());
        }
    }

    @NonNull
    @Override
    public DeviceLogHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.item_device_log, null);
        return new DeviceLogHolder(view, viewType);
    }

    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position, getItemCount());
    }
}
