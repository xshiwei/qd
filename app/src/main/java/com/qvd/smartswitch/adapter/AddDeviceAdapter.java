package com.qvd.smartswitch.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

@SuppressWarnings("unchecked")
public abstract class AddDeviceAdapter<T> extends RecyclerView.Adapter<AddDeviceHolder> {
    protected List<T> list;
    protected Context mContext;
    protected AddDeviceListener listener;
    protected LayoutInflater mInflater;
    private RecyclerView mRecyclerView;

    public AddDeviceAdapter(Context context, List<T> list, AddDeviceListener listener) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        this.list = list;
        this.listener = listener;
    }

    @Override
    public AddDeviceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(getLayoutId(viewType), parent, false);
        return getHolder(view, viewType);
    }

    protected abstract int getLayoutId(int viewType);

    @Override
    public void onBindViewHolder(AddDeviceHolder holder, int position) {
        holder.bindHolder(list.get(position), position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    protected abstract AddDeviceHolder getHolder(View view, int viewType);

}
