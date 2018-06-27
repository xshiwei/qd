package com.qvd.smartswitch.activity.capacity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/6/19 0019.
 */

public class WeatherCapacityActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.rl_sun)
    RelativeLayout rlSun;
    @BindView(R.id.rl_temperature)
    RelativeLayout rlTemperature;
    @BindView(R.id.rl_humidity)
    RelativeLayout rlHumidity;
    @BindView(R.id.rl_pm)
    RelativeLayout rlPm;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_weather_capacity;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }

    @OnClick({R.id.iv_common_actionbar_goback, R.id.rl_sun, R.id.rl_temperature, R.id.rl_humidity, R.id.rl_pm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.rl_sun:
                //日出 日落
                startActivity(new Intent(this, WeatherCapacityListActivity.class).putExtra("type", 1));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.rl_temperature:
                //室外温度
                startActivity(new Intent(this, WeatherCapacityListActivity.class).putExtra("type", 2));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.rl_humidity:
                //室外湿度
                startActivity(new Intent(this, WeatherCapacityListActivity.class).putExtra("type", 3));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.rl_pm:
                //PM2.5
                startActivity(new Intent(this, WeatherCapacityListActivity.class).putExtra("type", 4));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
        }
    }
}
