package com.qvd.smartswitch.activity.home;

import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.model.device.ScanResultVo;
import com.wang.avi.AVLoadingIndicatorView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/6/12 0012.
 */

public class DeviceConnectActivity extends BaseActivity {

    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.ll_one)
    LinearLayout llOne;
    @BindView(R.id.avi_loading)
    AVLoadingIndicatorView aviLoading;
    @BindView(R.id.avi_one)
    AVLoadingIndicatorView aviOne;
    @BindView(R.id.tv_one)
    TextView tvOne;
    @BindView(R.id.avi_two)
    AVLoadingIndicatorView aviTwo;
    @BindView(R.id.tv_two)
    TextView tvTwo;
    @BindView(R.id.ll_two)
    LinearLayout llTwo;
    @BindView(R.id.avi_three)
    AVLoadingIndicatorView aviThree;
    @BindView(R.id.tv_three)
    TextView tvThree;
    @BindView(R.id.ll_three)
    LinearLayout llThree;
    @BindView(R.id.avi_four)
    AVLoadingIndicatorView aviFour;
    @BindView(R.id.tv_four)
    TextView tvFour;
    @BindView(R.id.ll_four)
    LinearLayout llFour;
    @BindView(R.id.tv_complete)
    TextView tvComplete;
    /**
     * 当前设备
     */
    private ScanResultVo bleDevice;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_device_connect;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }

    @Override
    protected void initData() {
        super.initData();
        tvCommonActionbarTitle.setText("添加设备");
        bleDevice = (ScanResultVo) getIntent().getSerializableExtra("ScanResultVo");
        llOne.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                aviOne.hide();
                llTwo.setVisibility(View.VISIBLE);
            }
        }, 1000);
        addDevice();
    }

    /**
     * 添加设备
     */
    private void addDevice() {

    }

    @OnClick({R.id.iv_common_actionbar_goback, R.id.tv_complete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.tv_complete:
                break;
        }
    }
}
