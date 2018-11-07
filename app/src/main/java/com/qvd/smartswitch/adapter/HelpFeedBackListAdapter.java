package com.qvd.smartswitch.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.model.user.HelpFeedbackListVo;
import com.qvd.smartswitch.utils.CommonUtils;

import java.util.List;

/**
 * Created by Administrator on 2018/6/13 0013.
 */

public class HelpFeedBackListAdapter extends BaseQuickAdapter<HelpFeedbackListVo.DataBeanX.DataBean, BaseViewHolder> {

    public HelpFeedBackListAdapter(@Nullable List<HelpFeedbackListVo.DataBeanX.DataBean> data) {
        super(R.layout.item_user_help_and_feedback, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, HelpFeedbackListVo.DataBeanX.DataBean item) {
        helper.setText(R.id.tv_name, item.getDevice_name());
        if (!CommonUtils.isEmptyString(item.getDevice_feedback_pic())) {
            Glide.with(mContext).load(item.getDevice_feedback_pic()).into((ImageView) helper.getView(R.id.iv_pic));
        }else {
            Glide.with(mContext).load(R.mipmap.user_help_pic).into((ImageView) helper.getView(R.id.iv_pic));
        }
    }
}
