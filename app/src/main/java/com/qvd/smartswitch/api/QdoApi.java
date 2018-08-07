package com.qvd.smartswitch.api;

import com.qvd.smartswitch.model.device.AddDeviceListVo;
import com.qvd.smartswitch.model.home.DeviceListVo;
import com.qvd.smartswitch.model.home.HomeDetailsVo;
import com.qvd.smartswitch.model.home.HomeListVo;
import com.qvd.smartswitch.model.home.RoomListVo;
import com.qvd.smartswitch.model.home.RoomPicListVo;
import com.qvd.smartswitch.model.login.LoginVo;
import com.qvd.smartswitch.model.login.MessageVo;

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
     * 验证邮箱是否发送验证信息
     *
     * @param username
     * @param identity_type
     * @return
     */
    @POST("user/UsernameValidation")
    Observable<MessageVo> isUsernameValidation(@Query("username") String username, @Query("identity_type") String identity_type);

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
    Observable<MessageVo> register(@Query("username") String username, @Query("password") String password, @Query("password2") String password2, @Query("identity_type") String identity_type);

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
    Observable<MessageVo> addDevice(@QueryMap Map<String, String> map);

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

}
