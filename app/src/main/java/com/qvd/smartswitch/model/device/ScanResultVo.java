package com.qvd.smartswitch.model.device;

import java.io.Serializable;

public class ScanResultVo implements Serializable {
    //设备型号
    private String deviceNo;
    //设备名字
    private String deviceName;
    //设备mac地址
    private String deviceMac;
    /**
     * 设备连接类型 1表示蓝牙 2表示wifi
     */
    private int connectType;
    private int isFirstConnect;
    private String deviceId;

    public ScanResultVo(String deviceNo, String deviceName, String deviceMac, int connectType, int isFirstConnect, String deviceId) {
        this.deviceNo = deviceNo;
        this.deviceName = deviceName;
        this.deviceMac = deviceMac;
        this.connectType = connectType;
        this.isFirstConnect = isFirstConnect;
        this.deviceId = deviceId;
    }

    public int getIsFirstConnect() {
        return isFirstConnect;
    }

    public void setIsFirstConnect(int isFirstConnect) {
        this.isFirstConnect = isFirstConnect;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
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
