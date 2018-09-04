package com.qvd.smartswitch.activity.device;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.clj.fastble.BleManager;
import com.clj.fastble.data.BleDevice;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.MainActivity;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.login.MessageVo;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.ToastUtil;
import com.qvd.smartswitch.widget.MyProgressDialog;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/4/3.
 */

public class DeviceSettingActivity extends BaseActivity {


    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.rl_novice_guide)
    RelativeLayout rlNoviceGuide;
    @BindView(R.id.rl_quick_tour)
    RelativeLayout rlQuickTour;
    @BindView(R.id.rl_health_knowledge)
    RelativeLayout rlHealthKnowledge;
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


    private BleDevice bledevice;
    /**
     * 超时dialog
     */
    private MyProgressDialog progressDialog;
    private String device_id;
    private String device_table;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_device_control_setting;
    }

    @Override
    protected void initData() {
        super.initData();
        device_id = getIntent().getStringExtra("device_id");
        device_table = getIntent().getStringExtra("device_No");
        tvCommonActionbarTitle.setText(R.string.device_control_setting_title);
        progressDialog = MyProgressDialog.createProgressDialog(this, 5000,
                dialog -> {
                    dialog.dismiss();
                    ToastUtil.showToast("修改失败");
                });
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
                .title("设置设备名称")
                .inputRange(1, 20, getResources().getColor(R.color.red))
                .input("设置昵称", null, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                    }
                })
                .negativeText("取消")
                .positiveText("确定")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        CommonUtils.closeSoftKeyboard(DeviceSettingActivity.this);
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        //修改设备名称
                        EditText inputEditText = dialog.getInputEditText();
                        RetrofitService.qdoApi.updateSpecificDeviceName(device_id, inputEditText.getText().toString(), device_table)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<MessageVo>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onNext(MessageVo messageVo) {
                                        if (messageVo.getCode() == 200) {
                                            ToastUtil.showToast("修改成功");
                                        } else {
                                            ToastUtil.showToast("网络失败");
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        ToastUtil.showToast("网络失败");
                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                });
                    }
                })
                .show();
    }

    /**
     * 删除设备
     */
    private void deleteDevice() {
        new MaterialDialog.Builder(this)
                .title("您确定要删除该设备吗")
                .negativeText("取消")
                .positiveText("确定")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        RetrofitService.qdoApi.deleteDevice(device_id)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<MessageVo>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onNext(MessageVo messageVo) {
                                        if (messageVo.getCode() == 200) {
                                            ToastUtil.showToast("删除成功");
                                            startActivity(new Intent(DeviceSettingActivity.this, MainActivity.class));
                                            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                            finish();
                                        } else {
                                            ToastUtil.showToast("网络错误");
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        ToastUtil.showToast("网络错误");
                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                });
                    }
                })
                .show();
    }

    @OnClick({R.id.iv_common_actionbar_goback, R.id.rl_novice_guide, R.id.rl_quick_tour, R.id.rl_health_knowledge, R.id.rl_common_question, R.id.rl_item_and_privacy, R.id.rl_feedback, R.id.rl_about, R.id.rl_retry_name, R.id.rl_device_share, R.id.rl_location_manager, R.id.rl_check_update, R.id.rl_device_delete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.rl_novice_guide:
                //新手指导
                ToastUtil.showToast("功能开发中，敬请期待。。。");
                break;
            case R.id.rl_quick_tour:
                //快速指南
                ToastUtil.showToast("功能开发中，敬请期待。。。");
                break;
            case R.id.rl_health_knowledge:
                //健康知识
                ToastUtil.showToast("功能开发中，敬请期待。。。");
                break;
            case R.id.rl_common_question:
                //常见问题
                ToastUtil.showToast("功能开发中，敬请期待。。。");
                break;
            case R.id.rl_item_and_privacy:
                //条款和隐私
                ToastUtil.showToast("功能开发中，敬请期待。。。");
                break;
            case R.id.rl_feedback:
                //反馈问题
                ToastUtil.showToast("功能开发中，敬请期待。。。");
                break;
            case R.id.rl_about:
                //关于
                ToastUtil.showToast("功能开发中，敬请期待。。。");
                break;
            case R.id.rl_retry_name:
                //修改名称
                showPopupwindowName();
                break;
            case R.id.rl_device_share:
                //设备共享
                ToastUtil.showToast("功能开发中，敬请期待。。。");
                break;
            case R.id.rl_location_manager:
                //位置管理
                ToastUtil.showToast("功能开发中，敬请期待。。。");
                break;
            case R.id.rl_check_update:
                //检查更新
                ToastUtil.showToast("功能开发中，敬请期待。。。");
                break;
            case R.id.rl_device_delete:
                //设备删除
                deleteDevice();
                break;
        }
    }
}
