package com.qvd.smartswitch.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.model.home.HomeBackgroundVo;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Administrator on 2018/6/12 0012.
 */

public class HomeSettingPicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<HomeBackgroundVo> data;
    private int mPosition = 0;

    public HomeSettingPicAdapter(Context context, List<HomeBackgroundVo> data) {
        this.context = context;
        this.data = data;
    }

    public void setSelection(int position) {
        this.mPosition = position;
        notifyDataSetChanged();
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_home_setting_background, parent, false);
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

            if (mPosition == position) {
                ((MyViewHolder) holder).iv_selete.setVisibility(View.VISIBLE);
            } else {
                ((MyViewHolder) holder).iv_selete.setVisibility(View.GONE);
            }
            Picasso.with(context).load(data.get(position).getPic()).into(((MyViewHolder) holder).iv_background);
        }
    }


    @Override
    public int getItemCount() {
        return data.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_selete, iv_background;

        public MyViewHolder(View itemView) {
            super(itemView);
            iv_selete = itemView.findViewById(R.id.iv_selete);
            iv_background = itemView.findViewById(R.id.iv_background);
        }
    }
}
