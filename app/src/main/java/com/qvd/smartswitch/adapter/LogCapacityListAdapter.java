package com.qvd.smartswitch.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.vipulasri.timelineview.TimelineView;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.model.home.Test2Vo;

import java.util.List;

/**
 * Created by Administrator on 2018/6/12 0012.
 */

public class LogCapacityListAdapter extends RecyclerView.Adapter<DeviceLogHolder> {
    private final Context context;
    private final List<Test2Vo> data;
    private final int[] colors = {0xffFFAD6C, 0xff62f434, 0xffdeda78, 0xff7EDCFF, 0xff58fdea, 0xfffdc75f};//颜色组

    public LogCapacityListAdapter(Context context, List<Test2Vo> data) {
        this.context = context;
        this.data = data;
    }


    interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClickListener(View view, int position);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    @NonNull
    @Override
    public DeviceLogHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_capacity_log, parent, false);
        return new DeviceLogHolder(view, viewType);
    }

    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position, getItemCount());
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceLogHolder holder, int position) {
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(view -> {
                int position12 = holder.getLayoutPosition();
                onItemClickListener.onItemClick(holder.itemView, position12);
            });
            holder.itemView.setOnLongClickListener(view -> {
                int position1 = holder.getLayoutPosition();
                onItemClickListener.onItemClick(holder.itemView, position1);
                return false;
            });
        }
        holder.tvText.setText(data.get(position).getTest());
        holder.tvTime.setTextColor(colors[position % colors.length]);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
