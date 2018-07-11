package com.qvd.smartswitch.api;

import com.qvd.smartswitch.model.login.MessageVo;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

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
     * @param username
     * @param password
     * @return
     */
    @GET("user/login")
    Observable<MessageVo> login(@Query("username") String username, @Query("password") String password);
}
