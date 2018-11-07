package com.qvd.smartswitch.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.model.home.HomeListVo;

import java.util.List;

/**
 * Created by Administrator on 2018/6/12 0012.
 */

public class HomeMenuAdapter extends BaseQuickAdapter<HomeListVo.DataBean, BaseViewHolder> {

    public HomeMenuAdapter(@Nullable List<HomeListVo.DataBean> data) {
        super(R.layout.item_home_menu, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, HomeListVo.DataBean item) {
        helper.setText(R.id.tv_text, item.getFamily_name());
    }
}
