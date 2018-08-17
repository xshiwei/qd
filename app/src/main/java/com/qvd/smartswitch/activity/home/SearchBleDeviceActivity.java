package com.qvd.smartswitch.activity.home;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.adapter.HomeMenuAdapter;
import com.qvd.smartswitch.adapter.SearchBleDeviceListAdapter;
import com.qvd.smartswitch.model.device.ScanResultVo;
import com.qvd.smartswitch.utils.BleManageUtils;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.ToastUtil;
import com.qvd.smartswitch.widget.EmptyLayout;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class SearchBleDeviceActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.empty_view)
    EmptyLayout emptyView;
    @BindView(R.id.avi_loading)
    AVLoadingIndicatorView aviLoading;
    @BindView(R.id.tv_retry_scan)
    TextView tvRetryScan;

    /**
     * 当前蓝牙的名称
     */
    private String deviceNo;

    public final int REQUEST_ENABLE_BT = 110;

    private static final int REQUEST_CODE_OPEN_GPS = 1;

    private SearchBleDeviceListAdapter adapter;

    private List<BleDevice> list = new ArrayList<>();

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
        tvCommonActionbarTitle.setText("搜索设备");
        deviceNo = getIntent().getStringExtra("deviceNo");
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //判断是否支持蓝牙功能
        if (bluetoothAdapter == null) {
            ToastUtil.showToast("您的手机不支持蓝牙功能");
            return;
        }
        /**
         * 去打开蓝牙，否则直接扫描设备
         */
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        startScan();
    }

    /**
     * 开始扫描
     */
    private void startScan() {
        BleManageUtils.getInstance().initBleManage();
        BleManageUtils.getInstance().setScanRule(deviceNo);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startScanBle();
            }
        }, 1000);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SearchBleDeviceListAdapter(this, list);
        recyclerview.setAdapter(adapter);
        adapter.setOnItemClickListener(new HomeMenuAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //给设备连接传入设备信息
                BleDevice bleDevice = list.get(position);
                ScanResultVo resultVo = new ScanResultVo(bleDevice.getName(), CommonUtils.getDeviceName(bleDevice.getName()), bleDevice.getMac(), 1);
                startActivity(new Intent(SearchBleDeviceActivity.this, DeviceConnectActivity.class)
                        .putExtra("ScanResultVo", resultVo));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }

            @Override
            public void onItemLongClickListener(View view, int position) {

            }
        });
    }

    /**
     * 开始扫描
     */
    private void startScanBle() {
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
                list.clear();
                aviLoading.show();
                tvRetryScan.setVisibility(View.GONE);
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                list.add(bleDevice);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
                if (aviLoading != null) {
                    aviLoading.hide();
                }
                if (tvRetryScan != null) {
                    tvRetryScan.setVisibility(View.VISIBLE);
                }
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

    @OnClick({R.id.iv_common_actionbar_goback, R.id.tv_retry_scan})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.tv_retry_scan:
                startScan();
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        BleManager.getInstance().cancelScan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BleManageUtils.getInstance().DestroyBleManage();
    }
}
