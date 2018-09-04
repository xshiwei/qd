package com.qvd.smartswitch.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.home.HomeFragment;
import com.qvd.smartswitch.activity.home.HomeFragmentTest;
import com.qvd.smartswitch.model.home.HomeLeftListVo;
import com.qvd.smartswitch.model.home.TestVo;
import com.qvd.smartswitch.utils.CommonUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Administrator on 2018/6/6 0006.
 */

public class HomeListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<HomeLeftListVo.DataBean> data;
    private int mPosition;

    public void setCheckedPosition(int checkedPosition) {
        this.mPosition = checkedPosition;
        notifyDataSetChanged();
    }

    public HomeListAdapter(Context context, List<HomeLeftListVo.DataBean> data) {
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_home_list, parent, false);
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
            Picasso.with(context).load(data.get(position).getRoom_pic()).into(((MyViewHolder) holder).iv_item_pic);
            ((MyViewHolder) holder).tv_item_text.setText(data.get(position).getRoom_name());
            if (mPosition == position) {
                ((MyViewHolder) holder).tv_item_text.setTextColor(context.getResources().getColor(R.color.add_device_selete));
            } else {
                ((MyViewHolder) holder).tv_item_text.setTextColor(context.getResources().getColor(R.color.home_content_text));
            }
        }
    }


    @Override
    public int getItemCount() {
        return data.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_item_pic;
        TextView tv_item_text;

        public MyViewHolder(View itemView) {
            super(itemView);
            iv_item_pic = itemView.findViewById(R.id.iv_item_pic);
            tv_item_text = itemView.findViewById(R.id.tv_item_text);
        }
    }
}

