package com.qvd.smartswitch.activity.user;

import android.os.Bundle;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseFragment;

/**
 * Created by Administrator on 2018/6/14 0014.
 */

public class ReceiveFragment extends BaseFragment {

    public static ReceiveFragment newInstance(String param1) {
        ReceiveFragment fragment = new ReceiveFragment();
        Bundle args = new Bundle();
        args.putString("agrs1", param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_receive;
    }
}
