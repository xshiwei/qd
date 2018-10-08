package com.qvd.smartswitch.activity.qsOne;


import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;
import com.orhanobut.logger.Logger;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.MainActivity;
import com.qvd.smartswitch.activity.base.BaseNoTipActivity;
import com.qvd.smartswitch.activity.qsTwo.QsTwoSettingActivity;
import com.qvd.smartswitch.activity.qsTwo.QsTwoSoundControlActivity;
import com.qvd.smartswitch.activity.qsTwo.QsTwoTimingActivity;
import com.qvd.smartswitch.model.device.ScanResultVo;
import com.qvd.smartswitch.utils.BleManageUtils;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.ToastUtil;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/4/3.
 */

public class QsOneControlActivity extends BaseNoTipActivity {

    @BindView(R.id.iv_device_control_goback)
    ImageView ivDeviceControlGoback;
    @BindView(R.id.tv_device_control_title)
    TextView tvDeviceControlTitle;
    @BindView(R.id.iv_device_control_more)
    ImageView ivDeviceControlMore;
    @BindView(R.id.smart_refresh)
    SmartRefreshLayout smartRefresh;
    @BindView(R.id.iv_light_one)
    ImageView ivLightOne;
    @BindView(R.id.ll_log)
    LinearLayout llLog;
    @BindView(R.id.ll_sounding)
    LinearLayout llSounding;
    @BindView(R.id.ll_timing)
    LinearLayout llTiming;

    /**
     * 灯1未开启
     */
    private boolean isStateOne = false;

    private BleDevice mBledevice;

    private ScanResultVo resultVo;

    private MaterialDialog dialog;

    private TextView tvConnectText;

    private BluetoothAdapter bluetoothAdapter;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_qsone_control;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    tvConnectText.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    tvConnectText.setVisibility(View.GONE);
                    break;
            }
        }
    };


    @SuppressLint("CheckResult")
    @Override
    protected void initData() {
        super.initData();
        tvConnectText = findViewById(R.id.tv_connect_text);

        resultVo = (ScanResultVo) getIntent().getSerializableExtra("scanResult");

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        /**
         * 去打开蓝牙，否则直接扫描设备
         */
        if (!bluetoothAdapter.isEnabled()) {
            showOpenBlePopupWindow();
        } else {
            dialog = new MaterialDialog.Builder(QsOneControlActivity.this)
                    .content("设备正在初始化")
                    .progress(true, 0)
                    .autoDismiss(false)
                    .show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                }
            }, 3000);
        }

        tvDeviceControlTitle.setText(resultVo.getDeviceName());
        //设置刷新控件头部高度
        smartRefresh.setHeaderHeight(100);
        smartRefresh.setFooterHeight(1);
        smartRefresh.setEnableRefresh(true);
        smartRefresh.setEnableHeaderTranslationContent(true);
        smartRefresh.setEnableFooterTranslationContent(false);
        //设置头部样式
        smartRefresh.setRefreshHeader(new ClassicsHeader(this).setAccentColor(getResources().getColor(R.color.white)));
        smartRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                if (!BleManager.getInstance().isConnected(resultVo.getDeviceMac())) {
                    connectDevice();
                }
                ToastUtil.showToast("刷新完成");
                smartRefresh.finishRefresh(true);
            }
        });
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
                        ToastUtil.showToast("您需要打开蓝牙开关才能连接控制设备");
                        tvConnectText.setText("当前蓝牙未开启");
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        bluetoothAdapter.enable();
                        dialog = new MaterialDialog.Builder(QsOneControlActivity.this)
                                .content("设备正在初始化")
                                .progress(true, 0)
                                .autoDismiss(false)
                                .show();
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (resultVo != null && !BleManager.getInstance().isConnected(resultVo.getDeviceMac())) {
            connectDevice();
        }
        if (mBledevice != null) {
            getNotify();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    sendMessage();
                }
            }, 100);
        }
        retryConnectDevice();
    }

    /**
     * 连接设备
     */
    private void connectDevice() {
        BleManager.getInstance().connect(resultVo.getDeviceMac(), new BleGattCallback() {
            @Override
            public void onStartConnect() {

            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                handler.sendEmptyMessage(1);
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
                ToastUtil.showToast("连接成功");
                mBledevice = bleDevice;
                handler.sendEmptyMessage(2);
                getNotify();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sendMessage();
                    }
                }, 300);
            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {
                handler.sendEmptyMessage(1);
            }
        });
    }

    private void sendMessage() {
        BleManager.getInstance().write(mBledevice, "0000fff0-0000-1000-8000-00805f9b34fb", "0000fff6-0000-1000-8000-00805f9b34fb", HexUtil.hexStringToBytes("FE0001FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF"), true, new BleWriteCallback() {
            @Override
            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                Logger.e("灯泡初始化");
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

            }

            @Override
            public void onNotifyFailure(BleException exception) {
                Logger.e("notify->" + exception.toString());
            }

            @Override
            public void onCharacteristicChanged(byte[] data) {
                String s = HexUtil.formatHexString(data, false);
                if (s.equals("10")) {
                    isStateOne = false;
                    ivLightOne.setImageResource(R.mipmap.device_light_off);
                } else if (s.equals("11")) {
                    isStateOne = true;
                    ivLightOne.setImageResource(R.mipmap.device_light_on);
                }
            }
        });
    }


    /**
     * 重新连接设备
     */
    @SuppressLint("CheckResult")
    private void retryConnectDevice() {
        Observable.interval(1, 5, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<Long>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        if (!BleManager.getInstance().isConnected(resultVo.getDeviceMac())) {
                            tvConnectText.setText("设备掉线，正在重连...");
                            tvConnectText.setVisibility(View.VISIBLE);
                            BleManager.getInstance().connect(resultVo.getDeviceMac(), new BleGattCallback() {
                                @Override
                                public void onStartConnect() {

                                }

                                @Override
                                public void onConnectFail(BleDevice bleDevice, BleException exception) {
                                    mBledevice = bleDevice;
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
                                    getNotify();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            tvConnectText.setText("设备未连接，下拉连接设备");
                                            handler.sendEmptyMessage(2);
                                            sendMessage();
                                        }
                                    }, 300);
                                }

                                @Override
                                public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {

                                }
                            });
                        }
                        if (!BleManager.getInstance().isConnected(resultVo.getDeviceMac())) {
                            handler.sendEmptyMessage(2);
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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                BleManager.getInstance().write(mBledevice, "0000fff0-0000-1000-8000-00805f9b34fb", "0000fff6-0000-1000-8000-00805f9b34fb", HexUtil.hexStringToBytes(s), true, new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        String text = "";
                        if (!isStateOne) {
                            text = "手动打开一号灯";
                            isStateOne = true;
                            ivLightOne.setImageResource(R.mipmap.device_light_on);
                        } else {
                            text = "手动关闭一号灯";
                            isStateOne = false;
                            ivLightOne.setImageResource(R.mipmap.device_light_off);
                        }
                        CommonUtils.addDeviceLog(resultVo.getDeviceId(), "qs02", text);
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {
                        ToastUtil.showToast("操作失败");
                    }
                });
            }
        }, 150);
    }

    @Override
    protected void onPause() {
        BleManager.getInstance().stopNotify(mBledevice, "0000fff0-0000-1000-8000-00805f9b34fb", "0000fff6-0000-1000-8000-00805f9b34fb");
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        finish();
        super.onBackPressed();
    }

    @OnClick({R.id.iv_device_control_goback, R.id.iv_device_control_more, R.id.ll_log, R.id.ll_sounding, R.id.ll_timing})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_device_control_goback:
                startActivity(new Intent(this, MainActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                finish();
                break;
            case R.id.iv_device_control_more:
                startActivity(new Intent(this, QsTwoSettingActivity.class)
                        .putExtra("device_id", resultVo.getDeviceId()));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.ll_log:
                if (isStateOne) {
                    //关灯
                    writeToBleOne(String.valueOf("fe010010ffffffffffffffffffffffffffffffff"));
                } else {
                    //开灯
                    writeToBleOne(String.valueOf("fe010011ffffffffffffffffffffffffffffffff"));
                }
                break;
            case R.id.ll_sounding:
                Intent intent = new Intent(QsOneControlActivity.this, QsTwoSoundControlActivity.class);
                intent.putExtra("bledevice", mBledevice);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.ll_timing:
                startActivity(new Intent(QsOneControlActivity.this, QsTwoTimingActivity.class)
                        .putExtra("bledevice", mBledevice)
                        .putExtra("device_id", resultVo.getDeviceId()));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BleManageUtils.getInstance().DestroyBleManage();
    }

}
