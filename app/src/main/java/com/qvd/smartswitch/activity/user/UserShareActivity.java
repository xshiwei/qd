package com.qvd.smartswitch.activity.user;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.adapter.TabLayoutAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/6/15 0015.
 */

public class UserShareActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tbl_share)
    TabLayout tblShare;
    @BindView(R.id.vp_share)
    ViewPager vpShare;


    /**
     * 定义tablayout的title集合
     */
    private final List<String> list_title = new ArrayList<>();

    @Override
    protected int setLayoutId() {
        return R.layout.activity_user_share;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }

    @Override
    protected void initData() {
        super.initData();
        ShareFragment shareFragment = ShareFragment.newInstance("share");
        ReceiveFragment receiveFragment = ReceiveFragment.newInstance("receive");

        /*
      定义要装fragment的列表
     */
        List<Fragment> list_fragment = new ArrayList<>();
        list_fragment.add(shareFragment);
        list_fragment.add(receiveFragment);

        list_title.add(getString(R.string.common_share));
        list_title.add(getString(R.string.common_receive));

        tblShare.setTabMode(View.FOCUSABLES_TOUCH_MODE);
        //TabLayout加载viewpager
        tblShare.setupWithViewPager(vpShare);
        //为TabLayout添加tab名称
        tblShare.addTab(tblShare.newTab().setText(list_title.get(0)));
        tblShare.addTab(tblShare.newTab().setText(list_title.get(1)));

        TabLayoutAdapter adapter = new TabLayoutAdapter(getSupportFragmentManager(), list_fragment, list_title);
        //viewpager加载adapter
        vpShare.setAdapter(adapter);
        vpShare.setCurrentItem(0);
    }

    @OnClick(R.id.iv_common_actionbar_goback)
    public void onViewClicked() {
        finish();
    }
}
