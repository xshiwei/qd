package com.qvd.smartswitch.activity.qsTwo;


import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.content.Intent;
import android.graphics.Color;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;
import com.nightonke.boommenu.BoomButtons.TextOutsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.orhanobut.logger.Logger;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseHandler;
import com.qvd.smartswitch.activity.base.BaseNoTipActivity;
import com.qvd.smartswitch.activity.base.BaseRunnable;
import com.qvd.smartswitch.activity.device.DeviceBleTimingListActivity;
import com.qvd.smartswitch.activity.device.DeviceLogActivity;
import com.qvd.smartswitch.activity.device.DeviceSplashActivity;
import com.qvd.smartswitch.model.device.ScanResultVo;
import com.qvd.smartswitch.utils.BleManageUtils;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.OnMultiClickListener;
import com.qvd.smartswitch.utils.ToastUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/4/3.
 */

public class QsTwoControlActivity extends BaseNoTipActivity {

    private static final int REQUEST_ENABLE_BT = 101;
    @BindView(R.id.iv_device_control_goback)
    ImageView ivDeviceControlGoback;
    @BindView(R.id.tv_device_control_title)
    TextView tvDeviceControlTitle;
    @BindView(R.id.iv_device_control_more)
    ImageView ivDeviceControlMore;
    @BindView(R.id.ll_log)
    LinearLayout llLog;
    @BindView(R.id.ll_sounding)
    LinearLayout llSounding;
    @BindView(R.id.ll_timing)
    LinearLayout llTiming;
    @BindView(R.id.rl_bottom)
    RelativeLayout rlBottom;
    /**
     * 灯1未开启
     */
    private boolean isStateOne = false;

    private BleDevice mBledevice;

    private ScanResultVo resultVo;

    private TextView tvConnectText;

    private BluetoothAdapter bluetoothAdapter;

    private ImageView ivLightOne;
    private ImageView ivSwitchOne;
    private AVLoadingIndicatorView avi_loading;

    /**
     * 代表共享进来的权限
     */
    private int is_control;
    private BluetoothGatt connect;

    private SmartRefreshLayout smartRefresh;

    /**
     * 定义失败次数，当达到一定的失败次数时，提示用户重启蓝牙。
     */
    private int count = 0;

    private final MyHandler myHandler = new MyHandler(this);

    private static class MyHandler extends BaseHandler<QsTwoControlActivity> {

        MyHandler(QsTwoControlActivity reference) {
            super(reference);
        }

        @Override
        protected void handleMessage(QsTwoControlActivity reference, Message msg) {
        }
    }

    @Override
    protected int setLayoutId() {
        return R.layout.activity_qstwo_control;
    }


    @SuppressLint("CheckResult")
    @Override
    protected void initData() {
        super.initData();
        initControlView();
        Glide.with(this).load(R.mipmap.device_light_off).into(ivLightOne);
        Glide.with(this).load(R.mipmap.device_switch_off).into(ivSwitchOne);
        avi_loading.hide();
        if (resultVo.getIsFirstConnect() == 1) {
            startActivity(new Intent(this, DeviceSplashActivity.class)
                    .putExtra("scanResult", resultVo));
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            finish();
            return;
        } else if (is_control == 0) {
            //共享进来的,仅可查看
            ivDeviceControlMore.setVisibility(View.GONE);
            rlBottom.setVisibility(View.GONE);
            tvConnectText.setVisibility(View.GONE);
            smartRefresh.setEnableRefresh(false);
        } else {
            ivSwitchOne.setOnClickListener(new OnMultiClickListener() {
                @Override
                public void onMultiClick(View v) {
                    if (isStateOne) {
                        //关灯
                        writeToBleOne(String.valueOf("fe010010ffffffffffffffffffffffffffffffff"));
                    } else {
                        //开灯
                        writeToBleOne(String.valueOf("fe010011ffffffffffffffffffffffffffffffff"));
                    }
                }
            });
            ivLightOne.setOnClickListener(v -> {
                if (isStateOne) {
                    //关灯
                    writeToBleOne(String.valueOf("fe010010ffffffffffffffffffffffffffffffff"));
                } else {
                    //开灯
                    writeToBleOne(String.valueOf("fe010011ffffffffffffffffffffffffffffffff"));
                }
            });
            //设置刷新控件头部高度
            smartRefresh.setHeaderHeight(100);
            smartRefresh.setFooterHeight(1);
            smartRefresh.setEnableRefresh(true);
            smartRefresh.setEnableHeaderTranslationContent(true);
            smartRefresh.setEnableFooterTranslationContent(false);
            //设置头部样式
            smartRefresh.setRefreshHeader(new ClassicsHeader(this).setAccentColor(getResources().getColor(R.color.white)));
            smartRefresh.setOnRefreshListener(refreshlayout -> {
                if (bluetoothAdapter.isEnabled()) {
                    if (!BleManager.getInstance().isConnected(resultVo.getDeviceMac())) {
                        connectDevice();
                    } else {
                        getNotify();
                        smartRefresh.finishRefresh();
                    }
                } else {
                    smartRefresh.finishRefresh();
                    showOpenBlePopupWindow();
                }
            });
        }
        if (!bluetoothAdapter.isEnabled()) {
            showOpenBlePopupWindow();
        }

        showTip();
    }

    private void initControlView() {
        tvConnectText = findViewById(R.id.tv_connect_text);
        ivLightOne = findViewById(R.id.iv_light_one);
        ivSwitchOne = findViewById(R.id.iv_switch_one);
        avi_loading = findViewById(R.id.avi_loading);
        smartRefresh = findViewById(R.id.smart_refresh);
        resultVo = (ScanResultVo) getIntent().getSerializableExtra("scanResult");
        is_control = getIntent().getIntExtra("is_control", -1);
        tvDeviceControlTitle.setText(resultVo.getDeviceName());
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BleManageUtils.getInstance().initBleManage();
    }

    /**
     * 展示打开Wifi的popouwindow
     */
    private void showOpenBlePopupWindow() {
        new MaterialDialog.Builder(this)
                .content(R.string.common_open_ble_content)
                .negativeText(R.string.common_cancel)
                .positiveText(R.string.common_confirm)
                .onNegative((dialog, which) -> {
                    ToastUtil.showToast(getString(R.string.common_open_ble_can_control_device));
                    tvConnectText.setText(R.string.common_ble_not_open);
                    avi_loading.hide();
                })
                .onPositive((dialog, which) -> {
                    if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    }
                })
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (resultVo != null && bluetoothAdapter.isEnabled() && !BleManager.getInstance().isConnected(resultVo.getDeviceMac())) {
            connectDevice();
        }
        if (mBledevice != null) {
            getNotify();
        }
    }

    /**
     * 展示提示
     */
    private void showTip() {
        Observable.intervalRange(1, 1, 3, 0, TimeUnit.MINUTES)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Long aLong) {
                        new MaterialDialog.Builder(QsTwoControlActivity.this)
                                .content(R.string.common_use_timeout_tip)
                                .negativeText(R.string.comon_not_tip)
                                .positiveText(R.string.common_confirm)
                                .onNegative((dialog, which) -> {

                                })
                                .onPositive((dialog, which) -> showTip())
                                .canceledOnTouchOutside(false)
                                .show();
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
     * 连接设备
     */
    private void connectDevice() {
        connect = BleManager.getInstance().connect(resultVo.getDeviceMac(), new BleGattCallback() {
            @Override
            public void onStartConnect() {
                smartRefresh.finishRefresh(true);
                avi_loading.show();
                avi_loading.setVisibility(View.VISIBLE);
                tvConnectText.setText(R.string.common_device_is_connecting);
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                avi_loading.hide();
                if (exception.getCode() == 100) {
                    tvConnectText.setText(R.string.common_device_connect_timeout);
                } else {
                    if (is_control == 1) {
                        tvConnectText.setText(R.string.common_device_connect_error_one);
                    } else {
                        tvConnectText.setText(R.string.common_device_connect_error_two);
                    }
                }
                count++;
                if (count > 2) {
                    showRestartBleDialog();
                }
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                count = 0;
                mBledevice = bleDevice;
                tvConnectText.setText(R.string.common_device_connect_success);
                avi_loading.hide();
                myHandler.post(new BaseRunnable(() -> getNotify()));
            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {
                avi_loading.hide();
                tvConnectText.setText(R.string.common_device_prepare_retry_connect);
                Logger.e("error==" + isActiveDisConnected);
                if (!isActiveDisConnected && !QsTwoControlActivity.this.isDestroyed()) {
                    myHandler.postDelayed(new BaseRunnable(() -> connectDevice()), 1000);
                }
            }
        });
    }

    /**
     * 展示重启蓝牙的dialog
     */
    private void showRestartBleDialog() {
        new MaterialDialog.Builder(this)
                .content(R.string.common_connect_fail_retry_open_ble)
                .negativeText(R.string.common_cancel)
                .positiveText(R.string.common_confirm)
                .onPositive((dialog, which) -> {
                    if (bluetoothAdapter.disable()) {
                        myHandler.postDelayed(new BaseRunnable(() -> {
                            if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
                                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                            }
                        }), 200);
                    }
                })
                .show();
    }

    private void sendMessage() {
        BleManager.getInstance().write(mBledevice, "0000fff0-0000-1000-8000-00805f9b34fb", "0000fff6-0000-1000-8000-00805f9b34fb", HexUtil.hexStringToBytes("FE0001FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF"), true, new BleWriteCallback() {
            @Override
            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                Logger.e("send success");
            }

            @Override
            public void onWriteFailure(BleException exception) {
            }
        });
    }


    /**
     * 获取通知
     */
    @SuppressLint("CheckResult")
    private void getNotify() {
        BleManager.getInstance().notify(mBledevice, "0000fff0-0000-1000-8000-00805f9b34fb", "0000fff6-0000-1000-8000-00805f9b34fb", new BleNotifyCallback() {
            @Override
            public void onNotifySuccess() {
                Logger.e("notify success");
                sendMessage();
            }

            @Override
            public void onNotifyFailure(BleException exception) {
                Logger.e("notify exception" + exception.getDescription());
            }

            @Override
            public void onCharacteristicChanged(byte[] data) {
                String s = HexUtil.formatHexString(data, false);
                switch (s) {
                    case "1020":
                        isStateOne = false;
                        if (!QsTwoControlActivity.this.isDestroyed()) {
                            Glide.with(QsTwoControlActivity.this).load(R.mipmap.device_light_off).into(ivLightOne);
                            Glide.with(QsTwoControlActivity.this).load(R.mipmap.device_switch_off).into(ivSwitchOne);
                        }
                        break;
                    case "1120":
                        isStateOne = true;
                        if (!QsTwoControlActivity.this.isDestroyed()) {
                            Glide.with(QsTwoControlActivity.this).load(R.mipmap.device_light_on).into(ivLightOne);
                            Glide.with(QsTwoControlActivity.this).load(R.mipmap.device_switch_on).into(ivSwitchOne);
                        }
                        break;
                    case "1021":
                        isStateOne = false;
                        if (!QsTwoControlActivity.this.isDestroyed()) {
                            Glide.with(QsTwoControlActivity.this).load(R.mipmap.device_light_off).into(ivLightOne);
                            Glide.with(QsTwoControlActivity.this).load(R.mipmap.device_switch_off).into(ivSwitchOne);
                        }
                        break;
                    case "1121":
                        isStateOne = true;
                        if (!QsTwoControlActivity.this.isDestroyed()) {
                            Glide.with(QsTwoControlActivity.this).load(R.mipmap.device_light_on).into(ivLightOne);
                            Glide.with(QsTwoControlActivity.this).load(R.mipmap.device_switch_on).into(ivSwitchOne);
                        }
                        break;
                }
            }
        });
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.light_background_end).statusBarDarkFont(false).init();
    }

    /**
     * 向灯泡一写入数据
     *
     * @param s
     */
    synchronized private void writeToBleOne(final String s) {
        if (mBledevice == null) {
            return;
        }
        myHandler.postDelayed(new BaseRunnable(() -> BleManager.getInstance().write(mBledevice, "0000fff0-0000-1000-8000-00805f9b34fb", "0000fff6-0000-1000-8000-00805f9b34fb", HexUtil.hexStringToBytes(s), true, new BleWriteCallback() {
            @Override
            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                String text;
                if (!isStateOne) {
                    text = getString(R.string.common_manual_open_one_light);
                    isStateOne = true;
                    if (!QsTwoControlActivity.this.isDestroyed()) {
                        Glide.with(QsTwoControlActivity.this).load(R.mipmap.device_switch_on).into(ivSwitchOne);
                        Glide.with(QsTwoControlActivity.this).load(R.mipmap.device_light_on).into(ivLightOne);
                    }
                } else {
                    text = getString(R.string.common_manual_close_one_light);
                    isStateOne = false;
                    if (!QsTwoControlActivity.this.isDestroyed()) {
                        Glide.with(QsTwoControlActivity.this).load(R.mipmap.device_switch_off).into(ivSwitchOne);
                        Glide.with(QsTwoControlActivity.this).load(R.mipmap.device_light_off).into(ivLightOne);
                    }
                }
                CommonUtils.addDeviceLog(resultVo.getDeviceId(), "qs02", text);
            }

            @Override
            public void onWriteFailure(BleException exception) {

            }
        })), 150);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (connect != null) {
            connect.disconnect();
            connect.close();
        }
        BleManageUtils.getInstance().DestroyBleManage();
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @OnClick({R.id.iv_device_control_goback, R.id.iv_device_control_more, R.id.ll_log, R.id.ll_sounding, R.id.ll_timing})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_device_control_goback:
                finish();
                break;
            case R.id.iv_device_control_more:
                startActivity(new Intent(this, QsTwoSettingActivity.class)
                        .putExtra("scanResult", resultVo)
                        .putExtra("is_control", is_control));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.ll_log:
                startActivity(new Intent(QsTwoControlActivity.this, DeviceLogActivity.class)
                        .putExtra("device_id", resultVo.getDeviceId())
                        .putExtra("device_type", "qs02"));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.ll_sounding:
                Intent intent = new Intent(QsTwoControlActivity.this, QsTwoSoundControlActivity.class);
                intent.putExtra("bledevice", mBledevice);
                intent.putExtra("device_id", resultVo.getDeviceId());
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.ll_timing:
                startActivity(new Intent(QsTwoControlActivity.this, DeviceBleTimingListActivity.class)
                        .putExtra("bledevice", mBledevice)
                        .putExtra("device_id", resultVo.getDeviceId()));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
        }
    }

}
