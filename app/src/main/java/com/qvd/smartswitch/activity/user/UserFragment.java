package com.qvd.smartswitch.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseFragment;
import com.qvd.smartswitch.activity.login.LoginActivity;
import com.qvd.smartswitch.api.CacheSetting;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.user.UserInfoVo;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.ConfigUtils;
import com.qvd.smartswitch.utils.SharedPreferencesUtil;
import com.qvd.smartswitch.utils.ToastUtil;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.EvictDynamicKey;

/**
 * Created by Administrator on 2018/6/14 0014.
 */

public class UserFragment extends BaseFragment {

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
    private String userId;


    public static UserFragment newInstance(String param1) {
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        args.putString("agrs1", param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_user;
    }

    @Override
    public void onResume() {
        super.onResume();
        userId = SharedPreferencesUtil.getString(getActivity(), SharedPreferencesUtil.USER_ID);
        if (CommonUtils.isEmptyString(userId)) {
            tvNickname.setText("未登录，点击立即去登录");
            tvNum.setVisibility(View.GONE);
        } else {
            getUserInfo();
        }
    }

    private void getUserInfo() {
        CacheSetting.getCache().getUserInfo(RetrofitService.qdoApi.getUserInfo(ConfigUtils.user_id),
                new DynamicKey(userId), new EvictDynamicKey(false))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UserInfoVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(UserInfoVo userInfoVo) {
                        if (userInfoVo != null) {
                            if (userInfoVo.getCode() == 200) {
                                if (userInfoVo.getData() != null) {
                                    tvNickname.setText(userInfoVo.getData().getUser_name());
                                    tvNum.setText("设备数量: " + userInfoVo.getData().getDevices_count());
                                } else {
                                    tvNickname.setText("名称为空");
                                    tvNum.setText("设备数量: 0");
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    @OnClick({R.id.iv_message, R.id.rl_portrait, R.id.rl_family, R.id.rl_share, R.id.rl_help, R.id.rl_setting})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_message:
                //消息中心
                ToastUtil.showToast("功能开发中。请期待");
                break;
            case R.id.rl_portrait:
                //头像
                if (CommonUtils.isEmptyString(userId)) {
                    startActivity(new Intent(mActivity, LoginActivity.class));
                    getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                } else {
                    startActivity(new Intent(getActivity(), UserInformationActivity.class));
                    getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
                break;
            case R.id.rl_family:
                //我的家庭
                ToastUtil.showToast("功能开发中。请期待");
//                startActivity(new Intent(getActivity(), UserFamilyActivity.class));
//                getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.rl_share:
                //共享设备
                startActivity(new Intent(getActivity(), UserShareActivity.class));
                getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.rl_help:
                //反馈帮助
                startActivity(new Intent(getActivity(), UserHelpActivity.class));
                getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.rl_setting:
                //设置
                ToastUtil.showToast("功能开发中。请期待");
//                startActivity(new Intent(getActivity(), UserSettingActivity.class));
//                getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
        }
    }
}
