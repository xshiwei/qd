package com.qvd.smartswitch.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.model.user.DeviceShareManageListVo;
import com.qvd.smartswitch.utils.CommonUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DeviceShareManageListAdapter extends BaseQuickAdapter<DeviceShareManageListVo.DataBean, BaseViewHolder> {
    public DeviceShareManageListAdapter(@Nullable List<DeviceShareManageListVo.DataBean> data) {
        super(R.layout.item_device_share_manage, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DeviceShareManageListVo.DataBean item) {

        helper.setText(R.id.tv_name, item.getUser_name())
                .setText(R.id.tv_text, item.getAdd_time().split(" ")[0] + "共享 " + CommonUtils.getShareState(item.getIs_share()))
                .addOnClickListener(R.id.tv_delete);
        if (!CommonUtils.isEmptyString(item.getUser_avatar())) {
            Picasso.with(mContext).load(item.getUser_avatar()).into((ImageView) helper.getView(R.id.civ_portrait));
        }
    }
}
