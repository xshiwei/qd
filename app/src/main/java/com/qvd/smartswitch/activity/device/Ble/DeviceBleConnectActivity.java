package com.qvd.smartswitch.activity.device.Ble;

import android.content.Intent;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.activity.base.BaseHandler;
import com.qvd.smartswitch.activity.base.BaseRunnable;
import com.qvd.smartswitch.activity.device.DeviceSetRoomActivity;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.device.AddQS02Vo;
import com.qvd.smartswitch.model.device.ScanResultVo;
import com.qvd.smartswitch.utils.ConfigUtils;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/6/12 0012.
 */

public class DeviceBleConnectActivity extends BaseActivity {

    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.ll_one)
    RelativeLayout llOne;
    @BindView(R.id.avi_loading)
    AVLoadingIndicatorView aviLoading;
    @BindView(R.id.avi_one)
    AVLoadingIndicatorView aviOne;
    @BindView(R.id.tv_one)
    TextView tvOne;
    @BindView(R.id.avi_two)
    AVLoadingIndicatorView aviTwo;
    @BindView(R.id.tv_two)
    TextView tvTwo;
    @BindView(R.id.ll_two)
    RelativeLayout llTwo;
    @BindView(R.id.avi_three)
    AVLoadingIndicatorView aviThree;
    @BindView(R.id.tv_three)
    TextView tvThree;
    @BindView(R.id.ll_three)
    RelativeLayout llThree;
    @BindView(R.id.avi_four)
    AVLoadingIndicatorView aviFour;
    @BindView(R.id.tv_four)
    TextView tvFour;
    @BindView(R.id.ll_four)
    RelativeLayout llFour;
    @BindView(R.id.ll_complete)
    LinearLayout llComplete;

    private TextView tvComplete;
    /**
     * 当前设备
     */
    private ScanResultVo bleDevice;

    /**
     * 返回的device_id
     */
    private String device_id;

    private final MyHandle myHandle = new MyHandle(this);

    private static class MyHandle extends BaseHandler<DeviceBleConnectActivity> {

        MyHandle(DeviceBleConnectActivity reference) {
            super(reference);
        }

        @Override
        protected void handleMessage(DeviceBleConnectActivity reference, Message msg) {

        }
    }


    @Override
    protected int setLayoutId() {
        return R.layout.activity_device_connect;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }

    @Override
    protected void initData() {
        super.initData();
        tvComplete = findViewById(R.id.tv_complete);
        tvCommonActionbarTitle.setText(R.string.device_ble_connect_title);
        bleDevice = (ScanResultVo) getIntent().getSerializableExtra("ScanResultVo");
        initDeviceConnect();
    }


    /**
     * 初始化
     */
    private void initDeviceConnect() {
        llOne.setVisibility(View.VISIBLE);
        aviLoading.show();
        llComplete.setVisibility(View.GONE);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        aviOne.hide();
        llTwo.setVisibility(View.VISIBLE);
        addDevice();
    }

    /**
     * 添加蓝牙设备
     */
    private void addDevice() {
        switch (bleDevice.getDeviceNo()) {
            case "qs02":
                addQS02();
                break;
        }
    }


    /**
     * 添加QS02设备
     */
    private void addQS02() {
        Map<String, String> map = new HashMap<>();
        map.put("device_mac", bleDevice.getDeviceMac());
        map.put("room_id", ConfigUtils.defaultRoomId);
        map.put("user_id", ConfigUtils.user_id);
        map.put("family_id", ConfigUtils.family_locate.getFamily_id());
        RetrofitService.qdoApi.addDeviceQS02(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AddQS02Vo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(AddQS02Vo messageVo) {
                        if (messageVo != null) {
                            switch (messageVo.getCode()) {
                                case 200:
                                    aviTwo.hide();
                                    llThree.setVisibility(View.VISIBLE);
                                    myHandle.postDelayed(new BaseRunnable(() -> {
                                        aviThree.hide();
                                        llFour.setVisibility(View.VISIBLE);
                                    }), 1000);
                                    myHandle.postDelayed(new BaseRunnable(() -> {
                                        aviFour.hide();
                                        llComplete.setVisibility(View.VISIBLE);
                                        device_id = messageVo.getDevice_id();
                                        setDefault();
                                    }), 1000);
                                    break;
                                case 500:
                                    aviTwo.hide();
                                    llComplete.setVisibility(View.VISIBLE);
                                    tvComplete.setText(R.string.device_ble_connect_isbinging);
                                    break;
                                default:
                                    aviTwo.hide();
                                    llComplete.setVisibility(View.VISIBLE);
                                    tvComplete.setText(R.string.device_ble_connect_add_fail);
                                    break;
                            }
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        aviTwo.hide();
                        llComplete.setVisibility(View.VISIBLE);
                        tvComplete.setText(R.string.device_ble_connect_add_fail);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    /*
    设置默认3秒后进入
     */
    private void setDefault() {
        Observable.intervalRange(1, 3, 0, 1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Long aLong) {
                        int time = 4 - aLong.intValue();
                        tvComplete.setText("完成(" + time + ")");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        bleDevice.setDeviceId(device_id);
                        startActivity(new Intent(DeviceBleConnectActivity.this, DeviceSetRoomActivity.class)
                                .putExtra("scanResult", bleDevice));
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                        finish();
                    }
                });
    }

    @OnClick({R.id.iv_common_actionbar_goback, R.id.tv_complete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.tv_complete:
                //失败
                if (tvComplete.getText().toString().equals(R.string.device_ble_connect_add_fail)) {
                    llOne.setVisibility(View.GONE);
                    llTwo.setVisibility(View.GONE);
                    llThree.setVisibility(View.GONE);
                    llFour.setVisibility(View.GONE);
                    initDeviceConnect();
                } else if (tvComplete.getText().toString().equals(getString(R.string.device_ble_connect_isbinging))) {
                    finish();
                } else {
                    //成功则跳转到设置房间里
                    bleDevice.setDeviceId(device_id);
                    startActivity(new Intent(this, DeviceSetRoomActivity.class)
                            .putExtra("scanResult", bleDevice));
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    finish();
                }
                break;
        }
    }

}
