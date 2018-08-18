package com.qvd.smartswitch.activity.device;


import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;
import com.iflytek.sunflower.FlowerCollector;
import com.orhanobut.logger.Logger;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.db.DeviceTimgTimeDaoOpe;
import com.qvd.smartswitch.model.DeviceTimgTimeVo;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.SnackbarUtils;
import com.qvd.smartswitch.utils.ToastUtil;
import com.qvd.smartswitch.widget.MyProgressDialog;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.carbswang.android.numberpickerview.library.NumberPickerView;

/**
 * Created by Administrator on 2018/4/17 0017.
 */

public class DeviceTimingActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.tv_device_timing_save)
    TextView tvDeviceTimingSave;
    @BindView(R.id.tv_device_timing_time)
    TextView tvDeviceTimingTime;
    @BindView(R.id.rl_device_timing_repeat)
    RelativeLayout rlDeviceTimingRepeat;
    @BindView(R.id.rl_device_timing_operation)
    RelativeLayout rlDeviceTimingOperation;
    @BindView(R.id.tv_device)
    TextView tvDevice;
    @BindView(R.id.tv_state)
    TextView tvState;
    @BindView(R.id.picker_hour)
    NumberPickerView pickerHour;
    @BindView(R.id.picker_minute)
    NumberPickerView pickerMinute;
    @BindView(R.id.tv_timing)
    TextView tvTiming;
    @BindView(R.id.tv_delete_timing)
    TextView tvDeleteTiming;
    @BindView(R.id.rl_timing)
    RelativeLayout rlTiming;


    private PopupWindow popupWindow;
    private PopupWindow popupWindow2;

    private BleDevice bledevice;
    /**
     * 判断当前选择哪个设备
     */
    private int isDevice = 1;

    /**
     * 判断当前操作
     *
     * @return
     */
    private boolean isState = true;

    /**
     * 当前小时
     */
    private String mHour;

    /**
     * 当前分钟
     */
    private String mMinute;
    private MyProgressDialog progressDialog;
    private int newHour = 0;
    private int newMinute = 0;

    /**
     * 获得存储的定时信息
     */
    private DeviceTimgTimeVo timgTime;
    private GregorianCalendar calendar;
    /**
     * 判断是否超过当天
     */
    private String isOutDay;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_device_timing;
    }

    @Override
    protected void initData() {
        super.initData();
        bledevice = getIntent().getParcelableExtra("bledevice");
        tvCommonActionbarTitle.setText("设备定时");
        List<DeviceTimgTimeVo> deviceTimgTimeVos = DeviceTimgTimeDaoOpe.queryList(this, CommonUtils.getMac(bledevice.getMac()));
        if (deviceTimgTimeVos != null) {
            timgTime = deviceTimgTimeVos.get(0);
        }
        if (timgTime != null) {
            if (CommonUtils.isEmptyString(timgTime.getContent())) {
                rlTiming.setVisibility(View.GONE);
            } else {
                tvTiming.setText(timgTime.getContent());
            }
        } else {
            rlTiming.setVisibility(View.GONE);
        }
        progressDialog = MyProgressDialog.createProgressDialog(this, 5000,
                new MyProgressDialog.OnTimeOutListener() {
                    @Override
                    public void onTimeOut(ProgressDialog dialog) {
                        dialog.dismiss();
                        ToastUtil.showToast("设置失败");
                    }
                });
        calendar = (GregorianCalendar) GregorianCalendar.getInstance();
        int h = calendar.get(Calendar.HOUR_OF_DAY);
        int m = calendar.get(Calendar.MINUTE);
        setData(pickerHour, 0, 23, h);
        setData(pickerMinute, 0, 59, m);
        pickerHour.setOnValueChangeListenerInScrolling(new NumberPickerView.OnValueChangeListenerInScrolling() {
            @Override
            public void onValueChangeInScrolling(NumberPickerView picker, int oldVal, int newVal) {
                newHour = oldVal;
                newMinute = pickerMinute.getValue();
                List<String> timing = CommonUtils.getTiming(newHour, newMinute);
                tvDeviceTimingTime.setText(timing.get(0) + "小时" + timing.get(1) + "分钟之后执行");
                mHour = timing.get(0);
                mMinute = timing.get(1);
                isOutDay = timing.get(2);
                Logger.e("hour->" + newVal);
            }
        });
        pickerMinute.setOnValueChangeListenerInScrolling(new NumberPickerView.OnValueChangeListenerInScrolling() {
            @Override
            public void onValueChangeInScrolling(NumberPickerView picker, int oldVal, int newVal) {
                newMinute = oldVal;
                newHour = pickerHour.getValue();
                List<String> timing = CommonUtils.getTiming(newHour, newMinute);
                tvDeviceTimingTime.setText(timing.get(0) + "小时" + timing.get(1) + "分钟后执行");
                mHour = timing.get(0);
                mMinute = timing.get(1);
                isOutDay = timing.get(2);
                Logger.e("minute->" + newVal);
            }
        });
        CommonUtils.getConnectNotify(this, bledevice, tvDevice);
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

    @OnClick({R.id.iv_common_actionbar_goback, R.id.tv_device_timing_save, R.id.rl_device_timing_repeat, R.id.rl_device_timing_operation, R.id.tv_delete_timing})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.tv_device_timing_save:
                //实现定时功能
                if (mHour.equals("00") && mMinute.equals("00")) {
                    SnackbarUtils.Short(view, "时间不能设置为零");
                    return;
                }
                setTiming();
                break;
            case R.id.rl_device_timing_repeat:
                showRepeatPopuwindow();
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                break;
            case R.id.rl_device_timing_operation:
                showOperationPopuwindow();
                popupWindow2.showAtLocation(view, Gravity.CENTER, 0, 0);
                break;
            case R.id.tv_delete_timing:
                //取消定时
                show();
                break;
        }
    }

    private void show() {
        new MaterialDialog.Builder(this)
                .title("您确定要删除这条智能吗？")
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
                        writeToBleCancleTiming();
                    }
                })
                .show();
    }

    /**
     * 取消定时
     */
    private void writeToBleCancleTiming() {
        progressDialog.setMessage("正在设置");
        progressDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                BleManager.getInstance().write(bledevice, "0000fff0-0000-1000-8000-00805f9b34fb", "0000fff6-0000-1000-8000-00805f9b34fb", HexUtil.hexStringToBytes("FE030408FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF"), new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        DeviceTimgTimeDaoOpe.deleteData(DeviceTimingActivity.this, timgTime);
                        rlTiming.setVisibility(View.GONE);
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {

                    }
                });
            }
        }, 100);
    }

    /**
     * 设置定时
     */
    private void setTiming() {
        String one;
        if (isDevice == 1) {
            if (isState) {
                one = "21";
            } else {
                one = "20";
            }
        } else {
            if (isState) {
                one = "11";
            } else {
                one = "10";
            }
        }
        String s = "fe0304" + one + mHour + mMinute + "ffffffffffffffffffffffffffff";
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
        BleManager.getInstance().write(bledevice, "0000fff0-0000-1000-8000-00805f9b34fb", "0000fff6-0000-1000-8000-00805f9b34fb", HexUtil.hexStringToBytes(s), new BleWriteCallback() {
            @Override
            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                Logger.e("write success, current: " + current
                        + " total: " + total
                        + " justWrite: " + HexUtil.formatHexString(justWrite, true));
                String content = "";
                if (isDevice == 1) {
                    content += "电灯一";
                } else {
                    content += "电灯二";
                }
                if (isOutDay.equals("true")) {
                    content += (calendar.get(Calendar.MONTH) + 1) + "月" + (calendar.get(Calendar.DAY_OF_MONTH) + 1) + "日" + newHour + "时" + newMinute + "分";
                } else {
                    content += (calendar.get(Calendar.MONTH) + 1) + "月" + calendar.get(Calendar.DAY_OF_MONTH) + "日" + newHour + "时" + newMinute + "分";
                }
                if (isState) {
                    content += "开";
                } else {
                    content += "关";
                }
                DeviceTimgTimeDaoOpe.insertData(DeviceTimingActivity.this, new DeviceTimgTimeVo(null, CommonUtils.getMac(bledevice.getMac()), content));
                //SharedPreferencesUtil.putTimgTime(DeviceTimingActivity.this, SharedPreferencesUtil.TIMG_TIME + bledevice.getMac(), content);
                progressDialog.dismiss();
                ToastUtil.showToast("定时成功");
                finish();
            }

            @Override
            public void onWriteFailure(BleException exception) {
                Logger.e("write" + exception.toString());
            }
        });
    }

    /**
     * 设置重复的popuwindow
     */
    private void showRepeatPopuwindow() {
        LayoutInflater inflater = LayoutInflater.from(this);
        // 引入窗口配置文件
        View view = inflater.inflate(R.layout.popupwindow_repeat, null, false);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, false);
        TextView tv_one = view.findViewById(R.id.tv_one);
        TextView tv_two = view.findViewById(R.id.tv_two);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.setAnimationStyle(R.style.AnimBottom);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        CommonUtils.setBackgroundAlpha(this, 0.5f);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                CommonUtils.setBackgroundAlpha(DeviceTimingActivity.this, 1.0f);
            }
        });

        tv_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDevice = 1;
                tvDevice.setText("一");
                popupWindow.dismiss();
            }
        });

        tv_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDevice = 2;
                tvDevice.setText("二");
                popupWindow.dismiss();
            }
        });
    }

    /**
     * 设置操作的popuwindow
     */
    private void showOperationPopuwindow() {
        LayoutInflater inflater = LayoutInflater.from(this);
        // 引入窗口配置文件
        View view = inflater.inflate(R.layout.popupwindow_operation, null);
        popupWindow2 = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, false);
        TextView tv_popupwindow_open = view.findViewById(R.id.tv_popupwindow_open);
        TextView tv_popupwindow_close = view.findViewById(R.id.tv_popupwindow_close);
        popupWindow2.setBackgroundDrawable(new ColorDrawable());
        popupWindow2.setAnimationStyle(R.style.AnimBottom);
        popupWindow2.setOutsideTouchable(true);
        popupWindow2.setFocusable(true);
        CommonUtils.setBackgroundAlpha(this, 0.5f);
        popupWindow2.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                CommonUtils.setBackgroundAlpha(DeviceTimingActivity.this, 1.0f);
            }
        });

        tv_popupwindow_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isState = true;
                tvState.setText("开");
                popupWindow2.dismiss();
            }
        });

        tv_popupwindow_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isState = false;
                tvState.setText("关");
                popupWindow2.dismiss();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BleManager.getInstance().stopNotify(bledevice, "0000fff0-0000-1000-8000-00805f9b34fb", "0000fff6-0000-1000-8000-00805f9b34fb");
    }

}
