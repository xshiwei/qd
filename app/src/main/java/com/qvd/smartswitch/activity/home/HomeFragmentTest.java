package com.qvd.smartswitch.activity.home;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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

import com.melnykov.fab.FloatingActionButton;
import com.orhanobut.logger.Logger;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseFragment;
import com.qvd.smartswitch.activity.wifi.DeviceWifiControlActivity;
import com.qvd.smartswitch.activity.wifi.MqttTestActivity;
import com.qvd.smartswitch.adapter.HomeContentAdapter;
import com.qvd.smartswitch.adapter.HomeContentTwoAdapter;
import com.qvd.smartswitch.adapter.HomeListAdapter;
import com.qvd.smartswitch.adapter.HomeMenuAdapter;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.home.HomeListVo;
import com.qvd.smartswitch.model.home.Test1Vo;
import com.qvd.smartswitch.model.home.Test2Vo;
import com.qvd.smartswitch.model.home.TestVo;
import com.qvd.smartswitch.model.login.MessageVo;
import com.qvd.smartswitch.utils.ConfigUtils;
import com.qvd.smartswitch.utils.PermissionUtils;
import com.qvd.smartswitch.utils.SnackbarUtils;
import com.qvd.smartswitch.utils.ToastUtil;
import com.qvd.smartswitch.widget.EmptyLayout;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yanzhenjie.permission.Permission;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
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
    @BindView(R.id.rv_content_two)
    RecyclerView rvContentTwo;    //常用和全部那里显示这个
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


    public static int mPosition = 0; //通过这个位置来设置当前的图片类型和文字颜色
    /**
     * 列表适配器
     */
    private HomeListAdapter listAdapter;
    /**
     * 列表数据源
     */
    private List<TestVo> list = new ArrayList<>();
    /**
     * 常用内容适配器
     */
    private HomeContentTwoAdapter twoAdapter;
    /**
     * 常用内容数据源
     */
    private List<Test2Vo> listContent2 = new ArrayList<>();
    /**
     * 房间点击后设备适配器
     */
    private HomeContentAdapter contentAdapter;

    /**
     * 服务器获取的设备数据源
     */
    private List<Test1Vo> contentList = new ArrayList<>();
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
        initEvent();
        setMenuOnClick();
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
                //根据当前获取的位置对应的类别请求数据并显示数据。
                if (list.get(position).getType() == 1) {
                    twoAdapter = new HomeContentTwoAdapter(getActivity(), listContent2);
                    rvContentTwo.setLayoutManager(new GridLayoutManager(getActivity(), 2, LinearLayoutManager.VERTICAL, false));
                    rvContentTwo.setAdapter(twoAdapter);
                    twoAdapter.notifyDataSetChanged();
                    rvContentTwo.setVisibility(View.VISIBLE);
                    rvContent.setVisibility(View.GONE);
                } else if (list.get(position).getType() == 2) {
                    contentAdapter = new HomeContentAdapter(getActivity(), contentList);
                    rvContent.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                    rvContent.setAdapter(contentAdapter);
                    contentAdapter.notifyDataSetChanged();
                    rvContentTwo.setVisibility(View.GONE);
                    rvContent.setVisibility(View.VISIBLE);
                    //家庭内容区域设备点击事件
                    contentAdapter.setOnItemClickListener(new HomeContentAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            //ToastUtil.showToast("点击了。。。" + footer.getDevice());
                        }

                        @Override
                        public void onItemLongClickListener(View view, int position) {

                        }
                    });
                }
            }

            @Override
            public void onItemLongClickListener(View view, int position) {

            }
        });
    }


    /**
     * 初始化加载界面
     */
    private void initEvent() {
        rlList.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                rvContentTwo.setVisibility(View.VISIBLE);
            }
        }, 1500);
        //获取左侧list列表数据
        list.clear();
        list.add(new TestVo("常用", 1));
        list.add(new TestVo("常用", 2));
        list.add(new TestVo("常用", 1));

        //获取右侧常用内容数据
        listContent2.clear();
        for (int i = 0; i < 2; i++) {
            listContent2.add(new Test2Vo("电动牙刷"));
            listContent2.add(new Test2Vo("电动牙刷"));
        }
        //获取右侧家庭设备数据
        contentList.clear();
        for (int i = 0; i < 2; i++) {
            contentList.add(new Test1Vo("办公室"));
        }

        //获取menu列表数据
        getHomeMenuList();

        //设置左侧列表
        listAdapter = new HomeListAdapter(getActivity(), list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        rvList.setLayoutManager(layoutManager);
        rvList.setAdapter(listAdapter);

        //设置常用列表,默认进入首页显示该列表
        twoAdapter = new HomeContentTwoAdapter(getActivity(), listContent2);
        rvContentTwo.setLayoutManager(new GridLayoutManager(getActivity(), 2, LinearLayoutManager.VERTICAL, false));
        rvContentTwo.setAdapter(twoAdapter);

        //设置刷新控件头部高度
        refreshLayout.setHeaderHeight(100);
        refreshLayout.setEnableLoadmoreWhenContentNotFull(true);//是否在列表不满一页时候开启上拉加载功能
        //设置头部样式
        refreshLayout.setRefreshHeader(new MaterialHeader(getActivity()));

        //设置FloatingActionButton
        fab.attachToRecyclerView(rvContent);
        fab.attachToRecyclerView(rvContentTwo);
    }

    /**
     * 获取家庭列表
     */
    private void getHomeMenuList() {
        RetrofitService.qdoApi.getFamilyList(ConfigUtils.user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HomeListVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HomeListVo homeListVo) {
                        if (homeListVo != null) {
                            if (homeListVo.getCode() == 200) {
                                menuList.clear();
                                for (HomeListVo.DataBean dataBean : homeListVo.getData()) {
                                    if (dataBean.getIs_opened() == 1) {
                                        tvSceneSetting.setText(dataBean.getFamily_name());
                                        ConfigUtils.family_locate = dataBean;
                                    }
                                    menuList.add(dataBean);
                                    emptylayout.hide();
                                }
                            } else if (homeListVo.getCode() == 400) {
                                emptylayout.showError();
                                SnackbarUtils.Short(mRootView, "获取失败").show();
                            } else {
                                SnackbarUtils.Short(mRootView, "连接超时").show();
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
                                emptylayout.showLoading(R.layout.view_loading, getString(R.string.device_scaning));
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
                startActivity(new Intent(getActivity(), AddDeviceActivity.class));
                getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.home_setting:
                //房间管理
                startActivity(new Intent(getActivity(), RoomManageActivity.class).putExtra("family_id", ConfigUtils.family_locate.getFamily_id()));
                getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.tv_scene_setting:
                //用户注册后默认是有一个房间的，不存在为空。
                shouPopupwindow();
                popupWindow.showAsDropDown(tvSceneSetting, 30, 0);
                break;
            case R.id.fab:
                //声音控制界面
//                startActivity(new Intent(getActivity(), LoginTestActivity.class));
//                startActivity(new Intent(getActivity(), ConfirmLightFlickerActivity.class));
//                startActivity(new Intent(getActivity(), MqttTestActivity.class));
//                startActivity(new Intent(getActivity(), DeviceWifiControlActivity.class));
//                startActivity(new Intent(getActivity(), DeviceConnectActivity.class));
                startActivity(new Intent(getActivity(), SetDeviceToRoomActivity.class));
                getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
        }
    }

}
