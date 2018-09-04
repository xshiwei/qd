package com.qvd.smartswitch.api;

import com.qvd.smartswitch.model.device.RoomDeviceListVo;
import com.qvd.smartswitch.model.home.HomeLeftListVo;
import com.qvd.smartswitch.model.home.HomeListVo;
import com.qvd.smartswitch.model.home.RoomListVo;
import com.qvd.smartswitch.model.user.UserInfoVo;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.EvictDynamicKey;
import io.rx_cache2.LifeCache;


/**
 * Created by xushiwei on 2018/8/19.
 */

public interface CacheProviders {

    //如果为true,就会重新通过Retrofit获取新的数据,如果为false就会使用这个缓存

    /**
     * 获取家庭列表的列表
     *
     * @param listVoObservable
     * @param dynamicKey
     * @param evictDynamicKey
     * @return
     */
    Observable<HomeListVo> getHomeMenuList(Observable<HomeListVo> listVoObservable, DynamicKey dynamicKey, EvictDynamicKey evictDynamicKey);

    /**
     * 获取首页左侧房间列表
     *
     * @param leftListVoObservable
     * @param dynamicKey
     * @param evictDynamicKey
     * @return
     */
    Observable<HomeLeftListVo> getHomeLeftList(Observable<HomeLeftListVo> leftListVoObservable, DynamicKey dynamicKey, EvictDynamicKey evictDynamicKey);

    /**
     * 获取首页房间设备信息
     *
     * @param roomDeviceListVoObservable
     * @param dynamicKey
     * @param evictDynamicKey
     * @return
     */
    Observable<RoomDeviceListVo> getRoomDeviceList(Observable<RoomDeviceListVo> roomDeviceListVoObservable, DynamicKey dynamicKey, EvictDynamicKey evictDynamicKey);

    /**
     * 获取房间常用设备
     *
     * @param roomDeviceListVoObservable
     * @param dynamicKey
     * @param evictDynamicKey
     * @return
     */
    Observable<RoomDeviceListVo> getCommonDeviceList(Observable<RoomDeviceListVo> roomDeviceListVoObservable, DynamicKey dynamicKey, EvictDynamicKey evictDynamicKey);

    /**
     * 获取家庭列表
     *
     * @param listVoObservable
     * @param dynamicKey
     * @param evictDynamicKey
     * @return
     */
    Observable<HomeListVo> getHomeManger(Observable<HomeListVo> listVoObservable, DynamicKey dynamicKey, EvictDynamicKey evictDynamicKey);

    /**
     * 获取房间列表
     *
     * @param listVoObservable
     * @param dynamicKey
     * @param evictDynamicKey
     * @return
     */
    Observable<RoomListVo> getRoomManger(Observable<RoomListVo> listVoObservable, DynamicKey dynamicKey, EvictDynamicKey evictDynamicKey);

    /**
     * 获取用户信息
     *
     * @param userInfoVoObservable
     * @param dynamicKey
     * @param evictDynamicKey
     * @return
     */
    @LifeCache(duration = 1, timeUnit = TimeUnit.HOURS)
    Observable<UserInfoVo> getUserInfo(Observable<UserInfoVo> userInfoVoObservable, DynamicKey dynamicKey, EvictDynamicKey evictDynamicKey);
}
