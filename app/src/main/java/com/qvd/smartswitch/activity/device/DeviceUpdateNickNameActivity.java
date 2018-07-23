package com.qvd.smartswitch.activity.device;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.clj.fastble.BleManager;
import com.clj.fastble.data.BleDevice;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.db.DeviceNickNameDaoOpe;
import com.qvd.smartswitch.model.DeviceNickNameVo;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.SnackbarUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2018/4/16 0016.
 */

public class DeviceUpdateNickNameActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.et_device_update_nickname)
    EditText etDeviceUpdateNickname;
    @BindView(R.id.tv_device_update_nickname_confirm)
    TextView tvDeviceUpdateNicknameConfirm;

    private BleDevice bledevice;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_device_update_nickname;
    }

    @OnClick({R.id.iv_common_actionbar_goback, R.id.tv_device_update_nickname_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.tv_device_update_nickname_confirm:
                if (CommonUtils.isEmptyString(etDeviceUpdateNickname.getText().toString())) {
                    SnackbarUtils.Short(tvCommonActionbarTitle, "昵称不能为空").show();
                } else {
                    updateNickname();
                }
                break;
        }
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }

    @Override
    protected void initData() {
        super.initData();
        tvCommonActionbarTitle.setText("修改昵称");
        bledevice = getIntent().getParcelableExtra("bledevice");
        // CommonUtils.getNotify(this, bledevice, "0000fff0-0000-1000-8000-00805f9b34fb", "0000fff6-0000-1000-8000-00805f9b34fb");
        CommonUtils.getConnectNotify(this, bledevice, tvCommonActionbarTitle);
    }

    /**
     * 修改设备昵称
     */
    private void updateNickname() {
        final AlertDialog builder = new AlertDialog.Builder(this)
                .create();
        builder.show();
        if (builder.getWindow() == null) return;
        builder.getWindow().setContentView(R.layout.popuwindow_device_log);//设置弹出框加载的布局
        TextView msg = (TextView) builder.findViewById(R.id.tv_msg);
        Button cancle = (Button) builder.findViewById(R.id.btn_cancle);
        Button sure = (Button) builder.findViewById(R.id.btn_sure);
        if (msg == null || cancle == null || sure == null) return;
        msg.setText("您确定要修改吗");
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceNickNameVo deviceNickNameVo1 = DeviceNickNameDaoOpe.queryOne(DeviceUpdateNickNameActivity.this, CommonUtils.getMac(bledevice.getMac()));
                DeviceNickNameVo deviceNickNameVo = new DeviceNickNameVo(deviceNickNameVo1.getId(), deviceNickNameVo1.getDeviceId()
                        , deviceNickNameVo1.getDeviceName(), CommonUtils.getDate(), etDeviceUpdateNickname.getText().toString(), deviceNickNameVo1.getPic(), deviceNickNameVo1.getType());
                DeviceNickNameDaoOpe.updateData(DeviceUpdateNickNameActivity.this, deviceNickNameVo);
                SnackbarUtils.Short(tvCommonActionbarTitle, "修改成功").show();
                builder.dismiss();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BleManager.getInstance().stopNotify(bledevice, "0000fff0-0000-1000-8000-00805f9b34fb", "0000fff6-0000-1000-8000-00805f9b34fb");
    }

}
