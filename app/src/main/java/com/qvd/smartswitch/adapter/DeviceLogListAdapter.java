package com.qvd.smartswitch.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.model.DeviceLogVo;
import com.qvd.smartswitch.utils.MatchingUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by xushiwei on 2017/11/7.
 */

public class DeviceLogListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int MYLIVE_MODE_CHECK = 0;
    int mEditMode = MYLIVE_MODE_CHECK;
    private Context context;
    private List<DeviceLogVo> data;
    private OnItemClickListener onItemClickListener;

    public DeviceLogListAdapter(Context context, List<DeviceLogVo> data) {
        this.context = context;
        this.data = data;
    }

    public void notifyAdapter(List<DeviceLogVo> deviceLogVoList, boolean isAdd) {
        if (!isAdd) {
            this.data = deviceLogVoList;
        } else {
            this.data.addAll(deviceLogVoList);
        }
        notifyDataSetChanged();
    }

    public List<DeviceLogVo> getMyLiveList() {
        if (data == null) {
            data = new ArrayList<>();
        }
        return data;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClickListener(int pos, List<DeviceLogVo> myLiveList);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_device_log_list, parent, false);
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
            if (mEditMode == MYLIVE_MODE_CHECK) {
                ((MyViewHolder) holder).iv_item_device_log_selete.setVisibility(View.GONE);
            } else {
                ((MyViewHolder) holder).iv_item_device_log_selete.setVisibility(View.VISIBLE);

                if (data.get(position).isSelete()) {
                    ((MyViewHolder) holder).iv_item_device_log_selete.setImageResource(R.mipmap.device_log_selete);
                } else {
                    ((MyViewHolder) holder).iv_item_device_log_selete.setImageResource(R.mipmap.device_log_not_selete);
                }
            }
            ((MyViewHolder) holder).tv_item_device_log_nickname.setText(data.get(position).getDeviceNickname());
            ((MyViewHolder) holder).tv_item_device_log_data.setText(data.get(position).getDate());
            ((MyViewHolder) holder).tv_item_device_log_state.setText(MatchingUtils.getLogDeviceState(data.get(position).getState()));
            ((MyViewHolder) holder).tv_item_device_log_type.setText(MatchingUtils.getLogDeviceType(data.get(position).getType()));
        }
    }

    public void setEditMode(int editMode) {
        mEditMode = editMode;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return data.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_item_device_log_selete;
        TextView tv_item_device_log_nickname, tv_item_device_log_type, tv_item_device_log_data, tv_item_device_log_state;

        public MyViewHolder(View itemView) {
            super(itemView);
            iv_item_device_log_selete = itemView.findViewById(R.id.iv_item_device_log_selete);
            tv_item_device_log_nickname = itemView.findViewById(R.id.tv_item_device_log_nickname);
            tv_item_device_log_type = itemView.findViewById(R.id.tv_item_device_log_type);
            tv_item_device_log_data = itemView.findViewById(R.id.tv_item_device_log_data);
            tv_item_device_log_state = itemView.findViewById(R.id.tv_item_device_log_state);
        }
    }


}
