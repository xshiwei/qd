package com.qvd.smartswitch.activity.home;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.widget.MyPopupWindowOne;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SetDeviceToRoomActivity extends BaseActivity {


    @BindView(R.id.iv_device_pic)
    ImageView ivDevicePic;
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.rl_device_name)
    RelativeLayout rlDeviceName;
    @BindView(R.id.tv_room_name)
    TextView tvRoomName;
    @BindView(R.id.rl_selete_room)
    RelativeLayout rlSeleteRoom;
    @BindView(R.id.iv_set_common)
    ImageView ivSetCommon;
    @BindView(R.id.rl_set_common)
    RelativeLayout rlSetCommon;
    @BindView(R.id.tv_complete)
    TextView tvComplete;

    private MyPopupWindowOne mCancelPopupWindow;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_set_device_to_room;
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @OnClick({R.id.iv_common_actionbar_goback, R.id.rl_device_name, R.id.rl_set_common, R.id.tv_complete, R.id.rl_selete_room})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                showCancelPopupWindow();
                break;
            case R.id.rl_device_name:
                break;
            case R.id.rl_set_common:
                break;
            case R.id.tv_complete:
                break;
            case R.id.rl_selete_room:
                break;
        }
    }

    /**
     * 取消的popupWindow
     */
    private void showCancelPopupWindow() {
        mCancelPopupWindow = new MyPopupWindowOne(this, "是否保存本次修改", "取消", "保存", new MyPopupWindowOne.IPopupWindowListener() {
            @Override
            public void cancel() {
                //保存设备
                mCancelPopupWindow.dismiss();
                finish();
            }

            @Override
            public void confirm() {
                //保存设备，并进入设备控制界面
                mCancelPopupWindow.dismiss();
            }
        });
        mCancelPopupWindow.showPopupWindow(ivCommonActionbarGoback);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
