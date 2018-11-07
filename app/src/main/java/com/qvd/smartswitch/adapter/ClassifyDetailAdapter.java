package com.qvd.smartswitch.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.model.home.RightBean;

import java.util.List;


public class ClassifyDetailAdapter extends AddDeviceAdapter<RightBean> {

    private final Context context;
    private final List<RightBean> list;

    public ClassifyDetailAdapter(Context context, List<RightBean> list, AddDeviceListener listener) {
        super(context, list, listener);
        this.context = context;
        this.list = list;
    }


    @Override
    protected int getLayoutId(int viewType) {
        return viewType == 0 ? R.layout.item_title : R.layout.item_classify_detail;
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).isTitle() ? 0 : 1;
    }

    @Override
    protected AddDeviceHolder getHolder(View view, int viewType) {
        return new ClassifyHolder(view, viewType, listener);
    }

    public class ClassifyHolder extends AddDeviceHolder<RightBean> {
        TextView tvCity;
        ImageView avatar;
        TextView tvTitle;

        ClassifyHolder(View itemView, int type, AddDeviceListener listener) {
            super(itemView, type, listener);
            switch (type) {
                case 0:
                    tvTitle = itemView.findViewById(R.id.tv_title);
                    break;
                case 1:
                    tvCity = itemView.findViewById(R.id.tvCity);
                    avatar = itemView.findViewById(R.id.ivAvatar);
                    break;
            }

        }

        @Override
        public void bindHolder(RightBean sortBean, int position) {
            int itemViewType = ClassifyDetailAdapter.this.getItemViewType(position);
            switch (itemViewType) {
                case 0:
                    tvTitle.setText(sortBean.getName());
                    break;
                case 1:
                    Glide.with(mContext).load(sortBean.getImgsrc()).into(avatar);
                    tvCity.setText(sortBean.getName());
                    break;
            }

        }
    }
}
