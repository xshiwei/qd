package com.qvd.smartswitch.activity.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qvd.smartswitch.MyApplication;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseFragment;
import com.qvd.smartswitch.utils.CacheUtils;
import com.qvd.smartswitch.utils.SnackbarUtils;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2018/4/2.
 */

public class UserFragment extends BaseFragment {

    @BindView(R.id.rl_user_handbook)
    RelativeLayout rlUserHandbook;
    @BindView(R.id.rl_user_faq)
    RelativeLayout rlUserFaq;
    @BindView(R.id.rl_user_about_company)
    RelativeLayout rlUserAboutCompany;
    @BindView(R.id.rl_user_clear_cache)
    RelativeLayout rlUserClearCache;
    @BindView(R.id.tv_user_cache)
    TextView tvUserCache;
    @BindView(R.id.rl_sound_control)
    RelativeLayout rlSoundControl;
    Unbinder unbinder;

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
    protected void initData() {
        super.initData();
        try {
            tvUserCache.setText(CacheUtils.getTotalCacheSize(MyApplication.getContext()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @OnClick({R.id.rl_user_handbook, R.id.rl_user_faq, R.id.rl_user_about_company, R.id.rl_user_clear_cache, R.id.rl_sound_control})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_user_handbook:
                //进入用户手册
                break;
            case R.id.rl_user_faq:
                //FAQ
                break;
            case R.id.rl_user_about_company:
                //关于科微多
                startActivity(new Intent(getActivity(), AboutCompanyActivity.class));
                getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.rl_user_clear_cache:
                //清除缓存
                new AlertDialog.Builder(getActivity())
                        .setMessage(R.string.user_delete_cache_title)
                        .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CacheUtils.clearAllCache(MyApplication.getContext());
                                SnackbarUtils.Short(rlUserClearCache, "缓存已清理").show();
                                try {
                                    tvUserCache.setText(CacheUtils.getTotalCacheSize(MyApplication.getContext()));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).setNegativeButton(R.string.dialog_cancle, null).create().show();
                break;
            case R.id.rl_sound_control:
                //进入语音识别界面
                startActivity(new Intent(getActivity(), SoundControlActivity.class));
                break;
        }
    }

}
