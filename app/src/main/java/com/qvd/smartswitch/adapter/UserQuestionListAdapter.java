package com.qvd.smartswitch.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.model.home.Test2Vo;

import java.util.List;

/**
 * Created by Administrator on 2018/6/13 0013.
 */

public class UserQuestionListAdapter extends BaseQuickAdapter<Test2Vo, BaseViewHolder> {

    public UserQuestionListAdapter(@Nullable List<Test2Vo> data) {
        super(R.layout.item_user_question, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Test2Vo item) {
        helper.setText(R.id.tv_text, item.getTest());
    }
}
