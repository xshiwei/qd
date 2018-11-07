package com.qvd.smartswitch.utils;

import android.content.Context;
import android.location.LocationManager;
import android.text.TextUtils;

import com.clj.fastble.BleManager;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.qvd.smartswitch.MyApplication;

import java.util.UUID;

public class BleManageUtils {

    private static class BleManageUtilsHolder {
        private static final BleManageUtils INSTANCE = new BleManageUtils();
    }

    private BleManageUtils() {
    }

    public static BleManageUtils getInstance() {
        return BleManageUtilsHolder.INSTANCE;
    }

    /**
     * 初始化BleManager
     */
    public void initBleManage() {
        BleManager.getInstance()
                .enableLog(true)
//                .setReConnectCount(2, 2000)//设置重连次数以及相隔时间
                .setSplitWriteNum(20)//设置分包发送的默认字节数
                .setMaxConnectCount(7)//设置最大连接数
                .setOperateTimeout(5000)//设置操作超时时间
                .setConnectOverTime(20000);//设置连接超时时间
    }

    /**
     * 设置扫描规则
     */
    public void setScanRule(String str_name) {
        String[] uuids;
        String str_uuid = "";
        if (TextUtils.isEmpty(str_uuid)) {
            uuids = null;
        } else {
            uuids = str_uuid.split(",");
        }
        UUID[] serviceUuids = null;
        if (uuids != null && uuids.length > 0) {
            serviceUuids = new UUID[uuids.length];
            for (int i = 0; i < uuids.length; i++) {
                String name = uuids[i];
                String[] components = name.split("-");
                if (components.length != 5) {
                    serviceUuids[i] = null;
                } else {
                    serviceUuids[i] = UUID.fromString(uuids[i]);
                }
            }
        }

        String[] names;
        if (TextUtils.isEmpty(str_name)) {
            names = null;
        } else {
            names = str_name.split(",");
        }
        String mac = "";

        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
                .setServiceUuids(serviceUuids)      // 只扫描指定的服务的设备，可选
                .setDeviceName(true, names)   // 只扫描指定广播名的设备，可选
                .setDeviceMac(mac)                  // 只扫描指定mac的设备，可选
                .setAutoConnect(true)      // 连接时的autoConnect参数，可选，默认false
                .setScanTimeOut(8000)              // 扫描超时时间，可选，默认10秒
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);
    }

    /**
     * 检查gps定位是否打开
     *
     * @return
     */
    public boolean checkGPSIsOpen() {
        LocationManager locationManager = (LocationManager) MyApplication.getContext().getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }


    /**
     * 销毁BleManage
     */
    public void DestroyBleManage() {
        BleManager.getInstance().disconnectAllDevice();
        BleManager.getInstance().destroy();
    }

}
