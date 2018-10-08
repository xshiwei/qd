package com.qvd.smartswitch.activity.home;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
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
import com.google.gson.Gson;
import com.melnykov.fab.FloatingActionButton;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.TextOutsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.orhanobut.logger.Logger;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseFragment;
import com.qvd.smartswitch.activity.device.DeviceAddActivity;
import com.qvd.smartswitch.activity.device.DeviceItemAndPrivacyActivity;
import com.qvd.smartswitch.activity.device.DeviceShareActivity;
import com.qvd.smartswitch.activity.device.DeviceSplashActivity;
import com.qvd.smartswitch.activity.login.LoginActivity;
import com.qvd.smartswitch.activity.login.WelcomeActivity;
import com.qvd.smartswitch.activity.qsThree.QsThreeControlActivity;
import com.qvd.smartswitch.activity.qsTwo.QsTwoControlActivity;
import com.qvd.smartswitch.adapter.HomeDeviceListAdapter;
import com.qvd.smartswitch.adapter.HomeListAdapter;
import com.qvd.smartswitch.adapter.HomeMenuAdapter;
import com.qvd.smartswitch.adapter.HomeShareListAdapter;
import com.qvd.smartswitch.adapter.ShareDeviceListAdapter;
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
import com.qvd.smartswitch.model.user.UserReceiverDeviceListVo;
import com.qvd.smartswitch.receiver.NetFragmentBroadcastReceiver;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.ConfigUtils;
import com.qvd.smartswitch.utils.PermissionUtils;
import com.qvd.smartswitch.utils.SharedPreferencesUtil;
import com.qvd.smartswitch.utils.ToastUtil;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.stephentuso.welcome.WelcomeHelper;
import com.wang.avi.AVLoadingIndicatorView;
import com.yanzhenjie.permission.Permission;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.EvictDynamicKey;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by Administrator on 2018/6/6 0006.
 */

public class HomeFragmentTest extends BaseFragment implements AppBarLayout.OnOffsetChangedListener, WeatherSearch.OnWeatherSearchListener, NetFragmentBroadcastReceiver.NetEvevt {

    @BindView(R.id.tv_temperature)
    TextView tvTemperature;
    @BindView(R.id.tv_city)
    TextView tvCity;
    @BindView(R.id.tv_outdoor_air)
    TextView tvOutdoorAir;
    @BindView(R.id.tv_water_quality)
    TextView tvWaterQuality;
    @BindView(R.id.tv_humidness)
    TextView tvHumidness;
    @BindView(R.id.rv_list)
    RecyclerView rvList;
    @BindView(R.id.rv_content)
    RecyclerView rvContent;     //家庭，个人，车载那里显示这个
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout smartRefresh;
    @BindView(R.id.tv_scene_setting)
    TextView tvSceneSetting;
    @BindView(R.id.rl_list)
    RelativeLayout rlList;
    @BindView(R.id.tv_day)
    TextView tvDay;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rl_weather)
    RelativeLayout rlWeather;
    @BindView(R.id.main_collapsing)
    CollapsingToolbarLayout mainCollapsing;
    @BindView(R.id.materialup_appbar)
    AppBarLayout appbarLayout;
    @BindView(R.id.bmb)
    BoomMenuButton bmb;
    @BindView(R.id.ll_add_device)
    LinearLayout llAddDevice;
    @BindView(R.id.tv_add_text)
    TextView tvAddText;
    @BindView(R.id.ll_weather)
    LinearLayout llWeather;
    @BindView(R.id.tv_text)
    TextView tvText;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.avi_weather)
    AVLoadingIndicatorView avi_weather;


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
    /**
     * menu家庭列表数据
     */
    private List<HomeListVo.DataBean> menuList = new ArrayList<>();


    /**
     * 代表左侧list选择的区域
     */
    public int mPosition;

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
     * menu数据适配器
     */
    private HomeMenuAdapter menuAdapter;


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

    private String userId;

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

    @Override
    protected void initData() {
        super.initData();
        PermissionUtils.requestPermission(getActivity(), Permission.Group.LOCATION);
        appbarLayout.addOnOffsetChangedListener(this);
        mMaxScrollSize = appbarLayout.getTotalScrollRange();
        userId = SharedPreferencesUtil.getString(getActivity(), SharedPreferencesUtil.USER_ID);
        evevt = this;
        //设置刷新控件头部高度
        smartRefresh.setHeaderHeight(100);
        smartRefresh.setFooterHeight(1);
        smartRefresh.setEnableHeaderTranslationContent(false);//是否上拉Footer的时候向上平移列表或者内容
        //设置头部样式
        smartRefresh.setRefreshHeader(new MaterialHeader(getActivity()));
        smartRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                if (null != mLocationClient) {
                    avi_weather.show();
                    tvText.setText("正在加载天气信息...");
                    llWeather.setVisibility(View.VISIBLE);
                    rlWeather.setVisibility(View.GONE);
                    mLocationClient.setLocationOption(mLocationOption);
                    //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
                    mLocationClient.stopLocation();
                    mLocationClient.startLocation();
                    tvText.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            avi_weather.hide();
                            tvText.setText("天气信息获取失败");
                        }
                    }, 5000);
                }
                mLocationClient.setLocationListener(locationListener);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setUpdate();
                    }
                }, 1000);
                smartRefresh.finishRefresh(3000, true);
            }
        });

        //初始化定位
        mLocationClient = new AMapLocationClient(getActivity().getApplicationContext());
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        /**
         * 设置定位场景，目前支持三种场景（签到、出行、运动，默认无场景）
         */
        mLocationOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);
        //设置定位模式为低功耗
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
        mLocationOption.setInterval(20000);
        //启动定位
        if (null != mLocationClient) {
            mLocationClient.setLocationOption(mLocationOption);
            //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
            mLocationClient.stopLocation();
            mLocationClient.startLocation();
        }
        mLocationClient.setLocationListener(locationListener);

//        initMenu();
        tvText.postDelayed(new Runnable() {
            @Override
            public void run() {
                tvText.setText("获取天气信息失败");
                avi_weather.hide();
            }
        }, 5000);
    }


    /**
     * 在这里显示网络状态改变
     */
    @Override
    public void onNetChange(int netMobile) {
        if (netMobile == -1) {
            isRoomList = false;
            isCommonDevice = false;
            isHomeLeftList = false;
            isHomeMenu = false;
            if (CommonUtils.isEmptyString(userId)) {
                //展示需要登录的界面
                tvSceneSetting.setText("立即登录");
                llAddDevice.setVisibility(View.VISIBLE);
                rvContent.setVisibility(View.GONE);
                tvAddText.setText("立即登录");
            } else {
                initEvent();
            }
            ToastUtil.showToast("网络未连接，请检查网络后再试");
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setUpdate();
                }
            }, 500);
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
            tvSceneSetting.setText("立即登录");
            llAddDevice.setVisibility(View.VISIBLE);
            rvContent.setVisibility(View.GONE);
            tvAddText.setText("立即登录");
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
                .normalText("语音控制")
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        ToastUtil.showToast("该功能正在开发中。。。");
                    }
                })
                .textSize(12)
                .normalColor(getResources().getColor(R.color.orange_background))
                .pieceColor(Color.WHITE);
        TextOutsideCircleButton.Builder builder2 = new TextOutsideCircleButton.Builder()
                .normalImageRes(R.mipmap.home_add)
                .normalText("添加设备")
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        //添加设备
                        if (CommonUtils.isEmptyString(userId)) {
                            startActivity(new Intent(getActivity(), LoginActivity.class));
                            getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                        } else {
                            startActivity(new Intent(getActivity(), DeviceAddActivity.class));
                            getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                        }
                    }
                })
                .textSize(12)
                .normalColor(getResources().getColor(R.color.app_red_color))
                .pieceColor(Color.WHITE);
        bmb.addBuilder(builder1);
        bmb.addBuilder(builder2);
    }

    @Override
    public void onResume() {
        super.onResume();
        userId = SharedPreferencesUtil.getString(getActivity(), SharedPreferencesUtil.USER_ID);
        if (CommonUtils.isEmptyString(userId)) {
            //展示需要登录的界面
            list.clear();
            contentList.clear();
            tvSceneSetting.setText("立即登录");
            llAddDevice.setVisibility(View.VISIBLE);
            rvContent.setVisibility(View.GONE);
            tvAddText.setText("立即登录");
        } else {
            initEvent();
        }
        broadcastReceiver = new NetFragmentBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        getActivity().registerReceiver(broadcastReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(broadcastReceiver);
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
                    if (!CommonUtils.isEmptyString(city)) {
                        tvCity.setText("" + city);
                        weatherSearchQuery = new WeatherSearchQuery(city, WeatherSearchQuery.WEATHER_TYPE_LIVE);
                        mweathersearch = new WeatherSearch(getActivity());
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
     * 设置菜单点击事件
     */
    private void setMenuOnClick() {
        //设置左边菜单点击事件
        listAdapter.setOnItemClickListener(new HomeListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //根据当前获取的位置对应的类别请求数据并显示数据。type = 1表示常用，type=2表示房间
                if (list.get(position).getType() == 1) {
                    getCommonDevice();
                } else if (list.get(position).getType() == 2) {
                    getRoomListDevice(list.get(position).getRoom_id());
                } else if (list.get(position).getType() == 3) {
                    getShareList();
                }
                listAdapter.setCheckedPosition(position);
                mPosition = position;
            }

            @Override
            public void onItemLongClickListener(View view, int position) {

            }
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
                                    contentAdapter = new HomeDeviceListAdapter(getActivity(), contentList);
                                    rvContent.setLayoutManager(new GridLayoutManager(getActivity(), 2, LinearLayoutManager.VERTICAL, false));
                                    rvContent.setAdapter(contentAdapter);
                                    contentAdapter.notifyDataSetChanged();
                                    setOnItemClick();
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
                                    contentAdapter = new HomeDeviceListAdapter(getActivity(), contentList);
                                    rvContent.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                                    rvContent.setAdapter(contentAdapter);
                                    contentAdapter.notifyDataSetChanged();
                                    llAddDevice.setVisibility(View.GONE);
                                    rvContent.setVisibility(View.VISIBLE);
                                    setOnItemClick();
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
        CacheSetting.getCache().getHomeShareDeviceList(RetrofitService.qdoApi.getShareRoomDevices(ConfigUtils.user_id), new DynamicKey(ConfigUtils.user_id), new EvictDynamicKey(isShareList))
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
                                    shareListAdapter = new HomeShareListAdapter(getActivity(), shareList);
                                    rvContent.setLayoutManager(new GridLayoutManager(getActivity(), 2, LinearLayoutManager.VERTICAL, false));
                                    rvContent.setAdapter(shareListAdapter);
                                    shareListAdapter.notifyDataSetChanged();
                                    llAddDevice.setVisibility(View.GONE);
                                    rvContent.setVisibility(View.VISIBLE);
                                    setShareOnItemClick();
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
        tvAddText.setText("添加设备");
        getHomeMenuList();
    }

    /**
     * 获取首页数据
     */
    private void getHomeMenuList() {
        CacheSetting.getCache().getHomeMenuList(RetrofitService.qdoApi.getFamilyList(userId), new DynamicKey(userId), new EvictDynamicKey(isHomeMenu))
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
                        return CacheSetting.getCache().getHomeLeftList(RetrofitService.qdoApi.getHomeRoomList(
                                ConfigUtils.family_locate.getFamily_id(), userId),
                                new DynamicKey(ConfigUtils.family_locate.getFamily_id()),
                                new EvictDynamicKey(isHomeLeftList));
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<HomeLeftListVo>() {
                    @Override
                    public void accept(HomeLeftListVo homeLeftListVo) throws Exception {
                        if (homeLeftListVo.getCode() == 200) {
                            list.clear();
                            list.addAll(homeLeftListVo.getData());
                            //设置左侧列表
                            listAdapter = new HomeListAdapter(getActivity(), list);
                            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false) {
                                @Override
                                public boolean canScrollVertically() {
                                    return true;
                                }
                            };
                            rvList.setLayoutManager(layoutManager);
                            rvList.setAdapter(listAdapter);
                            setMenuOnClick();
                        } else {
                            llAddDevice.setVisibility(View.GONE);
                            rvContent.setVisibility(View.GONE);
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
                .doOnNext(new Consumer<DefaultRoomVo>() {
                    @Override
                    public void accept(DefaultRoomVo defaultRoomVo) throws Exception {
                        if (defaultRoomVo.getCode() == 200) {
                            ConfigUtils.defaultRoomId = defaultRoomVo.getRoom_id();
                        } else {
                            llAddDevice.setVisibility(View.GONE);
                            rvContent.setVisibility(View.GONE);
                        }
                    }
                })
                .observeOn(Schedulers.io())
                .filter(new Predicate<DefaultRoomVo>() {
                    @Override
                    public boolean test(DefaultRoomVo defaultRoomVo) throws Exception {
                        return defaultRoomVo.getCode() == 200;
                    }
                })
                .observeOn(Schedulers.io())
                .concatMap(new Function<DefaultRoomVo, ObservableSource<RoomDeviceListVo>>() {
                    @Override
                    public ObservableSource<RoomDeviceListVo> apply(DefaultRoomVo defaultRoomVo) throws Exception {
                        return CacheSetting.getCache().getCommonDeviceList(
                                RetrofitService.qdoApi.getCommonDeviceList(userId, ConfigUtils.family_locate.getFamily_id()),
                                new DynamicKey(ConfigUtils.family_locate.getFamily_id()),
                                new EvictDynamicKey(isCommonDevice));
                    }
                })
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
                                    contentAdapter = new HomeDeviceListAdapter(getActivity(), contentList);
                                    rvContent.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                                    rvContent.setAdapter(contentAdapter);
                                    contentAdapter.notifyDataSetChanged();
                                    llAddDevice.setVisibility(View.GONE);
                                    rvContent.setVisibility(View.VISIBLE);
                                    setOnItemClick();
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
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    /**
     * 分享点击事件
     */
    private void setShareOnItemClick() {
        shareListAdapter.setOnItemClickListener(new HomeShareListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                HomeShareDeviceListVo.DataBean dataBean = shareList.get(position);
                switch (dataBean.getDevice_no()) {
                    case "qs02":
                        startActivity(new Intent(getActivity(), QsTwoControlActivity.class)
                                .putExtra("scanResult", new ScanResultVo(dataBean.getDevice_no(),
                                        dataBean.getDevice_name(), dataBean.getDevice_mac(),
                                        dataBean.getConnect_type(), 0, dataBean.getDevice_id()))
                                .putExtra("is_control", dataBean.getIs_control()));
                        getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                        break;
                    case "qs03":
                        startActivity(new Intent(getActivity(), QsThreeControlActivity.class)
                                .putExtra("scanResult", new ScanResultVo(dataBean.getDevice_no(),
                                        dataBean.getDevice_name(), dataBean.getDevice_mac(),
                                        dataBean.getConnect_type(), 0, dataBean.getDevice_id())));
                        getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                        break;
                }
            }

            @Override
            public void onItemLongClickListener(View view, int position) {
                showDeleteShareDeviceDialog(position);
            }

            @Override
            public void onToggleButtonClickListener(boolean state, int position) {

            }
        });
    }

    /**
     * 内容点击事件
     */
    private void setOnItemClick() {
        //家庭内容区域设备点击事件
        contentAdapter.setOnItemClickListener(new HomeDeviceListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                RoomDeviceListVo.DataBean dataBean = contentList.get(position);
                switch (dataBean.getDevice_no()) {
                    case "qs02":
                        startActivity(new Intent(getActivity(), QsTwoControlActivity.class)
                                .putExtra("scanResult", new ScanResultVo(dataBean.getDevice_no(),
                                        dataBean.getDevice_name(), dataBean.getDevice_mac(),
                                        dataBean.getConnect_type(), dataBean.getIs_first_connect(), dataBean.getDevice_id())));
                        getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                        break;
                    case "qs03":
                        startActivity(new Intent(getActivity(), QsThreeControlActivity.class)
                                .putExtra("scanResult", new ScanResultVo(dataBean.getDevice_no(),
                                        dataBean.getDevice_name(), dataBean.getDevice_mac(),
                                        dataBean.getConnect_type(), dataBean.getIs_first_connect(), dataBean.getDevice_id())));
                        getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                        break;
                }
            }

            @Override
            public void onItemLongClickListener(View view, int position) {
                seleteDevice = contentList.get(position);
                seletePosition = position;
                if (seleteDevice.getIs_common() == 1) {
                    showDialogTwo();
                } else {
                    showDialog();
                }
            }

            @Override
            public void onToggleButtonClickListener(boolean state, int position) {

            }
        });
    }

    /**
     * 展示删除共享设备的dialog
     */
    private void showDeleteShareDeviceDialog(int position) {
        new MaterialDialog.Builder(getActivity())
                .title("删除所选中的一个设备吗")
                .negativeText("取消")
                .positiveText("确定")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        deleteReceiver(position);
                    }
                }).show();
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
                            ToastUtil.showToast("删除成功");
                            getShareList();
                        } else {
                            ToastUtil.showToast("删除失败");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showToast("删除失败");
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @OnClick({R.id.tv_scene_setting, R.id.ll_add_device, R.id.fab})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_add_device:
                //添加设备
                if (CommonUtils.isEmptyString(userId)) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                } else {
                    startActivity(new Intent(getActivity(), DeviceAddActivity.class));
                    getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
                break;
            case R.id.tv_scene_setting:
                //用户注册后默认是有一个房间的，不存在为空。
                if (CommonUtils.isEmptyString(userId)) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                } else {
                    shouPopupwindow();
                    popupWindow.showAsDropDown(tvSceneSetting, 30, 0);
                }
                break;
            case R.id.fab:
                if (CommonUtils.isEmptyString(userId)) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                } else {
                    startActivity(new Intent(getActivity(), DeviceAddActivity.class));
                    getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
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
            llWeather.animate()
                    .scaleY(0).scaleX(0)
                    .start();
        }
        if (percentage <= PERCENTAGE_TO_ANIMATE_AVATAR && !mIsAvatarShown) {
            mIsAvatarShown = true;
            rlWeather.animate()
                    .scaleY(1).scaleX(1)
                    .start();
            llWeather.animate()
                    .scaleY(1).scaleX(1)
                    .start();
        }
    }

    @Override
    public void onWeatherLiveSearched(LocalWeatherLiveResult weatherLiveResult, int i) {
        //  获得天气查询结果
        if (i == 1000) {
            if (weatherLiveResult != null && weatherLiveResult.getLiveResult() != null) {
                llWeather.setVisibility(View.GONE);
                rlWeather.setVisibility(View.VISIBLE);
                LocalWeatherLive liveResult = weatherLiveResult.getLiveResult();
                tvDay.setText("" + CommonUtils.getWeek());
                if (CommonUtils.isEmptyString(liveResult.getTemperature())) {
                    tvTemperature.setText("Null");
                } else {
                    tvTemperature.setText(liveResult.getTemperature() + "°");
                }
                if (CommonUtils.isEmptyString(liveResult.getWeather())) {
                    tvOutdoorAir.setText("天气: Null");
                } else {
                    tvOutdoorAir.setText("天气: " + liveResult.getWeather());
                }
                if (CommonUtils.isEmptyString(liveResult.getHumidity())) {
                    tvHumidness.setText("湿度: Null");
                } else {
                    tvHumidness.setText("湿度: " + liveResult.getHumidity() + "%");
                }
                if (CommonUtils.isEmptyString(liveResult.getWindPower())) {
                    tvWaterQuality.setText("风向: Null");
                } else {
                    tvWaterQuality.setText(liveResult.getWindDirection() + "风 " + liveResult.getWindPower() + "级");
                }
            } else {
                tvText.setText("天气信息获取失败");
                avi_weather.hide();
                llWeather.setVisibility(View.VISIBLE);
                rlWeather.setVisibility(View.GONE);
            }
        } else {
            avi_weather.hide();
            tvText.setText("天气信息获取失败");
            llWeather.setVisibility(View.VISIBLE);
            rlWeather.setVisibility(View.GONE);
        }
    }

    @Override
    public void onWeatherForecastSearched(LocalWeatherForecastResult localWeatherForecastResult, int i) {

    }

    /**
     * 显示长按列表展示的dialog（设置常用）
     */
    private void showDialog() {
        new MaterialDialog.Builder(getActivity())
                .title("功能选择")
                .titleColor(getResources().getColor(R.color.home_setting_text))
                .items(R.array.home_device_dialog)
                .itemsColor(getResources().getColor(R.color.app_red_color))
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
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
                                getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                break;
                        }
                    }
                })
                .negativeText("取消")
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
                            ToastUtil.showToast("设置成功");
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
        new MaterialDialog.Builder(getActivity())
                .title("功能选择")
                .titleColor(getResources().getColor(R.color.home_setting_text))
                .items(R.array.home_device_dialog_two)
                .itemsColor(getResources().getColor(R.color.app_red_color))
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
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
                                getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                break;
                        }
                    }
                })
                .negativeText("取消")
                .show();
    }


    /**
     * 显示删除设备的dialog
     */
    private void showDeleteDialog() {
        new MaterialDialog.Builder(getActivity())
                .title("您确定要删除该设备吗")
                .titleColor(getResources().getColor(R.color.home_setting_text))
                .negativeText("取消")
                .positiveText("确定")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        RetrofitService.qdoApi.deleteDevice(seleteDevice.getDevice_id())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<MessageVo>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onNext(MessageVo messageVo) {
                                        if (messageVo.getCode() == 200) {
                                            ToastUtil.showToast("删除成功");
                                            contentList.remove(seletePosition);
                                            contentAdapter.notifyDataSetChanged();
                                            if (contentList.size() == 0) {
                                                llAddDevice.setVisibility(View.VISIBLE);
                                                rvContent.setVisibility(View.GONE);
                                            }
//                                            if (seleteDevice.getDevice_no().equals("qs03")) {
//                                                cancleSetNetwork(seleteDevice.getDevice_mac());
//                                            }
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
                })
                .show();
    }

    /**
     * 取消配网
     */
    private void cancleSetNetwork(String deviceMac) {
        RetrofitService.qdoApi.cancelWifiNetwork(deviceMac)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MessageVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MessageVo messageVo) {

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
     * 显示重命名的dialog
     */
    private void showRetryNameDialog() {
        new MaterialDialog.Builder(getActivity())
                .title("设置设备名称")
                .titleColor(getResources().getColor(R.color.home_setting_text))
                .inputRange(1, 20, getResources().getColor(R.color.red))
                .input(seleteDevice.getDevice_name(), null, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                    }
                })
                .negativeText("取消")
                .positiveText("确定")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        CommonUtils.closeSoftKeyboard(getActivity());
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        //修改设备名称
                        EditText inputEditText = dialog.getInputEditText();
                        RetrofitService.qdoApi.updateSpecificDeviceName(seleteDevice.getDevice_id(), inputEditText.getText().toString(), seleteDevice.getTable_type())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<MessageVo>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onNext(MessageVo messageVo) {
                                        if (messageVo.getCode() == 200) {
                                            ToastUtil.showToast("修改成功");
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
                    }
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
                                new MaterialDialog.Builder(getActivity())
                                        .title("移动设备")
                                        .titleColor(getResources().getColor(R.color.home_setting_text))
                                        .items(strList)
                                        .itemsColor(getResources().getColor(R.color.app_red_color))
                                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                                            @Override
                                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                                return true;
                                            }
                                        })
                                        .positiveText("确定")
                                        .negativeText("取消")
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
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
                                                                    ToastUtil.showToast("移动成功");
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
                                            }
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