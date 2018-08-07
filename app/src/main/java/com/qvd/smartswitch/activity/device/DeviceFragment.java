package com.qvd.smartswitch.activity.device;


import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleMtuChangedCallback;
import com.clj.fastble.callback.BleRssiCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.orhanobut.logger.Logger;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseFragment;
import com.qvd.smartswitch.adapter.DeviceListAdapter;
import com.qvd.smartswitch.db.DeviceNickNameDaoOpe;
import com.qvd.smartswitch.model.DeviceNickNameVo;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.SnackbarUtils;
import com.qvd.smartswitch.widget.EmptyLayout;
import com.qvd.smartswitch.widget.MyPopupWindowThree;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by Administrator on 2018/4/2.
 */

public class DeviceFragment extends BaseFragment {


    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    private static final int REQUEST_CODE_OPEN_GPS = 1;
    @BindView(R.id.tv_device_text)
    TextView tvDeviceText;
    @BindView(R.id.tv_device_scan)
    TextView tvDeviceScan;
    @BindView(R.id.rl_device_bar)
    RelativeLayout rlDeviceBar;
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;


    private DeviceListAdapter adapter;
    public final int REQUEST_ENABLE_BT = 110;

    private BleDevice mBleDevice = null;
    private EmptyLayout emptyView;

    private MyPopupWindowThree popupWindowName;

    private ProgressBar mProgressBar;
    private TextView mTextView;
    private int mPosition;


    public static DeviceFragment newInstance(String param1) {
        DeviceFragment fragment = new DeviceFragment();
        Bundle args = new Bundle();
        args.putString("agrs1", param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_device;
    }


    @Override
    protected void initData() {
        super.initData();
        emptyView = mRootView.findViewById(R.id.empty_view);
        checkPermission();
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 1000)//设置重连次数以及相隔时间
                .setSplitWriteNum(20)//设置分包发送的默认字节数
                .setMaxConnectCount(7)//设置最大连接数
                .setOperateTimeout(3000)//设置操作超时时间
                .setConnectOverTime(5000);//设置连接超时时间
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new DeviceListAdapter(getActivity());
        recyclerview.setAdapter(adapter);
    }

    @Override
    protected void initView() {
        adapter.setOnDeviceClickListener(new DeviceListAdapter.OnDeviceClickListener() {
            @Override
            public void onConnect(View v, View v1, View v2, int position) throws Exception {
                BleDevice bleDevice = adapter.getList().get(position);
                mPosition = position;
                mProgressBar = (ProgressBar) v1;
                mTextView = (TextView) v2;
                if (BleManager.getInstance().isConnected(bleDevice)) {
                    if (CommonUtils.isEmptyString(DeviceNickNameDaoOpe.queryOne(getActivity(), CommonUtils.getMac(bleDevice.getMac())).getDeviceNickname())) {
                        showPopupwindowName();
                        popupWindowName.showAtLocation(mRootView, Gravity.CENTER, 0, 0);
                        mBleDevice = bleDevice;
                    } else {
                        Intent intent = new Intent(getActivity(), DeviceControlTwoActivity.class);
                        intent.putExtra("bledevice", bleDevice);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    }
                } else {
                    connect(bleDevice);
                }
            }

            @Override
            public void onDisConnect(View v, int position) {
                BleDevice bleDevice = adapter.getList().get(position);
                if (BleManager.getInstance().isConnected(bleDevice)) {
                    BleManager.getInstance().disconnect(bleDevice);
                    BluetoothGatt bluetoothGatt = BleManager.getInstance().getBluetoothGatt(bleDevice);
                    if (bluetoothGatt != null) {
                        bluetoothGatt.disconnect();
                        bluetoothGatt.close();
                    }

                }
            }
        });

    }

    /**
     * 显示更换名字的popupwindow
     */
    private void showPopupwindowName() {
        popupWindowName = new MyPopupWindowThree(getActivity(), "设置设备名称", "", new MyPopupWindowThree.IPopupWindowListener() {
            @Override
            public void cancel() {
                popupWindowName.dismiss();
                CommonUtils.closeSoftKeyboard(getActivity());
            }

            @Override
            public void confirm() {
                EditText etEditText = popupWindowName.getEtEditText();
                DeviceNickNameVo deviceNickNameVo = new DeviceNickNameVo(DeviceNickNameDaoOpe.queryOne(getActivity(), CommonUtils.getMac(mBleDevice.getMac())).getId(), CommonUtils.getMac(mBleDevice.getMac()),
                        mBleDevice.getName(), CommonUtils.getDate(), etEditText.getText().toString().trim(), null, null);
                DeviceNickNameDaoOpe.updateData(getActivity(), deviceNickNameVo);
                int postion = adapter.getPostion(mBleDevice);
                adapter.notifyItemChanged(postion);
                popupWindowName.dismiss();
                CommonUtils.closeSoftKeyboard(getActivity());
            }
        });
        final EditText etEditText = popupWindowName.getEtEditText();
        final TextView tvConfirm = popupWindowName.getTvConfirm();
        final TextView tvError = popupWindowName.getTvError();
        if (etEditText.getText().toString().equals("")) {
            tvConfirm.setEnabled(false);
            tvConfirm.setTextColor(etEditText.getResources().getColor(R.color.home_setting_text_three));
        }

        etEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 20) {
                    tvError.setVisibility(View.VISIBLE);
                    tvError.setText("长度超过最大");
                    tvConfirm.setEnabled(false);
                    tvConfirm.setTextColor(getResources().getColor(R.color.home_setting_text_three));
                } else if (s.toString().length() == 0) {
                    tvConfirm.setEnabled(false);
                    tvConfirm.setTextColor(getResources().getColor(R.color.home_setting_text_three));
                    etEditText.setCursorVisible(false);
                } else {
                    tvError.setVisibility(View.GONE);
                    tvConfirm.setEnabled(true);
                    tvConfirm.setTextColor(getResources().getColor(R.color.popupwindow_confirm_text));
                }
            }
        });
    }

    @Override
    public void onResume() {
        //这里需要判断有没删除
        super.onResume();
        List<BleDevice> list = adapter.getList();
        for (int i = 0; i < list.size(); i++) {
            DeviceNickNameVo deviceNickNameVo = DeviceNickNameDaoOpe.queryOne(getActivity(), CommonUtils.getMac(list.get(i).getMac()));
            if (deviceNickNameVo == null) {
                if (BleManager.getInstance().isConnected(list.get(i))) {
                    BleManager.getInstance().disconnect(list.get(i));
                    adapter.removeDevice(list.get(i));
                }
            }
        }
        updataDeviceList();
        //showConnect();
    }

    /**
     * 显示已连接的设备
     */
    private void showConnect() {
        List<BleDevice> deviceList = BleManager.getInstance().getAllConnectedDevice();
        adapter.clearConnectedDevice();
        for (BleDevice bleDevice : deviceList) {
            adapter.addDevice(bleDevice);
        }
        adapter.notifyDataSetChanged();
    }


    /**
     * 定时刷新列表
     */
    @SuppressLint("CheckResult")
    private void updataDeviceList() {
        Observable.interval(2, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<Long>bindUntilEvent(FragmentEvent.PAUSE))
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        List<BleDevice> allConnectedDevice = BleManager.getInstance().getAllConnectedDevice();
        for (BleDevice bleDevice : allConnectedDevice) {
            BluetoothGatt bluetoothGatt = BleManager.getInstance().getBluetoothGatt(bleDevice);
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * 设置扫描规则
     */
    private void setScanRule() {
        String[] uuids;
        String str_uuid = "";
        if (TextUtils.isEmpty(str_uuid)) {
            uuids = null;
        } else {
            uuids = str_uuid.split(",");
        }
        UUID[] serviceUuids = null;
        if (uuids != null && uuids.length > 0) {
            serviceUuids = new UUID[uuids.length];
            for (int i = 0; i < uuids.length; i++) {
                String name = uuids[i];
                String[] components = name.split("-");
                if (components.length != 5) {
                    serviceUuids[i] = null;
                } else {
                    serviceUuids[i] = UUID.fromString(uuids[i]);
                }
            }
        }

        String[] names;
        String str_name = "SimpleBLEPeripheral,Qevdo,QS,Qevdo-QS02";
        if (TextUtils.isEmpty(str_name)) {
            names = null;
        } else {
            names = str_name.split(",");
        }
        String mac = "";

        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
                .setServiceUuids(serviceUuids)      // 只扫描指定的服务的设备，可选
                .setDeviceName(true, names)   // 只扫描指定广播名的设备，可选
                .setDeviceMac(mac)                  // 只扫描指定mac的设备，可选
                .setAutoConnect(true)      // 连接时的autoConnect参数，可选，默认false
                .setScanTimeOut(6000)              // 扫描超时时间，可选，默认10秒
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);
    }

    /**
     * 开始扫描
     */
    private void startScan() {
        List<BleDevice> allConnectedDevice = BleManager.getInstance().getAllConnectedDevice();
        for (BleDevice bleDevice : allConnectedDevice) {
            BleManager.getInstance().disconnect(bleDevice);
            BluetoothGatt bluetoothGatt = BleManager.getInstance().getBluetoothGatt(bleDevice);
            bluetoothGatt.close();
        }
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
                adapter.clearScanDevice();
                tvDeviceScan.setText(R.string.device_stop_scan);
                emptyView.showLoading(R.layout.view_loading, getString(R.string.device_scaning));
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                boolean b = DeviceNickNameDaoOpe.queryIs(getActivity(), CommonUtils.getMac(bleDevice.getMac()));
                if (!b) {
                    DeviceNickNameVo deviceNickNameVo = new DeviceNickNameVo(null, CommonUtils.getMac(bleDevice.getMac()), bleDevice.getName(),
                            CommonUtils.getDate(), null, null, null);
                    DeviceNickNameDaoOpe.saveData(getActivity(), deviceNickNameVo);
                }
                adapter.addDevice(bleDevice);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
                if (isAdded()) {
                    tvDeviceScan.setText(getString(R.string.device_scan));
                }
                if (scanResultList != null && scanResultList.size() > 0) {
                    emptyView.hide();
                } else {
                    emptyView.showEmpty();
                }
            }
        });
    }


    /**
     * 连接
     *
     * @param bleDevice
     */
    private void connect(BleDevice bleDevice) {
        BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
            @Override
            public void onStartConnect() {
                mProgressBar.setVisibility(View.VISIBLE);
                mTextView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                mProgressBar.setVisibility(View.GONE);
                mTextView.setVisibility(View.GONE);
                SnackbarUtils.Short(coordinatorLayout, getString(R.string.device_connect_fail)).show();
                final BluetoothGatt bluetoothGatt = BleManager.getInstance().getBluetoothGatt(bleDevice);
                if (bluetoothGatt != null) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            bluetoothGatt.disconnect();
                            bluetoothGatt.close();
                        }
                    }, 100);
                }
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                mProgressBar.setVisibility(View.GONE);
                mTextView.setVisibility(View.GONE);
                SnackbarUtils.Short(coordinatorLayout, "连接成功").show();
                int postion = adapter.getPostion(bleDevice);
                adapter.notifyItemChanged(postion);
                readRssi(bleDevice);
                setMtu(bleDevice, 512);
            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, final BluetoothGatt gatt, int status) {
                mProgressBar.setVisibility(View.GONE);
                mTextView.setVisibility(View.GONE);
                int postion = adapter.getPostion(bleDevice);
                adapter.notifyItemChanged(postion);
                SnackbarUtils.Short(coordinatorLayout, "断开连接").show();
                if (!isActiveDisConnected) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            gatt.connect();
                        }
                    }, 100);
                }
            }
        });
    }

    /**
     * 获取设备的信号强度Rssi
     *
     * @param bleDevice
     */
    private void readRssi(BleDevice bleDevice) {
        BleManager.getInstance().readRssi(bleDevice, new BleRssiCallback() {
            @Override
            public void onRssiFailure(BleException exception) {
                Logger.e("onRssiFailure" + exception.toString());
            }

            @Override
            public void onRssiSuccess(int rssi) {
                Logger.e("onRssiSuccess: " + rssi);
            }
        });
    }

    /**
     * 设置最大传输单元MTU
     *
     * @param bleDevice
     * @param mtu
     */
    private void setMtu(BleDevice bleDevice, int mtu) {
        BleManager.getInstance().setMtu(bleDevice, mtu, new BleMtuChangedCallback() {
            @Override
            public void onSetMTUFailure(BleException exception) {
                Logger.e("onsetMTUFailure" + exception.toString());
            }

            @Override
            public void onMtuChanged(int mtu) {
                Logger.e("onMtuChanged: " + mtu);
            }
        });
    }

    private boolean checkGPSIsOpen() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null)
            return false;
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_OPEN_GPS) {
            if (checkGPSIsOpen()) {
                setScanRule();
                startScan();
            }
        }
    }


    @OnClick(R.id.tv_device_scan)
    public void onViewClicked() {
        if (tvDeviceScan.getText().toString().equals(getString(R.string.device_scan))) {
            BleManager.getInstance().disconnectAllDevice();
            checkPermission();
        } else {
            BleManager.getInstance().cancelScan();
        }
    }

    private void checkPermission() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //判断是否支持蓝牙功能
        if (bluetoothAdapter == null) {
            SnackbarUtils.Short(coordinatorLayout, "您的手机不支持蓝牙功能").show();
            return;
        }
        /**
         * 去打开蓝牙，否则直接扫描设备
         */
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            setScanRule();
            startScan();
        }
    }


}
