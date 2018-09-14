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
import com.qvd.smartswitch.utils.ToastUtil;
import com.qvd.smartswitch.widget.EmptyLayout;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.vivian.timelineitemdecoration.itemdecoration.DotItemDecoration;
import com.vivian.timelineitemdecoration.itemdecoration.SpanIndexListener;

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
    @BindView(R.id.emptylayout)
    EmptyLayout emptylayout;

    private List<DeviceLogVo.DataBean> list = new ArrayList<>();
    private List<DeviceLogVo.DataBean> addMoreList = new ArrayList<>();
    private LogDeviceListAdapter adapter;
    private DotItemDecoration mItemDecoration;

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

    @Override
    protected int setLayoutId() {
        return R.layout.activity_device_log;
    }

    @Override
    protected void initData() {
        super.initData();
        tvCommonActionbarTitle.setText("操作日志");
        deviceId = getIntent().getStringExtra("device_id");
        device_type = getIntent().getStringExtra("device_type");
        getfirstData();
        fab.attachToRecyclerView(recyclerview);
        smartRefresh.setHeaderHeight(100);
        smartRefresh.setFooterHeight(100);
        smartRefresh.setEnableLoadmoreWhenContentNotFull(true);//是否在列表不满一页时候开启上拉加载功能
        smartRefresh.setRefreshHeader(new MaterialHeader(this).setShowBezierWave(true));
        smartRefresh.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale));
        refreshlayout();
    }

    private void initMode() {
        mItemDecoration = new DotItemDecoration
                .Builder(this)
                .setOrientation(DotItemDecoration.VERTICAL) //设置方向
                .setItemStyle(DotItemDecoration.STYLE_DRAW)
                .setTopDistance(20)//设置距离顶部高度
                .setItemInterVal(10)
                .setItemPaddingLeft(10)
                .setItemPaddingRight(10)
                .setDotColor(getResources().getColor(R.color.capacity_tablayout_text_two))
                .setDotRadius(5)  //设置圆点的半径
                .setDotPaddingTop(20)//设置第一个点距离上部高度
                .setDotInItemOrientationCenter(false)
                .setLineColor(getResources().getColor(R.color.home_setting_text_two))
                .setLineWidth(1)//设置线的宽度
                .setEndText("END")  //设置结尾的文字
                .setTextColor(getResources().getColor(R.color.home_content_text_two))
                .setTextSize(10)//设置结尾文字大小
                .setDotPaddingText(2)
                .setBottomDistance(40)//结尾线的高度
                .create();

        mItemDecoration.setSpanIndexListener(new SpanIndexListener() {
            @Override
            public void onSpanIndexChange(View view, int spanIndex) {
                view.setBackgroundResource(spanIndex == 0 ? R.drawable.device_log_left : R.drawable.device_log_right);
            }
        });
        recyclerview.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerview.addItemDecoration(mItemDecoration);
        adapter = new LogDeviceListAdapter(DeviceLogActivity.this, list);
        recyclerview.setAdapter(adapter);
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 刷新数据
     */
    private void refreshlayout() {
        //刷新数据
        smartRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                //传入true表示刷新成功，false表示刷新失败
                if (list.size() > 0) {
                    list.clear();
                    page = 1;
                    getlastData();
                }
                refreshlayout.finishRefresh(2000, true);
            }
        });
        //加载数据
        smartRefresh.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                //传入true表示刷新成功，false表示刷新失败
                page++;
                getMoreData();
                refreshlayout.finishLoadmore(2000, true);
            }
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
                            emptylayout.hide();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e(e.getMessage());
                        ToastUtil.showToast("加载失败");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 获取刷新数据
     */
    private void getfirstData() {
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
                            if (deviceLogVo.getData() != null && deviceLogVo.getCode() == 200) {
                                list.clear();
                                list.addAll(deviceLogVo.getData());
                                initMode();
                                adapter.notifyDataSetChanged();
                                emptylayout.hide();
                            } else {
                                emptylayout.showEmpty();
                            }
                        } else {
                            emptylayout.showError();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e(e.getMessage());
                        emptylayout.showError();
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
                            if (deviceLogVo.getData() != null && deviceLogVo.getCode() == 200) {
                                list.clear();
                                list.addAll(deviceLogVo.getData());
                                adapter.notifyDataSetChanged();
                                emptylayout.hide();
                            } else {
                                emptylayout.showEmpty();
                            }
                        } else {
                            emptylayout.showError();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e(e.getMessage());
                        emptylayout.showError();
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
