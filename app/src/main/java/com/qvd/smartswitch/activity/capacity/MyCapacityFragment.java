package com.qvd.smartswitch.activity.capacity;

import android.os.Bundle;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseFragment;

/**
 * Created by Administrator on 2018/6/14 0014.
 */

public class MyCapacityFragment extends BaseFragment {

    public static MyCapacityFragment newInstance(String param1) {
        MyCapacityFragment fragment = new MyCapacityFragment();
        Bundle args = new Bundle();
        args.putString("agrs1", param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_my_capacity;
    }
}
