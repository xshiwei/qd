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
import com.qvd.smartswitch.activity.device.DeviceFragment;
import com.qvd.smartswitch.db.DeviceNickNameDaoOpe;
import com.qvd.smartswitch.model.DeviceNickNameVo;
import com.qvd.smartswitch.utils.CommonUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/4/2.
 */

public class DeviceListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<BleDevice> bleDeviceList;

    public DeviceListAdapter(Context context) {
        this.context = context;
        bleDeviceList = new ArrayList<>();
    }

    public void addDevice(BleDevice bleDevice) {
        removeDevice(bleDevice);
        bleDeviceList.add(bleDevice);
    }

    public List<BleDevice> getList() {
        return bleDeviceList;
    }

    public int getPostion(BleDevice bleDevice) {
        int postion = 0;
        for (int i = 0; i < bleDeviceList.size(); i++) {
            BleDevice device = bleDeviceList.get(i);
            if (bleDevice.getKey().equals(device.getKey())) {
                postion = i;
            }
        }
        return postion;
    }

    public void removeDevice(BleDevice bleDevice) {
        for (int i = 0; i < bleDeviceList.size(); i++) {
            BleDevice device = bleDeviceList.get(i);
            if (bleDevice.getKey().equals(device.getKey())) {
                bleDeviceList.remove(i);
            }
        }
    }

    public void clearConnectedDevice() {
        for (int i = 0; i < bleDeviceList.size(); i++) {
            BleDevice device = bleDeviceList.get(i);
            if (BleManager.getInstance().isConnected(device)) {
                bleDeviceList.remove(i);
            }
        }
    }

    public void clearScanDevice() {
        for (int i = 0; i < bleDeviceList.size(); i++) {
            BleDevice device = bleDeviceList.get(i);
            if (!BleManager.getInstance().isConnected(device)) {
                bleDeviceList.remove(i);
            }
        }
    }

    public void clear() {
        clearConnectedDevice();
        clearScanDevice();
    }

    public interface OnDeviceClickListener {
        void onConnect(View v, View v1, View v2, int position) throws Exception;

        void onDisConnect(View v, int position);
    }

    private OnDeviceClickListener mListener;

    public void setOnDeviceClickListener(OnDeviceClickListener listener) {
        this.mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_device_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof MyViewHolder) {
            final BleDevice bleDevice = bleDeviceList.get(position);
            DeviceNickNameVo deviceNickNameVo = DeviceNickNameDaoOpe.queryOne(context, CommonUtils.getMac(bleDevice.getMac()));
            if (bleDevice != null && deviceNickNameVo != null) {
                boolean isConnected = BleManager.getInstance().isConnected(bleDevice);
                String name = bleDevice.getName();

                String deviceNickname = deviceNickNameVo.getDeviceNickname();
                if (!CommonUtils.isEmptyString(deviceNickname)) {
                    ((MyViewHolder) holder).tv_decive_name.setText(deviceNickname);
                } else {
                    if (CommonUtils.isEmptyString(name)) {
                        ((MyViewHolder) holder).tv_decive_name.setText("The unKnown device");
                    } else {
                        ((MyViewHolder) holder).tv_decive_name.setText(name);
                    }
                }

                if (isConnected) {
                    Picasso.with(context).load(R.mipmap.device_connect).into(((MyViewHolder) holder).iv_item_device_pic);
                    ((MyViewHolder) holder).tv_decive_state.setText("已连接");
                    ((MyViewHolder) holder).tv_item_device_off.setVisibility(View.VISIBLE);
                } else {
                    Picasso.with(context).load(R.mipmap.device_unconnect).into(((MyViewHolder) holder).iv_item_device_pic);
                    ((MyViewHolder) holder).tv_decive_state.setText("未连接");
                    ((MyViewHolder) holder).tv_item_device_off.setVisibility(View.GONE);
                }
            }

            ((MyViewHolder) holder).tv_item_device_off.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onDisConnect(((MyViewHolder) holder).tv_item_device_off, position);
                    }
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        try {
                            mListener.onConnect(holder.itemView, ((MyViewHolder) holder).progress, ((MyViewHolder) holder).tv_connecting, position);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

        }
    }


    @Override
    public int getItemCount() {
        return bleDeviceList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_decive_state, tv_item_device_off, tv_decive_name, tv_connecting;
        ImageView iv_item_device_pic;
        ProgressBar progress;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_decive_state = itemView.findViewById(R.id.tv_decive_state);
            tv_item_device_off = itemView.findViewById(R.id.tv_item_device_off);
            tv_decive_name = itemView.findViewById(R.id.tv_decive_name);
            iv_item_device_pic = itemView.findViewById(R.id.iv_item_device_pic);
            progress = itemView.findViewById(R.id.progress);
            tv_connecting = itemView.findViewById(R.id.tv_connecting);
        }
    }
}
