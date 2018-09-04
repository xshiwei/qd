package com.qvd.smartswitch.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.activity.home.HomeFragmentTest;
import com.qvd.smartswitch.utils.NetUtils;

/**
 * 自定义检查手机网络状态是否切换的广播接受器
 *
 * @author cj
 */
public class NetFragmentBroadcastReceiver extends BroadcastReceiver {
    public NetEvevt evevt = HomeFragmentTest.evevt;

    @Override
    public void onReceive(Context context, Intent intent) {
        // 如果相等的话就说明网络状态发生了变化
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            int netWorkState = NetUtils.getNetWorkState(context);
            // 接口回调传过去状态的类型
            if (evevt != null) {
                evevt.onNetChange(netWorkState);
            }
        }
    }

    // 自定义接口
    public interface NetEvevt {
        void onNetChange(int netMobile);
    }

}
