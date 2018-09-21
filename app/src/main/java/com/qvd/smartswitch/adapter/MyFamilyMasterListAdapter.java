package com.qvd.smartswitch.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.model.user.FamilyListVo;
import com.qvd.smartswitch.utils.CommonUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyFamilyMasterListAdapter extends BaseQuickAdapter<FamilyListVo.DataBean.MasterFamilyMembersBean, BaseViewHolder> {

    public MyFamilyMasterListAdapter(@Nullable List<FamilyListVo.DataBean.MasterFamilyMembersBean> data) {
        super(R.layout.item_family_initiative, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, FamilyListVo.DataBean.MasterFamilyMembersBean item) {
        helper.setText(R.id.tv_user, item.getUser_name());
        if (!CommonUtils.isEmptyString(item.getUser_avatar())) {
            Picasso.with(mContext).load(item.getUser_avatar()).into((ImageView) helper.getView(R.id.iv_user));
        }
        if (item.getIs_agree() == 1) {
            helper.setText(R.id.tv_device_num, item.getFamily_members_relation());
        } else {
            helper.setText(R.id.tv_device_num, "对方还未接受你的邀请");
        }
    }
}
