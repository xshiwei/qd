package com.qvd.smartswitch.activity.qsTwo;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.MainActivity;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.activity.device.DeviceCommonQuestionActivity;
import com.qvd.smartswitch.activity.device.DeviceGuideActivity;
import com.qvd.smartswitch.activity.device.DeviceItemAndPrivacyActivity;
import com.qvd.smartswitch.activity.device.DeviceLogActivity;
import com.qvd.smartswitch.activity.device.DeviceShareActivity;
import com.qvd.smartswitch.activity.user.UserFeedbackActivity;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.device.ScanResultVo;
import com.qvd.smartswitch.model.login.MessageVo;
import com.qvd.smartswitch.utils.BleManageUtils;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.ToastUtil;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/4/3.
 */

public class QsTwoSettingActivity extends BaseActivity {


    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.rl_common_question)
    RelativeLayout rlCommonQuestion;
    @BindView(R.id.rl_item_and_privacy)
    RelativeLayout rlItemAndPrivacy;
    @BindView(R.id.rl_feedback)
    RelativeLayout rlFeedback;
    @BindView(R.id.rl_about)
    RelativeLayout rlAbout;
    @BindView(R.id.rl_retry_name)
    RelativeLayout rlRetryName;
    @BindView(R.id.rl_device_share)
    RelativeLayout rlDeviceShare;
    @BindView(R.id.rl_location_manager)
    RelativeLayout rlLocationManager;
    @BindView(R.id.rl_check_update)
    RelativeLayout rlCheckUpdate;
    @BindView(R.id.rl_device_delete)
    RelativeLayout rlDeviceDelete;

    private ScanResultVo scanResult;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_qstwo_setting;
    }

    @Override
    protected void initData() {
        super.initData();
        scanResult = (ScanResultVo) getIntent().getSerializableExtra("scanResult");
        int is_control = getIntent().getIntExtra("is_control", -1);
        if (is_control == 1) {
            rlRetryName.setVisibility(View.GONE);
            rlDeviceShare.setVisibility(View.GONE);
        }
        tvCommonActionbarTitle.setText(R.string.device_control_setting_title);
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.home_list_background).init();
    }


    /**
     * 显示更换名字的popupwindow
     */
    private void showPopupwindowName() {
        new MaterialDialog.Builder(this)
                .title(R.string.common_set_device_name)
                .inputRange(1, 20, getResources().getColor(R.color.red))
                .input(getString(R.string.common_set_name), null, false, (dialog, input) -> {

                })
                .negativeText(R.string.common_cancel)
                .positiveText(R.string.common_confirm)
                .onNegative((dialog, which) -> CommonUtils.closeSoftKeyboard(QsTwoSettingActivity.this))
                .onPositive((dialog, which) -> {
                    //修改设备名称
                    EditText inputEditText = dialog.getInputEditText();
                    RetrofitService.qdoApi.updateSpecificDeviceName(scanResult.getDeviceId(), Objects.requireNonNull(inputEditText).getText().toString(), scanResult.getDeviceNo())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<MessageVo>() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onNext(MessageVo messageVo) {
                                    if (messageVo.getCode() == 200) {
                                        ToastUtil.showToast(getString(R.string.common_update_success));
                                    } else {
                                        ToastUtil.showToast(getString(R.string.common_update_fail));
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {
                                }

                                @Override
                                public void onComplete() {

                                }
                            });
                })
                .show();
    }

    /**
     * 删除设备
     */
    private void deleteDevice() {
        new MaterialDialog.Builder(this)
                .title(R.string.common_delete_device)
                .negativeText(R.string.common_cancel)
                .positiveText(R.string.common_confirm)
                .onPositive((dialog, which) -> RetrofitService.qdoApi.deleteDevice(scanResult.getDeviceId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<MessageVo>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(MessageVo messageVo) {
                                if (messageVo.getCode() == 200) {
                                    ToastUtil.showToast(getString(R.string.common_delete_success));
                                    startActivity(new Intent(QsTwoSettingActivity.this, MainActivity.class));
                                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                    BleManageUtils.getInstance().DestroyBleManage();
                                    finish();
                                } else {
                                    ToastUtil.showToast(getString(R.string.common_delete_fail));
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                            }

                            @Override
                            public void onComplete() {

                            }
                        }))
                .show();
    }

    @OnClick({R.id.iv_common_actionbar_goback, R.id.rl_common_question, R.id.rl_item_and_privacy, R.id.rl_feedback, R.id.rl_about, R.id.rl_retry_name,
            R.id.rl_device_share, R.id.rl_location_manager, R.id.rl_check_update, R.id.rl_device_delete, R.id.rl_log, R.id.rl_quick_tour})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.rl_common_question:
                //常见问题
                startActivity(new Intent(this, DeviceCommonQuestionActivity.class)
                        .putExtra("type", scanResult.getDeviceNo()));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.rl_item_and_privacy:
                //条款和隐私
                startActivity(new Intent(this, DeviceItemAndPrivacyActivity.class)
                        .putExtra("device_id", scanResult.getDeviceId())
                        .putExtra("device_no", scanResult.getDeviceNo()));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.rl_feedback:
                //反馈问题
                startActivity(new Intent(this, UserFeedbackActivity.class)
                        .putExtra("type", scanResult.getDeviceNo()));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.rl_about:
                //关于
                ToastUtil.showToast(getString(R.string.common_features_being_development));
                break;
            case R.id.rl_retry_name:
                //修改名称
                showPopupwindowName();
                break;
            case R.id.rl_device_share:
                //设备共享
                startActivity(new Intent(this, DeviceShareActivity.class)
                        .putExtra("device_id", scanResult.getDeviceId())
                        .putExtra("device_type", scanResult.getDeviceNo()));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.rl_location_manager:
                //位置管理
                ToastUtil.showToast(getString(R.string.common_features_being_development));
                break;
            case R.id.rl_check_update:
                //检查更新
                ToastUtil.showToast(getString(R.string.common_features_being_development));
                break;
            case R.id.rl_device_delete:
                //设备删除
                deleteDevice();
                break;
            case R.id.rl_log:
                //设备操作日志
                startActivity(new Intent(this, DeviceLogActivity.class)
                        .putExtra("device_id", scanResult.getDeviceId())
                        .putExtra("device_type", scanResult.getDeviceNo()));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.rl_quick_tour:
                //快速指南
                startActivity(new Intent(this, DeviceGuideActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
        }
    }
}
