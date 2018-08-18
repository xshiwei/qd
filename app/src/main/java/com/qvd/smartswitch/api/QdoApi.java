package com.qvd.smartswitch.api;

import com.qvd.smartswitch.model.device.AddDeviceListVo;
import com.qvd.smartswitch.model.device.AddQS02Vo;
import com.qvd.smartswitch.model.device.IsWifiNetWorkVo;
import com.qvd.smartswitch.model.device.RoomDeviceListVo;
import com.qvd.smartswitch.model.home.DefaultRoomVo;
import com.qvd.smartswitch.model.home.DeviceListVo;
import com.qvd.smartswitch.model.home.HomeDetailsVo;
import com.qvd.smartswitch.model.home.HomeLeftListVo;
import com.qvd.smartswitch.model.home.HomeListVo;
import com.qvd.smartswitch.model.home.RoomListVo;
import com.qvd.smartswitch.model.home.RoomPicListVo;
import com.qvd.smartswitch.model.login.LoginVo;
import com.qvd.smartswitch.model.login.MessageVo;
import com.qvd.smartswitch.model.login.RegisterVo;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by Administrator on 2018/4/13 0013.
 */

public interface QdoApi {

    /**
     * 判断是否有相同的用户名和账号
     *
     * @param username
     * @return
     */
    @GET("user/isSameUsername")
    Observable<MessageVo> isSameUserName(@Query("username") String username);

    /**
     * 发送邮箱或者手机号验证信息
     *
     * @return
     */
    @POST("user/UsernameValidation")
    Observable<MessageVo> isUsernameValidation(@QueryMap Map<String, Object> map);


    /**
     * 匹配账号和验证码
     *
     * @param username
     * @param verification_code
     * @return
     */
    @GET("match/verification_code")
    Observable<MessageVo> isVerificationCode(@Query("username") String username, @Query("verification_code") String verification_code);

    /**
     * 用户注册
     *
     * @param username
     * @param password
     * @param password2
     * @param identity_type
     * @return
     */
    @POST("user/register")
    Observable<RegisterVo> register(@Query("username") String username, @Query("password") String password, @Query("password2") String password2, @Query("identity_type") String identity_type);

    /**
     * 用户登录
     *
     * @param username
     * @param password
     * @return
     */
    @GET("user/login")
    Observable<LoginVo> login(@Query("username") String username, @Query("password") String password);

    /**
     * 添加新的家庭
     *
     * @param family_name
     * @param family_location
     * @param user_id
     * @param family_background
     * @return
     */
    @POST("add/family")
    Observable<MessageVo> addFamily(@Query("family_name") String family_name, @Query("family_location") String family_location,
                                    @Query("user_id") String user_id, @Query("family_background") String family_background);

    /**
     * 更新家庭
     *
     * @param family_name
     * @param family_location
     * @param family_id
     * @param family_background
     * @return
     */
    @POST("update/family")
    Observable<MessageVo> updateFamily(@Query("family_name") String family_name, @Query("family_location") String family_location,
                                       @Query("family_id") String family_id, @Query("family_background") String family_background);

    /**
     * 删除家庭
     *
     * @param family_id
     * @return
     */
    @POST("delete/family")
    Observable<MessageVo> deleteFamily(@Query("family_id") String family_id, @Query("user_id") String user_id);

    /**
     * 获取单个家庭信息
     *
     * @param family_id
     * @return
     */
    @GET("get/family")
    Observable<HomeDetailsVo> getFamily(@Query("family_id") String family_id);


    /**
     * 切换家庭
     *
     * @param family_id
     * @param user_id
     * @return
     */
    @POST("switch/family")
    Observable<MessageVo> switchFamily(@Query("family_id") String family_id, @Query("user_id") String user_id);

    /**
     * 获取家庭列表
     *
     * @param user_id
     * @return
     */
    @GET("get/familyNameList")
    Observable<HomeListVo> getFamilyList(@Query("user_id") String user_id);

    /**
     * 添加房间
     *
     * @param room_name
     * @param room_pic
     * @param family_id
     * @return
     */
    @POST("add/room")
    Observable<MessageVo> addRoom(@Query("room_name") String room_name, @Query("room_pic") String room_pic, @Query("family_id") String family_id);

    /**
     * 更新房间
     *
     * @param map
     * @return
     */
    @POST("update/room")
    Observable<MessageVo> updateRoom(@QueryMap Map<String, String> map);

    /**
     * 删除房间
     *
     * @param room_id
     * @return
     */
    @POST("delete/room")
    Observable<MessageVo> deleteRoom(@Query("room_id") String room_id);

    /**
     * 获取单个房间的信息
     *
     * @param room_id
     * @return
     */
    @GET("get/room")
    Observable<MessageVo> getRoom(@Query("room_id") String room_id);

    /**
     * 获取每个家庭所建立的房间列表
     *
     * @param family_id
     * @return
     */
    @GET("get/roomList")
    Observable<RoomListVo> getRoomList(@Query("family_id") String family_id);

    /**
     * 添加设备
     *
     * @param map
     * @return
     */
    @POST("add/device")
    Observable<MessageVo> addDevice(@QueryMap Map<String, Object> map);

    /**
     * 修改设备
     *
     * @param map
     * @return
     */
    @POST("update/device")
    Observable<MessageVo> updateDevice(@QueryMap Map<String, String> map);

    /**
     * 删除设备
     *
     * @param device_id
     * @return
     */
    @POST("delete/device")
    Observable<MessageVo> deleteDevice(@Query("device_id") String device_id);

    /**
     * 获取单个设备信息
     *
     * @param device_id
     * @return
     */
    @GET("get/device")
    Observable<MessageVo> getDevice(@Query("device_id") String device_id);

    /**
     * 获取用户所拥有的所有设备列表
     *
     * @param map
     * @return
     */
    @GET("get/deviceList")
    Observable<DeviceListVo> getDeviceList(@QueryMap Map<String, String> map);

    /**
     * 获取房间的图标列表
     *
     * @return
     */
    @GET("get/system_room_pic_list")
    Observable<RoomPicListVo> getRoomPicList();

    /**
     * 获取系统设备列表
     *
     * @return
     */
    @GET("get/qevdo_device_type_list")
    Observable<AddDeviceListVo> getAddDeviceList();

    /**
     * 添加QS02设备
     *
     * @param map
     * @return
     */
    @POST("add/device_qs02")
    Observable<AddQS02Vo> addDeviceQS02(@QueryMap Map<String, String> map);

    /**
     * 更新设备名称
     *
     * @param device_id
     * @param device_name
     * @return
     */
    @POST("update/specific_device_name")
    Observable<MessageVo> updateSpecificDeviceName(@Query("device_id") String device_id, @Query("device_name") String device_name, @Query("table_type") String table_type);

    /**
     * 设置和取消常用
     *
     * @param device_id
     * @return
     */
    @POST("set/common_device")
    Observable<MessageVo> setCommonDevice(@Query("device_id") String device_id, @Query("is_common") int is_common);


    /**
     * 更新设备房间
     *
     * @param device_id
     * @param room_id
     * @return
     */
    @POST("update/device_room")
    Observable<MessageVo> updateDeviceRoom(@Query("device_id") String device_id, @Query("room_id") String room_id);

    /**
     * 获取首页左侧列表
     *
     * @param family_id
     * @param user_id
     * @return
     */
    @GET("get/home_room_list")
    Observable<HomeLeftListVo> getHomeLeftList(@Query("family_id") String family_id, @Query("user_id") String user_id);

    /**
     * 获取默认房间id
     *
     * @param family_id
     * @return
     */
    @GET("get/default_room_id")
    Observable<DefaultRoomVo> getDefaultRoomId(@Query("family_id") String family_id);


    /**
     * 在wifi配对之前先提交信息
     *
     * @param user_id
     * @param room_id
     * @param family_id
     * @param device_mac
     * @return
     */
    @POST("add/temp_id_data")
    Observable<MessageVo> addTempIdData(@Query("user_id") String user_id, @Query("room_id") String room_id, @Query("family_id") String family_id, @Query("device_mac") String device_mac);

    /**
     * 判断wifi设备有无配网成功
     *
     * @param device_mac
     * @param table_type
     * @return
     */
    @GET("match/wifi_device_is_networking")
    Observable<IsWifiNetWorkVo> getIsWifiNetWorking(@Query("device_mac") String device_mac, @Query("table_type") String table_type);

    /**
     * 取消设备配网
     *
     * @param device_mac
     * @return
     */
    @POST("delete/temp_id_data")
    Observable<MessageVo> cancelWifiNetwork(@Query("device_mac") String device_mac);


    /**
     * 获取常用设备列表
     *
     * @param user_id
     * @param family_id
     * @return
     */
    @GET("get/common_device_list")
    Observable<RoomDeviceListVo> getCommonDeviceList(@Query("user_id") String user_id, @Query("family_id") String family_id);


    /**
     * 获取房间设备列表
     *
     * @param user_id
     * @param room_id
     * @return
     */
    @GET("get/devices_in_room")
    Observable<RoomDeviceListVo> getRoomDeviceList(@Query("user_id") String user_id, @Query("room_id") String room_id);

    /**
     * 判断设备是否被绑定
     *
     * @param user_id
     * @param device_mac
     * @return
     */
    @GET("get/device_is_binding")
    Observable<MessageVo> isDeviceBinding(@Query("user_id") String user_id, @Query("device_mac") String device_mac);

}
