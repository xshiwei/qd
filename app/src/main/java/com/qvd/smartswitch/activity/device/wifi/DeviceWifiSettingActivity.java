package com.qvd.smartswitch.activity.device.wifi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.isupatches.wisefy.WiseFy;
import com.isupatches.wisefy.callbacks.AddWPA2NetworkCallbacks;
import com.isupatches.wisefy.callbacks.ConnectToNetworkCallbacks;
import com.isupatches.wisefy.callbacks.SearchForAccessPointCallbacks;
import com.orhanobut.logger.Logger;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseNoTipActivity;
import com.qvd.smartswitch.activity.device.DeviceSetRoomActivity;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.device.IsWifiNetWorkVo;
import com.qvd.smartswitch.model.device.ScanResultVo;
import com.qvd.smartswitch.model.login.MessageVo;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.ConfigUtils;
import com.qvd.smartswitch.utils.PermissionUtils;
import com.qvd.smartswitch.utils.SnackbarUtils;
import com.qvd.smartswitch.utils.ToastUtil;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.wang.avi.AVLoadingIndicatorView;
import com.yanzhenjie.permission.Permission;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DeviceWifiSettingActivity extends BaseNoTipActivity {
    @BindView(R.id.tv_back)
    TextView tvBack;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.tv_wifi_name)
    TextView tvWifiName;
    @BindView(R.id.tv_change_network)
    TextView tvChangeNetwork;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;
    @BindView(R.id.rl_setting_wifi_password)
    RelativeLayout rlSettingWifiPassword;
    @BindView(R.id.tv_text)
    TextView tvText;
    @BindView(R.id.tv_find_device)
    TextView tvFindDevice;
    @BindView(R.id.tv_device_register)
    TextView tvDeviceRegister;
    @BindView(R.id.tv_device_init)
    TextView tvDeviceInit;
    @BindView(R.id.rl_device_connect)
    RelativeLayout rlDeviceConnect;
    @BindView(R.id.avi_loading)
    AVLoadingIndicatorView aviLoading;

    /**
     * wifi控制实例
     */
    private WiseFy wiseFy;
    /**
     * 当前连接的wifi
     */
    private WifiInfo currentNetwork;

    /**
     * 当前路由的密码
     */
    private String password;

    /**
     * 当前路由的SSID
     *
     * @return
     */
    private String wifiSSID;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                case 0:
                    String obj = (String) msg.obj;
                    showfailPopupWindow(obj);
                    break;
                case 1:
                    tvFindDevice.setTextColor(getResources().getColor(R.color.room_manage_add_text));
                    sendDeviceMessage(mSSID);
                    break;
                case 2:
                    tvDeviceRegister.setTextColor(getResources().getColor(R.color.room_manage_add_text));
                    startQueryNetwork();
                    break;
            }
        }
    };

    //获取到wifi的信息
    private String mSSID;
    /**
     * 设备mac地址
     */
    private String deviceMac;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_device_wifi_setting;
    }

    @Override
    protected void initData() {
        super.initData();
        PermissionUtils.requestPermission(this, Permission.Group.LOCATION);
        mSSID = getIntent().getStringExtra("wifi_ssid");
        wiseFy = new WiseFy.brains(this).logging(true).getSmarts();
    }

    /**
     * 初始化WiFi
     */
    private void initWifi() {
        //检查当前设备是否开启wifi
        if (wiseFy.isWifiEnabled()) {
            //判断当前是否有连接wifi
            isConnectWifi();
        } else {
            new Handler().postDelayed(() -> {
                showOpenWifiPopupWindow();
            }, 2000);
        }
    }

    /**
     * 判断当前是否有连接wifi
     */
    private void isConnectWifi() {
        //判断当前设备是否连接到wifi网络
        if (wiseFy.isDeviceConnectedToWifiNetwork()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ToastUtil.showToast("定位权限未打开");
                return;
            }
            //获取当前连接wifi信息 SSID: Xiaomi_Qvedo, BSSID: 40:31:3c:10:a4:26, MAC: 02:00:00:00:00:00, Supplicant state: COMPLETED, RSSI: -37, Link speed: 144Mbps, Frequency: 2412MHz, Net ID: 3, Metered hint: false, score: 60
            currentNetwork = wiseFy.getCurrentNetwork();
            String ssid = currentNetwork.getSSID();
            //设置当前wifi名称
            tvWifiName.setText("当前Wi-Fi:" + getNewSSID(ssid));
        } else {
            tvWifiName.setText("当前无Wi-Fi连接");
        }
    }

    private String getNewSSID(String ssid) {
        String substring = ssid.substring(1, ssid.length() - 1);
        return substring;
    }

    @Override
    protected void onResume() {
        super.onResume();
        initWifi();
    }

    /**
     * 展示打开Wifi的popouwindow
     */
    private void showOpenWifiPopupWindow() {
        new MaterialDialog.Builder(this)
                .title("您当前Wi-Fi网络未开启，不能进行连接设备操作,点击确定开启Wi-Fi。")
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
                        boolean b = wiseFy.enableWifi();
                        if (b) {
                            ToastUtil.showToast("Wi-Fi开启成功");
                            isConnectWifi();
                        } else {
                            ToastUtil.showToast("Wi-Fi开启失败");
                        }
                    }
                })
                .show();
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(false).transparentBar().init();
    }

    @OnClick({R.id.tv_back, R.id.tv_change_network, R.id.tv_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                showBackPopupWindow("您确定要取消配网吗");
                break;
            case R.id.tv_change_network:
                Intent intent = new Intent();
                ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.wifi.WifiSettings");
                intent.setComponent(cn);
                startActivity(intent);
                break;
            case R.id.tv_confirm:
                if (CommonUtils.isEmptyString(etPassword.getText().toString())) {
                    SnackbarUtils.Short(tvBack, "密码不能为空").show();
                } else if (!wiseFy.isDeviceConnectedToWifiNetwork()) {
                    tvWifiName.setText("当前无Wi-Fi连接");
                    SnackbarUtils.Short(tvBack, "未连接Wi-Fi").show();
                } else {
                    //保存密码
                    password = etPassword.getText().toString();
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ToastUtil.showToast("定位权限未打开");
                        return;
                    }
                    wifiSSID = getNewSSID(wiseFy.getCurrentNetwork().getSSID());
                    rlSettingWifiPassword.setVisibility(View.GONE);
                    tvText.setVisibility(View.GONE);
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    rlDeviceConnect.setVisibility(View.VISIBLE);
                    if (wiseFy.isNetworkSaved(mSSID)) {
                        searchWifi();
                    } else {
                        addWifi();
                    }
                }
                break;
        }
    }


    /**
     * 添加wifi
     */
    private void addWifi() {
        wiseFy.addWPA2Network(mSSID, "88888888", new AddWPA2NetworkCallbacks() {
            @Override
            public void addWPA2NetworkWiseFyFailure(int i) {
                showMyFailPopupWindow("当前设备添加失败，是否需要重试？");
            }

            @Override
            public void failureAddingWPA2Network(int i) {
                showMyFailPopupWindow("当前设备添加失败，是否需要重试？");
            }

            @Override
            public void wpa2NetworkAdded(int i, WifiConfiguration wifiConfiguration) {
                Logger.e("添加成功");
                searchWifi();
            }
        });
    }

    /**
     * 开始搜索
     */
    private void searchWifi() {
        wiseFy.searchForAccessPoint(mSSID, 5000, true, new SearchForAccessPointCallbacks() {
            @Override
            public void searchForAccessPointWiseFyFailure(int i) {
                showMyFailPopupWindow("当前设备搜索失败，是否需要重试？");
            }

            @Override
            public void accessPointFound(ScanResult scanResult) {
                //2、开始连接Wifi
                Logger.e("开始连接wifi");
                deviceMac = scanResult.BSSID;
                handler.sendEmptyMessage(1);
            }

            @Override
            public void accessPointNotFound() {
                showMyFailPopupWindow("当前未搜索到该类设备，是否需要重试？");
            }
        });
    }


    /**
     * 发送消息给服务器
     */
    private void sendDeviceMessage(String ssid) {
        RetrofitService.qdoApi.addTempIdData(ConfigUtils.user_id, ConfigUtils.defaultRoomId, ConfigUtils.family_locate.getFamily_id(), deviceMac)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MessageVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MessageVo messageVo) {
                        if (messageVo != null) {
                            if (messageVo.getCode() == 200) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        connectWifi(ssid);
                                        Logger.e("ssid=========" + ssid);
                                    }
                                }, 1000);
                            } else {
                                showMyFailPopupWindow("请确保当前wifi连接是路由器wifi后，再重试");
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        showMyFailPopupWindow("请确保当前wifi连接是路由器wifi后，再重试");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void showMyFailPopupWindow(String s) {
        Message message = new Message();
        message.obj = s;
        message.what = 0;
        handler.sendMessage(message);
    }

    /**
     * 连接目标wifi
     */
    private void connectWifi(String ssid) {
        wiseFy.connectToNetwork(ssid, 8000, new ConnectToNetworkCallbacks() {
            @Override
            public void connectedToNetwork() {
                Logger.e("连接成功");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        startSocket();
                    }
                }).start();
            }

            @Override
            public void connectToNetworkWiseFyFailure(int i) {
                showMyFailPopupWindow("当前设备连接失败，是否需要重试？");
            }

            @Override
            public void failureConnectingToNetwork() {
                showMyFailPopupWindow("当前设备连接失败，是否需要重试？");
            }

            @Override
            public void networkNotFoundToConnectTo() {
                showMyFailPopupWindow("当前未连接到该类设备，是否需要重试？");
            }
        });
    }


    /**
     * 查询配对
     */
    private void startQueryNetwork() {
        Observable.intervalRange(1, 8, 5, 4, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<Long>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Observer<Long>() {
                    public ScanResultVo resultVo;
                    public String device_id;
                    public boolean isStart = false;

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Long aLong) {
                        RetrofitService.qdoApi.getIsWifiNetWorking(deviceMac, mSSID)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<IsWifiNetWorkVo>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onNext(IsWifiNetWorkVo isWifiNetWorkVo) {
                                        if (isWifiNetWorkVo != null) {
                                            if (isWifiNetWorkVo.getCode() == 200) {
                                                if (isWifiNetWorkVo.getData().getIs_networking() == 1) {
                                                    tvDeviceInit.setTextColor(getResources().getColor(R.color.room_manage_add_text));
                                                    device_id = isWifiNetWorkVo.getData().getDevice_id();
                                                    resultVo = new ScanResultVo(mSSID, CommonUtils.getDeviceName(mSSID), deviceMac, 2, -1, device_id);
                                                    isStart = true;
                                                    startActivity(new Intent(DeviceWifiSettingActivity.this, DeviceSetRoomActivity.class)
                                                            .putExtra("scanResult", resultVo));
                                                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                                    finish();
                                                }
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
                    public void onError(Throwable e) {
                        Logger.e("onerror");
                        showMyFailPopupWindow("配网失败，是否重试");
                    }

                    @Override
                    public void onComplete() {
                        Logger.e("oncomplete");
                        //成功则跳转到设置房间里
                        if (!isStart) {
                            if (resultVo != null) {
                                startActivity(new Intent(DeviceWifiSettingActivity.this, DeviceSetRoomActivity.class)
                                        .putExtra("device_id", device_id)
                                        .putExtra("scanResult", resultVo));
                                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                finish();
                            } else {
                                showMyFailPopupWindow("配网失败，是否重试");
                            }
                        }
                    }
                });
    }

    /**
     * 开始wifi模块配网
     */
    private void startSocket() {
        Socket socket = null;
        OutputStream out = null;
        PrintWriter pw = null;
        try {
            socket = new Socket();
            SocketAddress socketAddress = new InetSocketAddress("192.168.4.1", 8266);
            socket.connect(socketAddress, 5000);
            out = socket.getOutputStream();
            pw = new PrintWriter(out);
            JSONObject object = new JSONObject();
            object.put("state", 1);
            object.put("ssid", wifiSSID);
            object.put("psw", password);
            pw.write(object.toString());
            pw.flush();
        } catch (SocketTimeoutException Se) {
            showMyFailPopupWindow("当前设备连接失败，是否需要重试？");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (socket != null) {
                    socket.close();
                }
                if (pw != null) {
                    pw.close();
                }
                //获取服务器是否配网成功。20秒的超时处理
                handler.sendEmptyMessage(2);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 展示失败的popupWindow
     */
    private void showfailPopupWindow(String title) {
        if (!DeviceWifiSettingActivity.this.isFinishing()) {
            try {
                new MaterialDialog.Builder(this)
                        .content(title)
                        .negativeText("取消")
                        .positiveText("确定")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                //取消配网
                                cancleSetNetwork();
                                wiseFy.dump();
                                finish();
                            }
                        })
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                tvFindDevice.setTextColor(getResources().getColor(R.color.home_manage_text));
                                tvDeviceRegister.setTextColor(getResources().getColor(R.color.home_manage_text));
                                tvDeviceInit.setTextColor(getResources().getColor(R.color.home_manage_text));
                                cancleSetNetwork();
                                if (wiseFy.isNetworkSaved(mSSID)) {
                                    searchWifi();
                                } else {
                                    addWifi();
                                }
                            }
                        })
                        .canceledOnTouchOutside(false)
                        .show();
            } catch (WindowManager.BadTokenException e) {

            }
        }
    }

    /**
     * 展示失败的popupWindow
     */
    private void showBackPopupWindow(String title) {
        if (!DeviceWifiSettingActivity.this.isFinishing()) {
            try {
                new MaterialDialog.Builder(this)
                        .content(title)
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
                                //取消配网
                                cancleSetNetwork();
                                wiseFy.dump();
                                finish();
                            }
                        })
                        .canceledOnTouchOutside(false)
                        .show();
            } catch (WindowManager.BadTokenException e) {
            }
        }
    }

    /**
     * 取消配网
     */
    private void cancleSetNetwork() {
        RetrofitService.qdoApi.cancelWifiNetwork(deviceMac)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MessageVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MessageVo messageVo) {

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
    protected void onDestroy() {
        super.onDestroy();
        wiseFy.dump();
    }

    @Override
    public void onBackPressed() {
        showBackPopupWindow("您确定要取消配网吗");
    }
}
