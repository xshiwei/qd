package com.qvd.smartswitch.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.model.user.FamilyListVo;
import com.qvd.smartswitch.utils.CommonUtils;

import java.util.List;

public class MyFamilySlaveListAdapter extends BaseQuickAdapter<FamilyListVo.DataBean.SlaveFamilyMembersBean, BaseViewHolder> {
    public MyFamilySlaveListAdapter(@Nullable List<FamilyListVo.DataBean.SlaveFamilyMembersBean> data) {
        super(R.layout.item_family_passivity, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, FamilyListVo.DataBean.SlaveFamilyMembersBean item) {
        helper.setText(R.id.tv_name, item.getUser_name());
        if (!CommonUtils.isEmptyString(item.getUser_avatar())) {
            Glide.with(mContext).load(item.getUser_avatar()).into((ImageView) helper.getView(R.id.civ_portrait));
        }
        if (item.getIs_agree() == 1) {
            helper.setText(R.id.tv_relation, "你是他/她的" + item.getFamily_members_relation());
            helper.setVisible(R.id.iv_more, true);
        } else {
            helper.setText(R.id.tv_relation, "请求添加你为他/她的家人");
            helper.setVisible(R.id.iv_more, false);
        }
    }
}
