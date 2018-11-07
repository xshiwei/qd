package com.qvd.smartswitch.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.model.home.HomeBackgroundVo;

import java.util.List;

/**
 * Created by Administrator on 2018/6/12 0012.
 */

public class HomeSettingPicAdapter extends BaseQuickAdapter<HomeBackgroundVo, BaseViewHolder> {
    private int mPosition = 0;

    public HomeSettingPicAdapter(@Nullable List<HomeBackgroundVo> data) {
        super(R.layout.item_home_setting_background, data);
    }

    public void setSelection(int position) {
        this.mPosition = position;
        notifyDataSetChanged();
    }

    @Override
    protected void convert(BaseViewHolder helper, HomeBackgroundVo item) {
        if (mPosition == helper.getLayoutPosition()) {
            helper.setVisible(R.id.iv_selete, true);
        } else {
            helper.setGone(R.id.iv_selete, true);
        }
        Glide.with(mContext).load(item.getPic()).into((ImageView) helper.getView(R.id.iv_background));
    }
}
