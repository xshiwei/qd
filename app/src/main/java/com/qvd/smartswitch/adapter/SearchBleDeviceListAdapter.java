package com.qvd.smartswitch.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.clj.fastble.BleManager;
import com.clj.fastble.data.BleDevice;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.db.DeviceNickNameDaoOpe;
import com.qvd.smartswitch.model.DeviceNickNameVo;
import com.qvd.smartswitch.utils.CommonUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/4/2.
 */

public class SearchBleDeviceListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<BleDevice> bleDeviceList;

    public SearchBleDeviceListAdapter(Context context, List<BleDevice> deviceList) {
        this.context = context;
        this.bleDeviceList = deviceList;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClickListener(View view, int position);
    }

    private HomeMenuAdapter.OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(HomeMenuAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search_ble_device, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof MyViewHolder) {
            if (onItemClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = holder.getLayoutPosition();
                        onItemClickListener.onItemClick(holder.itemView, position);
                    }
                });
            }
            ((MyViewHolder) holder).tv_decive_name.setText(bleDeviceList.get(position).getName());
            ((MyViewHolder) holder).tv_decive_rssi.setText(bleDeviceList.get(position).getRssi()+"");
        }
    }


    @Override
    public int getItemCount() {
        return bleDeviceList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_decive_name, tv_decive_rssi;
        ImageView iv_item_device_pic;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_decive_name = itemView.findViewById(R.id.tv_decive_name);
            tv_decive_rssi = itemView.findViewById(R.id.tv_decive_rssi);
            iv_item_device_pic = itemView.findViewById(R.id.iv_item_device_pic);
        }
    }
}
