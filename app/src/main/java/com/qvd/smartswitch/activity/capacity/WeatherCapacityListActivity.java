package com.qvd.smartswitch.activity.capacity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/6/19 0019.
 */

public class WeatherCapacityListActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.tv_start)
    TextView tvStart;
    @BindView(R.id.tv_end)
    TextView tvEnd;

    /**
     * 代表是哪种类型
     */
    private int type;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_weather_capacity_list;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }

    @Override
    protected void initData() {
        super.initData();
        Intent intent = getIntent();
        type = intent.getIntExtra("type", -1);
        setType();
    }

    /**
     * 根据传入的type类型设置
     */
    private void setType() {
        switch (type) {
            case 1:
                tvCommonActionbarTitle.setText("日出/日落");
                tvStart.setText("日出时");
                tvStart.setText("日落时");
                break;
            case 2:
                tvCommonActionbarTitle.setText("室外温度");
                tvStart.setText("高于指定温度");
                tvEnd.setText("低于指定温度");
                break;
            case 3:
                tvCommonActionbarTitle.setText("室外湿度");
                tvStart.setText("高于指定湿度");
                tvEnd.setText("低于指定湿度");
                break;
            case 4:
                tvCommonActionbarTitle.setText("室外PM2.5");
                tvStart.setText("高于指定浓度");
                tvEnd.setText("低于指定浓度");
                break;
        }
    }

    @OnClick({R.id.iv_common_actionbar_goback, R.id.tv_start, R.id.tv_end})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.tv_start:
                setStart(type);
                break;
            case R.id.tv_end:
                setEnd(type);
                break;
        }
    }

    /**
     * 根据type判断tv_start点击事件
     *
     * @param type
     */
    private void setEnd(int type) {
        switch (type) {
            case 1:
                showAddress();
                break;
            case 2:
                startActivity(new Intent(this, WeatherCapacityDetailsActivity.class).putExtra("type", 2).putExtra("name", "低于指定温度"));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case 3:
                startActivity(new Intent(this, WeatherCapacityDetailsActivity.class).putExtra("type", 3).putExtra("name", "低于指定湿度"));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case 4:
                startActivity(new Intent(this, WeatherCapacityDetailsActivity.class).putExtra("type", 4).putExtra("name", "低于指定浓度"));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
        }
    }

    /**
     * 根据type判断tv_start点击事件
     *
     * @param type
     */
    private void setStart(int type) {
        switch (type) {
            case 1:
                showAddress();
                break;
            case 2:
                startActivity(new Intent(this, WeatherCapacityDetailsActivity.class).putExtra("type", 2).putExtra("name", "高于指定温度"));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case 3:
                startActivity(new Intent(this, WeatherCapacityDetailsActivity.class).putExtra("type", 3).putExtra("name", "高于指定湿度"));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case 4:
                startActivity(new Intent(this, WeatherCapacityDetailsActivity.class).putExtra("type", 4).putExtra("name", "高于指定浓度"));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
        }
    }

    /**
     * 显示城市列表
     */
    private void showAddress() {

    }


}
