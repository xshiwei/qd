package com.qvd.smartswitch.activity.qsTwo;


import android.app.ProgressDialog;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.device.DeviceAddTimingVo;
import com.qvd.smartswitch.model.login.MessageVo;
import com.qvd.smartswitch.utils.BleManageUtils;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.SnackbarUtils;
import com.qvd.smartswitch.utils.ToastUtil;
import com.qvd.smartswitch.widget.MyProgressDialog;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.carbswang.android.numberpickerview.library.NumberPickerView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by Administrator on 2018/4/17 0017.
 */

public class QsTwoTimingActivity extends BaseActivity {


    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.tv_device_timing_save)
    TextView tvDeviceTimingSave;
    @BindView(R.id.tv_open_light)
    TextView tvOpenLight;
    @BindView(R.id.tv_close_light)
    TextView tvCloseLight;
    @BindView(R.id.tv_device_one)
    TextView tvDeviceOne;
    @BindView(R.id.tv_device_two)
    TextView tvDeviceTwo;

    private BleDevice bledevice;
    /**
     * 判断当前选择哪个设备
     */
    private int isDevice = 1;

    private NumberPickerView pickerHour;
    private NumberPickerView pickerMinute;
    private TextView tvText;

    /**
     * 判断当前操作
     *
     * @return
     */
    private boolean isState = true;

    private MyProgressDialog progressDialog;


    /**
     * 表示当前滚轮的值
     */
    private int newHour = 0;
    private int newMinute = 0;

    /**
     * 表示当前间隔小时数
     */
    private String hourTime;
    private String minuteTime;

    private GregorianCalendar calendar;
    private String device_id;

    /**
     * 判断当前设备是否有定时在执行 true表示有 false表示没有
     */
    private boolean state;
    private String timing_id;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_qstwo_timing;
    }

    @Override
    protected void initData() {
        super.initData();
        pickerHour = findViewById(R.id.picker_hour);
        pickerMinute = findViewById(R.id.picker_minute);
        tvText = findViewById(R.id.tv_text);
        bledevice = getIntent().getParcelableExtra("bledevice");
        device_id = getIntent().getStringExtra("device_id");
        state = getIntent().getBooleanExtra("state", false);
        timing_id = getIntent().getStringExtra("timing_id");
        tvCommonActionbarTitle.setText("蓝牙智能开关定时");
        progressDialog = MyProgressDialog.createProgressDialog(this, 5000,
                new MyProgressDialog.OnTimeOutListener() {
                    @Override
                    public void onTimeOut(ProgressDialog dialog) {
                        dialog.dismiss();
                        ToastUtil.showToast("设置失败");
                    }
                });
        calendar = new GregorianCalendar();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        setData(pickerHour, 0, 23, hour);
        setData(pickerMinute, 0, 59, minute);
        pickerHour.setOnValueChangeListenerInScrolling(new NumberPickerView.OnValueChangeListenerInScrolling() {
            @Override
            public void onValueChangeInScrolling(NumberPickerView picker, int oldVal, int newVal) {
                newHour = oldVal;
                newMinute = pickerMinute.getValue();
                List<String> timing = CommonUtils.getTiming(newHour, newMinute);
                hourTime = timing.get(0);
                minuteTime = timing.get(1);
                tvText.setText(timing.get(0) + "小时" + timing.get(1) + "分钟后");
            }
        });
        pickerMinute.setOnValueChangeListenerInScrolling(new NumberPickerView.OnValueChangeListenerInScrolling() {
            @Override
            public void onValueChangeInScrolling(NumberPickerView picker, int oldVal, int newVal) {
                newMinute = oldVal;
                newHour = pickerHour.getValue();
                List<String> timing = CommonUtils.getTiming(newHour, newMinute);
                hourTime = timing.get(0);
                minuteTime = timing.get(1);
                tvText.setText(timing.get(0) + "小时" + timing.get(1) + "分钟后");
            }
        });
//        if (bledevice != null) {
//            CommonUtils.getConnectNotify(this, bledevice, tvCommonActionbarTitle);
//        }
    }

    private void setData(NumberPickerView picker, int minValue, int maxValue, int value) {
        picker.setMinValue(minValue);
        picker.setMaxValue(maxValue);
        picker.setValue(value);
    }


    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }


    /**
     * 设置定时
     */
    private void setTiming() {
        String one;
        if (isDevice == 1) {
            if (isState) {
                one = "11";
            } else {
                one = "10";
            }
        } else {
            if (isState) {
                one = "21";
            } else {
                one = "20";
            }
        }
        String s = "fe0304" + one + hourTime + minuteTime + "ffffffffffffffffffffffffffff";
        if (BleManager.getInstance().isConnected(bledevice)) {
            writeToBle(s);
        } else {
            ToastUtil.showToast("当前设备已断开");
        }

    }

    /**
     * 添加定时
     */
    private void setTimingData() {
        Gson gson = new Gson();
        DeviceAddTimingVo deviceAddTimingVo = new DeviceAddTimingVo();
        List<String> list = new ArrayList<>();
        deviceAddTimingVo.setStart_time(CommonUtils.getDate());
        deviceAddTimingVo.setDevice_id(device_id);
        deviceAddTimingVo.setEnd_time(newHour + ":" + newMinute);
        deviceAddTimingVo.setTiming_content(getContent());
        deviceAddTimingVo.setTiming_date(list);
        String s = gson.toJson(deviceAddTimingVo);
        RequestBody body = RequestBody.create(MediaType.parse("Content-Type: application/json"), s);
        RetrofitService.qdoApi.addDeviceTiming(body)
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
                        ToastUtil.showToast("定时失败");
                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }


    /**
     * 向Ble写入数据
     *
     * @param s
     */
    private void writeToBle(String s) {
        progressDialog.setMessage("正在设置");
        progressDialog.show();
        BleManager.getInstance().write(bledevice, "0000fff0-0000-1000-8000-00805f9b34fb", "0000fff7-0000-1000-8000-00805f9b34fb", HexUtil.hexStringToBytes(s), new BleWriteCallback() {
            @Override
            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                progressDialog.dismiss();
                setTimingData();
                ToastUtil.showToast("定时成功");
                finish();
            }

            @Override
            public void onWriteFailure(BleException exception) {
                Logger.e("write" + exception.toString());
            }
        });
    }

    private String getContent() {
        String text1 = "";
        String text2 = "";
        String content = "";
        if (isState) {
            text1 = "打开";
        } else {
            text1 = "关闭";
        }
        if (isDevice == 1) {
            text2 = "电灯一";
        } else {
            text2 = "电灯二";
        }
        content = "定时设置在" + hourTime + "小时" + minuteTime + "分钟后" + text1 + text2;
        return content;
    }

    /**
     * 当前假如有定时正在执行，则提示用户
     */
    private void showDialogTip() {
        new MaterialDialog.Builder(this)
                .content("您当前有一个定时正在执行，蓝牙设备只支持设置一个定时，您是否确认取消上一个定时来设置当前定时？")
                .negativeText("取消")
                .positiveText("确认")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        updateTimingState();
                    }
                }).show();
    }

    /**
     * 更新定时的状态
     */
    private void updateTimingState() {
        RetrofitService.qdoApi.updateDeviceTimingState(timing_id, "0")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MessageVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MessageVo messageVo) {
                        if (messageVo.getCode() == 200) {
                            setTiming();
                        } else {
                            ToastUtil.showToast("设置失败");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showToast("设置失败");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @OnClick({R.id.iv_common_actionbar_goback, R.id.tv_device_timing_save, R.id.tv_open_light, R.id.tv_close_light, R.id.tv_device_one, R.id.tv_device_two})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.tv_device_timing_save:
                //实现定时功能
                if (newHour == 00 && newMinute == 00) {
                    ToastUtil.showToast("时间不能设置为零");
                    return;
                }
                if (bledevice != null) {
                    if (state) {
                        showDialogTip();
                    } else {
                        setTiming();
                    }
                } else {
                    ToastUtil.showToast("设备未连接，不能设置定时");
                }
                break;
            case R.id.tv_open_light:
                tvOpenLight.setTextColor(getResources().getColor(R.color.white));
                tvCloseLight.setTextColor(getResources().getColor(R.color.home_setting_text));
                tvOpenLight.setBackground(getResources().getDrawable(R.drawable.circle_green_five));
                tvCloseLight.setBackground(getResources().getDrawable(R.drawable.circle_white_five));
                isState = true;
                break;
            case R.id.tv_close_light:
                tvOpenLight.setTextColor(getResources().getColor(R.color.home_setting_text));
                tvCloseLight.setTextColor(getResources().getColor(R.color.white));
                tvOpenLight.setBackground(getResources().getDrawable(R.drawable.circle_white_five));
                tvCloseLight.setBackground(getResources().getDrawable(R.drawable.circle_green_five));
                isState = false;
                break;
            case R.id.tv_device_one:
                tvDeviceOne.setTextColor(getResources().getColor(R.color.white));
                tvDeviceTwo.setTextColor(getResources().getColor(R.color.home_setting_text));
                tvDeviceOne.setBackground(getResources().getDrawable(R.drawable.circle_green_five));
                tvDeviceTwo.setBackground(getResources().getDrawable(R.drawable.circle_white_five));
                isDevice = 1;
                break;
            case R.id.tv_device_two:
                tvDeviceOne.setTextColor(getResources().getColor(R.color.home_setting_text));
                tvDeviceTwo.setTextColor(getResources().getColor(R.color.white));
                tvDeviceOne.setBackground(getResources().getDrawable(R.drawable.circle_white_five));
                tvDeviceTwo.setBackground(getResources().getDrawable(R.drawable.circle_green_five));
                isDevice = 2;
                break;
        }
    }
}
