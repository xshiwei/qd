package com.qvd.smartswitch.activity.device;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;
import com.orhanobut.logger.Logger;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.MainActivity;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.activity.home.AddHomeActivity;
import com.qvd.smartswitch.db.DeviceNickNameDaoOpe;
import com.qvd.smartswitch.model.DeviceNickNameVo;
import com.qvd.smartswitch.utils.CommandUtils;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.SnackbarUtils;
import com.qvd.smartswitch.utils.ToastUtil;
import com.qvd.smartswitch.widget.MyProgressDialog;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/4/3.
 */

public class DeviceControlSettingActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.rl_device_setting_log)
    RelativeLayout rlDeviceSettingLog;
    @BindView(R.id.rl_device_setting_password)
    RelativeLayout rlDeviceSettingPassword;
    @BindView(R.id.rl_device_setting_timing)
    RelativeLayout rlDeviceSettingTiming;
    @BindView(R.id.rl_device_setting_name)
    RelativeLayout rlDeviceSettingName;
    @BindView(R.id.rl_device_setting_pic)
    RelativeLayout rlDeviceSettingPic;
    @BindView(R.id.rl_device_setting_type)
    RelativeLayout rlDeviceSettingType;
    @BindView(R.id.rl_device_setting_update)
    RelativeLayout rlDeviceSettingUpdate;
    @BindView(R.id.rl_device_setting_delete)
    RelativeLayout rlDeviceSettingDelete;
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.tv_device_name)
    TextView tvDeviceName;

    private BleDevice bledevice;
    /**
     * 超时dialog
     */
    private MyProgressDialog progressDialog;

    /**
     * 设备昵称
     */
    private String deviceNickname;


    @Override
    protected int setLayoutId() {
        return R.layout.activity_device_control_setting_two;
    }

    @Override
    protected void initData() {
        super.initData();
        tvCommonActionbarTitle.setText(R.string.device_control_setting_title);
        bledevice = getIntent().getParcelableExtra("bledevice");
        progressDialog = MyProgressDialog.createProgressDialog(this, 5000,
                new MyProgressDialog.OnTimeOutListener() {
                    @Override
                    public void onTimeOut(ProgressDialog dialog) {
                        dialog.dismiss();
                        ToastUtil.showToast("修改失败");
                    }
                });
        deviceNickname = DeviceNickNameDaoOpe.queryOne(this, CommonUtils.getMac(bledevice.getMac())).getDeviceNickname();
        tvDeviceName.setText(deviceNickname);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getNotify();
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }

    private void getNotify() {
        CommonUtils.getConnectNotify(this, bledevice, coordinatorLayout);
    }

    @OnClick({R.id.iv_common_actionbar_goback, R.id.rl_device_setting_log, R.id.rl_device_setting_password, R.id.rl_device_setting_timing, R.id.rl_device_setting_name, R.id.rl_device_setting_pic, R.id.rl_device_setting_type, R.id.rl_device_setting_update, R.id.rl_device_setting_delete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.rl_device_setting_log:
                //日志
                startActivity(new Intent(this, DeviceLogActivity.class).putExtra("bledevice", bledevice));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.rl_device_setting_password:
                //修改密码

                break;
            case R.id.rl_device_setting_timing:
                //定时
                startActivity(new Intent(this, DeviceTimingActivity.class).putExtra("bledevice", bledevice));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.rl_device_setting_name:
                //设置名称
                showPopupwindowName();
            case R.id.rl_device_setting_pic:
                //更换图标
                startActivity(new Intent(this, DeviceUpdatePicActivity.class).putExtra("bledevice", bledevice));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.rl_device_setting_type:
                //设备类型
                startActivity(new Intent(this, DeviceSetTypeActivity.class).putExtra("bledevice", bledevice));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.rl_device_setting_update:
                //固件更新
                break;
            case R.id.rl_device_setting_delete:
                //删除设备
                deleteDevice();
                break;
        }
    }


    /**
     * 显示更换名字的popupwindow
     */
    private void showPopupwindowName() {
        new MaterialDialog.Builder(this)
                .title("设置设备名称")
                .inputRange(1, 20, getResources().getColor(R.color.red))
                .input(deviceNickname, null, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                    }
                })
                .negativeText("取消")
                .positiveText("确定")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        CommonUtils.closeSoftKeyboard(DeviceControlSettingActivity.this);
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        //修改设备名称
                    }
                })
                .show();
    }

    /**
     * 向Ble写入数据
     *
     * @param s
     */
    private void writeToBle(final String s) {
        progressDialog.setMessage("正在修改");
        progressDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                BleManager.getInstance().write(bledevice, "0000fff0-0000-1000-8000-00805f9b34fb", "0000fff6-0000-1000-8000-00805f9b34fb", HexUtil.hexStringToBytes(CommandUtils.updatePassword(s)), new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        Logger.e("write success, current: " + current
                                + " total: " + total
                                + " justWrite: " + HexUtil.formatHexString(justWrite, true));
                        progressDialog.dismiss();
                        ToastUtil.showToast("修改成功");
                        startActivity(new Intent(DeviceControlSettingActivity.this, MainActivity.class));
                        finish();
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {
                        Logger.e("write" + exception.toString());
                    }
                });
            }
        }, 100);
    }

    /**
     * 删除设备
     */
    private void deleteDevice() {
        final AlertDialog builder = new AlertDialog.Builder(this)
                .create();
        builder.show();
        if (builder.getWindow() == null) return;
        builder.getWindow().setContentView(R.layout.popuwindow_device_log);//设置弹出框加载的布局
        TextView msg = (TextView) builder.findViewById(R.id.tv_msg);
        Button cancle = (Button) builder.findViewById(R.id.btn_cancle);
        Button sure = (Button) builder.findViewById(R.id.btn_sure);
        if (msg == null || cancle == null || sure == null) return;
        msg.setText("您确定要删除吗");
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.setMessage("正在删除");
                        progressDialog.dismiss();
                    }
                }, 3000);
                progressDialog.dismiss();
                DeviceNickNameVo deviceNickNameVo1 = DeviceNickNameDaoOpe.queryOne(DeviceControlSettingActivity.this, CommonUtils.getMac(bledevice.getMac()));
                DeviceNickNameDaoOpe.deleteByKeyData(DeviceControlSettingActivity.this, deviceNickNameVo1.getId());
                BleManager.getInstance().disconnect(bledevice);
                SnackbarUtils.Short(coordinatorLayout, "删除成功").show();
                builder.dismiss();
                startActivity(new Intent(DeviceControlSettingActivity.this, MainActivity.class));
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
