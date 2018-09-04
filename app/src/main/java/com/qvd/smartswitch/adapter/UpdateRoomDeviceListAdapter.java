package com.qvd.smartswitch.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.model.device.RoomDeviceListVo;
import com.qvd.smartswitch.model.home.DeviceListVo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by xushiwei on 2017/11/7.
 */

public class UpdateRoomDeviceListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<RoomDeviceListVo.DataBean> data;
    private String room_name;
    private OnItemClickListener onItemClickListener;

    public UpdateRoomDeviceListAdapter(Context context, List<RoomDeviceListVo.DataBean> data, String room_name) {
        this.context = context;
        this.data = data;
        this.room_name = room_name;
    }

    public List<RoomDeviceListVo.DataBean> getAllList() {
        if (data == null) {
            data = new ArrayList<>();
        }
        return data;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClickListener(int pos, List<RoomDeviceListVo.DataBean> myLiveList);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_add_room_device, parent, false);
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
                        onItemClickListener.onItemClickListener(position, data);
                    }
                });
            }
            if (data.get(position).getIs_selete()) {
                ((MyViewHolder) holder).iv_item_device_selete.setImageResource(R.mipmap.device_log_selete);
            } else {
                ((MyViewHolder) holder).iv_item_device_selete.setImageResource(R.mipmap.device_log_not_selete);
            }
            Picasso.with(context).load(data.get(position).getDevice_pic()).into(((MyViewHolder) holder).iv_pic);
            ((MyViewHolder) holder).tv_text.setText(data.get(position).getDevice_name());
            ((MyViewHolder) holder).tv_room.setText(room_name + "");

        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_item_device_selete, iv_pic;
        TextView tv_text, tv_room;

        public MyViewHolder(View itemView) {
            super(itemView);
            iv_item_device_selete = itemView.findViewById(R.id.iv_item_device_selete);
            iv_pic = itemView.findViewById(R.id.iv_pic);
            tv_text = itemView.findViewById(R.id.tv_text);
            tv_room = itemView.findViewById(R.id.tv_room);
        }
    }


}