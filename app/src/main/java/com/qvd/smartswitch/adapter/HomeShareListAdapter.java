package com.qvd.smartswitch.adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.model.device.HomeShareDeviceListVo;
import com.qvd.smartswitch.model.device.RoomDeviceListVo;
import com.zcw.togglebutton.ToggleButton;

import java.util.List;

/**
 * Created by Administrator on 2018/6/6 0006.
 */

public class HomeShareListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<HomeShareDeviceListVo.DataBean> data;
    private View view;

    public HomeShareListAdapter(Context context, List<HomeShareDeviceListVo.DataBean> data) {
        this.context = context;
        this.data = data;
    }


    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClickListener(View view, int position);

        void onToggleButtonClickListener(boolean state, int position);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.item_home_device, parent, false);
        return new ViewHolderContent(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolderContent) {
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
                        onItemClickListener.onItemLongClickListener(view, position);
                        return true;
                    }
                });

                ((ViewHolderContent) holder).togglebutton.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
                    @Override
                    public void onToggle(boolean on) {
                        int position = holder.getLayoutPosition();
                        onItemClickListener.onToggleButtonClickListener(on, position);
                    }
                });

            }
            ((ViewHolderContent) holder).tv_device_name.setText(data.get(position).getDevice_name());
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolderContent extends RecyclerView.ViewHolder {
        TextView tv_device_name, tv_device_state;
        ImageView iv_device_pic;
        ToggleButton togglebutton;


        public ViewHolderContent(View itemView) {
            super(itemView);
            tv_device_name = itemView.findViewById(R.id.tv_device_name);
            tv_device_state = itemView.findViewById(R.id.tv_device_state);
            iv_device_pic = itemView.findViewById(R.id.iv_device_pic);
            togglebutton = itemView.findViewById(R.id.togglebutton);
        }
    }
}
