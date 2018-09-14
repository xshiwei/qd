package com.qvd.smartswitch.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.model.device.DeviceCommonQuestionVo;
import com.qvd.smartswitch.model.device.DeviceTimingVo;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Administrator on 2018/6/13 0013.
 */

public class BleTimingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<DeviceTimingVo.DataBean> data;

    public BleTimingAdapter(Context context, List<DeviceTimingVo.DataBean> data) {
        this.context = context;
        this.data = data;
    }


    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClickListener(View view, int position);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ble_timing, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {
            if (onItemClickListener != null) {
                holder.itemView.setOnClickListener(view -> {
                    int position1 = holder.getLayoutPosition();
                    onItemClickListener.onItemClick(holder.itemView, position1);
                });
                holder.itemView.setOnLongClickListener(view -> {
                    int position12 = holder.getLayoutPosition();
                    onItemClickListener.onItemClick(holder.itemView, position12);
                    return false;
                });
            }
            ((MyViewHolder) holder).tv_time.setText(data.get(position).getStart_time());
            ((MyViewHolder) holder).tv_content.setText(data.get(position).getTiming_contetnt());
            if (data.get(position).getTiming_state().equals("1")) {
                Picasso.with(context).load(R.mipmap.device_ble_timing_one).into(((MyViewHolder) holder).iv_state);
            } else {
                Picasso.with(context).load(R.mipmap.device_ble_timing_two).into(((MyViewHolder) holder).iv_state);
            }
        }
    }


    @Override
    public int getItemCount() {
        return data.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_time, tv_content;
        ImageView iv_state;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_content = itemView.findViewById(R.id.tv_content);
            iv_state = itemView.findViewById(R.id.iv_state);
        }
    }
}
