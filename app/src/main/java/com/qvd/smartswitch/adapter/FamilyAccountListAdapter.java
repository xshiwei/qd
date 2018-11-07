package com.qvd.smartswitch.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.model.user.FamilyListCommonVo;
import com.qvd.smartswitch.utils.CommonUtils;

import java.util.List;

public class FamilyAccountListAdapter extends BaseQuickAdapter<FamilyListCommonVo, BaseViewHolder> {
    public FamilyAccountListAdapter(@Nullable List<FamilyListCommonVo> data) {
        super(R.layout.item_family_passivity, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, FamilyListCommonVo item) {
        helper.setText(R.id.tv_name, item.getFamily_name())
                .setText(R.id.tv_relation, item.getFamily_relation());
        if (!CommonUtils.isEmptyString(item.getFamily_avatar())) {
            Glide.with(mContext).load(item.getFamily_avatar()).into((ImageView) helper.getView(R.id.civ_portrait));
        }
    }
}
