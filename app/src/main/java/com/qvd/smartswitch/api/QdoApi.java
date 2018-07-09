package com.qvd.smartswitch.api;

import com.qvd.smartswitch.model.login.MessageVo;

import io.reactivex.Observable;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2018/4/13 0013.
 */

public interface QdoApi {

    @POST("user/isSameUsername")
    Observable<MessageVo> IsSameUserName(@Query("username") String username);

}
