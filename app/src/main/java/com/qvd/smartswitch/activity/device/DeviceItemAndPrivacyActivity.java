package com.qvd.smartswitch.activity.device;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

    @Override
    protected int setLayoutId() {
        return R.layout.activity_device_item_privacy;
    }

    @Override
    protected void initData() {
        super.initData();
        device_id = getIntent().getStringExtra("device_id");
        String device_no = getIntent().getStringExtra("device_no");
        tvCommonActionbarTitle.setText(R.string.device_item_and_privacy_title);
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
                .title(R.string.device_item_and_privacy_confirm_repeal_impower)
                .content(getString(R.string.device_item_and_privacy_content))
                .negativeText(R.string.common_cancel)
                .positiveText(R.string.common_confirm)
                .onNegative((dialog, which) -> {

                })
                .onPositive((dialog, which) -> RetrofitService.qdoApi.deleteDevice(device_id)
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
                                    ToastUtil.showToast(getString(R.string.common_delete_success));
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
                        })).show();
    }
}
