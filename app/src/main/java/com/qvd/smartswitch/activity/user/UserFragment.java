package com.qvd.smartswitch.activity.user;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseFragment;
import com.qvd.smartswitch.activity.robot.RobotControlActivity;
import com.qvd.smartswitch.api.CacheSetting;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.user.UserInfoVo;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.ConfigUtils;
import com.qvd.smartswitch.utils.SharedPreferencesUtil;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.EvictDynamicKey;

import static com.qvd.smartswitch.utils.CommonUtils.startIntentLogin;

/**
 * Created by Administrator on 2018/6/14 0014.
 */

public class UserFragment extends BaseFragment {

    @BindView(R.id.iv_message)
    ImageView ivMessage;
    @BindView(R.id.iv_portrait)
    CircleImageView ivPortrait;
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
        userId = SharedPreferencesUtil.getString(Objects.requireNonNull(getActivity()), SharedPreferencesUtil.USER_ID);
        if (CommonUtils.isEmptyString(userId)) {
            tvNickname.setText(R.string.common_not_login);
            tvNum.setText(R.string.user_no_device);
            Glide.with(this).load(R.mipmap.user_portrait).into(ivPortrait);
        } else {
            getUserInfo();
        }
    }

    private void getUserInfo() {
        CacheSetting.getCache().getUserInfo(RetrofitService.qdoApi.getUserInfo(userId),
                new DynamicKey(userId), new EvictDynamicKey(true))
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
                                    ConfigUtils.USERNAME = userInfoVo.getData().getUser_name();
                                    ConfigUtils.USERAVATAR = userInfoVo.getData().getUser_avatar();
                                    tvNum.setText(getString(R.string.common_device_num) + userInfoVo.getData().getDevices_count());
                                    Glide.with(getActivity()).load(userInfoVo.getData().getUser_avatar()).into(ivPortrait);
                                } else {
                                    tvNickname.setText(R.string.user_getdate_error);
                                    tvNum.setText(R.string.user_no_device);
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        tvNickname.setText(R.string.user_getdate_error);
                        tvNum.setText(R.string.user_no_device);
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
                startIntentLogin(userId, getActivity(), RobotControlActivity.class);
                break;
            case R.id.rl_portrait:
                //头像
                startIntentLogin(userId, getActivity(), UserInformationActivity.class);
                break;
            case R.id.rl_family:
                //我的家庭
                startIntentLogin(userId, getActivity(), UserFamilyActivity.class);
                break;
            case R.id.rl_share:
                //共享设备
                startIntentLogin(userId, getActivity(), UserShareActivity.class);
                break;
            case R.id.rl_help:
                //反馈帮助
                startIntentLogin(userId, getActivity(), UserHelpActivity.class);
                break;
            case R.id.rl_setting:
                //设置
                startIntentLogin(userId, getActivity(), UserSettingActivity.class);
                break;
        }
    }

}
