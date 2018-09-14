package com.qvd.smartswitch.activity.qsOne;


import android.app.ProgressDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;
import com.orhanobut.logger.Logger;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.ToastUtil;
import com.qvd.smartswitch.widget.MyProgressDialog;

import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.OnClick;
import cn.carbswang.android.numberpickerview.library.NumberPickerView;

/**
 * Created by Administrator on 2018/4/17 0017.
 */

public class QsOneTimingActivity extends BaseActivity {


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

    private int newHour = 0;
    private int newMinute = 0;

    private GregorianCalendar calendar;
    private String device_id;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_qsone_timing;
    }

    @Override
    protected void initData() {
        super.initData();
        pickerHour = findViewById(R.id.picker_hour);
        pickerMinute = findViewById(R.id.picker_minute);
        tvText = findViewById(R.id.tv_text);
        bledevice = getIntent().getParcelableExtra("bledevice");
        device_id = getIntent().getStringExtra("device_id");
        tvCommonActionbarTitle.setText("蓝牙智能开关定时");
        progressDialog = MyProgressDialog.createProgressDialog(this, 5000,
                new MyProgressDialog.OnTimeOutListener() {
                    @Override
                    public void onTimeOut(ProgressDialog dialog) {
                        dialog.dismiss();
                        ToastUtil.showToast("设置失败");
                    }
                });
        setData(pickerHour, 0, 23, 0);
        setData(pickerMinute, 0, 59, 0);
        pickerHour.setOnValueChangeListenerInScrolling(new NumberPickerView.OnValueChangeListenerInScrolling() {
            @Override
            public void onValueChangeInScrolling(NumberPickerView picker, int oldVal, int newVal) {
                newHour = oldVal;
                newMinute = pickerMinute.getValue();
                tvText.setText(newHour + "小时" + newMinute + "分钟后");
            }
        });
        pickerMinute.setOnValueChangeListenerInScrolling(new NumberPickerView.OnValueChangeListenerInScrolling() {
            @Override
            public void onValueChangeInScrolling(NumberPickerView picker, int oldVal, int newVal) {
                newMinute = oldVal;
                newHour = pickerHour.getValue();
                Logger.e("minute->" + newVal);
                tvText.setText(newHour + "小时" + newMinute + "分钟后");
            }
        });
        CommonUtils.getConnectNotify(this, bledevice, tvCommonActionbarTitle);
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
        if (isState) {
            one = "11";
        } else {
            one = "10";
        }
        String s1 = "";
        String s2 = "";
        if (newHour < 10) {
            s1 = "0" + String.valueOf(newHour);
        }
        if (newMinute < 10) {
            s2 = "0" + String.valueOf(newMinute);
        }
        String s = "fe0304" + one + s1 + s2 + "ffffffffffffffffffffffffffff";
        Logger.e("content==" + s);
        writeToBle(s);
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
                ToastUtil.showToast("定时成功");
                String text1 = "";
                String content = "";
                if (isState) {
                    text1 = "打开";
                } else {
                    text1 = "关闭";
                }
                content = "定时设置在" + newHour + "小时" + newMinute + "分钟后" + text1;
                CommonUtils.addDeviceLog(device_id, bledevice.getName(), content);
                finish();
            }

            @Override
            public void onWriteFailure(BleException exception) {
                Logger.e("write" + exception.toString());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BleManager.getInstance().stopNotify(bledevice, "0000fff0-0000-1000-8000-00805f9b34fb", "0000fff6-0000-1000-8000-00805f9b34fb");
    }

    @OnClick({R.id.iv_common_actionbar_goback, R.id.tv_device_timing_save, R.id.tv_open_light, R.id.tv_close_light})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.tv_device_timing_save:
                //实现定时功能
                if (newHour == 0 && newMinute == 0) {
                    ToastUtil.showToast("时间不能设置为零");
                    return;
                }
                setTiming();
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
