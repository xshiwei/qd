package com.qvd.smartswitch.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.model.user.RecentSharePeopleListVo;
import com.qvd.smartswitch.utils.CommonUtils;

import java.util.List;

public class RecentSharePeopleListAdapter extends BaseQuickAdapter<RecentSharePeopleListVo.DataBean, BaseViewHolder> {
    public RecentSharePeopleListAdapter(@Nullable List<RecentSharePeopleListVo.DataBean> data) {
        super(R.layout.item_device_share_people, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, RecentSharePeopleListVo.DataBean item) {
        helper.setText(R.id.tv_name, item.getUser_name())
                .setText(R.id.tv_account, item.getUser_account());
        if (!CommonUtils.isEmptyString(item.getUser_avatar())) {
            Glide.with(mContext).load(item.getUser_avatar()).into((ImageView) helper.getView(R.id.iv_portrait));
        }
    }
}
