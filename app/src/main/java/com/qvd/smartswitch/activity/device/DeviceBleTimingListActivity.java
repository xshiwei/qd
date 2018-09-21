package com.qvd.smartswitch.activity.device;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;
import com.orhanobut.logger.Logger;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.activity.qsTwo.QsTwoTimingActivity;
import com.qvd.smartswitch.adapter.BleTimingAdapter;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.device.DeviceTimingVo;
import com.qvd.smartswitch.model.login.MessageVo;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.ToastUtil;
import com.qvd.smartswitch.widget.EmptyLayout;
import com.qvd.smartswitch.widget.MyProgressDialog;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DeviceBleTimingListActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.tv_add_timing)
    TextView tvAddTiming;
    @BindView(R.id.emptylayout)
    EmptyLayout emptyLayout;
    @BindView(R.id.smart_refresh)
    SmartRefreshLayout smartRefreshLayout;

    private BleTimingAdapter adapter;
    private List<DeviceTimingVo.DataBean> list = new ArrayList<>();
    private BleDevice bledevice;
    private String device_id;
    private DeviceTimingVo.DataBean seleteDataBean;
    private MyProgressDialog progressDialog;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_device_ble_timing_list;
    }

    @Override
    protected void initData() {
        super.initData();
        tvCommonActionbarTitle.setText("蓝牙开关定时");
        bledevice = getIntent().getParcelableExtra("bledevice");
        device_id = getIntent().getStringExtra("device_id");
        progressDialog = MyProgressDialog.createProgressDialog(this, 5000,
                new MyProgressDialog.OnTimeOutListener() {
                    @Override
                    public void onTimeOut(ProgressDialog dialog) {
                        dialog.dismiss();
                        ToastUtil.showToast("取消失败");
                    }
                });
        //设置刷新控件头部高度
        smartRefreshLayout.setHeaderHeight(100);
        smartRefreshLayout.setFooterHeight(1);
        smartRefreshLayout.setEnableHeaderTranslationContent(true);
        //设置头部样式
        smartRefreshLayout.setRefreshHeader(new ClassicsHeader(this));
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getData();
                smartRefreshLayout.finishRefresh(2000, true);
            }
        });

    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new BleTimingAdapter(this, list);
        recyclerview.setAdapter(adapter);
        adapter.setOnItemClickListener(new BleTimingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                seleteDataBean = list.get(position);
                if (seleteDataBean.getTiming_state().equals("1")) {
                    showCancelDialog();
                } else {
                    showDeleteDialog();
                }
            }

            @Override
            public void onItemLongClickListener(View view, int position) {

            }
        });
    }

    /**
     * 展示删除的dialog
     */
    private void showDeleteDialog() {
        new MaterialDialog.Builder(this)
                .content("您确定要删除这条定时记录吗？")
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
                        if (BleManager.getInstance().isConnected(bledevice)) {
                            deleteTiming();
                        } else {
                            ToastUtil.showToast("设备已掉线");
                        }
                    }
                }).show();
    }


    /**
     * 删除设备定时
     */
    private void deleteTiming() {
        RetrofitService.qdoApi.deleteDeviceTiming(seleteDataBean.getTiming_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MessageVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MessageVo messageVo) {
                        if (messageVo.getCode() == 200) {
                            list.remove(seleteDataBean);
                            adapter.notifyDataSetChanged();
                            ToastUtil.showToast("删除成功");
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
     * 展示取消设备的dialog
     */
    private void showCancelDialog() {
        new MaterialDialog.Builder(this)
                .content("您确定要取消定时吗？")
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
                        writeToBle();
                    }
                }).show();
    }

    /**
     * 更新定时的状态
     */
    private void updateTimingState() {
        RetrofitService.qdoApi.updateDeviceTimingState(seleteDataBean.getTiming_id(), "0")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MessageVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MessageVo messageVo) {
                        if (messageVo.getCode() == 200) {
                            getData();
                            ToastUtil.showToast("取消成功");
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
     * 取消设备定时
     */
    private void writeToBle() {
        if (bledevice == null) {
            ToastUtil.showToast("设备未连接，不能取消定时");
            return;
        }
        progressDialog.setMessage("正在设置");
        progressDialog.show();
        BleManager.getInstance().write(bledevice, "0000fff0-0000-1000-8000-00805f9b34fb", "0000fff7-0000-1000-8000-00805f9b34fb", HexUtil.hexStringToBytes("FE030408FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF"), new BleWriteCallback() {
            @Override
            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                progressDialog.dismiss();
                updateTimingState();
            }

            @Override
            public void onWriteFailure(BleException exception) {
                Logger.e("write" + exception.toString());
            }
        });
    }


    /**
     * 获取数据
     */
    private void getData() {
        RetrofitService.qdoApi.getDeviceTimingInfo(device_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DeviceTimingVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(DeviceTimingVo deviceTimingVo) {
                        if (deviceTimingVo.getCode() == 200) {
                            if (deviceTimingVo.getData() != null) {
                                list.clear();
                                list.addAll(deviceTimingVo.getData());
                                adapter.notifyDataSetChanged();
                                emptyLayout.hide();
                            } else {
                                emptyLayout.showEmpty();
                            }
                        } else {
                            emptyLayout.showError();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        emptyLayout.showError();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @OnClick({R.id.iv_common_actionbar_goback, R.id.tv_add_timing})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.tv_add_timing:
                boolean b = false;
                String s = "";
                for (DeviceTimingVo.DataBean dataBean : list) {
                    if (dataBean.getTiming_state().equals("1")) {
                        b = true;
                        s = dataBean.getTiming_id();
                    }
                }
                startActivity(new Intent(this, QsTwoTimingActivity.class)
                        .putExtra("bledevice", bledevice)
                        .putExtra("device_id", device_id)
                        .putExtra("state", b)
                        .putExtra("timing_id", s));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
        }
    }
}
