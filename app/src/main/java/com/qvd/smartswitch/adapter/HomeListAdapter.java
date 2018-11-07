package com.qvd.smartswitch.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.model.home.HomeLeftListVo;

import java.util.List;

/**
 * Created by Administrator on 2018/6/6 0006.
 */

public class HomeListAdapter extends BaseQuickAdapter<HomeLeftListVo.DataBean, BaseViewHolder> {
    private int mPosition;

    public HomeListAdapter(@Nullable List<HomeLeftListVo.DataBean> data) {
        super(R.layout.item_home_list, data);
    }

    public void setCheckedPosition(int checkedPosition) {
        this.mPosition = checkedPosition;
        notifyDataSetChanged();
    }

    @Override
    protected void convert(BaseViewHolder helper, HomeLeftListVo.DataBean item) {
        Glide.with(mContext).load(item.getRoom_pic()).into((ImageView) helper.getView(R.id.iv_item_pic));
        helper.setText(R.id.tv_item_text, item.getRoom_name());
        if (mPosition == helper.getLayoutPosition()) {
            helper.setTextColor(R.id.tv_item_text, mContext.getResources().getColor(R.color.add_device_selete));
        } else {
            helper.setTextColor(R.id.tv_item_text, mContext.getResources().getColor(R.color.home_content_text));
        }
    }
}

