package com.qvd.smartswitch.utils;

import java.util.List;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;


public class WifiAdmin {
    // 定义一个WifiManager对象
    private final WifiManager mWifiManager;

    public WifiAdmin(Context context) {
        // 取得WifiManager对象
        mWifiManager = (WifiManager) context.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
    }

    /**
     * Function: 打开wifi功能<br>
     *
     * @author ZYT DateTime 2014-5-14 上午11:01:11<br>
     * @return true:打开成功；false:打开失败<br>
     */
    private boolean openWifi() {
        boolean bRet = true;
        if (!mWifiManager.isWifiEnabled()) {
            bRet = mWifiManager.setWifiEnabled(true);
        }
        return bRet;
    }

    /**
     * Function: 提供一个外部接口，传入要连接的无线网 <br>
     *
     * @author ZYT DateTime 2014-5-13 下午11:46:54<br>
     * @param SSID
     *            SSID
     * @param Password
     * @param Type
     * <br>
     * @return true:连接成功；false:连接失败<br>
     */
    public boolean connect(String SSID, String Password, WifiConnect.WifiCipherType Type) {
        if (!this.openWifi()) {
            return false;
        }
        // 开启wifi功能需要一段时间(我在手机上测试一般需要1-3秒左右)，所以要等到wifi
        // 状态变成WIFI_STATE_ENABLED的时候才能执行下面的语句
        while (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
            try {
                // 为了避免程序一直while循环，让它睡个100毫秒在检测……
                Thread.currentThread();
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
        }

        System.out.println("WifiAdmin#connect==连接结束");

        WifiConfiguration wifiConfig = createWifiInfo(SSID, Password, Type);
        //
        if (wifiConfig == null) {
            return false;
        }

        WifiConfiguration tempConfig = this.isExsits(SSID);

        int tempId = wifiConfig.networkId;
        if (tempConfig != null) {
            mWifiManager.removeNetwork(tempConfig.networkId);
        }

        int netID = mWifiManager.addNetwork(wifiConfig);

        // 断开连接
        mWifiManager.disconnect();
        // 重新连接
        // netID = wifiConfig.networkId;
        // 设置为true,使其他的连接断开
        boolean bRet = mWifiManager.enableNetwork(netID, true);
        mWifiManager.reconnect();
        return bRet;
    }

    // 查看以前是否也配置过这个网络
    private WifiConfiguration isExsits(String SSID) {
        List<WifiConfiguration> existingConfigs = mWifiManager
                .getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }

    private WifiConfiguration createWifiInfo(String SSID, String Password,
                                             WifiConnect.WifiCipherType Type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
        if (Type == WifiConnect.WifiCipherType.WIFICIPHER_NOPASS) {
            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type == WifiConnect.WifiCipherType.WIFICIPHER_WEP) {
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type == WifiConnect.WifiCipherType.WIFICIPHER_WPA) {
            // 修改之后配置
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);

        } else {
            return null;
        }
        return config;
    }
}