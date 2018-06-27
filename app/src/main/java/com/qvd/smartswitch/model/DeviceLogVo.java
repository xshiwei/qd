package com.qvd.smartswitch.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Transient;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2018/4/2.
 */
@Entity
public class DeviceLogVo {
    @Id(autoincrement = true)
    private Long id;
    @NotNull
    private String deviceId;
    @NotNull
    private String deviceName;

    @NotNull
    private String deviceNickname;

    @NotNull
    private String date;
    /**
     * 代表开关状态 0代表关，1代表开
     */
    @NotNull
    private int state;
    /**
     * 代表连接状态 1代表蓝牙 2代表wifi
     */
    @NotNull
    private int type;
    @Transient
    public boolean isSelete;

    @Generated(hash = 931381488)
    public DeviceLogVo(Long id, @NotNull String deviceId,
                       @NotNull String deviceName, @NotNull String deviceNickname,
                       @NotNull String date, int state, int type) {
        this.id = id;
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.deviceNickname = deviceNickname;
        this.date = date;
        this.state = state;
        this.type = type;
    }

    @Generated(hash = 57705920)
    public DeviceLogVo() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeviceId() {
        return this.deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return this.deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceNickname() {
        return this.deviceNickname;
    }

    public void setDeviceNickname(String deviceNickname) {
        this.deviceNickname = deviceNickname;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getState() {
        return this.state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public boolean isSelete() {
        return isSelete;
    }

    public void setSelete(boolean selete) {
        isSelete = selete;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }


}
