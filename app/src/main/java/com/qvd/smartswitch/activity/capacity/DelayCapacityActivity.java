package com.qvd.smartswitch.activity.capacity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;
import cn.carbswang.android.numberpickerview.library.NumberPickerView;

/**
 * Created by Administrator on 2018/6/20 0020.
 */

public class DelayCapacityActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.tv_save)
    TextView tvSave;
    @BindView(R.id.picker_minute)
    NumberPickerView pickerMinute;
    @BindView(R.id.picker_second)
    NumberPickerView pickerSecond;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_delay_capacity;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }

    @Override
    protected void initData() {
        super.initData();
        setData(pickerSecond);
        setData(pickerMinute);
    }

    private void setData(NumberPickerView picker) {
        picker.setMinValue(0);
        picker.setMaxValue(59);
        picker.setValue(0);
    }


    @OnClick({R.id.iv_common_actionbar_goback, R.id.tv_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.tv_save:
                break;
        }
    }
}
