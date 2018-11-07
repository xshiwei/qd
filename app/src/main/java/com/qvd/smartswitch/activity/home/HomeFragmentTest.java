package com.qvd.smartswitch.activity.home;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLive;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearchQuery;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.melnykov.fab.FloatingActionButton;
import com.nightonke.boommenu.BoomButtons.TextOutsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.orhanobut.logger.Logger;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.SplashActivity;
import com.qvd.smartswitch.activity.base.BaseFragment;
import com.qvd.smartswitch.activity.base.BaseHandler;
import com.qvd.smartswitch.activity.base.BaseRunnable;
import com.qvd.smartswitch.activity.device.DeviceAddActivity;
import com.qvd.smartswitch.activity.device.DeviceShareActivity;
import com.qvd.smartswitch.activity.login.LoginActivity;
import com.qvd.smartswitch.activity.qsThree.QsThreeControlActivity;
import com.qvd.smartswitch.activity.qsTwo.QsTwoControlActivity;
import com.qvd.smartswitch.adapter.HomeDeviceListAdapter;
import com.qvd.smartswitch.adapter.HomeListAdapter;
import com.qvd.smartswitch.adapter.HomeMenuAdapter;
import com.qvd.smartswitch.adapter.HomeShareListAdapter;
import com.qvd.smartswitch.api.CacheSetting;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.device.HomeShareDeviceListVo;
import com.qvd.smartswitch.model.device.RoomDeviceListVo;
import com.qvd.smartswitch.model.device.ScanResultVo;
import com.qvd.smartswitch.model.device.UpdateDeviceRoomVo;
import com.qvd.smartswitch.model.home.DefaultRoomVo;
import com.qvd.smartswitch.model.home.HomeLeftListVo;
import com.qvd.smartswitch.model.home.HomeListVo;
import com.qvd.smartswitch.model.home.RoomListVo;
import com.qvd.smartswitch.model.login.MessageVo;
import com.qvd.smartswitch.model.user.DeleteReceiveShareVo;
import com.qvd.smartswitch.receiver.NetFragmentBroadcastReceiver;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.ConfigUtils;
import com.qvd.smartswitch.utils.PermissionUtils;
import com.qvd.smartswitch.utils.SharedPreferencesUtil;
import com.qvd.smartswitch.utils.ToastUtil;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.OnClick;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.EvictDynamicKey;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static com.qvd.smartswitch.utils.CommonUtils.startIntentAnim;
import static com.qvd.smartswitch.utils.CommonUtils.startIntentLogin;

/**
 * Created by Administrator on 2018/6/6 0006.
 */

public class HomeFragmentTest extends BaseFragment implements AppBarLayout.OnOffsetChangedListener, WeatherSearch.OnWeatherSearchListener, NetFragmentBroadcastReceiver.NetEvevt {

    public static NetFragmentBroadcastReceiver.NetEvevt evevt;
    /**
     * 列表适配器
     */
    private HomeListAdapter listAdapter;
    /**
     * 列表数据源
     */
    private List<HomeLeftListVo.DataBean> list = new ArrayList<>();
    /**
     * 房间点击后设备适配器
     */
    private HomeDeviceListAdapter contentAdapter;

    /**
     * 服务器获取的设备数据源
     */
    private List<RoomDeviceListVo.DataBean> contentList = new ArrayList<>();

    private HomeShareListAdapter shareListAdapter;

    /**
     * 共享的list
     */
    private List<HomeShareDeviceListVo.DataBean> shareList = new ArrayList<>();

    /**
     * 点击设置后显示弹窗
     */
    private PopupWindow popupWindow;

    private LinearLayout llAddDevice;

    private SmartRefreshLayout smartRefresh;

    private RelativeLayout rlWeather;

    private TextView tvAddText;
    /**
     * menu家庭列表数据
     */
    private List<HomeListVo.DataBean> menuList = new ArrayList<>();


    /**
     * 代表左侧list选择的区域
     */
    private int mPosition;

    private static final int PERCENTAGE_TO_ANIMATE_AVATAR = 20;
    private boolean mIsAvatarShown = true;
    private int mMaxScrollSize;

    //声明AMapLocationClient类对象
    private AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    private AMapLocationClientOption mLocationOption = null;


    private RoomDeviceListVo.DataBean seleteDevice;

    private NetFragmentBroadcastReceiver broadcastReceiver;

    /**
     * 代表内容区选择的位置
     */
    private int seletePosition;

    /**
     * 判断首页menu数据是否更新
     */
    private boolean isHomeMenu = false;
    /**
     * 判断首页左侧列表数据是否更新
     */
    private boolean isHomeLeftList = false;
    /**
     * 判断首页常用设备是否更新
     */
    private boolean isCommonDevice = false;
    /**
     * 判断首页房间设备是否更新
     */
    private boolean isRoomList = false;
    /**
     * 判断首页分享列表是否更新
     */
    private boolean isShareList = false;

    private RecyclerView rvContent;

    private String userId;
    private AppBarLayout appbarLayout;
    private TextView tvTemperature;
    private TextView tvCity;
    private TextView tvOutdoorAir;
    private TextView tvWaterQuality;
    private TextView tvHumidness;
    private ImageView iv_home_pic;
    private RecyclerView rvList;
    private TextView tvSceneSetting;
    private TextView tvDay;
    private BoomMenuButton bmb;
    private GridLayoutManager gridLayoutManager;
    private LinearLayoutManager linearLayoutManager;
    private LinearLayoutManager linearLayoutManager2;
    private LocalWeatherLive liveResult;


    public static HomeFragmentTest newInstance(String param1) {
        HomeFragmentTest fragment = new HomeFragmentTest();
        Bundle args = new Bundle();
        args.putString("agrs1", param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_home_two;
    }

    private final MyHandle myHandle = new MyHandle(this);

    private static class MyHandle extends BaseHandler<HomeFragmentTest> {

        MyHandle(HomeFragmentTest reference) {
            super(reference);
        }

        @Override
        protected void handleMessage(HomeFragmentTest reference, Message msg) {

        }
    }

    @Override
    protected void initData() {
        super.initData();
        initHomeView();
        Glide.with(getActivity()).load(R.mipmap.home_background).into(iv_home_pic);
        if (!AndPermission.hasPermissions(this, Permission.Group.LOCATION)) {
            PermissionUtils.requestPermission(getActivity(), Permission.Group.LOCATION);
        }
        appbarLayout.addOnOffsetChangedListener(this);
        mMaxScrollSize = appbarLayout.getTotalScrollRange();
        evevt = this;
        contentAdapter = new HomeDeviceListAdapter(contentList);
        contentAdapter.setHasStableIds(true);
        setOnItemClick();
        shareListAdapter = new HomeShareListAdapter(shareList);
        setShareOnItemClick();
        gridLayoutManager = new GridLayoutManager(getActivity(), 2, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        linearLayoutManager2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        //初始化定位
        mLocationClient = new AMapLocationClient(getActivity());
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);
        //设置定位模式为低功耗
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
        mLocationOption.setInterval(200000);
        mLocationOption.setHttpTimeOut(5000);
        //启动定位
        if (null != mLocationClient) {
            mLocationClient.setLocationOption(mLocationOption);
            //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
            mLocationClient.stopLocation();
            mLocationClient.startLocation();
        }
        mLocationClient.setLocationListener(locationListener);

        //设置刷新控件头部高度
        smartRefresh.setHeaderHeight(100);
        smartRefresh.setFooterHeight(1);
        smartRefresh.setEnableHeaderTranslationContent(false);//是否上拉Footer的时候向上平移列表或者内容
        //设置头部样式
        smartRefresh.setRefreshHeader(new MaterialHeader(getActivity()));
        smartRefresh.setOnRefreshListener((RefreshLayout refreshlayout) -> {
            myHandle.postDelayed(new BaseRunnable(() -> {
                if (liveResult == null) {
                    //启动定位
                    if (null != mLocationClient) {
                        mLocationClient.setLocationOption(mLocationOption);
                        //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
                        mLocationClient.stopLocation();
                        mLocationClient.startLocation();
                    }
                    mLocationClient.setLocationListener(locationListener);
                }
                HomeFragmentTest.this.setUpdate();
            }), 1000);
        });
        //注册监听网络状态改变的广播
        broadcastReceiver = new NetFragmentBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        Objects.requireNonNull(getActivity()).registerReceiver(broadcastReceiver, filter);
//        initMenu();
    }

    @Override
    public void onResume() {
        super.onResume();
        userId = SharedPreferencesUtil.getString(Objects.requireNonNull(getActivity()), SharedPreferencesUtil.USER_ID);
        setUpdate();
    }

    private void initHomeView() {
        llAddDevice = Objects.requireNonNull(getActivity()).findViewById(R.id.ll_add_device);
        rvContent = getActivity().findViewById(R.id.rv_content);
        smartRefresh = getActivity().findViewById(R.id.refreshLayout);
        rlWeather = getActivity().findViewById(R.id.rl_weather);
        tvAddText = getActivity().findViewById(R.id.tv_add_text);
        tvTemperature = getActivity().findViewById(R.id.tv_temperature);
        tvCity = getActivity().findViewById(R.id.tv_city);
        tvOutdoorAir = getActivity().findViewById(R.id.tv_outdoor_air);
        tvWaterQuality = getActivity().findViewById(R.id.tv_water_quality);
        tvHumidness = getActivity().findViewById(R.id.tv_humidness);
        rvList = getActivity().findViewById(R.id.rv_list);
        tvSceneSetting = getActivity().findViewById(R.id.tv_scene_setting);
        RelativeLayout rlList = getActivity().findViewById(R.id.rl_list);
        tvDay = getActivity().findViewById(R.id.tv_day);
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        CollapsingToolbarLayout mainCollapsing = getActivity().findViewById(R.id.main_collapsing);
        bmb = getActivity().findViewById(R.id.bmb);
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        appbarLayout = getActivity().findViewById(R.id.materialup_appbar);
        iv_home_pic = getActivity().findViewById(R.id.iv_home_pic);
    }

    /**
     * 在这里显示网络状态改变
     */
    @Override
    public void onNetChange(int netMobile) {
        if (netMobile == -1) {
            if (!isHidden()) {
                isRoomList = false;
                isCommonDevice = false;
                isHomeLeftList = false;
                isHomeMenu = false;
                if (CommonUtils.isEmptyString(userId)) {
                    //展示需要登录的界面
                    tvSceneSetting.setText(R.string.common_immediately_login);
                    llAddDevice.setVisibility(View.VISIBLE);
                    rvContent.setVisibility(View.GONE);
                    tvAddText.setText(R.string.common_immediately_login);
                } else {
                    initEvent();
                }
            }
            ToastUtil.showToast(getString(R.string.common_network_disconnected));
        } else {
            if (CommonUtils.isEmptyString(ConfigUtils.user_id)) {
                AutoLogin();
            }
            if (!isHidden()) {
                myHandle.post(this::setUpdate);
            }
        }
    }

    /**
     * 刷新数据
     */
    private void setUpdate() {
        isRoomList = true;
        isCommonDevice = true;
        isHomeLeftList = true;
        isHomeMenu = true;
        isShareList = true;
        if (CommonUtils.isEmptyString(userId)) {
            //展示需要登录的界面
            list.clear();
            contentList.clear();
            tvSceneSetting.setText(R.string.common_immediately_login);
            llAddDevice.setVisibility(View.VISIBLE);
            rvContent.setVisibility(View.GONE);
            tvAddText.setText(R.string.common_immediately_login);
        } else {
            initEvent();
        }
    }

    /**
     * 初始化菜单
     */
    private void initMenu() {
        TextOutsideCircleButton.Builder builder1 = new TextOutsideCircleButton.Builder()
                .normalImageRes(R.mipmap.device_sound_pic)
                .normalText(getString(R.string.common_voice_control))
                .listener(index -> ToastUtil.showToast(getString(R.string.common_features_being_development)))
                .textSize(12)
                .normalColor(getResources().getColor(R.color.orange_background))
                .pieceColor(Color.WHITE);
        TextOutsideCircleButton.Builder builder2 = new TextOutsideCircleButton.Builder()
                .normalImageRes(R.mipmap.home_add)
                .normalText(getString(R.string.common_add_device))
                .listener(index -> {
                    //添加设备
                    if (CommonUtils.isEmptyString(userId)) {
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        startIntentAnim(getActivity());
                    } else {
                        startActivity(new Intent(getActivity(), DeviceAddActivity.class));
                        startIntentAnim(getActivity());
                    }
                })
                .textSize(12)
                .normalColor(getResources().getColor(R.color.app_red_color))
                .pieceColor(Color.WHITE);
        bmb.addBuilder(builder1);
        bmb.addBuilder(builder2);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (broadcastReceiver != null) {
            Objects.requireNonNull(getActivity()).unregisterReceiver(broadcastReceiver);
        }
    }

    /**
     * 定位监听
     */
    private final AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    //可在其中解析amapLocation获取相应内容。
                    String city = aMapLocation.getCity();//城市信息
                    if (!CommonUtils.isEmptyString(city)) {
                        tvCity.setText("" + city);
                        WeatherSearchQuery weatherSearchQuery = new WeatherSearchQuery(city, WeatherSearchQuery.WEATHER_TYPE_LIVE);
                        WeatherSearch mweathersearch = new WeatherSearch(getActivity());
                        mweathersearch.setOnWeatherSearchListener(HomeFragmentTest.this);
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
     * 设置菜单点击事件
     */
    private void setMenuOnClick() {
        //设置左边菜单点击事件
        listAdapter.setOnItemClickListener((adapter, view, position) -> {
            //根据当前获取的位置对应的类别请求数据并显示数据。type = 1表示常用，type=2表示房间
            switch (list.get(position).getType()) {
                case 1:
                    getCommonDevice();
                    break;
                case 2:
                    getRoomListDevice(list.get(position).getRoom_id());
                    break;
                case 3:
                    getShareList();
                    break;
            }
            listAdapter.setCheckedPosition(position);
            mPosition = position;
        });
    }


    /**
     * 获取房间设备
     */
    private void getRoomListDevice(String roomid) {
        CacheSetting.getCache().getRoomDeviceList(RetrofitService.qdoApi.getRoomDeviceList(userId, roomid)
                , new DynamicKey(roomid), new EvictDynamicKey(isRoomList))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RoomDeviceListVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(RoomDeviceListVo roomDeviceListVo) {
                        contentList.clear();
                        if (roomDeviceListVo != null) {
                            if (roomDeviceListVo.getCode() == 200) {
                                if (roomDeviceListVo.getData() != null && roomDeviceListVo.getData().size() > 0) {
                                    //获取右侧家庭设备数据
                                    contentList.addAll(roomDeviceListVo.getData());
                                    rvContent.setLayoutManager(gridLayoutManager);
                                    rvContent.setAdapter(contentAdapter);
                                    contentAdapter.notifyDataSetChanged();
                                    llAddDevice.setVisibility(View.GONE);
                                    rvContent.setVisibility(View.VISIBLE);
                                } else {
                                    llAddDevice.setVisibility(View.VISIBLE);
                                    rvContent.setVisibility(View.GONE);
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    /**
     * 获取常用设备
     */
    private void getCommonDevice() {
        CacheSetting.getCache().getCommonDeviceList(
                RetrofitService.qdoApi.getCommonDeviceList(userId, ConfigUtils.family_locate.getFamily_id()),
                new DynamicKey(ConfigUtils.family_locate.getFamily_id()),
                new EvictDynamicKey(isCommonDevice))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RoomDeviceListVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(RoomDeviceListVo commonDeviceListVo) {
                        contentList.clear();
                        if (commonDeviceListVo != null) {
                            if (commonDeviceListVo.getCode() == 200) {
                                if (commonDeviceListVo.getData() != null && commonDeviceListVo.getData().size() > 0) {
                                    //获取右侧家庭设备数据
                                    contentList.addAll(commonDeviceListVo.getData());
                                    rvContent.setLayoutManager(linearLayoutManager);
                                    rvContent.setAdapter(contentAdapter);
                                    contentAdapter.notifyDataSetChanged();
                                    llAddDevice.setVisibility(View.GONE);
                                    rvContent.setVisibility(View.VISIBLE);
                                } else {
                                    llAddDevice.setVisibility(View.VISIBLE);
                                    rvContent.setVisibility(View.GONE);
                                }
                            }

                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    /**
     * 获取分享列表
     */
    private void getShareList() {
        CacheSetting.getCache().getHomeShareDeviceList(RetrofitService.qdoApi.getShareRoomDevices(userId), new DynamicKey(userId), new EvictDynamicKey(isShareList))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HomeShareDeviceListVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HomeShareDeviceListVo homeShareDeviceListVo) {
                        shareList.clear();
                        if (homeShareDeviceListVo != null) {
                            if (homeShareDeviceListVo.getCode() == 200) {
                                if (homeShareDeviceListVo.getData() != null && homeShareDeviceListVo.getData().size() > 0) {
                                    //获取右侧家庭设备数据
                                    shareList.addAll(homeShareDeviceListVo.getData());
                                    rvContent.setLayoutManager(gridLayoutManager);
                                    rvContent.setAdapter(shareListAdapter);
                                    shareListAdapter.notifyDataSetChanged();
                                    llAddDevice.setVisibility(View.GONE);
                                    rvContent.setVisibility(View.VISIBLE);
                                } else {
                                    llAddDevice.setVisibility(View.VISIBLE);
                                    rvContent.setVisibility(View.GONE);
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 初始化加载界面
     */
    private void initEvent() {
        //获取首页数据
        tvAddText.setText(R.string.common_add_device);
        getHomeMenuList();
    }

    /**
     * 获取首页数据
     */
    private void getHomeMenuList() {
        CacheSetting.getCache().getHomeMenuList(RetrofitService.qdoApi.getFamilyList(userId), new DynamicKey(userId), new EvictDynamicKey(isHomeMenu))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(homeListVo -> {
                    if (homeListVo != null) {
                        menuList.clear();
                        if (homeListVo.getCode() == 200) {
                            for (HomeListVo.DataBean dataBean : homeListVo.getData()) {
                                if (dataBean.getIs_opened() == 1) {
                                    tvSceneSetting.setText(dataBean.getFamily_name());
                                    ConfigUtils.family_locate = dataBean;
                                }
                                menuList.add(dataBean);
                            }
                        } else if (homeListVo.getCode() == 400) {
                            llAddDevice.setVisibility(View.GONE);
                            rvContent.setVisibility(View.GONE);
                        }
                    }
                })
                .observeOn(Schedulers.io())
                .filter(homeListVo -> homeListVo.getCode() == 200)
                .observeOn(Schedulers.io())
                .concatMap((Function<HomeListVo, ObservableSource<HomeLeftListVo>>) homeListVo -> CacheSetting.getCache().getHomeLeftList(RetrofitService.qdoApi.getHomeRoomList(
                        ConfigUtils.family_locate.getFamily_id(), userId),
                        new DynamicKey(ConfigUtils.family_locate.getFamily_id()),
                        new EvictDynamicKey(isHomeLeftList)))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(homeLeftListVo -> {
                    if (homeLeftListVo.getCode() == 200) {
                        list.clear();
                        list.addAll(homeLeftListVo.getData());
                        //设置左侧列表
                        listAdapter = new HomeListAdapter(list);
                        rvList.setLayoutManager(linearLayoutManager2);
                        rvList.setAdapter(listAdapter);
                        setMenuOnClick();
                    } else {
                        llAddDevice.setVisibility(View.GONE);
                        rvContent.setVisibility(View.GONE);
                    }
                })
                .observeOn(Schedulers.io())
                .filter(homeLeftListVo -> homeLeftListVo.getCode() == 200)
                .observeOn(Schedulers.io())
                .concatMap((Function<HomeLeftListVo, ObservableSource<DefaultRoomVo>>) homeLeftListVo -> RetrofitService.qdoApi.getDefaultRoomId(ConfigUtils.family_locate.getFamily_id()))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(defaultRoomVo -> {
                    if (defaultRoomVo.getCode() == 200) {
                        ConfigUtils.defaultRoomId = defaultRoomVo.getRoom_id();
                    } else {
                        llAddDevice.setVisibility(View.GONE);
                        rvContent.setVisibility(View.GONE);
                    }
                })
                .observeOn(Schedulers.io())
                .filter(defaultRoomVo -> defaultRoomVo.getCode() == 200)
                .observeOn(Schedulers.io())
                .concatMap((Function<DefaultRoomVo, ObservableSource<RoomDeviceListVo>>) defaultRoomVo -> CacheSetting.getCache().getCommonDeviceList(
                        RetrofitService.qdoApi.getCommonDeviceList(userId, ConfigUtils.family_locate.getFamily_id()),
                        new DynamicKey(ConfigUtils.family_locate.getFamily_id()),
                        new EvictDynamicKey(isCommonDevice)))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RoomDeviceListVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(RoomDeviceListVo commonDeviceListVo) {
                        if (commonDeviceListVo != null) {
                            if (commonDeviceListVo.getCode() == 200) {
                                if (commonDeviceListVo.getData() != null && commonDeviceListVo.getData().size() > 0) {
                                    //获取右侧常用家庭数据
                                    contentList.clear();
                                    contentList.addAll(commonDeviceListVo.getData());
                                    rvContent.setLayoutManager(linearLayoutManager);
                                    rvContent.setAdapter(contentAdapter);
                                    contentAdapter.notifyDataSetChanged();
                                    llAddDevice.setVisibility(View.GONE);
                                    rvContent.setVisibility(View.VISIBLE);
                                } else {
                                    llAddDevice.setVisibility(View.VISIBLE);
                                    rvContent.setVisibility(View.GONE);
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        llAddDevice.setVisibility(View.VISIBLE);
                        rvContent.setVisibility(View.GONE);
                        smartRefresh.finishRefresh(true);
                    }

                    @Override
                    public void onComplete() {
                        smartRefresh.finishRefresh(true);
                    }
                });
    }


    /**
     * 分享点击事件
     */
    private void setShareOnItemClick() {
        shareListAdapter.setOnItemClickListener((adapter, view, position) -> {
            HomeShareDeviceListVo.DataBean dataBean = shareList.get(position);
            switch (dataBean.getDevice_no()) {
                case "qs02":
                    startActivity(new Intent(getActivity(), QsTwoControlActivity.class)
                            .putExtra("scanResult", new ScanResultVo(dataBean.getDevice_no(),
                                    dataBean.getDevice_name(), dataBean.getDevice_mac(),
                                    dataBean.getConnect_type(), 0, dataBean.getDevice_id()))
                            .putExtra("is_control", dataBean.getIs_control()));
                    startIntentAnim(getActivity());
                    break;
                case "qs03":
                    startActivity(new Intent(getActivity(), QsThreeControlActivity.class)
                            .putExtra("scanResult", new ScanResultVo(dataBean.getDevice_no(),
                                    dataBean.getDevice_name(), dataBean.getDevice_mac(),
                                    dataBean.getConnect_type(), 0, dataBean.getDevice_id())));
                    startIntentAnim(getActivity());
                    break;
            }
        });
        shareListAdapter.setOnItemLongClickListener((adapter, view, position) -> {
            showDeleteShareDeviceDialog(position);
            return true;
        });
    }

    /**
     * 内容点击事件
     */
    private void setOnItemClick() {
        //家庭内容区域设备点击事件
        contentAdapter.setOnItemClickListener((adapter, view, position) -> {
            RoomDeviceListVo.DataBean dataBean = contentList.get(position);
            switch (dataBean.getDevice_no()) {
                case "qs02":
                    startActivity(new Intent(getActivity(), QsTwoControlActivity.class)
                            .putExtra("scanResult", new ScanResultVo(dataBean.getDevice_no(),
                                    dataBean.getDevice_name(), dataBean.getDevice_mac(),
                                    dataBean.getConnect_type(), dataBean.getIs_first_connect(), dataBean.getDevice_id())));
                    startIntentAnim(getActivity());
                    break;
                case "qs03":
                    startActivity(new Intent(getActivity(), QsThreeControlActivity.class)
                            .putExtra("scanResult", new ScanResultVo(dataBean.getDevice_no(),
                                    dataBean.getDevice_name(), dataBean.getDevice_mac(),
                                    dataBean.getConnect_type(), dataBean.getIs_first_connect(), dataBean.getDevice_id())));
                    startIntentAnim(getActivity());
                    break;
            }
        });
        contentAdapter.setOnItemLongClickListener((adapter, view, position) -> {
            seleteDevice = contentList.get(position);
            seletePosition = position;
            if (seleteDevice.getIs_common() == 1) {
                showDialogTwo();
            } else {
                showDialog();
            }
            return true;
        });
    }

    /**
     * 展示删除共享设备的dialog
     */
    private void showDeleteShareDeviceDialog(int position) {
        new MaterialDialog.Builder(Objects.requireNonNull(getActivity()))
                .content(R.string.common_delete_share_device_content)
                .negativeText(R.string.common_cancel)
                .positiveText(R.string.common_confirm)
                .onPositive((dialog, which) -> deleteReceiver(position)).show();
    }

    /**
     * 删除接受共享的设备
     *
     * @param position
     */
    private void deleteReceiver(int position) {
        Gson gson = new Gson();
        DeleteReceiveShareVo feedBackVo = new DeleteReceiveShareVo();
        List<String> list1 = new ArrayList<>();
        list1.add(shareList.get(position).getDevice_share_id());
        feedBackVo.setDevice_share_id(list1);
        String s = gson.toJson(feedBackVo);
        RequestBody body = RequestBody.create(MediaType.parse("Content-Type: application/json"), s);
        RetrofitService.qdoApi.deleteObjectUserShareDevicesInfo(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MessageVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MessageVo messageVo) {
                        if (messageVo.getCode() == 200) {
                            ToastUtil.showToast(getString(R.string.common_delete_success));
                            getShareList();
                        } else {
                            ToastUtil.showToast(getString(R.string.common_delete_fail));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showToast(getString(R.string.common_delete_fail));
                    }

                    @Override
                    public void onComplete() {

                    }
                });
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
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // 设置点击窗口外边窗口消失
        popupWindow.setOutsideTouchable(true);
        // 设置此参数获得焦点，否则无法点击
        popupWindow.setFocusable(true);
        RecyclerView recycleView = view.findViewById(R.id.recyclerview);
        RelativeLayout rl_home_manage = view.findViewById(R.id.rl_home_manage);
        recycleView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        /*
      menu数据适配器
     */
        HomeMenuAdapter menuAdapter = new HomeMenuAdapter(menuList);
        recycleView.setAdapter(menuAdapter);
        menuAdapter.setOnItemClickListener((adapter, view1, position) -> {
            popupWindow.dismiss();
            //切换家庭
            replaceFamily(menuList.get(position).getFamily_id());
        });
        //家庭管理设置
        rl_home_manage.setOnClickListener(v -> {
            popupWindow.dismiss();
            startActivity(new Intent(getActivity(), HomeManageActivity.class));
            startIntentAnim(getActivity());
        });
    }

    /**
     * 切换家庭
     */
    private void replaceFamily(String family_id) {
        RetrofitService.qdoApi.switchFamily(family_id, userId)
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
                                ToastUtil.showToast(getString(R.string.common_server_error));
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @OnClick({R.id.tv_scene_setting, R.id.ll_add_device, R.id.fab})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_add_device:
                //添加设备
                startIntentLogin(userId, getActivity(), DeviceAddActivity.class);
                break;
            case R.id.tv_scene_setting:
                //用户注册后默认是有一个房间的，不存在为空。
                if (CommonUtils.isEmptyString(userId)) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    startIntentAnim(getActivity());
                } else {
                    shouPopupwindow();
                    popupWindow.showAsDropDown(tvSceneSetting, 30, 0);
                }
                break;
            case R.id.fab:
                startIntentLogin(userId, getActivity(), DeviceAddActivity.class);
//                startActivity(new Intent(getActivity(), MqttTestActivity.class));
                break;
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (mMaxScrollSize == 0)
            mMaxScrollSize = appBarLayout.getTotalScrollRange();
        int percentage = (Math.abs(verticalOffset)) * 100 / mMaxScrollSize;
        if (percentage >= PERCENTAGE_TO_ANIMATE_AVATAR && mIsAvatarShown) {
            mIsAvatarShown = false;
            rlWeather.animate()
                    .scaleY(0).scaleX(0)
                    .start();
        }
        if (percentage <= PERCENTAGE_TO_ANIMATE_AVATAR && !mIsAvatarShown) {
            mIsAvatarShown = true;
            rlWeather.animate()
                    .scaleY(1).scaleX(1)
                    .start();
        }
    }

    @Override
    public void onWeatherLiveSearched(LocalWeatherLiveResult weatherLiveResult, int i) {
        //  获得天气查询结果
        if (i == 1000) {
            if (weatherLiveResult != null && weatherLiveResult.getLiveResult() != null) {
                liveResult = weatherLiveResult.getLiveResult();
                tvDay.setText("" + CommonUtils.getWeek());
                if (CommonUtils.isEmptyString(liveResult.getTemperature())) {
                    tvTemperature.setText(R.string.common_null + "°");
                } else {
                    tvTemperature.setText(liveResult.getTemperature() + "°");
                }
                if (CommonUtils.isEmptyString(liveResult.getWeather())) {
                    tvOutdoorAir.setText(getString(R.string.common_outdoor_air) + getString(R.string.common_null));
                } else {
                    tvOutdoorAir.setText(getString(R.string.common_outdoor_air) + liveResult.getWeather());
                }
                if (CommonUtils.isEmptyString(liveResult.getHumidity())) {
                    tvHumidness.setText(getString(R.string.common_humidness) + getString(R.string.common_null));
                } else {
                    tvHumidness.setText(getString(R.string.common_humidness) + liveResult.getHumidity() + "%");
                }
                if (CommonUtils.isEmptyString(liveResult.getWindPower())) {
                    tvWaterQuality.setText(getString(R.string.common_water_quality) + getString(R.string.common_null));
                } else {
                    tvWaterQuality.setText(getString(R.string.common_water_quality) + liveResult.getWindDirection() + "风" + liveResult.getWindPower() + "级");
                }
            }
        }
    }

    @Override
    public void onWeatherForecastSearched(LocalWeatherForecastResult localWeatherForecastResult, int i) {

    }

    /**
     * 显示长按列表展示的dialog（设置常用）
     */
    private void showDialog() {
        new MaterialDialog.Builder(Objects.requireNonNull(getActivity()))
                .content(R.string.home_device_function_selete)
                .items(R.array.home_device_dialog)
                .itemsColor(getResources().getColor(R.color.app_red_color))
                .itemsCallback((dialog, itemView, position, text) -> {
                    switch (position) {
                        case 0:
                            showMoveRoomDialog();
                            break;
                        case 1:
                            showRetryNameDialog();
                            break;
                        case 2:
                            showDeleteDialog();
                            break;
                        case 3:
                            showCommonDevice(1);
                            break;
                        case 4:
                            startActivity(new Intent(getActivity(), DeviceShareActivity.class)
                                    .putExtra("device_id", contentList.get(seletePosition).getDevice_id())
                                    .putExtra("device_type", contentList.get(seletePosition).getDevice_no()));
                            startIntentAnim(getActivity());
                            break;
                    }
                })
                .negativeText(R.string.common_cancel)
                .show();
    }


    /**
     * 设置和取消常用设备
     */
    private void showCommonDevice(int isCommon) {
        RetrofitService.qdoApi.setCommonDevice(seleteDevice.getDevice_id(), isCommon)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MessageVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MessageVo messageVo) {
                        if (messageVo.getCode() == 200) {
                            ToastUtil.showToast(getString(R.string.common_set_success));
                            if (isCommon == 0 && mPosition == 0) {
                                contentList.remove(seletePosition);
                                contentAdapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    /**
     * 显示长按列表展示的dialog（取消常用）
     */
    private void showDialogTwo() {
        new MaterialDialog.Builder(Objects.requireNonNull(getActivity()))
                .content(R.string.home_device_function_selete)
                .items(R.array.home_device_dialog_two)
                .itemsColor(getResources().getColor(R.color.app_red_color))
                .itemsCallback((dialog, itemView, position, text) -> {
                    switch (position) {
                        case 0:
                            showMoveRoomDialog();
                            break;
                        case 1:
                            showRetryNameDialog();
                            break;
                        case 2:
                            showDeleteDialog();
                            break;
                        case 3:
                            showCommonDevice(0);
                            break;
                        case 4:
                            startActivity(new Intent(getActivity(), DeviceShareActivity.class)
                                    .putExtra("device_id", contentList.get(seletePosition).getDevice_id())
                                    .putExtra("device_type", contentList.get(seletePosition).getDevice_no()));
                            startIntentAnim(getActivity());
                            break;
                    }
                })
                .negativeText("取消")
                .show();
    }


    /**
     * 显示删除设备的dialog
     */
    private void showDeleteDialog() {
        new MaterialDialog.Builder(Objects.requireNonNull(getActivity()))
                .content(R.string.home_device_delete_device)
                .negativeText(R.string.common_cancel)
                .positiveText(R.string.common_confirm)
                .onPositive((dialog, which) -> RetrofitService.qdoApi.cancelWifiNetwork(seleteDevice.getDevice_mac())
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .flatMap((Function<MessageVo, ObservableSource<MessageVo>>) messageVo -> RetrofitService.qdoApi.deleteDevice(seleteDevice.getDevice_id()))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<MessageVo>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(MessageVo messageVo) {
                                if (messageVo.getCode() == 200) {
                                    ToastUtil.showToast(getString(R.string.common_delete_success));
                                    contentList.remove(seletePosition);
                                    contentAdapter.notifyDataSetChanged();
                                    if (contentList.size() == 0) {
                                        llAddDevice.setVisibility(View.VISIBLE);
                                        rvContent.setVisibility(View.GONE);
                                    }
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                ToastUtil.showToast(getString(R.string.common_delete_fail));
                            }

                            @Override
                            public void onComplete() {

                            }
                        }))
                .show();
    }

    /**
     * 显示重命名的dialog
     */
    private void showRetryNameDialog() {
        new MaterialDialog.Builder(Objects.requireNonNull(getActivity()))
                .content(R.string.home_device_set_device_name)
                .inputRange(1, 20, getResources().getColor(R.color.red))
                .input(seleteDevice.getDevice_name(), null, false, (dialog, input) -> {

                })
                .negativeText(R.string.common_cancel)
                .positiveText(R.string.common_confirm)
                .onNegative((dialog, which) -> CommonUtils.closeSoftKeyboard(getActivity()))
                .onPositive((dialog, which) -> {
                    //修改设备名称
                    EditText inputEditText = dialog.getInputEditText();
                    RetrofitService.qdoApi.updateSpecificDeviceName(seleteDevice.getDevice_id(), Objects.requireNonNull(inputEditText).getText().toString(), seleteDevice.getTable_type())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<MessageVo>() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onNext(MessageVo messageVo) {
                                    if (messageVo.getCode() == 200) {
                                        ToastUtil.showToast(getString(R.string.common_update_success));
                                        contentList.get(seletePosition).setDevice_name(inputEditText.getText().toString());
                                        contentAdapter.notifyDataSetChanged();
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {

                                }

                                @Override
                                public void onComplete() {

                                }
                            });
                })
                .show();
    }

    /**
     * 显示移动设备到别的房间的dialog
     */
    private void showMoveRoomDialog() {
        RetrofitService.qdoApi.getRoomList(ConfigUtils.family_locate.getFamily_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RoomListVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(RoomListVo roomListVo) {
                        if (roomListVo.getCode() == 200) {
                            if (roomListVo.getData() != null && roomListVo.getData().size() > 0) {
                                List<String> strList = new ArrayList<>();
                                for (RoomListVo.DataBean dataBean : roomListVo.getData()) {
                                    strList.add(dataBean.getRoom_name());
                                }
                                new MaterialDialog.Builder(Objects.requireNonNull(getActivity()))
                                        .content(R.string.home_device_move_device)
                                        .titleColor(getResources().getColor(R.color.home_setting_text))
                                        .items(strList)
                                        .itemsColor(getResources().getColor(R.color.app_red_color))
                                        .itemsCallbackSingleChoice(-1, (dialog, itemView, which, text) -> true)
                                        .positiveText(R.string.common_confirm)
                                        .negativeText(R.string.common_cancel)
                                        .onPositive((dialog, which) -> {
                                            int selectedIndex = dialog.getSelectedIndex();
                                            Gson gson = new Gson();
                                            List<String> list = new ArrayList<>();
                                            list.add(seleteDevice.getDevice_id());
                                            UpdateDeviceRoomVo updateDeviceRoomVo = new UpdateDeviceRoomVo();
                                            updateDeviceRoomVo.setRoom_id(roomListVo.getData().get(selectedIndex).getRoom_id());
                                            updateDeviceRoomVo.setDevice_id(list);
                                            String body = gson.toJson(updateDeviceRoomVo);
                                            RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type: application/json"), body);
                                            RetrofitService.qdoApi.updateDeviceRoom(requestBody)
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(new Observer<MessageVo>() {
                                                        @Override
                                                        public void onSubscribe(Disposable d) {

                                                        }

                                                        @Override
                                                        public void onNext(MessageVo messageVo) {
                                                            if (messageVo.getCode() == 200) {
                                                                ToastUtil.showToast(getString(R.string.home_device_move_success));
                                                                contentList.remove(seletePosition);
                                                                contentAdapter.notifyDataSetChanged();
                                                                if (contentList.size() == 0) {
                                                                    llAddDevice.setVisibility(View.VISIBLE);
                                                                    rvContent.setVisibility(View.GONE);
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onError(Throwable e) {

                                                        }

                                                        @Override
                                                        public void onComplete() {

                                                        }
                                                    });
                                        })
                                        .show();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}