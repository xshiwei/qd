package com.qvd.smartswitch.model.device;

import java.io.Serializable;

public class ScanResultVo implements Serializable {
    private String deviceNo;
    private String deviceName;
    private String deviceMac;
    /**
     * 设备连接类型 1表示蓝牙 2表示wifi
     */
    private int connectType;

    public ScanResultVo(String deviceNo, String deviceName, String deviceMac, int connectType) {
        this.deviceNo = deviceNo;
        this.deviceName = deviceName;
        this.deviceMac = deviceMac;
        this.connectType = connectType;
    }

    public String getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        this.deviceNo = deviceNo;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceMac() {
        return deviceMac;
    }

    public void setDeviceMac(String deviceMac) {
        this.deviceMac = deviceMac;
    }

    public int getConnectType() {
        return connectType;
    }

    public void setConnectType(int connectType) {
        this.connectType = connectType;
    }
}
