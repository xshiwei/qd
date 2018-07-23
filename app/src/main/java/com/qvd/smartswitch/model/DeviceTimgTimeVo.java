package com.qvd.smartswitch.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class DeviceTimgTimeVo {
    @Id(autoincrement = true)
    private Long id;
    @NotNull
    private String deviceId;
    @NotNull
    private String content;
    @Generated(hash = 1881380006)
    public DeviceTimgTimeVo(Long id, @NotNull String deviceId,
            @NotNull String content) {
        this.id = id;
        this.deviceId = deviceId;
        this.content = content;
    }
    @Generated(hash = 2089962877)
    public DeviceTimgTimeVo() {
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
    public String getContent() {
        return this.content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    
}
