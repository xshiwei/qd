package com.qvd.smartswitch.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.widget.FrameLayout;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.clj.fastble.BleManager;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.activity.capacity.CapacityFragment;
import com.qvd.smartswitch.activity.home.HomeFragmentTest;
import com.qvd.smartswitch.activity.user.UserFragmentTest;
import com.qvd.smartswitch.utils.SysApplication;
import com.qvd.smartswitch.utils.ToastUtil;

import butterknife.BindView;

public class MainActivity extends BaseActivity implements BottomNavigationBar.OnTabSelectedListener {

    @BindView(R.id.home_container)
    FrameLayout homeContainer;
    @BindView(R.id.bottom_navigation_bar)
    BottomNavigationBar bottomNavigationBar;

    private Fragment homeFragment;
    //private Fragment deviceFragment;
    private Fragment capacityFragment;
    private Fragment userFragment;

    private long firstTime = 0;


    @Override
    protected int setLayoutId() {
        return R.layout.activity_main;
    }


    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.keyboardEnable(true).init();
    }

    @Override
    protected void initView() {
        SysApplication.getInstance().addActivity(this);
        bottomNavigationBar
                .setActiveColor(R.color.deepskyblue)
                .setInActiveColor(R.color.gray)
                .setBarBackgroundColor(R.color.white);
        bottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_DEFAULT);
        int lastSelectedPosition = 0;
        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.mipmap.home_navgationbar_selete, "首页").setInactiveIcon(ContextCompat.getDrawable(this, R.mipmap.home_navgationbar_unselete)))
                .addItem(new BottomNavigationItem(R.mipmap.device_navgationbar_selete, "智能").setInactiveIcon(ContextCompat.getDrawable(this, R.mipmap.device_navgationbar_unselete)))
                .addItem(new BottomNavigationItem(R.mipmap.user_navgationbar_selete, "我的").setInactiveIcon(ContextCompat.getDrawable(this, R.mipmap.user_navgationbar_unselete)))
                .setFirstSelectedPosition(lastSelectedPosition)
                .initialise();
        bottomNavigationBar.setTabSelectedListener(this);
        setDefaultFragment();
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
                    SysApplication.getInstance().exit();
                }
                break;
        }
        return super.onKeyUp(keyCode, event);
    }


    /*设置默认Fragment*/
    private void setDefaultFragment() {
        if (homeFragment == null)
            //新版
            homeFragment = HomeFragmentTest.newInstance("Home");
        //旧版
        //homeFragment = HomeFragment.newInstance("Home");
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
        hideFrag(capacityFragment);
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
                    assert homeFragment != null;
                    homeFragment = HomeFragmentTest.newInstance("home");
                    //homeFragment = HomeFragment.newInstance("home");
                }
                mImmersionBar.fitsSystemWindows(false).transparentStatusBar().init();
                addFrag(homeFragment);
                getSupportFragmentManager().beginTransaction().show(homeFragment).commit();

                break;
            case 1:
                if (capacityFragment == null) {
                    assert capacityFragment != null;
                    capacityFragment = CapacityFragment.newInstance("device");
                    //capacityFragment = DeviceFragment.newInstance("device");
                }
                mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
                addFrag(capacityFragment);
                getSupportFragmentManager().beginTransaction().show(capacityFragment).commit();

                break;
            case 2:
                if (userFragment == null) {
                    assert userFragment != null;
                    userFragment = UserFragmentTest.newInstance("user");
                    //userFragment = UserFragment.newInstance("user");
                }
                mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
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
        BleManager.getInstance().disconnectAllDevice();
        BleManager.getInstance().destroy();
    }
}
