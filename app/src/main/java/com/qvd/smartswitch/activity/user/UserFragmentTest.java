package com.qvd.smartswitch.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2018/6/14 0014.
 */

public class UserFragmentTest extends BaseFragment {


    @BindView(R.id.iv_message)
    ImageView ivMessage;
    @BindView(R.id.iv_portrait)
    ImageView ivPortrait;
    @BindView(R.id.tv_nickname)
    TextView tvNickname;
    @BindView(R.id.tv_num)
    TextView tvNum;
    @BindView(R.id.rl_portrait)
    RelativeLayout rlPortrait;
    @BindView(R.id.rl_family)
    RelativeLayout rlFamily;
    @BindView(R.id.rl_share)
    RelativeLayout rlShare;
    @BindView(R.id.rl_help)
    RelativeLayout rlHelp;
    @BindView(R.id.rl_setting)
    RelativeLayout rlSetting;
    Unbinder unbinder;

    public static UserFragmentTest newInstance(String param1) {
        UserFragmentTest fragment = new UserFragmentTest();
        Bundle args = new Bundle();
        args.putString("agrs1", param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_user_test;
    }


    @OnClick({R.id.iv_message, R.id.rl_portrait, R.id.rl_family, R.id.rl_share, R.id.rl_help, R.id.rl_setting})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_message:
                //消息中心
                break;
            case R.id.rl_portrait:
                //头像
                startActivity(new Intent(getActivity(), UserInformationActivity.class));
                getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.rl_family:
                //我的家庭
                startActivity(new Intent(getActivity(), UserFamilyActivity.class));
                getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.rl_share:
                //共享设备
                startActivity(new Intent(getActivity(),UserShareActivity.class));
                break;
            case R.id.rl_help:
                //反馈帮助
                startActivity(new Intent(getActivity(), UserHelpActivity.class));
                getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.rl_setting:
                //设置
                startActivity(new Intent(getActivity(), UserSettingActivity.class));
                getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
        }
    }
}
