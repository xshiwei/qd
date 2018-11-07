package com.qvd.smartswitch.activity.capacity;

import android.content.Intent;
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

public class WeatherCapacityDetailsActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.tv_save)
    TextView tvSave;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_weather_capacity_details;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }

    @Override
    protected void initData() {
        super.initData();
        NumberPickerView picker = findViewById(R.id.picker);
        Intent intent = getIntent();
        int type = intent.getIntExtra("type", -1);
        /*
      传过来显示标题的名称
     */
        String name = intent.getStringExtra("name");
        tvCommonActionbarTitle.setText(name);
        switch (type) {
            case 2:
                picker.setDisplayedValues(getResources().getStringArray(R.array.temperature_display));
                picker.setHintText("°C");
                picker.setMaxValue(140);
                picker.setMinValue(0);
                picker.setValue(70);
                break;
            case 3:
                picker.setDisplayedValues(getResources().getStringArray(R.array.humidity_display));
                picker.setHintText("%");
                picker.setMaxValue(100);
                picker.setMinValue(0);
                picker.setValue(50);
                break;
            case 4:
                picker.setDisplayedValues(getResources().getStringArray(R.array.pm_display));
                picker.setHintText("ug/m³");
                picker.setMaxValue(399);
                picker.setMinValue(0);
                picker.setValue(1);
                break;
        }
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
