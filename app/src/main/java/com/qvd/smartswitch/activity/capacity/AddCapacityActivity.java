package com.qvd.smartswitch.activity.capacity;

import android.content.Intent;
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

public class AddCapacityActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.rl_manually)
    RelativeLayout rlManually;
    @BindView(R.id.rl_timing)
    RelativeLayout rlTiming;
    @BindView(R.id.rl_weather)
    RelativeLayout rlWeather;
    @BindView(R.id.rl_screen)
    RelativeLayout rlScreen;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_add_capacity;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }

    @OnClick({R.id.iv_common_actionbar_goback, R.id.rl_manually, R.id.rl_timing, R.id.rl_weather, R.id.rl_screen})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.rl_manually:
                //手动执行
                startActivity(new Intent(this, ManuallyCapacityActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.rl_timing:
                //定时
                startActivity(new Intent(this, TimingCapacityActivity.class));
                break;
            case R.id.rl_weather:
                //室外天气发生变化
                startActivity(new Intent(this, WeatherCapacityActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.rl_screen:
                //筛选
                break;
        }
    }
}
