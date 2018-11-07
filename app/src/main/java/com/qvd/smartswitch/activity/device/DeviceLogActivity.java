package com.qvd.smartswitch.activity.device;

import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;
import com.orhanobut.logger.Logger;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.adapter.LogDeviceListAdapter;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.device.DeviceLogVo;
import com.qvd.smartswitch.widget.EmptyLayout;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DeviceLogActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.smart_refresh)
    SmartRefreshLayout smartRefresh;

    private final List<DeviceLogVo.DataBean> list = new ArrayList<>();
    private final List<DeviceLogVo.DataBean> addMoreList = new ArrayList<>();
    private LogDeviceListAdapter adapter;

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 记录分页的数据位置
     */
    private int position;
    /**
     * 页数
     */
    private int page = 1;
    /**
     * 设备类型
     */
    private String device_type;

    private EmptyLayout empty_layout;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_device_log;
    }

    @Override
    protected void initData() {
        super.initData();
        empty_layout = findViewById(R.id.empty_layout);
        tvCommonActionbarTitle.setText(R.string.device_log_title);
        deviceId = getIntent().getStringExtra("device_id");
        device_type = getIntent().getStringExtra("device_type");
        initMode();
        getlastData();
        fab.attachToRecyclerView(recyclerview);
        empty_layout.setEmptyMessage(getString(R.string.device_log_log_empty), R.id.textViewMessage);
        empty_layout.setErrorButtonClickListener(v -> getlastData());
        smartRefresh.setHeaderHeight(100);
        smartRefresh.setFooterHeight(100);
        smartRefresh.setEnableLoadmoreWhenContentNotFull(true);//是否在列表不满一页时候开启上拉加载功能
        smartRefresh.setRefreshHeader(new ClassicsHeader(this).setAccentColor(getResources().getColor(R.color.home_setting_text)));
        smartRefresh.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale));
        refreshlayout();

    }

    private void initMode() {
        recyclerview.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        adapter = new LogDeviceListAdapter(list);
        recyclerview.setAdapter(adapter);
        recyclerview.setHasFixedSize(true);
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }

    /**
     * 刷新数据
     */
    private void refreshlayout() {
        //刷新数据
        smartRefresh.setOnRefreshListener(refreshlayout -> {
            //传入true表示刷新成功，false表示刷新失败
            if (list.size() > 0) {
                list.clear();
                page = 1;
                getlastData();
            }
        });
        //加载数据
        smartRefresh.setOnLoadmoreListener(refreshlayout -> {
            //传入true表示刷新成功，false表示刷新失败
            page++;
            getMoreData();
        });
    }

    /**
     * 获取加载数据
     */
    private void getMoreData() {
        Map<String, Object> map = new HashMap<>();
        map.put("device_id", deviceId);
        map.put("device_type", device_type);
        map.put("current_page", page);
        map.put("per_page_count", 10);

        RetrofitService.qdoApi.getDeviceLog(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DeviceLogVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(DeviceLogVo deviceLogVo) {
                        if (deviceLogVo.getData() == null) {
                            smartRefresh.finishLoadmoreWithNoMoreData();
                        } else {
                            addMoreList.clear();
                            addMoreList.addAll(deviceLogVo.getData());
                            position = list.size();
                            list.addAll(position, addMoreList);
                            adapter.notifyItemInserted(position);
                            smartRefresh.finishLoadmore(true);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e(e.getMessage());
                        smartRefresh.finishLoadmore(false);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 获取刷新数据
     */
    private void getlastData() {
        empty_layout.showLoading(R.mipmap.home_loading, getString(R.string.loading_view_message));
        Map<String, Object> map = new HashMap<>();
        map.put("device_id", deviceId);
        map.put("device_type", device_type);
        map.put("current_page", page);
        map.put("per_page_count", 10);
        RetrofitService.qdoApi.getDeviceLog(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DeviceLogVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(DeviceLogVo deviceLogVo) {
                        if (deviceLogVo != null) {
                            if (deviceLogVo.getCode() == 200) {
                                if (deviceLogVo.getData() != null) {
                                    list.clear();
                                    list.addAll(deviceLogVo.getData());
                                    empty_layout.hide();
                                    adapter.notifyDataSetChanged();
                                    smartRefresh.finishRefresh(true);
                                } else {
                                    smartRefresh.finishRefresh(true);
                                    empty_layout.showEmpty();
                                }
                            } else {
                                smartRefresh.finishRefresh(2000);
                                empty_layout.showError();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e(e.getMessage());
                        smartRefresh.finishRefresh(2000);
                        empty_layout.showError();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    @OnClick({R.id.iv_common_actionbar_goback, R.id.fab})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.fab:
                break;
        }
    }


}
