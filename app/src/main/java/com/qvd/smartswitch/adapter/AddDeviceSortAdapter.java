package com.qvd.smartswitch.adapter;


import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.qvd.smartswitch.R;

import java.util.List;

public class AddDeviceSortAdapter extends AddDeviceAdapter<String> {

    private int checkedPosition;

    public void setCheckedPosition(int checkedPosition) {
        this.checkedPosition = checkedPosition;
        notifyDataSetChanged();
    }

    public AddDeviceSortAdapter(Context context, List<String> list, AddDeviceListener listener) {
        super(context, list, listener);
    }

    @Override
    protected int getLayoutId(int viewType) {
        return R.layout.item_sort_list;
    }

    @Override
    protected AddDeviceHolder getHolder(View view, int viewType) {
        return new SortHolder(view, viewType, listener);
    }

    private class SortHolder extends AddDeviceHolder<String> {

        private TextView tvName;
        private View mView;

        SortHolder(View itemView, int type, AddDeviceListener listener) {
            super(itemView, type, listener);
            this.mView = itemView;
            tvName = (TextView) itemView.findViewById(R.id.tv_sort);
        }

        @Override
        public void bindHolder(String string, int position) {
            tvName.setText(string);
            if (position == checkedPosition) {
                mView.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                tvName.setTextColor(mContext.getResources().getColor(R.color.add_device_selete));
            } else {
                mView.setBackgroundColor(mContext.getResources().getColor(R.color.add_device_background));
                tvName.setTextColor(mContext.getResources().getColor(R.color.black));
            }
        }

    }
}
