package com.qvd.smartswitch.activity.device.wifi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.isupatches.wisefy.WiseFy;
import com.isupatches.wisefy.callbacks.SearchForAccessPointCallbacks;
import com.orhanobut.logger.Logger;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseHandler;
import com.qvd.smartswitch.activity.base.BaseNoTipActivity;
import com.qvd.smartswitch.activity.base.BaseRunnable;
import com.qvd.smartswitch.activity.device.DeviceSetRoomActivity;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.device.IsWifiNetWorkVo;
import com.qvd.smartswitch.model.device.ScanResultVo;
import com.qvd.smartswitch.model.login.MessageVo;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.ConfigUtils;
import com.qvd.smartswitch.utils.PermissionUtils;
import com.qvd.smartswitch.utils.ToastUtil;
import com.qvd.smartswitch.utils.WifiAdmin;
import com.qvd.smartswitch.utils.WifiConnect;
import com.qvd.smartswitch.widget.WaveProgress;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.yanzhenjie.permission.Permission;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Objects;
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
    @BindView(R.id.tv_device_init)
    TextView tvDeviceInit;
    @BindView(R.id.rl_device_connect)
    RelativeLayout rlDeviceConnect;

    /**
     * wifi控制实例
     */
    private WiseFy wiseFy;

    private TextView tvDeviceRegister;

    private TextView tvFindDevice;

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

    private WaveProgress waveProgress;

    private final MyHandle myHandle = new MyHandle(this);

    private static class MyHandle extends BaseHandler<DeviceWifiSettingActivity> {

        MyHandle(DeviceWifiSettingActivity reference) {
            super(reference);
        }

        @Override
        protected void handleMessage(DeviceWifiSettingActivity reference, Message msg) {
            int what = msg.what;
            switch (what) {
                case 0:
                    String obj = (String) msg.obj;
                    reference.showfailPopupWindow(obj);
                    break;
                case 1:
                    reference.tvFindDevice.setTextColor(reference.getResources().getColor(R.color.room_manage_add_text));
                    break;
                case 2:
                    reference.tvDeviceRegister.setTextColor(reference.getResources().getColor(R.color.room_manage_add_text));
                    reference.startQueryNetwork();
                    break;
            }
        }
    }

    //获取到wifi的信息
    private String mSSID;
    /**
     * 设备mac地址
     */
    private String deviceMac;

    private Disposable disposable;
    private WifiAdmin wifiAdmin;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_device_wifi_setting;
    }

    @Override
    protected void initData() {
        super.initData();
        tvDeviceRegister = findViewById(R.id.tv_device_register);
        tvFindDevice = findViewById(R.id.tv_find_device);
        waveProgress = findViewById(R.id.wave_progress_bar);
        PermissionUtils.requestPermission(this, Permission.Group.LOCATION);
        mSSID = getIntent().getStringExtra("wifi_ssid");
        wiseFy = new WiseFy.Brains(this).logging(true).getSmarts();
        wifiAdmin = new WifiAdmin(this);
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
            myHandle.postDelayed(new BaseRunnable(DeviceWifiSettingActivity.this::showOpenWifiPopupWindow), 500);
        }
    }

    /**
     * 判断当前是否有连接wifi
     */
    private void isConnectWifi() {
        //判断当前设备是否连接到wifi网络
        if (wiseFy.isDeviceConnectedToWifiNetwork()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ToastUtil.showToast(getString(R.string.common_location_not_open));
                return;
            }
            //获取当前连接wifi信息 SSID: Xiaomi_Qvedo, BSSID: 40:31:3c:10:a4:26, MAC: 02:00:00:00:00:00, Supplicant state: COMPLETED, RSSI: -37, Link speed: 144Mbps, Frequency: 2412MHz, Net ID: 3, Metered hint: false, score: 60
            /*
      当前连接的wifi
     */
            WifiInfo currentNetwork = wiseFy.getCurrentNetwork();
            String ssid = Objects.requireNonNull(currentNetwork).getSSID();
            //设置当前wifi名称
            tvWifiName.setText(getString(R.string.device_wifi_setting_current_wifi) + getNewSSID(ssid));
        } else {
            tvWifiName.setText(R.string.device_share_setting_current_not_wifi);
        }
    }

    private String getNewSSID(String ssid) {
        return ssid.substring(1, ssid.length() - 1);
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
                .content(R.string.common_open_wifi)
                .negativeText(R.string.common_cancel)
                .positiveText(R.string.common_confirm)
                .onNegative((dialog, which) -> finish())
                .onPositive((dialog, which) -> {
                    boolean b = wiseFy.enableWifi();
                    if (b) {
                        ToastUtil.showToast(getString(R.string.common_wifi_open_success));
                        isConnectWifi();
                    } else {
                        ToastUtil.showToast(getString(R.string.common_wifi_open_fail));
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
                showBackPopupWindow();
                break;
            case R.id.tv_change_network:
                Intent intent = new Intent();
                ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.wifi.WifiSettings");
                intent.setComponent(cn);
                startActivity(intent);
                break;
            case R.id.tv_confirm:
                if (CommonUtils.isEmptyString(etPassword.getText().toString())) {
                    ToastUtil.showToast(getString(R.string.device_wifi_setting_password_not_empty));
                } else if (!wiseFy.isDeviceConnectedToWifiNetwork()) {
                    tvWifiName.setText(R.string.common_current_not_wifi_connect);
                    ToastUtil.showToast(getString(R.string.common_not_connect_wifi));
                } else {
                    //保存密码
                    password = etPassword.getText().toString();
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ToastUtil.showToast(getString(R.string.common_location_not_open));
                        return;
                    }
                    wifiSSID = getNewSSID(Objects.requireNonNull(wiseFy.getCurrentNetwork()).getSSID());
                    rlSettingWifiPassword.setVisibility(View.GONE);
                    tvText.setVisibility(View.GONE);
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    rlDeviceConnect.setVisibility(View.VISIBLE);
                    startProgress();
                    searchWifi();
                }
                break;
        }
    }

    /**
     * 开始进度
     */
    private void startProgress() {
        waveProgress.setValue(0);
        disposable = Observable.intervalRange(1, 49, 0, 1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> waveProgress.setValue(aLong * 2));
    }

    /**
     * 重新开始
     */
    private void resetProgress() {
        if (disposable != null) {
            disposable.dispose();
        }
        startProgress();
    }

    /**
     * 开始搜索
     */
    private void searchWifi() {
        wiseFy.searchForAccessPoint(mSSID, 5000, true, new SearchForAccessPointCallbacks() {
            @Override
            public void wisefyFailure(int i) {
                showMyFailPopupWindow(getString(R.string.common_current_search_fail));
            }

            @Override
            public void accessPointFound(@NotNull ScanResult scanResult) {
                //2、开始连接Wifi
                deviceMac = scanResult.BSSID;
                myHandle.sendEmptyMessage(1);
                sendDeviceMessage(mSSID);
            }

            @Override
            public void accessPointNotFound() {
                showMyFailPopupWindow(getString(R.string.common_not_search_and_retry));
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
                            Logger.e("code==" + messageVo.getCode());
                            if (messageVo.getCode() == 200) {
                                connectWifi(ssid);
                            } else {
                                showMyFailPopupWindow(getString(R.string.device_wifi_setting_insure_wifi));
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e(e.getMessage());
                        showMyFailPopupWindow(getString(R.string.device_wifi_setting_insure_wifi));
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
        myHandle.sendMessage(message);
    }

    /**
     * 连接目标wifi
     */
    private void connectWifi(String ssid) {
        if (wifiAdmin.connect(ssid, "88888888", WifiConnect.WifiCipherType.WIFICIPHER_WPA)) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    Logger.e("开始socket");
                    startSocket();
                }
            }.start();
        } else {
            showMyFailPopupWindow(getString(R.string.common_device_connect_fail));
        }
    }


    /**
     * 查询配对
     */
    private void startQueryNetwork() {
        Observable.intervalRange(1, 8, 5, 5, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Observer<Long>() {
                    ScanResultVo resultVo;
                    String device_id;
                    boolean isStart = false;

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
                                                    Logger.e("success==" + isWifiNetWorkVo.getData().getDevice_id() + isWifiNetWorkVo.getCode() + isWifiNetWorkVo.getData().getIs_networking() + isWifiNetWorkVo.getMessage());
                                                    waveProgress.setValue(100);
                                                    tvDeviceInit.setTextColor(getResources().getColor(R.color.room_manage_add_text));
                                                    device_id = isWifiNetWorkVo.getData().getDevice_id();
                                                    resultVo = new ScanResultVo(mSSID, CommonUtils.getDeviceName(mSSID), deviceMac, 2, -1, device_id);
                                                    isStart = true;
                                                    startActivity(new Intent(DeviceWifiSettingActivity.this, DeviceSetRoomActivity.class)
                                                            .putExtra("scanResult", resultVo));
                                                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                                    finish();
                                                }
                                            } else {
                                                Logger.e(isWifiNetWorkVo.getCode() + isWifiNetWorkVo.getMessage());
                                            }
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Logger.e(e.getMessage());
                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                });
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e(e.getMessage());
                        showMyFailPopupWindow(getString(R.string.common_distribution_network_failure));
                    }

                    @Override
                    public void onComplete() {
                        Logger.e("oncomplete");
                        //成功则跳转到设置房间里
                        if (!isStart) {
                            if (resultVo != null) {
                                waveProgress.setValue(100);
                                startActivity(new Intent(DeviceWifiSettingActivity.this, DeviceSetRoomActivity.class)
                                        .putExtra("device_id", device_id)
                                        .putExtra("scanResult", resultVo));
                                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                finish();
                            } else {
                                showMyFailPopupWindow(getString(R.string.common_distribution_network_failure));
                            }
                        }
                    }
                });
    }

    /**
     * 开始wifi模块配网
     */
    @SuppressLint("MissingPermission")
    private void startSocket() {
        Socket socket = null;
        OutputStream out = null;
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress("192.168.4.1", 8266), 5000);
            Logger.e("开始发送");
            Logger.e("localport===" + socket.getLocalPort());
            out = socket.getOutputStream();
            JSONObject object = new JSONObject();
            object.put("state", 1);
            object.put("ssid", wifiSSID);
            object.put("psw", password);
            Logger.e("wifi===" + object.toString());
            out.write(object.toString().getBytes("utf-8"));
            out.flush();
        } catch (SocketTimeoutException Se) {
            showMyFailPopupWindow(getString(R.string.common_distribution_network_and_retry));
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (socket != null) {
                    socket.close();
                }
//                wiseFy.connectToNetwork(wifiSSID, 5000);
                myHandle.sendEmptyMessage(2);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 展示失败的popupWindow
     */
    private void showfailPopupWindow(String title) {
        if (disposable != null) {
            disposable.dispose();
        }
        if (!DeviceWifiSettingActivity.this.isFinishing()) {
            try {
                new MaterialDialog.Builder(this)
                        .content(title)
                        .negativeText(getString(R.string.common_cancel))
                        .positiveText(getString(R.string.common_confirm))
                        .onNegative((dialog, which) -> {
                            //取消配网
                            cancleSetNetwork();
                            wiseFy.dump();
                            finish();
                        })
                        .onPositive((dialog, which) -> {
                            startProgress();
                            tvFindDevice.setTextColor(getResources().getColor(R.color.home_manage_text));
                            tvDeviceRegister.setTextColor(getResources().getColor(R.color.home_manage_text));
                            tvDeviceInit.setTextColor(getResources().getColor(R.color.home_manage_text));
                            cancleSetNetwork();
                            searchWifi();
                        })
                        .canceledOnTouchOutside(false)
                        .show();
            } catch (WindowManager.BadTokenException ignored) {

            }
        }
    }

    /**
     * 展示失败的popupWindow
     */
    private void showBackPopupWindow() {
        if (!DeviceWifiSettingActivity.this.isFinishing()) {
            try {
                new MaterialDialog.Builder(this)
                        .content(R.string.common_insure_cancel_distribution_network)
                        .negativeText(getString(R.string.common_cancel))
                        .positiveText(getString(R.string.common_confirm))
                        .onNegative((dialog, which) -> {
                        })
                        .onPositive((dialog, which) -> {
                            //取消配网
                            resetProgress();
                            cancleSetNetwork();
                            wiseFy.dump();
                            finish();
                        })
                        .canceledOnTouchOutside(false)
                        .show();
            } catch (WindowManager.BadTokenException ignored) {
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
        showBackPopupWindow();
    }
}
