package com.qvd.smartswitch.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.model.device.DeviceCommonQuestionVo;
import com.qvd.smartswitch.model.home.RoomPicListVo;
import com.squareup.picasso.Picasso;

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
