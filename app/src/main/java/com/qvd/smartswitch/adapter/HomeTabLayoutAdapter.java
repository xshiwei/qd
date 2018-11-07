package com.qvd.smartswitch.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.qvd.smartswitch.activity.home.HomeDeviceFragment;

import java.util.List;

/**
 * Created by XuRedDan on 2017/9/15.
 */

public class HomeTabLayoutAdapter extends FragmentPagerAdapter {

    private final List<HomeDeviceFragment> list_fragment;                         //fragment列表
    private final List<String> list_Title;                              //tab名的列表


    public HomeTabLayoutAdapter(FragmentManager fm, List<HomeDeviceFragment> list_fragment, List<String> list_Title) {
        super(fm);
        this.list_fragment = list_fragment;
        this.list_Title = list_Title;
    }

    @Override
    public Fragment getItem(int position) {
        return list_fragment.get(position);
    }

    @Override
    public int getCount() {
        return list_Title.size();
    }

    //此方法用来显示tab上的名字
    @Override
    public CharSequence getPageTitle(int position) {
        return list_Title.get(position % list_Title.size());
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
