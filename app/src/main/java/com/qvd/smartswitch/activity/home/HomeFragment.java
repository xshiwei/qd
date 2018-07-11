package com.qvd.smartswitch.activity.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLive;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearchQuery;
import com.orhanobut.logger.Logger;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseFragment;
import com.qvd.smartswitch.adapter.HomeTypeAdapter;
import com.qvd.smartswitch.model.Type;
import com.qvd.smartswitch.utils.CommonUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;



/**
 * Created by Administrator on 2018/4/2.
 */

public class HomeFragment extends BaseFragment implements WeatherSearch.OnWeatherSearchListener {


    @BindView(R.id.iv_home_head)
    ImageView ivHomeHead;
    @BindView(R.id.tv_home_city)
    TextView tvHomeCity;
    @BindView(R.id.tv_home_temperature)
    TextView tvHomeTemperature;
    @BindView(R.id.tv_home_air_quality)
    TextView tvHomeAirQuality;
    @BindView(R.id.app_bar)
    FrameLayout appBar;
    @BindView(R.id.home_recycleview)
    RecyclerView homeRecycleview;


    //声明WeatherSearchQuery对象
    private WeatherSearchQuery weatherSearchQuery = null;
    //声明WeatherSearch对象
    private WeatherSearch mweathersearch = null;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;

    private HomeTypeAdapter adapter;
    private List<Type> list = new ArrayList<>();

    public static HomeFragment newInstance(String param1) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString("agrs1", param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_home;
    }

//    @Override
//    protected void initImmersionBar() {
//        super.initImmersionBar();
//        mImmersionBar.init();
//    }


    @Override
    protected void initData() {
        super.initData();
        Picasso.with(getActivity()).load(R.mipmap.home_headerimage_one).into(ivHomeHead);
//        AnimationDrawable ani = (AnimationDrawable) ivHomeHead.getBackground();
//        ani.start();
        //初始化定位
        mLocationClient = new AMapLocationClient(getActivity().getApplicationContext());
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        /**
         * 设置定位场景，目前支持三种场景（签到、出行、运动，默认无场景）
         */
        mLocationOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.Sport);
        //设置定位模式为低功耗
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
        mLocationOption.setInterval(60000);
        //启动定位
        if (null != mLocationClient) {
            mLocationClient.setLocationOption(mLocationOption);
            //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
            mLocationClient.stopLocation();
            mLocationClient.startLocation();
        }
        mLocationClient.setLocationListener(locationListener);
        for (int i = 0; i < 3; i++) {
            list.add(new Type(R.mipmap.home_device_one, "电灯"));
            list.add(new Type(R.mipmap.home_device_one, "电灯"));
            list.add(new Type(R.mipmap.home_device_one, "电灯"));
            list.add(new Type(R.mipmap.home_device_one, "电灯"));
        }
        adapter = new HomeTypeAdapter(getActivity(), list);
        homeRecycleview.setNestedScrollingEnabled(false);
        homeRecycleview.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        homeRecycleview.setLayoutManager(gridLayoutManager);
        homeRecycleview.setAdapter(adapter);
        adapter.setOnItemClickListener(new HomeTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                startActivity(new Intent(getActivity(), DeviceControlActivity.class));
                //ToastUtil.showToast("功能开发中...");
            }

            @Override
            public void onItemLongClickListener(View view, int position) {

            }
        });
    }


    /**
     * 定位监听
     */
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    //可在其中解析amapLocation获取相应内容。
                    String city = aMapLocation.getCity();//城市信息
                    if (CommonUtils.isEmptyString(city)) {
                        tvHomeCity.setText(R.string.home_unknowe_city);
                    } else {
                        tvHomeCity.setText(city);
                        weatherSearchQuery = new WeatherSearchQuery(city, WeatherSearchQuery.WEATHER_TYPE_LIVE);
                        mweathersearch = new WeatherSearch(getActivity());
                        mweathersearch.setOnWeatherSearchListener(HomeFragment.this);
                        mweathersearch.setQuery(weatherSearchQuery);
                        mweathersearch.searchWeatherAsyn(); //异步搜索
                    }
                } else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Logger.e("AmapError", "location Error, ErrCode:"
                            + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo());
                }
            }
        }
    };

    @Override
    protected void initView() {
        super.initView();
    }

    @Override
    protected void getData() {
        super.getData();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyLocation();
    }

    /**
     * 销毁定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void destroyLocation() {
        if (null != mLocationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            mLocationClient.onDestroy();
            mLocationClient = null;
            mLocationClient = null;
        }
    }

    @Override
    public void onWeatherLiveSearched(LocalWeatherLiveResult weatherLiveResult, int rCode) {
        //  获得天气查询结果
        if (rCode == 1000) {
            if (weatherLiveResult != null && weatherLiveResult.getLiveResult() != null) {
                LocalWeatherLive liveResult = weatherLiveResult.getLiveResult();
                if (CommonUtils.isEmptyString(liveResult.getTemperature())) {
                    tvHomeTemperature.setText("NULL");
                } else {
                    tvHomeTemperature.setText(liveResult.getTemperature());
                }
                if (CommonUtils.isEmptyString(liveResult.getWeather())) {
                    tvHomeAirQuality.setText("NULL");
                } else {
                    tvHomeAirQuality.setText(liveResult.getHumidity());
                }
            } else {
                Logger.w("无结果");
            }
        } else {
            Logger.w("获取天气错误");
        }
    }

    @Override
    public void onWeatherForecastSearched(LocalWeatherForecastResult localWeatherForecastResult, int i) {

    }
}
