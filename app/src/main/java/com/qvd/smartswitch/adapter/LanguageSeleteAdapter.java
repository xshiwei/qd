package com.qvd.smartswitch.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.model.home.TestVo;

import java.util.List;

public class LanguageSeleteAdapter extends BaseQuickAdapter<TestVo, BaseViewHolder> {
    public LanguageSeleteAdapter(@Nullable List<TestVo> data) {
        super(R.layout.item_language_selete, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, TestVo item) {
        helper.setText(R.id.tv_language, item.getTest());
        if (helper.getLayoutPosition() == 0) {
            helper.setTextColor(R.id.tv_language, mContext.getResources().getColor(R.color.add_home_background));
        } else {
            helper.setTextColor(R.id.tv_language, mContext.getResources().getColor(R.color.home_setting_text));
        }
    }
}
