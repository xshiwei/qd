package com.qvd.smartswitch.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.model.device.DeviceCommonQuestionVo;

import java.util.List;

/**
 * Created by Administrator on 2018/6/13 0013.
 */

public class CommonQuestionAdapter extends BaseQuickAdapter<DeviceCommonQuestionVo.DataBean, BaseViewHolder> {

    public CommonQuestionAdapter(@Nullable List<DeviceCommonQuestionVo.DataBean> data) {
        super(R.layout.item_common_question, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DeviceCommonQuestionVo.DataBean item) {
        helper.setText(R.id.tv_title, item.getQuestion_title());
    }
}
