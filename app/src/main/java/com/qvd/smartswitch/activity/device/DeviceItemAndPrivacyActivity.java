package com.qvd.smartswitch.activity.device;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.MainActivity;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.login.MessageVo;
import com.qvd.smartswitch.utils.BleManageUtils;
import com.qvd.smartswitch.utils.ToastUtil;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DeviceItemAndPrivacyActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.rl_item)
    RelativeLayout rlItem;
    @BindView(R.id.rl_privacy)
    RelativeLayout rlPrivacy;
    @BindView(R.id.tv_delete)
    TextView tvDelete;

    private String device_id;
    private String device_no;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_device_item_privacy;
    }

    @Override
    protected void initData() {
        super.initData();
        device_id = getIntent().getStringExtra("device_id");
        device_no = getIntent().getStringExtra("device_no");
        tvCommonActionbarTitle.setText("条款与隐私");
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }

    @OnClick({R.id.iv_common_actionbar_goback, R.id.rl_item, R.id.rl_privacy, R.id.tv_delete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.rl_item:
                startActivity(new Intent(this, DeviceItemActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.rl_privacy:
                startActivity(new Intent(this, DevicePrivacyActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.tv_delete:
                showDeleteDialog();
                break;
        }
    }

    /**
     * 展示删除的dialog
     */
    private void showDeleteDialog() {
        new MaterialDialog.Builder(this)
                .title("确认撤销授权吗？")
                .content("授权撤销后，设备会与您当前登录的账号解除绑定并清除该设备之前存储在服务端的数据；物理设备中存储的数据需要您进行人工本地物理重置后清除，若设备支持，您可直接在设备上进行重置或参照" +
                        "“隐私政策”中的撤销授权进行重置。上述数据撤销后均不可恢复。若要重新使用设备，需要您再次进行绑定并且重新授权。")
                .negativeText("取消")
                .positiveText("确认")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                    }
                })
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
                                            startActivity(new Intent(DeviceItemAndPrivacyActivity.this, MainActivity.class));
                                            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                            BleManageUtils.getInstance().DestroyBleManage();
                                            finish();
                                            ToastUtil.showToast("操作成功");
                                        } else {
                                            ToastUtil.showToast("操作失败");
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable e) {

                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                });
                    }
                }).show();
    }
}
