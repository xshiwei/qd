package com.qvd.smartswitch.api;

import com.qvd.smartswitch.model.device.AddDeviceListVo;
import com.qvd.smartswitch.model.device.AddQS02Vo;
import com.qvd.smartswitch.model.device.DeviceCommonQuestionVo;
import com.qvd.smartswitch.model.device.DeviceLogVo;
import com.qvd.smartswitch.model.device.DeviceTimingVo;
import com.qvd.smartswitch.model.device.FamilyDetailsVo;
import com.qvd.smartswitch.model.device.HomeShareDeviceListVo;
import com.qvd.smartswitch.model.device.IsWifiNetWorkVo;
import com.qvd.smartswitch.model.device.RoomDeviceListVo;
import com.qvd.smartswitch.model.device.ShareSuccessVo;
import com.qvd.smartswitch.model.home.AddHomeVo;
import com.qvd.smartswitch.model.home.AddRomeVo;
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
import com.qvd.smartswitch.model.user.DeviceShareManageListVo;
import com.qvd.smartswitch.model.user.FamilyListVo;
import com.qvd.smartswitch.model.user.HelpFeedbackListVo;
import com.qvd.smartswitch.model.user.QiNiuPicTokenVo;
import com.qvd.smartswitch.model.user.RecentSharePeopleListVo;
import com.qvd.smartswitch.model.user.ShareObjectInfoVo;
import com.qvd.smartswitch.model.user.UserFeedbackListVo;
import com.qvd.smartswitch.model.user.UserInfoVo;
import com.qvd.smartswitch.model.user.UserReceiverDeviceListVo;
import com.qvd.smartswitch.model.user.UserShareDeviceListVo;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
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
     * 忘记密码时发送邮箱或者手机号验证信息
     *
     * @return
     */
    @POST("user/user_forget_password_validation")
    Observable<MessageVo> userForgetPasswordValidation(@QueryMap Map<String, Object> map);

    /**
     * 用户设置新密码
     *
     * @return
     */
    @POST("user/user_set_new_password")
    Observable<MessageVo> userSetNewPassword(@Query("user_name") String user_name, @Query("password") String password, @Query("repeat_password") String repeat_password);

    /**
     * 获取个人信息
     *
     * @param user_id
     * @return
     */
    @GET("user/get_userInfo_detail")
    Observable<UserInfoVo> getUserInfo(@Query("user_id") String user_id);

    /**
     * 更新用户信息
     *
     * @param user_id
     * @param user_name
     * @param user_phone
     * @return
     */
    @POST("update/userInfo")
    Observable<MessageVo> updateuserInfo(@Query("user_id") String user_id, @Query("user_name") String user_name, @Query("user_phone") String user_phone,@Query("user_avatar")String user_avatar);

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
    Observable<AddHomeVo> addFamily(@Query("family_name") String family_name, @Query("family_location") String family_location,
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
    Observable<AddRomeVo> addRoom(@Query("room_name") String room_name, @Query("room_pic") String room_pic, @Query("family_id") String family_id);

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
     * 获取当前家庭所拥有的所有设备列表
     *
     * @param map
     * @return
     */
    @GET("get/devices_in_family")
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
     * @return
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("update/device_room")
    Observable<MessageVo> updateDeviceRoom(@Body RequestBody body);

    /**
     * 获取首页左侧列表
     *
     * @param family_id
     * @param user_id
     * @return
     */
    @GET("get/home_room_list")
    Observable<HomeLeftListVo> getHomeRoomList(@Query("family_id") String family_id, @Query("user_id") String user_id);

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

    /**
     * 获取设备日志列表
     *
     * @param map
     * @return
     */
    @GET("get/device_log")
    Observable<DeviceLogVo> getDeviceLog(@QueryMap Map<String, Object> map);

    /**
     * 添加设备日志消息
     *
     * @return
     */
    @POST("add/device_log")
    Observable<MessageVo> addDeviceLog(@Query("device_id") String device_id, @Query("device_type") String device_type, @Query("log_content") String log_content);

    /**
     * 修改设备是否第一次进入
     */
    @POST("update/device_first_connect_state")
    Observable<MessageVo> updateFirstConnectState(@Query("device_id") String device_id);

    /**
     * 获取设备常用问题
     *
     * @param device_no
     * @return
     */
    @GET("get/device_FAQ")
    Observable<DeviceCommonQuestionVo> getDeviceCommonQuestionList(@Query("device_no") String device_no);

    /**
     * 添加设备定时信息
     *
     * @return
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("add/device_timing")
    Observable<MessageVo> addDeviceTiming(@Body RequestBody body);

    /**
     * 获取定时列表
     *
     * @param device_id
     * @return
     */
    @GET("get/device_timing_info")
    Observable<DeviceTimingVo> getDeviceTimingInfo(@Query("device_id") String device_id);

    /**
     * 删除设备定时
     *
     * @param timing_id
     * @return
     */
    @POST("delete/device_timing")
    Observable<MessageVo> deleteDeviceTiming(@Query("timing_id") String timing_id);

    /**
     * 修改设备定时的状态
     *
     * @param timing_id
     * @param timing_state
     * @return
     */
    @POST("update/device_timing_state")
    Observable<MessageVo> updateDeviceTimingState(@Query("timing_id") String timing_id, @Query("timing_state") String timing_state);

    /**
     * 修改用户账号密码
     *
     * @param user_id
     * @param current_password
     * @param password
     * @param repeat_password
     * @return
     */
    @POST("user/update_user_password")
    Observable<MessageVo> updateUserPassword(@Query("user_id") String user_id, @Query("current_password") String current_password, @Query("password") String password, @Query("repeat_password") String repeat_password);

    /**
     * 获取系统用户反馈信息
     *
     * @param user_id
     * @return
     */
    @GET("get/user_feedback_category_info")
    Observable<HelpFeedbackListVo> getUserFeedbackCategoryInfo(@Query("user_id") String user_id);

    /**
     * 添加一条反馈信息
     *
     * @param user_id
     * @param contact_way
     * @param feedback_content
     * @param category_type
     * @return
     */
    @POST("add/user_feedback_info")
    Observable<MessageVo> addUserFeedbackInfo(@Query("user_id") String user_id, @Query("contact_way") String contact_way, @Query("feedback_content") String feedback_content, @Query("category_type") String category_type);

    /**
     * 获取单个系统反馈消息
     *
     * @param user_id
     * @return
     */
    @GET("get/user_feedback_info")
    Observable<UserFeedbackListVo> getUserFeedbackInfo(@Query("user_id") String user_id);

    /**
     * 删除一条反馈信息
     *
     * @param body
     * @return
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("delete/user_feedback_info")
    Observable<MessageVo> deleteUserFeedbackInfo(@Body RequestBody body);

    /**
     * 获取当前用户可共享的设备列表
     *
     * @return
     */
    @GET("get/user_share_devices_info")
    Observable<UserShareDeviceListVo> getUserShareDeviceList(@Query("user_id") String user_id);

    /**
     * 获取用户接受的共享设备列表
     *
     * @param user_id
     * @return
     */
    @GET("get/object_user_share_devices_info")
    Observable<UserReceiverDeviceListVo> getObjectUserShareDeviceInfo(@Query("user_id") String user_id);

    /**
     * 接受共享设备
     */
    @POST("accept/share_devices_info")
    Observable<MessageVo> acceptShareDevicesInfo(@Query("device_share_id") String device_share_id);

    /**
     * 删除被共享的消息
     *
     * @return
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("delete/object_user_share_devices_info")
    Observable<MessageVo> deleteObjectUserShareDevicesInfo(@Body RequestBody body);

    /**
     * 设备共享
     *
     * @return
     */
    @POST("add/share_devices_info")
    Observable<ShareSuccessVo> addShareDevicesInfo(@Query("device_id") String device_id, @Query("share_object_userid") String share_object_userid,
                                                   @Query("share_userid") String share_userid, @Query("table_type") String table_type,
                                                   @Query("is_control") int is_control);

    /**
     * 获取最近共享设备人列表
     */
    @GET("get/share_object_user_info")
    Observable<RecentSharePeopleListVo> getShareObjectUserInfo(@Query("user_id") String user_id);

    /**
     * 获取共享对象信息
     *
     * @return
     */
    @GET("get/add_share_object_user_info")
    Observable<ShareObjectInfoVo> getAddShareObjectUserInfo(@Query("user_identifier") String user_identifier);

    /**
     * 获取当前设备共享的对象列表
     *
     * @return
     */
    @GET("get/device_share_user_info")
    Observable<DeviceShareManageListVo> getDeviceShareUserInfo(@Query("user_id") String user_id, @Query("device_id") String device_id);

    /**
     * 取消对共享人的授权
     *
     * @return
     */
    @POST("cancel/devices_share")
    Observable<MessageVo> cancelDevicesShare(@Query("device_share_id") String device_share_id);

    /**
     * 获取分享设备列表
     *
     * @param user_id
     * @return
     */
    @GET("get/share_room_devices")
    Observable<HomeShareDeviceListVo> getShareRoomDevices(@Query("user_id") String user_id);

    /**
     * 获取用户所有的家庭成员
     *
     * @param user_id
     * @return
     */
    @GET("get/all_family_members")
    Observable<FamilyListVo> getAllFamilyMembers(@Query("user_id") String user_id);

    /**
     * 添加家庭关系成员
     *
     * @param user_id
     * @param family_members_userid
     * @param family_members_relation
     * @return
     */
    @POST("add/family_member")
    Observable<MessageVo> addFamilyMember(@Query("user_id") String user_id, @Query("family_members_userid") String family_members_userid, @Query("family_members_relation") String family_members_relation);

    /**
     * 添加分享到我的家人
     *
     * @param device_id
     * @param share_object_userid
     * @param share_userid
     * @param table_type
     * @return
     */
    @POST("share/family_members_of_device")
    Observable<ShareSuccessVo> addShareFamilymembersOfDevice(@Query("device_id") String device_id, @Query("share_object_userid") String share_object_userid,
                                                             @Query("share_userid") String share_userid, @Query("table_type") String table_type);

    /**
     * 同意成为对方家人
     *
     * @param family_members_id
     * @return
     */
    @POST("agree/family_members_relation")
    Observable<MessageVo> agreeFamilyMembersRelation(@Query("family_members_id") String family_members_id);

    /**
     * 解除关系
     *
     * @return
     */
    @POST("relieve/family_members")
    Observable<MessageVo> relieveFamilyMembers(@Query("family_members_id") String family_members_id);

    /**
     * 更新家人关系
     *
     * @return
     */
    @POST("update/family_members")
    Observable<MessageVo> updateFamilyMembers(@Query("family_members_id") String family_members_id, @Query("family_members_relation") String family_members_relation);

    /**
     * 查询家人的详细信息
     *
     * @param share_object_userid
     * @param share_userid
     * @return
     */
    @GET("get/family_members_detailInfo")
    Observable<FamilyDetailsVo> addGetFamilyMembersDetailInfo(@Query("share_object_userid") String share_object_userid, @Query("share_userid") String share_userid);

    /**
     * 获取七牛云上传图片的token
     * @return
     */
    @POST("create/qiniu_upload_token")
    Observable<QiNiuPicTokenVo> getQiNiuToken();
}
