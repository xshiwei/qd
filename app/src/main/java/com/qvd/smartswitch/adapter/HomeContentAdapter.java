package com.qvd.smartswitch.adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.model.device.RoomDeviceListVo;
import com.zcw.togglebutton.ToggleButton;

import java.util.List;

/**
 * Created by Administrator on 2018/6/6 0006.
 */

class HomeContentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;
    private final List<RoomDeviceListVo.DataBean> data;

    public HomeContentAdapter(Context context, List<RoomDeviceListVo.DataBean> data) {
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
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.item_home_content_item, parent, false);
        return new ViewHolderContent(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolderContent) {
            ((ViewHolderContent) holder).tv_text.setText(data.get(position).getDevice_name());
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
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolderContent extends RecyclerView.ViewHolder {
        final TextView tv_text;
        final TextView tv_state;
        final ImageView iv_pic;
        final ToggleButton btn_toggle;


        ViewHolderContent(View itemView) {
            super(itemView);
            tv_text = itemView.findViewById(R.id.tv_text);
            tv_state = itemView.findViewById(R.id.tv_state);
            iv_pic = itemView.findViewById(R.id.iv_pic);
            btn_toggle = itemView.findViewById(R.id.btn_toggle);
        }
    }
}

