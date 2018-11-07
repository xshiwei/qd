package com.qvd.smartswitch.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.widget.FrameLayout;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.activity.home.HomeFragmentTest;
import com.qvd.smartswitch.activity.user.UserFragment;
import com.qvd.smartswitch.service.LogService;
import com.qvd.smartswitch.utils.ActivityManager;
import com.qvd.smartswitch.utils.ToastUtil;
import com.wenming.library.LogReport;

import butterknife.BindView;

public class MainActivity extends BaseActivity implements BottomNavigationBar.OnTabSelectedListener {

    @BindView(R.id.home_container)
    FrameLayout homeContainer;

    @BindView(R.id.bottom_navigation_bar)
    BottomNavigationBar bottomNavigationBar;

    private Fragment homeFragment;
    //private Fragment deviceFragment;
//    private Fragment capacityFragment;
    private Fragment userFragment;

    private long firstTime = 0;

    private Intent intent;


    @Override
    protected int setLayoutId() {
        return R.layout.activity_main;
    }


    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.keyboardEnable(true).statusBarDarkFont(false).init();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //开启日志
        LogReport.getInstance().upload(this);
        intent = new Intent(this, LogService.class);
        startService(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void initView() {
        bottomNavigationBar
                .setActiveColor(R.color.deepskyblue)
                .setInActiveColor(R.color.gray)
                .setBarBackgroundColor(R.color.white);
        bottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_DEFAULT);
        int lastSelectedPosition = 0;
        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.mipmap.home_navgationbar_selete, getString(R.string.common_home)).setInactiveIcon(ContextCompat.getDrawable(this, R.mipmap.home_navgationbar_unselete)))
//                .addItem(new BottomNavigationItem(R.mipmap.device_navgationbar_unselete, "智能").setInactiveIcon(ContextCompat.getDrawable(this, R.mipmap.device_navgationbar_selete)))
                .addItem(new BottomNavigationItem(R.mipmap.user_navgationbar_selete, getString(R.string.common_user)).setInactiveIcon(ContextCompat.getDrawable(this, R.mipmap.user_navgationbar_unselete)))
                .setFirstSelectedPosition(lastSelectedPosition)
                .initialise();
        bottomNavigationBar.setTabSelectedListener(this);
//        setDefaultFragment();
        onTabSelected(0);
//        bottomNavigationBar.selectTab(0);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                long secondTime = System.currentTimeMillis();
                //如果两次按键时间间隔大于2秒，则不退出
                //两次按键小于2秒时，退出应用
                if (secondTime - firstTime > 2000) {
                    ToastUtil.showToast(getString(R.string.exit_app));
                    firstTime = secondTime;//更新firstTime
                    return true;
                } else {
                    stopService(intent);
                    ActivityManager.getInstance().exitApp();
                }

                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    /*设置默认Fragment*/
    private void setDefaultFragment() {
        if (homeFragment == null)
            mImmersionBar.fitsSystemWindows(false).transparentStatusBar().statusBarDarkFont(false).init();
        //新版
        homeFragment = HomeFragmentTest.newInstance("Home");
        //旧版
//        homeFragment = HomeFragment.newInstance("Home");
        addFrag(homeFragment);
        /*默认显示msgFrag*/
        getSupportFragmentManager().beginTransaction().show(homeFragment).commit();
    }

    /*添加Frag*/
    private void addFrag(Fragment frag) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        if (frag != null && !frag.isAdded())
            ft.add(R.id.home_container, frag);
        ft.commit();
    }

    /*隐藏所有fragment*/
    private void hideAllFrag() {
        hideFrag(homeFragment);
//        hideFrag(capacityFragment);
        hideFrag(userFragment);
    }

    /*隐藏frag*/
    private void hideFrag(Fragment frag) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (frag != null && frag.isAdded())
            ft.hide(frag);
        ft.commit();
    }

    @Override
    public void onTabSelected(int position) {
        hideAllFrag();//先隐藏所有frag
        switch (position) {
            case 0:
                if (homeFragment == null) {
                    assert false;
                    homeFragment = HomeFragmentTest.newInstance("home");
//                    homeFragment = HomeFragment.newInstance("home");
                }
                mImmersionBar.fitsSystemWindows(false).transparentStatusBar().statusBarDarkFont(false).init();
                addFrag(homeFragment);
                getSupportFragmentManager().beginTransaction().show(homeFragment).commit();
                break;
//            case 1:
//                if (capacityFragment == null) {
//                    assert capacityFragment != null;
//                    capacityFragment = CapacityFragment.newInstance("device");
////                    capacityFragment = DeviceFragment.newInstance("device");
//                }
//                mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).statusBarDarkFont(true, 1).init();
//                addFrag(capacityFragment);
//                getSupportFragmentManager().beginTransaction().show(capacityFragment).commit();
//                break;
            case 1:
                if (userFragment == null) {
                    assert false;
                    userFragment = UserFragment.newInstance("user");
                }
                mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).statusBarDarkFont(true, 1).init();
                addFrag(userFragment);
                getSupportFragmentManager().beginTransaction().show(userFragment).commit();
                break;
        }
    }

    @Override
    public void onTabUnselected(int position) {

    }

    @Override
    public void onTabReselected(int position) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
