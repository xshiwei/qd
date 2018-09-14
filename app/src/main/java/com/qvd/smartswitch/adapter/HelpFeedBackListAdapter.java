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
import com.qvd.smartswitch.model.user.HelpFeedbackListVo;
import com.qvd.smartswitch.utils.CommonUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Administrator on 2018/6/13 0013.
 */

public class HelpFeedBackListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<HelpFeedbackListVo.DataBeanX.DataBean> data;

    public HelpFeedBackListAdapter(Context context, List<HelpFeedbackListVo.DataBeanX.DataBean> data) {
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_help_and_feedback, parent, false);
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
            ((MyViewHolder) holder).tv_name.setText(data.get(position).getDevice_name());
            if (!CommonUtils.isEmptyString(data.get(position).getDevice_feedback_pic())) {
                Picasso.with(context).load(data.get(position).getDevice_feedback_pic()).into(((MyViewHolder) holder).iv_pic);
            }
        }
    }


    @Override
    public int getItemCount() {
        return data.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name;
        ImageView iv_pic;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            iv_pic = itemView.findViewById(R.id.iv_pic);
        }
    }
}
