package com.qvd.smartswitch.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;


/**
 * Created by Administrator on 2018/4/12 0012.
 */
@Entity
public class DeviceNickNameVo {
    @Id(autoincrement = true)
    private Long id;
    @NotNull
    private String deviceId;
    @NotNull
    private String deviceName;
    private String date;
    private String deviceNickname;
    private String pic;
    private String type;
    @Generated(hash = 1232034693)
    public DeviceNickNameVo(Long id, @NotNull String deviceId,
            @NotNull String deviceName, String date, String deviceNickname,
            String pic, String type) {
        this.id = id;
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.date = date;
        this.deviceNickname = deviceNickname;
        this.pic = pic;
        this.type = type;
    }
    @Generated(hash = 871255086)
    public DeviceNickNameVo() {
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
    public String getDate() {
        return this.date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getDeviceNickname() {
        return this.deviceNickname;
    }
    public void setDeviceNickname(String deviceNickname) {
        this.deviceNickname = deviceNickname;
    }
    public String getPic() {
        return this.pic;
    }
    public void setPic(String pic) {
        this.pic = pic;
    }
    public String getType() {
        return this.type;
    }
    public void setType(String type) {
        this.type = type;
    }

   


}
