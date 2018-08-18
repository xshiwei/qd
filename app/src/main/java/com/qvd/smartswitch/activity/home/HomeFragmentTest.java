package com.qvd.smartswitch.activity.home;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.melnykov.fab.FloatingActionButton;
import com.orhanobut.logger.Logger;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseFragment;
import com.qvd.smartswitch.activity.device.DeviceControlTwoActivity;
import com.qvd.smartswitch.activity.login.LoginTestActivity;
import com.qvd.smartswitch.activity.wifi.DeviceWifiControlActivity;
import com.qvd.smartswitch.adapter.HomeContentAdapter;
import com.qvd.smartswitch.adapter.HomeListAdapter;
import com.qvd.smartswitch.adapter.HomeMenuAdapter;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.device.RoomDeviceListVo;
import com.qvd.smartswitch.model.device.ScanResultVo;
import com.qvd.smartswitch.model.home.DefaultRoomVo;
import com.qvd.smartswitch.model.home.HomeLeftListVo;
import com.qvd.smartswitch.model.home.HomeListVo;
import com.qvd.smartswitch.model.login.MessageVo;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.ConfigUtils;
import com.qvd.smartswitch.utils.PermissionUtils;
import com.qvd.smartswitch.utils.SharedPreferencesUtil;
import com.qvd.smartswitch.utils.ToastUtil;
import com.qvd.smartswitch.widget.RecycleViewDivider;
import com.qvd.smartswitch.widget.EmptyLayout;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
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

/**
 * Created by Administrator on 2018/6/6 0006.
 */

public class HomeFragmentTest extends BaseFragment {

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
    @BindView(R.id.home_setting)
    ImageView homeSetting;
    @BindView(R.id.rv_content)
    RecyclerView rvContent;     //家庭，个人，车载那里显示这个
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.iv_menu)
    ImageView ivMenu;
    @BindView(R.id.tv_scene_setting)
    TextView tvSceneSetting;
    @BindView(R.id.rl_layout)
    RelativeLayout rlLayout;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.rl_list)
    RelativeLayout rlList;
    @BindView(R.id.emptylayout)
    EmptyLayout emptylayout;
    @BindView(R.id.emptylayout_content)
    EmptyLayout emptyLayoutContent;


    public static int mPosition = 0; //通过这个位置来设置当前的图片类型和文字颜色
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
    private HomeContentAdapter contentAdapter;

    /**
     * 服务器获取的设备数据源
     */
    private List<RoomDeviceListVo.DataBean> contentList = new ArrayList<>();
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
    private String userID;

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
        PermissionUtils.requestPermission(getActivity(), Permission.WRITE_EXTERNAL_STORAGE);
        PermissionUtils.requestPermission(getActivity(), Permission.READ_EXTERNAL_STORAGE);
        PermissionUtils.requestPermission(getActivity(), Permission.READ_PHONE_STATE);
    }

    @Override
    public void onResume() {
        super.onResume();
        userID = SharedPreferencesUtil.getString(getActivity(), SharedPreferencesUtil.USER_ID);
        if (CommonUtils.isEmptyString(userID)) {
            //展示需要登录的界面
            tvSceneSetting.setText("立即登录");
            emptylayout.showEmpty();
        } else {
            initEvent();
        }
    }

    /**
     * 设置菜单点击事件
     */
    private void setMenuOnClick() {
        //设置左边菜单点击事件
        listAdapter.setOnItemClickListener(new HomeListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //获取当前位置
                mPosition = position;
                listAdapter.notifyDataSetChanged();
                //根据当前获取的位置对应的类别请求数据并显示数据。type = 1表示常用，type=2表示房间
                if (list.get(position).getType() == 1) {
                    getCommonDevice();
                } else if (list.get(position).getType() == 2) {
                    getRoomListDevice(list.get(position).getRoom_id());
                }
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
        emptyLayoutContent.hide();
        contentList.clear();
        RetrofitService.qdoApi.getRoomDeviceList(ConfigUtils.user_id, roomid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RoomDeviceListVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(RoomDeviceListVo roomDeviceListVo) {
                        if (roomDeviceListVo != null) {
                            if (roomDeviceListVo.getCode() == 200) {
                                if (roomDeviceListVo.getData() != null && roomDeviceListVo.getData().size() > 0) {
                                    //获取右侧家庭设备数据
                                    contentList.addAll(roomDeviceListVo.getData());
                                    contentAdapter = new HomeContentAdapter(getActivity(), contentList);
                                    rvContent.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                                    rvContent.setAdapter(contentAdapter);
                                    contentAdapter.notifyDataSetChanged();
                                } else {
                                    emptyLayoutContent.showEmpty();
                                }
                            } else {
                                emptyLayoutContent.showError();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        emptyLayoutContent.showError();
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
        emptyLayoutContent.hide();
        RetrofitService.qdoApi.getCommonDeviceList(ConfigUtils.user_id, ConfigUtils.family_locate.getFamily_id())
                .subscribeOn(Schedulers.io())
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
                                    //获取右侧家庭设备数据
                                    contentList.addAll(commonDeviceListVo.getData());
                                    contentAdapter = new HomeContentAdapter(getActivity(), contentList);
                                    rvContent.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                                    rvContent.setAdapter(contentAdapter);
                                    contentAdapter.notifyDataSetChanged();
                                    //家庭内容区域设备点击事件
                                    contentAdapter.setOnItemClickListener(new HomeContentAdapter.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(View view, int position) {
                                            RoomDeviceListVo.DataBean dataBean = contentList.get(position);
                                            switch (dataBean.getDevice_no()) {
                                                case "qs02":
                                                    startActivity(new Intent(getActivity(), DeviceControlTwoActivity.class)
                                                            .putExtra("scanResult", new ScanResultVo(dataBean.getDevice_no(),
                                                                    CommonUtils.getDeviceName(dataBean.getDevice_no()), dataBean.getDevice_mac(),
                                                                    dataBean.getConnect_type()))
                                                            .putExtra("isFirstConnect", dataBean.getIs_first_connect()));
                                                    getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                                    break;
                                                case "qs03":
                                                    startActivity(new Intent(getActivity(), DeviceWifiControlActivity.class)
                                                            .putExtra("isFirstConnect", 1));
                                                    getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                                    break;
                                            }
                                        }

                                        @Override
                                        public void onItemLongClickListener(View view, int position) {

                                        }
                                    });
                                } else {
                                    emptyLayoutContent.showEmpty();
                                }
                            } else {
                                emptyLayoutContent.showError();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        emptyLayoutContent.showError();
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
//        getHomeMenuList();
        for (int i = 0; i < 10; i++) {
            contentList.add(new RoomDeviceListVo.DataBean());
        }
        contentAdapter = new HomeContentAdapter(getActivity(), contentList);
        rvContent.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rvContent.setAdapter(contentAdapter);
        //设置刷新控件头部高度
        refreshLayout.setHeaderHeight(100);
        refreshLayout.setFooterHeight(1);
        refreshLayout.setDragRate(0.15f);
        refreshLayout.setEnableHeaderTranslationContent(false);//是否上拉Footer的时候向上平移列表或者内容
        //设置头部样式
        refreshLayout.setRefreshHeader(new MaterialHeader(getActivity()));


        //设置FloatingActionButton
        fab.attachToRecyclerView(rvContent);
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
                                        tvSceneSetting.setText(dataBean.getFamily_name());
                                        ConfigUtils.family_locate = dataBean;
                                    }
                                    menuList.add(dataBean);
                                }
                            } else if (homeListVo.getCode() == 400) {
                                emptylayout.showError();
                                tvSceneSetting.setText("获取失败");
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
                        return RetrofitService.qdoApi.getHomeLeftList(ConfigUtils.family_locate.getFamily_id(), ConfigUtils.user_id);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<HomeLeftListVo>() {
                    @Override
                    public void accept(HomeLeftListVo homeLeftListVo) throws Exception {
                        if (homeLeftListVo.getCode() == 200) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    rlList.setVisibility(View.VISIBLE);
                                }
                            }, 1000);
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
                            emptylayout.showError();
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
                            emptylayout.showError();
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
                        return RetrofitService.qdoApi.getCommonDeviceList(ConfigUtils.user_id, ConfigUtils.family_locate.getFamily_id());
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
                                    //获取右侧家庭设备数据
                                    contentList.addAll(commonDeviceListVo.getData());
                                    contentAdapter = new HomeContentAdapter(getActivity(), contentList);
                                    rvContent.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                                    rvContent.setAdapter(contentAdapter);
                                    contentAdapter.notifyDataSetChanged();
                                    //家庭内容区域设备点击事件
                                    contentAdapter.setOnItemClickListener(new HomeContentAdapter.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(View view, int position) {
                                            RoomDeviceListVo.DataBean dataBean = contentList.get(position);
                                            switch (dataBean.getDevice_no()) {
                                                case "qs02":
                                                    startActivity(new Intent(getActivity(), DeviceControlTwoActivity.class)
                                                            .putExtra("scanResult", new ScanResultVo(dataBean.getDevice_no(),
                                                                    CommonUtils.getDeviceName(dataBean.getDevice_no()), dataBean.getDevice_mac(),
                                                                    dataBean.getConnect_type()))
                                                            .putExtra("isFirstConnect", dataBean.getIs_first_connect()));
                                                    getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                                    break;
                                                case "qs03":
                                                    startActivity(new Intent(getActivity(), DeviceWifiControlActivity.class)
                                                            .putExtra("isFirstConnect", 1));
                                                    getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                                    break;
                                            }
                                        }

                                        @Override
                                        public void onItemLongClickListener(View view, int position) {

                                        }
                                    });
                                } else {
                                    emptyLayoutContent.showEmpty();
                                }
                            } else {
                                emptylayout.showError();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        emptylayout.showError();
                        tvSceneSetting.setText("获取失败");
                    }

                    @Override
                    public void onComplete() {
                        emptylayout.hide();
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
                                emptylayout.showLoading(R.layout.view_loading_two, getString(R.string.device_scaning));
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
    @OnClick({R.id.iv_menu, R.id.home_setting, R.id.tv_scene_setting, R.id.fab})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_menu:
                //添加设备
                if (CommonUtils.isEmptyString(userID)) {
                    startActivity(new Intent(getActivity(), LoginTestActivity.class));
                    getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                } else {
                    startActivity(new Intent(getActivity(), AddDeviceActivity.class));
                    getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
                break;
            case R.id.home_setting:
                //房间管理
                if (ConfigUtils.family_locate == null) {
                    ToastUtil.showToast("网络连接失败");
                } else {
                    startActivity(new Intent(getActivity(), RoomManageActivity.class).putExtra("family_id", ConfigUtils.family_locate.getFamily_id()));
                    getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
                break;
            case R.id.tv_scene_setting:
                //用户注册后默认是有一个房间的，不存在为空。
                if (CommonUtils.isEmptyString(userID)) {
                    startActivity(new Intent(getActivity(), LoginTestActivity.class));
                    getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                } else {
                    shouPopupwindow();
                    popupWindow.showAsDropDown(tvSceneSetting, 30, 0);
                }
                break;
            case R.id.fab:
                //声音控制界面
//                startActivity(new Intent(getActivity(), LoginTestActivity.class));
//                startActivity(new Intent(getActivity(), ConfirmLightFlickerActivity.class));
//                startActivity(new Intent(getActivity(), MqttTestActivity.class));
//                startActivity(new Intent(getActivity(), DeviceWifiControlActivity.class));
//                startActivity(new Intent(getActivity(), DeviceConnectActivity.class));
//                startActivity(new Intent(getActivity(), SetDeviceToRoomActivity.class));
//                startActivity(new Intent(getActivity(), DeviceControlTwoActivity.class));
//                getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                showDialog();
                break;
        }
    }

    private void showDialog() {
        new MaterialDialog.Builder(getActivity())
                .content("检测到您的GPS未打开，可能导致搜索不到设备,是否去打开？")
                .negativeText("取消")
                .positiveText("确定")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                    }
                })
                .show();
    }

}
