package com.qvd.smartswitch.adapter;

import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.github.vipulasri.timelineview.TimelineView;
import com.qvd.smartswitch.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeviceLogHolder extends BaseViewHolder {
    @BindView(R.id.time_marker)
    TimelineView timeMarker;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_text)
    TextView tvText;

    public DeviceLogHolder(View view, int viewType) {
        super(view);
        ButterKnife.bind(this, itemView);
        timeMarker.initLine(viewType);
    }
}
