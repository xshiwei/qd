package com.qvd.smartswitch.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

@SuppressWarnings("unchecked")
abstract class AddDeviceAdapter<T> extends RecyclerView.Adapter<AddDeviceHolder> {
    private final List<T> list;
    final Context mContext;
    final AddDeviceListener listener;
    private final LayoutInflater mInflater;
    private RecyclerView mRecyclerView;

    AddDeviceAdapter(Context context, List<T> list, AddDeviceListener listener) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AddDeviceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(getLayoutId(viewType), parent, false);
        return getHolder(view, viewType);
    }

    protected abstract int getLayoutId(int viewType);

    @Override
    public void onBindViewHolder(@NonNull AddDeviceHolder holder, int position) {
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
