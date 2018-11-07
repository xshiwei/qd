package com.qvd.smartswitch.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;


public abstract class AddDeviceHolder<T> extends RecyclerView.ViewHolder {
    private final AddDeviceListener mListener;

    AddDeviceHolder(View itemView, int type, AddDeviceListener listener) {
        super(itemView);
        this.mListener = listener;
        itemView.setOnClickListener(v -> mListener.onItemClick(v.getId(), getAdapterPosition()));
    }

    public abstract void bindHolder(T t, int position);

}
