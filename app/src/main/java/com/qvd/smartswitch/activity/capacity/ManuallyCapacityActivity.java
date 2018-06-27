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

public class ManuallyCapacityActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.rl_switch)
    RelativeLayout rlSwitch;
    @BindView(R.id.rl_notification)
    RelativeLayout rlNotification;
    @BindView(R.id.rl_delay)
    RelativeLayout rlDelay;
    @BindView(R.id.rl_screen)
    RelativeLayout rlScreen;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_manually_capacity;
    }


    @OnClick({R.id.iv_common_actionbar_goback, R.id.rl_switch, R.id.rl_notification, R.id.rl_delay, R.id.rl_screen})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.rl_switch:
                //开关某条智能
                startActivity(new Intent(this, OpenOrCloseCapacityActivity.class));
                break;
            case R.id.rl_notification:
                //向手机发送通知
                startActivity(new Intent(this, NotificationCapacityActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.rl_delay:
                //延时
                startActivity(new Intent(this, DelayCapacityActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.rl_screen:
                //筛选
                break;
        }
    }
}
