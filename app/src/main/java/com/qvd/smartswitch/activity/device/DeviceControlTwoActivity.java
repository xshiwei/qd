package com.qvd.smartswitch.activity.device;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothGatt;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.model.device.ScanResultVo;
import com.qvd.smartswitch.utils.OnMultiClickListener;
import com.qvd.smartswitch.widget.MyPopupWindowLoading;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/4/3.
 */

public class DeviceControlTwoActivity extends BaseActivity {

    @BindView(R.id.iv_device_control_goback)
    ImageView ivDeviceControlGoback;
    @BindView(R.id.tv_device_control_title)
    TextView tvDeviceControlTitle;
    @BindView(R.id.iv_device_control_more)
    ImageView ivDeviceControlMore;
    @BindView(R.id.tv_connect_text)
    TextView tvConnectText;
    @BindView(R.id.smart_refresh)
    SmartRefreshLayout smartRefresh;
    @BindView(R.id.rl_log)
    RelativeLayout rlLog;
    @BindView(R.id.rl_timing)
    RelativeLayout rlTiming;
    @BindView(R.id.rl_switch)
    RelativeLayout rlSwitch;
    @BindView(R.id.iv_light_one)
    ImageView ivLightOne;
    @BindView(R.id.iv_switch_one)
    ImageView ivSwitchOne;
    @BindView(R.id.iv_light_two)
    ImageView ivLightTwo;
    @BindView(R.id.iv_switch_two)
    ImageView ivSwitchTwo;

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
    private MyPopupWindowLoading myPopupWindowLoading;

    /**
     * 是否第一次连接设备
     */
    private int isFirstConnect;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_device_control_two;
    }


    @SuppressLint("CheckResult")
    @Override
    protected void initData() {
        super.initData();
        final View mView = DeviceControlTwoActivity.this.getWindow().getDecorView();
        myPopupWindowLoading = new MyPopupWindowLoading(DeviceControlTwoActivity.this, "设备正在初始化");
        mView.post(new Runnable() {
            @Override
            public void run() {
                if (null != myPopupWindowLoading) {
                    myPopupWindowLoading.showPopupWindow(mView);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            myPopupWindowLoading.dismiss();
                        }
                    }, 5000);
                }
            }
        });
        resultVo = (ScanResultVo) getIntent().getSerializableExtra("scanResult");
        isFirstConnect = getIntent().getIntExtra("isFirstConnect", -1);
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
        smartRefresh.setEnableHeaderTranslationContent(false);
        smartRefresh.setEnableFooterTranslationContent(false);
        //设置头部样式
        smartRefresh.setRefreshHeader(new MaterialHeader(this));
        smartRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                connectDevice();
                smartRefresh.finishLoadmore(3000, true);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (resultVo.getDeviceMac() != null) {
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
                tvConnectText.setVisibility(View.VISIBLE);
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
                mBledevice = bleDevice;
                tvConnectText.setVisibility(View.GONE);
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
                tvConnectText.setVisibility(View.VISIBLE);
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
        final ProgressDialog dialog = new ProgressDialog(this);
        Observable.interval(1, 1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<Long>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Logger.e("这是一个千古难题！");
                        if (!BleManager.getInstance().isConnected(resultVo.getDeviceMac())) {
                            dialog.show();
                            dialog.setMessage("设备已断开，正在进行重连");
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
                                            dialog.dismiss();
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
                            tvConnectText.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.light_background_end).init();
    }

//    @OnClick({R.id.iv_device_control_goback, R.id.iv_device_control_more, R.id.iv_sound})
//    public void onViewClicked(View view) {
//        switch (view.getId()) {
//            case R.id.iv_device_control_goback:
//                startActivity(new Intent(this, MainActivity.class));
//                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
//                finish();
//                break;
//            case R.id.iv_device_control_more:
//
//                break;
//            case R.id.iv_sound:
//                //语音控制开关灯
//                Intent intent = new Intent(this, DeviceSoundControlActivity.class);
//                intent.putExtra("bledevice", mBledevice);
//                startActivity(intent);
//                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
//                break;
//        }
//    }


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
                        if (!isStateOne) {
                            isStateOne = true;
                            ivSwitchOne.setImageResource(R.mipmap.device_switch_on);
                            ivLightOne.setImageResource(R.mipmap.device_light_on);
                        } else {
                            isStateOne = false;
                            ivSwitchOne.setImageResource(R.mipmap.device_switch_off);
                            ivLightOne.setImageResource(R.mipmap.device_light_off);
                        }
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {

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
                        if (!isStatetwo) {
                            isStatetwo = true;
                            ivSwitchTwo.setImageResource(R.mipmap.device_switch_on);
                            ivLightTwo.setImageResource(R.mipmap.device_light_on);
                        } else {
                            isStatetwo = false;
                            ivSwitchTwo.setImageResource(R.mipmap.device_switch_off);
                            ivLightTwo.setImageResource(R.mipmap.device_light_off);
                        }
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {

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

    @OnClick({R.id.iv_device_control_goback, R.id.iv_device_control_more, R.id.rl_log, R.id.rl_timing, R.id.rl_switch})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_device_control_goback:
                startActivity(new Intent(this, MainActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                finish();
                break;
            case R.id.iv_device_control_more:
                startActivity(new Intent(this, DeviceControlSettingActivity.class).putExtra("bledevice", mBledevice));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.rl_log:
                break;
            case R.id.rl_timing:
                break;
            case R.id.rl_switch:
                break;
        }
    }
}
