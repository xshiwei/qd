package com.qvd.smartswitch.activity.qsTwo;


import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
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
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.TextOutsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.orhanobut.logger.Logger;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.MainActivity;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.activity.base.BaseNoTipActivity;
import com.qvd.smartswitch.activity.device.DeviceSettingActivity;
import com.qvd.smartswitch.activity.device.DeviceLogActivity;
import com.qvd.smartswitch.activity.device.DeviceSoundControlActivity;
import com.qvd.smartswitch.model.device.ScanResultVo;
import com.qvd.smartswitch.utils.BleManageUtils;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.OnMultiClickListener;
import com.qvd.smartswitch.utils.ToastUtil;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
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

public class QsTwoControlActivity extends BaseNoTipActivity {

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
    @BindView(R.id.iv_switch_one)
    ImageView ivSwitchOne;
    @BindView(R.id.iv_light_two)
    ImageView ivLightTwo;
    @BindView(R.id.iv_switch_two)
    ImageView ivSwitchTwo;
    @BindView(R.id.bmb)
    BoomMenuButton bmb;

    /**
     * 灯1未开启
     */
    private boolean isStateOne = false;
    /**
     * 灯2未开启
     */
    private boolean isStatetwo = false;

    private BleDevice mBledevice;

    private ScanResultVo resultVo;

    /**
     * 是否第一次连接设备
     */
    private int isFirstConnect;

    private MaterialDialog dialog;

    private TextView tvConnectText;

    /**
     * 设备deviceId
     */
    private String deviceId;
    private BluetoothAdapter bluetoothAdapter;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_qstwo_control;
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
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        /**
         * 去打开蓝牙，否则直接扫描设备
         */
        if (!bluetoothAdapter.isEnabled()) {
            showOpenBlePopupWindow();
        } else {
            dialog = new MaterialDialog.Builder(QsTwoControlActivity.this)
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
        resultVo = (ScanResultVo) getIntent().getSerializableExtra("scanResult");
        deviceId = getIntent().getStringExtra("deviceId");
        isFirstConnect = getIntent().getIntExtra("isFirstConnect", -1);
        tvDeviceControlTitle.setText(resultVo.getDeviceName());
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
        ivSwitchTwo.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                if (isStatetwo) {
                    //关灯
                    writeToBleTwo(String.valueOf("fe010020ffffffffffffffffffffffffffffffff"));
                } else {
                    //开灯
                    writeToBleTwo(String.valueOf("fe010021ffffffffffffffffffffffffffffffff"));
                }
            }
        });
        //设置刷新控件头部高度
        smartRefresh.setHeaderHeight(100);
        smartRefresh.setFooterHeight(1);
        smartRefresh.setEnableRefresh(true);
        smartRefresh.setEnableHeaderTranslationContent(false);
        smartRefresh.setEnableFooterTranslationContent(false);
        //设置头部样式
        smartRefresh.setRefreshHeader(new MaterialHeader(this));
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
        initMenu();
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
                        dialog = new MaterialDialog.Builder(QsTwoControlActivity.this)
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


    /**
     * 初始化菜单
     */
    private void initMenu() {
        TextOutsideCircleButton.Builder builder1 = new TextOutsideCircleButton.Builder()
                .normalImageRes(R.mipmap.device_log_pic)
                .normalText("日志")
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        startActivity(new Intent(QsTwoControlActivity.this, DeviceLogActivity.class)
                                .putExtra("device_id", deviceId)
                                .putExtra("device_type", "qs02"));
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    }
                })
                .textSize(12)
                .normalColor(getResources().getColor(R.color.orange_background))
                .pieceColor(Color.WHITE);
        TextOutsideCircleButton.Builder builder2 = new TextOutsideCircleButton.Builder()
                .normalImageRes(R.mipmap.device_sound_pic)
                .normalText("语音")
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        Intent intent = new Intent(QsTwoControlActivity.this, DeviceSoundControlActivity.class);
                        intent.putExtra("bledevice", mBledevice);
                        startActivity(intent);
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    }
                })
                .textSize(12)
                .normalColor(getResources().getColor(R.color.app_red_color))
                .pieceColor(Color.WHITE);
        TextOutsideCircleButton.Builder builder3 = new TextOutsideCircleButton.Builder()
                .normalImageRes(R.mipmap.device_timing_pic)
                .normalText("定时")
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        startActivity(new Intent(QsTwoControlActivity.this, QsTwoTimingActivity.class)
                                .putExtra("bledevice", mBledevice)
                                .putExtra("device_id", deviceId));
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    }
                })
                .textSize(12)
                .normalColor(getResources().getColor(R.color.blue_background))
                .pieceColor(Color.WHITE);
        bmb.addBuilder(builder1);
        bmb.addBuilder(builder2);
        bmb.addBuilder(builder3);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (resultVo != null && !BleManager.getInstance().isConnected(resultVo.getDeviceMac())) {
            connectDevice();
        }
        if (mBledevice != null) {
            retryConnectDevice();
            getNotify();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    sendMessage();
                }
            }, 100);
        }
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
                if (s.equals("1020")) {
                    isStateOne = false;
                    isStatetwo = false;
                    ivLightOne.setImageResource(R.mipmap.device_light_off);
                    ivLightTwo.setImageResource(R.mipmap.device_light_off);
                } else if (s.equals("1120")) {
                    isStateOne = true;
                    isStatetwo = false;
                    ivLightOne.setImageResource(R.mipmap.device_light_on);
                    ivLightTwo.setImageResource(R.mipmap.device_light_off);
                } else if (s.equals("1021")) {
                    isStateOne = false;
                    isStatetwo = true;
                    ivLightOne.setImageResource(R.mipmap.device_light_off);
                    ivLightTwo.setImageResource(R.mipmap.device_light_on);
                } else if (s.equals("1121")) {
                    isStateOne = true;
                    isStatetwo = true;
                    ivLightOne.setImageResource(R.mipmap.device_light_on);
                    ivLightTwo.setImageResource(R.mipmap.device_light_on);
                }
                Logger.e("notify->" + HexUtil.formatHexString(data, false));
            }
        });
    }


    /**
     * 重新连接设备
     */
    @SuppressLint("CheckResult")
    private void retryConnectDevice() {
        Observable.interval(1, 2, TimeUnit.SECONDS)
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
        mImmersionBar.fitsSystemWindows(false).transparentBar().statusBarDarkFont(false).init();
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
                            ivSwitchOne.setImageResource(R.mipmap.device_switch_on);
                            ivLightOne.setImageResource(R.mipmap.device_light_on);
                        } else {
                            text = "手动关闭一号灯";
                            isStateOne = false;
                            ivSwitchOne.setImageResource(R.mipmap.device_switch_off);
                            ivLightOne.setImageResource(R.mipmap.device_light_off);
                        }
                        CommonUtils.addDeviceLog(deviceId, "qs02", text);
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {
                        ToastUtil.showToast("操作失败");
                    }
                });
            }
        }, 150);
    }


    /**
     * 向灯泡二写入数据
     *
     * @param s
     */
    synchronized private void writeToBleTwo(final String s) {
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
                        if (!isStatetwo) {
                            text = "手动打开二号灯";
                            isStatetwo = true;
                            ivSwitchTwo.setImageResource(R.mipmap.device_switch_on);
                            ivLightTwo.setImageResource(R.mipmap.device_light_on);
                        } else {
                            text = "手动关闭一号灯";
                            isStatetwo = false;
                            ivSwitchTwo.setImageResource(R.mipmap.device_switch_off);
                            ivLightTwo.setImageResource(R.mipmap.device_light_off);
                        }
                        CommonUtils.addDeviceLog(deviceId, "qs02", text);
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

    @OnClick({R.id.iv_device_control_goback, R.id.iv_device_control_more})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_device_control_goback:
                startActivity(new Intent(this, MainActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                finish();
                break;
            case R.id.iv_device_control_more:
                startActivity(new Intent(this, DeviceSettingActivity.class)
                        .putExtra("device_id", deviceId)
                        .putExtra("device_No", "qs02"));
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
