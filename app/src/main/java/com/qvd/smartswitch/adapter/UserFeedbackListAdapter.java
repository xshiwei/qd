package com.qvd.smartswitch.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.model.user.UserFeedbackListVo;
import com.qvd.smartswitch.utils.CommonUtils;

import java.util.List;

public class UserFeedbackListAdapter extends BaseItemDraggableAdapter<UserFeedbackListVo.DataBean, BaseViewHolder> {

    public UserFeedbackListAdapter(@Nullable List<UserFeedbackListVo.DataBean> data) {
        super(R.layout.item_user_feedback_list, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, UserFeedbackListVo.DataBean item) {
        helper.setText(R.id.tv_content, item.getFeedback_content())
                .setText(R.id.tv_name, CommonUtils.getDeviceName(item.getCategory_type()) + "  |")
                .setText(R.id.tv_time, "  " + item.getFeedback_time());
    }
}
