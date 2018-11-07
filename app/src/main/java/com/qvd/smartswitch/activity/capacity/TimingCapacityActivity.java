package com.qvd.smartswitch.activity.capacity;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;

import java.util.Calendar;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.OnClick;
import cn.carbswang.android.numberpickerview.library.NumberPickerView;

/**
 * Created by Administrator on 2018/6/20 0020.
 */

public class TimingCapacityActivity extends BaseActivity {
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;
    @BindView(R.id.tv_timing)
    TextView tvTiming;
    @BindView(R.id.rl_timing)
    RelativeLayout rlTiming;
    @BindView(R.id.picker_hour)
    NumberPickerView pickerHour;
    @BindView(R.id.picker_minute)
    NumberPickerView pickerMinute;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_timing_capacity;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }

    @Override
    protected void initData() {
        super.initData();
        GregorianCalendar calendar = (GregorianCalendar) GregorianCalendar.getInstance();
        int h = calendar.get(Calendar.HOUR_OF_DAY);
        int m = calendar.get(Calendar.MINUTE);
        setData(pickerHour, 23, h);
        setData(pickerMinute, 59, m);
    }

    private void setData(NumberPickerView picker, int maxValue, int value) {
        picker.setMinValue(0);
        picker.setMaxValue(maxValue);
        picker.setValue(value);
    }

    @OnClick({R.id.tv_cancel, R.id.tv_confirm, R.id.rl_timing})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                break;
            case R.id.tv_confirm:
                break;
            case R.id.rl_timing:
                break;
        }
    }
}
