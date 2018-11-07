package com.qvd.smartswitch.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.model.home.HomeListVo;

import java.util.List;

/**
 * Created by Administrator on 2018/6/13 0013.
 */

public class SpaceManageListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;
    private final List<HomeListVo.DataBean> data;

    public SpaceManageListAdapter(Context context, List<HomeListVo.DataBean> data) {
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


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_home_manage, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {
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
            ((MyViewHolder) holder).tv_home.setText(data.get(position).getFamily_name());
        }
    }


    @Override
    public int getItemCount() {
        return data.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView tv_home;

        MyViewHolder(View itemView) {
            super(itemView);
            tv_home = itemView.findViewById(R.id.tv_home);
        }
    }
}
