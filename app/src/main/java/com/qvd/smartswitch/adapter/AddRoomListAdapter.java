package com.qvd.smartswitch.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.model.home.RoomPicListVo;
import com.qvd.smartswitch.model.home.Test2Vo;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Administrator on 2018/6/13 0013.
 */

public class AddRoomListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<RoomPicListVo.DataBean> data;

    public AddRoomListAdapter(Context context, List<RoomPicListVo.DataBean> data) {
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_add_room, parent, false);
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
            Picasso.with(context).load(data.get(position).getRoom_max_pic()).into(((MyViewHolder) holder).iv_pic);
            ((MyViewHolder) holder).tv_text.setText(data.get(position).getRoom_name());
        }
    }


    @Override
    public int getItemCount() {
        return data.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_pic;
        TextView tv_text;

        public MyViewHolder(View itemView) {
            super(itemView);
            iv_pic = itemView.findViewById(R.id.iv_pic);
            tv_text = itemView.findViewById(R.id.tv_text);
        }
    }
}
