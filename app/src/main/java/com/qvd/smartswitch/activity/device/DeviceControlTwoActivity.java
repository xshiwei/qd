package com.qvd.smartswitch.activity.device;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothGatt;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleIndicateCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;
import com.iflytek.sunflower.FlowerCollector;
import com.orhanobut.logger.Logger;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.db.DeviceDaoOpe;
import com.qvd.smartswitch.db.DeviceNickNameDaoOpe;
import com.qvd.smartswitch.model.DeviceLogVo;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.OnMultiClickListener;
import com.qvd.smartswitch.utils.SnackbarUtils;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
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
    @BindView(R.id.action_bar)
    RelativeLayout actionBar;
    @BindView(R.id.tv_timing_text)
    TextView tvTimingText;
    @BindView(R.id.iv_light_one)
    ImageView ivLightOne;
    @BindView(R.id.iv_switch_one)
    TextView ivSwitchOne;
    @BindView(R.id.iv_light_two)
    ImageView ivLightTwo;
    @BindView(R.id.iv_switch_two)
    TextView ivSwitchTwo;
    @BindView(R.id.tv_success)
    TextView tvSuccess;
    @BindView(R.id.tv_error)
    TextView tvError;
    @BindView(R.id.tv_total)
    TextView tvTotal;
    @BindView(R.id.rl_test)
    LinearLayout rlTest;
    @BindView(R.id.iv_sound)
    ImageView ivSound;
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    private static String TAG = DeviceControlTwoActivity.class.getSimpleName();

    /**
     * 灯1未开启
     */
    private boolean isStateOne = false;
    /**
     * 灯2未开启
     */
    private boolean isStatetwo = false;

    private BleDevice bledevice;
    private String deviceNickname;

    private int successCount = 0;
    private int errorCount = 0;
    private int count = 0;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_device_control_two;
    }


    @SuppressLint("CheckResult")
    @Override
    protected void initData() {
        super.initData();
        bledevice = getIntent().getParcelableExtra("bledevice");
        if (bledevice != null) {
            deviceNickname = DeviceNickNameDaoOpe.queryOne(this, CommonUtils.getMac(bledevice.getMac())).getDeviceNickname();
        }
        ivSwitchOne.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                count++;
                tvTotal.setText(count + "");
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
                count++;
                tvTotal.setText(count + "");
                if (isStatetwo) {
                    //关灯
                    writeToBleTwo(String.valueOf("fe010020ffffffffffffffffffffffffffffffff"));
                } else {
                    //开灯
                    writeToBleTwo(String.valueOf("fe010021ffffffffffffffffffffffffffffffff"));
                }
            }
        });
        final ProgressDialog dialog = new ProgressDialog(this);
        Observable.interval(5, 5, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<Long>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Logger.e("这是一个千古难题！");
                        if (!BleManager.getInstance().isConnected(bledevice)) {
                            dialog.show();
                            dialog.setMessage("设备已断开，正在进行重连");
                            BleManager.getInstance().connect(bledevice.getMac(), new BleGattCallback() {
                                @Override
                                public void onStartConnect() {

                                }

                                @Override
                                public void onConnectFail(BleDevice bleDevice, BleException exception) {
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
                                            SnackbarUtils.Short(coordinatorLayout, "连接成功").show();
                                        }
                                    }, 300);
                                }

                                @Override
                                public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {

                                }
                            });
                        }
                    }
                });
    }

    private void sendMessage() {
        BleManager.getInstance().write(bledevice, "0000fff0-0000-1000-8000-00805f9b34fb", "0000fff6-0000-1000-8000-00805f9b34fb", HexUtil.hexStringToBytes("FE0001FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF"), true, new BleWriteCallback() {
            @Override
            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                Logger.e("write success, current: " + current
                        + " total: " + total
                        + " justWrite: " + HexUtil.formatHexString(justWrite, true));
            }

            @Override
            public void onWriteFailure(BleException exception) {
                SnackbarUtils.Short(coordinatorLayout, "初始化灯泡状态失败");
            }
        });
    }

    /**
     * 获取通知
     */
    @SuppressLint("CheckResult")
    private void getNotify() {
        BleManager.getInstance().notify(bledevice, "0000fff0-0000-1000-8000-00805f9b34fb", "0000fff6-0000-1000-8000-00805f9b34fb", new BleNotifyCallback() {
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

    @Override
    protected void onResume() {
        super.onResume();
        //移动数据统计分析
        Logger.e("oncreate");
        FlowerCollector.onResume(this);
        FlowerCollector.onPageStart(TAG);
        if (bledevice != null) {
            getNotify();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    sendMessage();
                }
            }, 100);
        }
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }

    @OnClick({R.id.iv_device_control_goback, R.id.iv_device_control_more, R.id.iv_sound})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_device_control_goback:
                finish();
                break;
            case R.id.iv_device_control_more:
                startActivity(new Intent(this, DeviceControlSettingActivity.class).putExtra("bledevice", bledevice));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.iv_sound:
                //语音控制开关灯
                Intent intent = new Intent(this, DeviceSoundControlActivity.class);
                intent.putExtra("bledevice", bledevice);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
        }
    }


    /**
     * 向Ble写入数据
     *
     * @param s
     */
    synchronized private void writeToBleOne(final String s) {
        if (bledevice == null) {
            return;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                BleManager.getInstance().write(bledevice, "0000fff0-0000-1000-8000-00805f9b34fb", "0000fff6-0000-1000-8000-00805f9b34fb", HexUtil.hexStringToBytes(s), true, new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        successCount++;
                        tvSuccess.setText(successCount + "");
                        Logger.e("write success, current: " + current
                                + " total: " + total
                                + " justWrite: " + HexUtil.formatHexString(justWrite, true) + "count:" + successCount);
                        if (!isStateOne) {
                            isStateOne = true;
                            ivLightOne.setImageResource(R.mipmap.device_light_on);
                            DeviceLogVo deviceLogVo = new DeviceLogVo(null, CommonUtils.getMac(bledevice.getMac()), bledevice.getName(), deviceNickname, CommonUtils.getDate(), 1, 1);
                            DeviceDaoOpe.insertData(DeviceControlTwoActivity.this, deviceLogVo);
                        } else {
                            isStateOne = false;
                            ivLightOne.setImageResource(R.mipmap.device_light_off);
                            DeviceLogVo deviceLogVo = new DeviceLogVo(null, CommonUtils.getMac(bledevice.getMac()), bledevice.getName(), deviceNickname, CommonUtils.getDate(), 0, 1);
                            DeviceDaoOpe.insertData(DeviceControlTwoActivity.this, deviceLogVo);
                        }
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {
                        errorCount++;
                        tvError.setText(errorCount + "");
                        Logger.e("write->" + exception.toString() + "---------count=" + errorCount);
                    }
                });
            }
        }, 150);
    }


    /**
     * 向Ble写入数据
     *
     * @param s
     */
    synchronized private void writeToBleTwo(final String s) {
        if (bledevice == null) {
            return;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                BleManager.getInstance().write(bledevice, "0000fff0-0000-1000-8000-00805f9b34fb", "0000fff6-0000-1000-8000-00805f9b34fb", HexUtil.hexStringToBytes(s), true, new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        successCount++;
                        tvSuccess.setText(successCount + "");
                        Logger.e("write success, current: " + current
                                + " total: " + total
                                + " justWrite: " + HexUtil.formatHexString(justWrite, true) + "count:" + successCount);
                        if (!isStatetwo) {
                            isStatetwo = true;
                            ivLightTwo.setImageResource(R.mipmap.device_light_on);
                        } else {
                            isStatetwo = false;
                            ivLightTwo.setImageResource(R.mipmap.device_light_off);
                        }
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {
                        errorCount++;
                        tvError.setText(errorCount + "");
                        Logger.e("write->" + exception.toString() + "---------count=" + errorCount);
                    }
                });
            }
        }, 150);
    }


    @Override
    protected void onPause() {
        BleManager.getInstance().stopNotify(bledevice, "0000fff0-0000-1000-8000-00805f9b34fb", "0000fff6-0000-1000-8000-00805f9b34fb");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
