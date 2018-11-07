package com.qvd.smartswitch.activity.device.Ble;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseHandler;
import com.qvd.smartswitch.activity.base.BaseNoTipActivity;
import com.qvd.smartswitch.activity.base.BaseRunnable;
import com.qvd.smartswitch.adapter.SearchBleDeviceListAdapter;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.device.ScanResultVo;
import com.qvd.smartswitch.model.login.MessageVo;
import com.qvd.smartswitch.utils.BleManageUtils;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.ConfigUtils;
import com.qvd.smartswitch.utils.ToastUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

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
    @BindView(R.id.smart_refresh)
    SmartRefreshLayout smartRefresh;

    private AVLoadingIndicatorView avi_loading;

    /**
     * 当前蓝牙的名称
     */
    private String deviceNo;

    private TextView tvState;

    public final int REQUEST_ENABLE_BT = 110;

    private static final int REQUEST_CODE_OPEN_GPS = 1;

    private SearchBleDeviceListAdapter adapter;

    private List<BleDevice> list = new ArrayList<>();
    private BluetoothAdapter bluetoothAdapter;

    private final MyHandler myHandler = new MyHandler(this);

    private static class MyHandler extends BaseHandler<DeviceSearchBleActivity> {

        protected MyHandler(DeviceSearchBleActivity reference) {
            super(reference);
        }

        @Override
        protected void handleMessage(DeviceSearchBleActivity reference, Message msg) {

        }
    }

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
        tvState = findViewById(R.id.tv_state);
        avi_loading = findViewById(R.id.avi_loading);
        tvCommonActionbarTitle.setText(R.string.device_search_ble_title);
        deviceNo = getIntent().getStringExtra("deviceNo");
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BleManageUtils.getInstance().initBleManage();
        BleManageUtils.getInstance().setScanRule(deviceNo);
        //判断是否支持蓝牙功能
        if (bluetoothAdapter == null) {
            ToastUtil.showToast(getString(R.string.common_phone_nonsupport_ble));
            return;
        }
        if (!bluetoothAdapter.isEnabled()) {
            showOpenBlePopupWindow();
        } else {
            startScan();
        }
        recyclerview.setLayoutManager(new LinearLayoutManager(DeviceSearchBleActivity.this));
        adapter = new SearchBleDeviceListAdapter(list);
        recyclerview.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.setOnItemClickListener((adapter, view, position) -> {
            //给设备连接传入设备信息
            BleDevice bleDevice1 = list.get(position);
            ScanResultVo resultVo = new ScanResultVo(bleDevice1.getName(), CommonUtils.getDeviceName(bleDevice1.getName()), bleDevice1.getMac(), 1, -1, null);
            startActivity(new Intent(DeviceSearchBleActivity.this, DeviceBleConnectActivity.class)
                    .putExtra("ScanResultVo", resultVo));
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        });
        myEmptyLayout.setTextViewMessage(getString(R.string.common_retry_scan_ble));
        myEmptyLayout.setOnClickListener(v -> {
            if (!bluetoothAdapter.isEnabled()) {
                showOpenBlePopupWindow();
            } else {
                startScan();
            }
        });
        smartRefresh.setHeaderHeight(100);
        smartRefresh.setFooterHeight(1);
        smartRefresh.setEnableHeaderTranslationContent(true);//是否上拉Footer的时候向上平移列表或者内容
        //设置头部样式
        smartRefresh.setRefreshHeader(new ClassicsHeader(this));
        smartRefresh.setOnRefreshListener(refreshlayout -> startScan());
    }

    /**
     * 展示打开Wifi的popouwindow
     */
    private void showOpenBlePopupWindow() {
        new MaterialDialog.Builder(this)
                .content(R.string.common_open_ble_content)
                .negativeText(R.string.common_cancel)
                .positiveText(R.string.common_confirm)
                .onNegative((dialog, which) -> finish())
                .onPositive((dialog, which) -> {
                    bluetoothAdapter.enable();
                    startScan();
                })
                .show();
    }

    /**
     * 开始扫描
     */
    private void startScan() {
        myHandler.postDelayed(new BaseRunnable(() -> startScanBle()), 200);
    }

    /**
     * 开始扫描
     */
    private void startScanBle() {
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
                smartRefresh.finishRefresh(true);
                list.clear();
                avi_loading.show();
                tvState.setText(R.string.common_searching);
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
                if (list.size() == 0) {
                    adapter.setEmptyView(myEmptyLayout);
                }
                avi_loading.hide();
                tvState.setText(R.string.common_search_end);
            }
        });
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
                                adapter.notifyDataSetChanged();
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
            if (BleManageUtils.getInstance().checkGPSIsOpen()) {
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
