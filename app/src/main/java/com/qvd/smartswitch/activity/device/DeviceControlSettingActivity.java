package com.qvd.smartswitch.activity.device;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;
import com.orhanobut.logger.Logger;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.MainActivity;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.dao.DeviceNickNameVoDao;
import com.qvd.smartswitch.db.DeviceNickNameDaoOpe;
import com.qvd.smartswitch.model.DeviceNickNameVo;
import com.qvd.smartswitch.utils.CommandUtils;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.SnackbarUtils;
import com.qvd.smartswitch.utils.ToastUtil;
import com.qvd.smartswitch.widget.MyProgressDialog;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

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
     * 修改名称的弹出框
     */
    private PopupWindow popupWindowName;

    /**
     * 设备昵称
     */
    private String deviceNickname;
    /**
     * 修改密码的弹出框
     */
    private PopupWindow popupWindowPassword;


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
                showPopupwindowPassword();
                popupWindowPassword.showAtLocation(view, Gravity.CENTER, 0, 0);
                break;
            case R.id.rl_device_setting_timing:
                //定时
                startActivity(new Intent(this, DeviceTimingActivity.class).putExtra("bledevice", bledevice));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.rl_device_setting_name:
                //设置名称
                showPopupwindowName();
                popupWindowName.showAtLocation(view, Gravity.CENTER, 0, 0);
                break;
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
    private void showPopupwindowPassword() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.popupwindow_edittext, null, false);
        popupWindowPassword = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindowPassword.setBackgroundDrawable(new ColorDrawable());
        popupWindowPassword.setAnimationStyle(R.style.AnimBottom);
        popupWindowPassword.setOutsideTouchable(true);
        popupWindowPassword.setFocusable(true);
        CommonUtils.setBackgroundAlpha(this, 0.5f);
        //popupWindowPassword.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        //popupWindowPassword.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        popupWindowPassword.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                CommonUtils.setBackgroundAlpha(DeviceControlSettingActivity.this, 1.0f);
            }
        });

        TextView title = view.findViewById(R.id.tv_title);
        final EditText editText = view.findViewById(R.id.et_edittext);
        ImageView delete = view.findViewById(R.id.iv_delete);
        final TextView error = view.findViewById(R.id.tv_error);
        TextView cancel = view.findViewById(R.id.tv_cancel);
        final TextView confirm = view.findViewById(R.id.tv_confirm);

        title.setText("设置密码");
        editText.setHint("");
        if (editText.getText().toString().equals("")) {
            confirm.setEnabled(false);
            confirm.setTextColor(getResources().getColor(R.color.home_setting_text_three));
        }
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 6) {
                    error.setVisibility(View.VISIBLE);
                    error.setText("长度超过最大");
                    confirm.setEnabled(false);
                    confirm.setTextColor(getResources().getColor(R.color.home_setting_text_three));
                } else if (s.toString().length() == 0) {
                    confirm.setEnabled(false);
                    confirm.setTextColor(getResources().getColor(R.color.home_setting_text_three));
                    editText.setCursorVisible(false);
                } else if (s.length() == 6) {
                    error.setVisibility(View.GONE);
                    confirm.setEnabled(true);
                    confirm.setTextColor(getResources().getColor(R.color.popupwindow_confirm_text));
                } else {
                    confirm.setEnabled(false);
                    confirm.setTextColor(getResources().getColor(R.color.home_setting_text_three));
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindowPassword.dismiss();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
                editText.setCursorVisible(false);
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeToBle(editText.getText().toString());
                popupWindowPassword.dismiss();
            }
        });

    }


    /**
     * 显示更换名字的popupwindow
     */
    private void showPopupwindowName() {
        LayoutInflater inflater = LayoutInflater.from(this);
        final View view = inflater.inflate(R.layout.popupwindow_edittext, null, false);
        popupWindowName = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindowName.setBackgroundDrawable(new ColorDrawable());
        popupWindowName.setAnimationStyle(R.style.AnimBottom);
        popupWindowName.setOutsideTouchable(true);
        popupWindowName.setFocusable(true);
        CommonUtils.setBackgroundAlpha(this, 0.5f);
//        popupWindowName.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//        popupWindowName.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        popupWindowName.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                CommonUtils.setBackgroundAlpha(DeviceControlSettingActivity.this, 1.0f);
            }
        });

        TextView title = view.findViewById(R.id.tv_title);
        final EditText editText = view.findViewById(R.id.et_edittext);
        ImageView delete = view.findViewById(R.id.iv_delete);
        final TextView error = view.findViewById(R.id.tv_error);
        TextView cancel = view.findViewById(R.id.tv_cancel);
        final TextView confirm = view.findViewById(R.id.tv_confirm);

        title.setText("设置设备名称");
        editText.setHint(deviceNickname);
        if (editText.getText().toString().equals("")) {
            confirm.setEnabled(false);
            confirm.setTextColor(getResources().getColor(R.color.home_setting_text_three));
        }
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 20) {
                    error.setVisibility(View.VISIBLE);
                    error.setText("长度超过最大");
                    confirm.setEnabled(false);
                    confirm.setTextColor(getResources().getColor(R.color.home_setting_text_three));
                } else if (s.toString().length() == 0) {
                    confirm.setEnabled(false);
                    confirm.setTextColor(getResources().getColor(R.color.home_setting_text_three));
                    editText.setCursorVisible(false);
                } else {
                    error.setVisibility(View.GONE);
                    confirm.setEnabled(true);
                    confirm.setTextColor(getResources().getColor(R.color.popupwindow_confirm_text));
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.closeSoftKeyboard(DeviceControlSettingActivity.this);
                popupWindowName.dismiss();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
                editText.setCursorVisible(false);
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceNickNameVo deviceNickNameVo1 = DeviceNickNameDaoOpe.queryOne(DeviceControlSettingActivity.this, CommonUtils.getMac(bledevice.getMac()));
                DeviceNickNameVo deviceNickNameVo = new DeviceNickNameVo(deviceNickNameVo1.getId(), deviceNickNameVo1.getDeviceId()
                        , deviceNickNameVo1.getDeviceName(), CommonUtils.getDate(), editText.getText().toString(), deviceNickNameVo1.getPic(), deviceNickNameVo1.getType());
                DeviceNickNameDaoOpe.updateData(DeviceControlSettingActivity.this, deviceNickNameVo);
                SnackbarUtils.Short(coordinatorLayout, "修改成功").show();
                tvDeviceName.setText(editText.getText().toString());
                popupWindowName.dismiss();
                CommonUtils.closeSoftKeyboard(DeviceControlSettingActivity.this);
            }
        });

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
