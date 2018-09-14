package com.qvd.smartswitch.activity.device.Ble;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseNoTipActivity;
import com.qvd.smartswitch.adapter.HomeMenuAdapter;
import com.qvd.smartswitch.adapter.SearchBleDeviceListAdapter;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.device.ScanResultVo;
import com.qvd.smartswitch.model.login.MessageVo;
import com.qvd.smartswitch.utils.BleManageUtils;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.ConfigUtils;
import com.qvd.smartswitch.utils.ToastUtil;
import com.qvd.smartswitch.widget.EmptyLayout;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DeviceSearchBleActivity extends BaseNoTipActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;

    /**
     * 当前蓝牙的名称
     */
    private String deviceNo;

    public final int REQUEST_ENABLE_BT = 110;

    private static final int REQUEST_CODE_OPEN_GPS = 1;

    private SearchBleDeviceListAdapter adapter;

    private List<BleDevice> list = new ArrayList<>();
    private BluetoothAdapter bluetoothAdapter;
    private View decorView;

    private EmptyLayout emptyView;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_search_ble_device;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }

    @Override
    protected void initData() {
        super.initData();
        emptyView = findViewById(R.id.empty_view);
        decorView = this.getWindow().getDecorView();
        tvCommonActionbarTitle.setText("搜索设备");
        deviceNo = getIntent().getStringExtra("deviceNo");
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BleManageUtils.getInstance().initBleManage();
        BleManageUtils.getInstance().setScanRule(deviceNo);
        emptyView.setShowLoadingButton(false);
        emptyView.setEmptyButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!bluetoothAdapter.isEnabled()) {
                    showOpenBlePopupWindow();
                } else {
                    startScan();
                }
            }
        });
        //判断是否支持蓝牙功能
        if (bluetoothAdapter == null) {
            ToastUtil.showToast("您的手机不支持蓝牙功能");
            return;
        }
        /**
         * 去打开蓝牙，否则直接扫描设备
         */
        if (!bluetoothAdapter.isEnabled()) {
            showOpenBlePopupWindow();
        } else {
            startScan();
        }
    }

    /**
     * 展示打开Wifi的popouwindow
     */
    private void showOpenBlePopupWindow() {
        new MaterialDialog.Builder(this)
                .content("您当前蓝牙未开启，不能扫描连接蓝牙设备,点击确定开启蓝牙。")
                .negativeText("取消")
                .positiveText("确定")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        finish();
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        bluetoothAdapter.enable();
                        startScan();
                    }
                })
                .show();
    }

    /**
     * 开始扫描
     */
    private void startScan() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startScanBle();
            }
        }, 1000);
    }

    /**
     * 开始扫描
     */
    private void startScanBle() {
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
                list.clear();
                emptyView.showLoading(R.layout.view_loading, "正在搜索,整个过程大约需要8秒");
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                if (bleDevice.getName() != null) {
                    //判断该设备是否被当前用户绑定，没有则添加到列表
                    IsDeviceBinding(bleDevice);
                }
            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {

            }
        });
        decorView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (list != null && list.size() > 0) {
                    emptyView.hide();
                    BleManager.getInstance().cancelScan();
                } else {
                    BleManager.getInstance().cancelScan();
                    emptyView.showEmpty();
                }
            }
        }, 8000);
    }

    /**
     * 判断当前设备是否被当前用户绑定
     */
    private void IsDeviceBinding(BleDevice bleDevice) {
        RetrofitService.qdoApi.isDeviceBinding(ConfigUtils.user_id, bleDevice.getMac())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MessageVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MessageVo messageVo) {
                        if (messageVo != null) {
                            if (messageVo.getCode() == 400) {
                                list.add(bleDevice);
                                recyclerview.setLayoutManager(new LinearLayoutManager(DeviceSearchBleActivity.this));
                                adapter = new SearchBleDeviceListAdapter(DeviceSearchBleActivity.this, list);
                                recyclerview.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                                adapter.setOnItemClickListener(new HomeMenuAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, int position) {
                                        //给设备连接传入设备信息
                                        BleDevice bleDevice = list.get(position);
                                        ScanResultVo resultVo = new ScanResultVo(bleDevice.getName(), CommonUtils.getDeviceName(bleDevice.getName()), bleDevice.getMac(), 1, -1, null);
                                        startActivity(new Intent(DeviceSearchBleActivity.this, DeviceBleConnectActivity.class)
                                                .putExtra("ScanResultVo", resultVo));
                                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                    }

                                    @Override
                                    public void onItemLongClickListener(View view, int position) {

                                    }
                                });
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_OPEN_GPS) {
            if (BleManageUtils.getInstance().checkGPSIsOpen(this)) {
                BleManageUtils.getInstance().setScanRule(deviceNo);
                startScan();
            }
        }
    }

    @OnClick({R.id.iv_common_actionbar_goback})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BleManageUtils.getInstance().DestroyBleManage();
    }
}
