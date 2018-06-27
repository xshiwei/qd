package com.qvd.smartswitch.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.clj.fastble.BleManager;
import com.clj.fastble.data.BleDevice;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.db.DeviceNickNameDaoOpe;
import com.qvd.smartswitch.model.DeviceNickNameVo;
import com.qvd.smartswitch.utils.CommonUtils;

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
        void onConnect(BleDevice bleDevice) throws Exception;

        void onDisConnect(BleDevice bleDevice);

        void onDetail(BleDevice bleDevice);
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
                    ((MyViewHolder) holder).tv_content.setText(deviceNickname);
                } else {
                    if (CommonUtils.isEmptyString(name)) {
                        ((MyViewHolder) holder).tv_content.setText("The unKnown device");
                    } else {
                        ((MyViewHolder) holder).tv_content.setText(name);
                    }
                }

                if (isConnected) {
                    ((MyViewHolder) holder).tv_item_device_state.setVisibility(View.GONE);
                    ((MyViewHolder) holder).tv_item_device_state_two.setVisibility(View.VISIBLE);
                } else {
                    ((MyViewHolder) holder).tv_item_device_state.setVisibility(View.VISIBLE);
                    ((MyViewHolder) holder).tv_item_device_state_two.setVisibility(View.GONE);
                }
            }

            ((MyViewHolder) holder).tv_item_device_state.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        try {
                            mListener.onConnect(bleDevice);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            ((MyViewHolder) holder).tv_item_device_state_two.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onDisConnect(bleDevice);
                    }
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onDetail(bleDevice);
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
        TextView tv_content, tv_item_device_state, tv_item_device_state_two;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_item_device_state = itemView.findViewById(R.id.tv_item_device_state);
            tv_content = itemView.findViewById(R.id.tv_item_device_content);
            tv_item_device_state_two = itemView.findViewById(R.id.tv_item_device_state_two);
        }
    }
}
