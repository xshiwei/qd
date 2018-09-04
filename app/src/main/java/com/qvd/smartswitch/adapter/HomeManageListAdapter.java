package com.qvd.smartswitch.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.model.home.HomeListVo;
import com.qvd.smartswitch.model.home.Test2Vo;

import java.util.List;

/**
 * Created by Administrator on 2018/6/13 0013.
 */

public class HomeManageListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<HomeListVo.DataBean> data;

    public HomeManageListAdapter(Context context, List<HomeListVo.DataBean> data) {
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_home_manage, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {
            if (onItemClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        int position = holder.getLayoutPosition();
                        onItemClickListener.onItemClick(holder.itemView, position);
                    }
                });
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View view) {
                        int position = holder.getLayoutPosition();
                        onItemClickListener.onItemClick(holder.itemView, position);
                        return false;
                    }
                });
            }
            ((MyViewHolder) holder).tv_home.setText(data.get(position).getFamily_name());
            ((MyViewHolder) holder).tv_device_num.setText(data.get(position).getDevice_count()+"个设备");
            ((MyViewHolder) holder).tv_room_num.setText(data.get(position).getRoom_count()+"个房间");
        }
    }


    @Override
    public int getItemCount() {
        return data.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_home, tv_room_num, tv_device_num;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_home = itemView.findViewById(R.id.tv_home);
            tv_room_num = itemView.findViewById(R.id.tv_room_num);
            tv_device_num = itemView.findViewById(R.id.tv_device_num);
        }
    }
}
