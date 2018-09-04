package com.qvd.smartswitch.activity.home;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
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
import com.melnykov.fab.FloatingActionButton;
import com.orhanobut.logger.Logger;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseFragment;
import com.qvd.smartswitch.activity.device.DeviceAddActivity;
import com.qvd.smartswitch.activity.login.LoginActivity;
import com.qvd.smartswitch.activity.qsThree.QsThreeControlActivity;
import com.qvd.smartswitch.adapter.HomeMenuAdapter;
import com.qvd.smartswitch.adapter.HomeTabLayoutAdapter;
import com.qvd.smartswitch.adapter.TabLayoutAdapter;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.home.DefaultRoomVo;
import com.qvd.smartswitch.model.home.HomeLeftListVo;
import com.qvd.smartswitch.model.home.HomeListVo;
import com.qvd.smartswitch.model.login.MessageVo;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.ConfigUtils;
import com.qvd.smartswitch.utils.PermissionUtils;
import com.qvd.smartswitch.utils.ToastUtil;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yanzhenjie.permission.Permission;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/4/2.
 */

public class HomeFragment extends BaseFragment implements AppBarLayout.OnOffsetChangedListener, WeatherSearch.OnWeatherSearchListener {


    @BindView(R.id.materialup_profile_backdrop)
    ImageView ivBackground;
    @BindView(R.id.main_collapsing)
    CollapsingToolbarLayout mainCollapsing;
    @BindView(R.id.materialup_title_container)
    LinearLayout materialupTitleContainer;
    @BindView(R.id.materialup_tabs)
    TabLayout tabLayout;
    @BindView(R.id.materialup_appbar)
    AppBarLayout appbarLayout;
    @BindView(R.id.materialup_viewpager)
    ViewPager viewPager;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.tv_family)
    TextView tvFamily;
    @BindView(R.id.smart_refresh)
    SmartRefreshLayout smartRefresh;
    @BindView(R.id.rl)
    RelativeLayout rl;
    @BindView(R.id.tv_add_device)
    TextView tvAddDevice;
    @BindView(R.id.rl_login)
    RelativeLayout rlLogin;
    @BindView(R.id.tv_temperature)
    TextView tvTemperature;
    @BindView(R.id.tv_weather)
    TextView tvWeather;
    @BindView(R.id.tv_city)
    TextView tvCity;
    @BindView(R.id.tv_day)
    TextView tvDay;
    @BindView(R.id.tv_humidity)
    TextView tvHumidity;
    @BindView(R.id.tv_wind)
    TextView tvWind;


    private static final int PERCENTAGE_TO_ANIMATE_AVATAR = 20;
    private boolean mIsAvatarShown = true;
    private int mMaxScrollSize;

    //声明WeatherSearchQuery对象
    private WeatherSearchQuery weatherSearchQuery = null;
    //声明WeatherSearch对象
    private WeatherSearch mweathersearch = null;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;

    /**
     * tabLayout适配器
     */
    private HomeTabLayoutAdapter adapter;

    /**
     * 装载的fragment List
     */
    private List<HomeDeviceFragment> fragmentList = new ArrayList<>();

    /**
     * tabLayout 标题list
     */
    private List<String> titleList = new ArrayList<>();

    /**
     * 列表数据源
     */
    private List<HomeLeftListVo.DataBean> list = new ArrayList<>();

    /**
     * 点击设置后显示弹窗
     */
    private PopupWindow popupWindow;
    /**
     * menu家庭列表数据
     */
    private List<HomeListVo.DataBean> menuList = new ArrayList<>();
    /**
     * menu数据适配器
     */
    private HomeMenuAdapter menuAdapter;

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

    @Override
    protected void initData() {
        super.initData();
        appbarLayout.addOnOffsetChangedListener(this);
        mMaxScrollSize = appbarLayout.getTotalScrollRange();
        //设置刷新控件头部高度
        smartRefresh.setHeaderHeight(100);
        smartRefresh.setFooterHeight(1);
        smartRefresh.setEnableHeaderTranslationContent(false);//是否上拉Footer的时候向上平移列表或者内容
        //设置头部样式
        smartRefresh.setRefreshHeader(new MaterialHeader(getActivity()));
        smartRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                initEvent();
                smartRefresh.finishRefresh(2000, true);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (CommonUtils.isEmptyString(ConfigUtils.user_id)) {
            //展示需要登录的界面
            tvFamily.setText("立即登录");
            tabLayout.setVisibility(View.GONE);
            viewPager.setVisibility(View.GONE);
            rlLogin.setVisibility(View.VISIBLE);
        } else {
            initEvent();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (CommonUtils.isEmptyString(ConfigUtils.user_id)) {
                //展示需要登录的界面
                tvFamily.setText("立即登录");
                tabLayout.setVisibility(View.GONE);
                viewPager.setVisibility(View.GONE);
                rlLogin.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 初始化加载界面
     */
    private void initEvent() {
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
        tvDay.setText("日期: " + CommonUtils.getWeek());
        //获取首页数据
        getHomeMenuList();
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
                        tvCity.setText("位置: NULL");
                    } else {
                        tvCity.setText("位置: " + city);
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
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyLocation();
    }

    /**
     * 获取首页数据
     */
    private void getHomeMenuList() {
        RetrofitService.qdoApi.getFamilyList(ConfigUtils.user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<HomeListVo>() {
                    @Override
                    public void accept(HomeListVo homeListVo) throws Exception {
                        if (homeListVo != null) {
                            menuList.clear();
                            if (homeListVo.getCode() == 200) {
                                for (HomeListVo.DataBean dataBean : homeListVo.getData()) {
                                    if (dataBean.getIs_opened() == 1) {
                                        tvFamily.setText(dataBean.getFamily_name());
                                        ConfigUtils.family_locate = dataBean;
                                    }
                                    menuList.add(dataBean);
                                }
                            } else if (homeListVo.getCode() == 400) {
                                tvFamily.setText("网络错误");
                            }
                        }
                    }
                })
                .observeOn(Schedulers.io())
                .filter(new Predicate<HomeListVo>() {
                    @Override
                    public boolean test(HomeListVo homeListVo) throws Exception {
                        return homeListVo.getCode() == 200;
                    }
                })
                .observeOn(Schedulers.io())
                .concatMap(new Function<HomeListVo, ObservableSource<HomeLeftListVo>>() {
                    @Override
                    public ObservableSource<HomeLeftListVo> apply(HomeListVo homeListVo) throws Exception {
                        return RetrofitService.qdoApi.getHomeRoomList(ConfigUtils.family_locate.getFamily_id(), ConfigUtils.user_id);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<HomeLeftListVo>() {
                    @Override
                    public void accept(HomeLeftListVo homeLeftListVo) throws Exception {
                        if (homeLeftListVo.getData() != null && homeLeftListVo.getCode() == 200) {
                            list.clear();
                            //移除之前的Fragment
                            FragmentManager fm = getChildFragmentManager();
                            if (fragmentList.size() > 0) {
                                FragmentTransaction ft = fm.beginTransaction();
                                for (Fragment f : fragmentList) {
                                    ft.remove(f);
                                }
                                ft.commit();
                                ft = null;
                                fm.executePendingTransactions();
                            }
                            fragmentList.clear();
                            titleList.clear();
                            list.addAll(homeLeftListVo.getData());
                            //设置tabLayout
                            for (HomeLeftListVo.DataBean dataBean : homeLeftListVo.getData()) {
                                HomeDeviceFragment fragment = HomeDeviceFragment.newInstance(dataBean);
                                fragmentList.add(fragment);
                                titleList.add(dataBean.getRoom_name());
                            }
                            viewPager.setOffscreenPageLimit(3);
                            adapter = new HomeTabLayoutAdapter(getChildFragmentManager(), fragmentList, titleList);
                            viewPager.setAdapter(adapter);
                            tabLayout.setupWithViewPager(viewPager);
                        }
                    }
                })
                .observeOn(Schedulers.io())
                .filter(new Predicate<HomeLeftListVo>() {
                    @Override
                    public boolean test(HomeLeftListVo homeLeftListVo) throws Exception {
                        return homeLeftListVo.getCode() == 200;
                    }
                })
                .observeOn(Schedulers.io())
                .concatMap(new Function<HomeLeftListVo, ObservableSource<DefaultRoomVo>>() {
                    @Override
                    public ObservableSource<DefaultRoomVo> apply(HomeLeftListVo homeLeftListVo) throws Exception {
                        return RetrofitService.qdoApi.getDefaultRoomId(ConfigUtils.family_locate.getFamily_id());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DefaultRoomVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(DefaultRoomVo defaultRoomVo) {
                        if (defaultRoomVo.getCode() == 200) {
                            ConfigUtils.defaultRoomId = defaultRoomVo.getRoom_id();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        tvFamily.setText("网络错误");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    /**
     * 给图片设置消失显示动画效果
     *
     * @param appBarLayout
     * @param verticalOffset
     */
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (mMaxScrollSize == 0)
            mMaxScrollSize = appBarLayout.getTotalScrollRange();
        int percentage = (Math.abs(verticalOffset)) * 100 / mMaxScrollSize;
        if (percentage >= PERCENTAGE_TO_ANIMATE_AVATAR && mIsAvatarShown) {
            mIsAvatarShown = false;
            rl.animate()
                    .scaleY(0).scaleX(0)
                    .start();
        }
        if (percentage <= PERCENTAGE_TO_ANIMATE_AVATAR && !mIsAvatarShown) {
            mIsAvatarShown = true;
            rl.animate()
                    .scaleY(1).scaleX(1)
                    .start();
        }
    }

    /**
     * 显示popupwindow
     */
    private void shouPopupwindow() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.popupwindow_home_setting, null, false);
        view.getBackground().setAlpha(80);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, false);
        // 需要设置一下此参数，点击外边可消失
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        // 设置点击窗口外边窗口消失
        popupWindow.setOutsideTouchable(true);
        // 设置此参数获得焦点，否则无法点击
        popupWindow.setFocusable(true);
        RecyclerView recycleView = view.findViewById(R.id.recyclerview);
        RelativeLayout rl_home_manage = view.findViewById(R.id.rl_home_manage);
        recycleView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        menuAdapter = new HomeMenuAdapter(getActivity(), menuList);
        recycleView.setAdapter(menuAdapter);
        menuAdapter.setOnItemClickListener(new HomeMenuAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                popupWindow.dismiss();
                //切换家庭
                replaceFamily(menuList.get(position).getFamily_id());
            }

            @Override
            public void onItemLongClickListener(View view, int position) {

            }
        });
        //家庭管理设置
        rl_home_manage.setOnClickListener(v -> {
            popupWindow.dismiss();
            startActivity(new Intent(getActivity(), HomeManageActivity.class));
            getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        });
    }


    /**
     * 切换家庭
     */
    private void replaceFamily(String family_id) {
        RetrofitService.qdoApi.switchFamily(family_id, ConfigUtils.user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MessageVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MessageVo messageVo) {
                        if (messageVo != null) {
                            if (messageVo.getCode() == 200) {
                                initEvent();
                            } else {
                                ToastUtil.showToast("切换失败");
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @OnClick({R.id.tv_family, R.id.fab, R.id.tv_add_device, R.id.tv_login_two})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_family:
                //用户注册后默认是有一个房间的，不存在为空。
                if (CommonUtils.isEmptyString(ConfigUtils.user_id)) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                } else {
                    shouPopupwindow();
                    popupWindow.showAsDropDown(tvFamily, 30, 0);
                }
                break;
            case R.id.fab:
                startActivity(new Intent(getActivity(), QsThreeControlActivity.class));
                break;
            case R.id.tv_add_device:
                //添加设备
                if (CommonUtils.isEmptyString(ConfigUtils.user_id)) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                } else {
                    startActivity(new Intent(getActivity(), DeviceAddActivity.class));
                    getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
                break;
            case R.id.tv_login_two:
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
        }
    }

    @Override
    public void onWeatherLiveSearched(LocalWeatherLiveResult weatherLiveResult, int i) {
        //  获得天气查询结果
        if (i == 1000) {
            if (weatherLiveResult != null && weatherLiveResult.getLiveResult() != null) {
                LocalWeatherLive liveResult = weatherLiveResult.getLiveResult();
                if (CommonUtils.isEmptyString(liveResult.getTemperature())) {
                    tvTemperature.setText("温度: NULL");
                } else {
                    tvTemperature.setText("温度: " + liveResult.getTemperature() + "°");
                }
                if (CommonUtils.isEmptyString(liveResult.getWeather())) {
                    tvWeather.setText("天气: NULL");
                } else {
                    tvWeather.setText("天气: " + liveResult.getWeather());
                }
                if (CommonUtils.isEmptyString(liveResult.getHumidity())) {
                    tvHumidity.setText("湿度: NULL");
                } else {
                    tvHumidity.setText("湿度: " + liveResult.getHumidity() + "%");
                }
                if (CommonUtils.isEmptyString(liveResult.getWindPower())) {
                    tvWind.setText("风向: NULL");
                } else {
                    tvWind.setText(liveResult.getWindDirection() + "风 " + liveResult.getWindPower() + "级");
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
