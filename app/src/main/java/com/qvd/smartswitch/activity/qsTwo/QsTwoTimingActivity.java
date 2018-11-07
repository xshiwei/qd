package com.qvd.smartswitch.activity.qsTwo;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.clj.fastble.BleManager;
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
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.ToastUtil;
import com.qvd.smartswitch.widget.MyProgressDialog;

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

    private BleDevice bledevice;

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
        tvCommonActionbarTitle.setText(R.string.qstwo_timing_title);
        progressDialog = MyProgressDialog.createProgressDialog(this, 5000,
                dialog -> {
                    dialog.dismiss();
                    ToastUtil.showToast(getString(R.string.common_set_fail));
                });
        GregorianCalendar calendar = new GregorianCalendar();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        setData(pickerHour, 23, hour);
        setData(pickerMinute, 59, minute);
        pickerHour.setOnValueChangeListenerInScrolling((picker, oldVal, newVal) -> {
            newHour = oldVal;
            newMinute = pickerMinute.getValue();
            List<String> timing = CommonUtils.getTiming(newHour, newMinute);
            hourTime = timing.get(0);
            minuteTime = timing.get(1);
            tvText.setText(timing.get(0) + getString(R.string.common_hour) + timing.get(1) + getString(R.string.comon_minute));
        });
        pickerMinute.setOnValueChangeListenerInScrolling((picker, oldVal, newVal) -> {
            newMinute = oldVal;
            newHour = pickerHour.getValue();
            List<String> timing = CommonUtils.getTiming(newHour, newMinute);
            hourTime = timing.get(0);
            minuteTime = timing.get(1);
            tvText.setText(timing.get(0) + getString(R.string.common_hour) + timing.get(1) + getString(R.string.comon_minute));
        });
    }

    private void setData(NumberPickerView picker, int maxValue, int value) {
        picker.setMinValue(0);
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
        if (isState) {
            one = "11";
        } else {
            one = "10";
        }
        String s = "fe0304" + one + hourTime + minuteTime + "ffffffffffffffffffffffffffff";
        if (BleManager.getInstance().isConnected(bledevice)) {
            writeToBle(s);
        } else {
            ToastUtil.showToast(getString(R.string.common_current_device_disconnect));
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
                        if (messageVo != null) {
                            if (messageVo.getCode() == 200) {
                                ToastUtil.showToast(getString(R.string.common_timing_success));
                                finish();
                            } else {
                                ToastUtil.showToast(getString(R.string.common_timing_fail));
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


    /**
     * 向Ble写入数据
     *
     * @param s
     */
    private void writeToBle(String s) {
        progressDialog.setMessage(getString(R.string.common_setting));
        progressDialog.show();
        BleManager.getInstance().write(bledevice, "0000fff0-0000-1000-8000-00805f9b34fb", "0000fff7-0000-1000-8000-00805f9b34fb", HexUtil.hexStringToBytes(s), new BleWriteCallback() {
            @Override
            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                progressDialog.dismiss();
                setTimingData();
            }

            @Override
            public void onWriteFailure(BleException exception) {
                Logger.e("write" + exception.toString());
            }
        });
    }

    private String getContent() {
        String text1;
        String content;
        if (isState) {
            text1 = "打开";
        } else {
            text1 = "关闭";
        }
        content = "定时设置在" + hourTime + "小时" + minuteTime + "分钟后" + text1;
        return content;
    }

    /**
     * 当前假如有定时正在执行，则提示用户
     */
    private void showDialogTip() {
        new MaterialDialog.Builder(this)
                .content(R.string.common_timing_tip)
                .negativeText(R.string.common_cancel)
                .positiveText(R.string.common_confirm)
                .onNegative((dialog, which) -> {

                })
                .onPositive((dialog, which) -> updateTimingState()).show();
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
                            ToastUtil.showToast(getString(R.string.common_set_fail));
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

    @OnClick({R.id.iv_common_actionbar_goback, R.id.tv_device_timing_save, R.id.tv_open_light, R.id.tv_close_light, R.id.tv_device_one, R.id.tv_device_two})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.tv_device_timing_save:
                //实现定时功能
                if (newHour == 0 && newMinute == 0) {
                    ToastUtil.showToast(getString(R.string.common_time_not_zero));
                    return;
                }
                if (bledevice != null) {
                    if (state) {
                        showDialogTip();
                    } else {
                        setTiming();
                    }
                } else {
                    ToastUtil.showToast(getString(R.string.common_device_disconnect_and_set_timing));
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
        }
    }
}
