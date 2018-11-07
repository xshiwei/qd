package com.qvd.smartswitch.activity.user;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.login.MessageVo;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.ConfigUtils;
import com.qvd.smartswitch.utils.ToastUtil;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class UpdatePasswordActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.tv_forget_password)
    TextView tvForgetPassword;
    @BindView(R.id.et_old_password)
    EditText etOldPassword;
    @BindView(R.id.et_new_password)
    EditText etNewPassword;
    @BindView(R.id.et_once_new_password)
    EditText etOnceNewPassword;
    @BindView(R.id.riv_confirm_update)
    RippleView rivConfirmUpdate;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_update_password;
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }

    @OnClick({R.id.iv_common_actionbar_goback, R.id.tv_forget_password, R.id.riv_confirm_update})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.tv_forget_password:
                break;
            case R.id.riv_confirm_update:
                confirmUpdate();
                break;
        }
    }

    private void confirmUpdate() {
        if (valid()) {
            return;
        }
        RetrofitService.qdoApi.updateUserPassword(ConfigUtils.user_id, etOldPassword.getText().toString(), etNewPassword.getText().toString(), etOnceNewPassword.getText().toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MessageVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MessageVo messageVo) {
                        switch (messageVo.getCode()) {
                            case 200:
                                ToastUtil.showToast(getString(R.string.common_update_success));
                                finish();
                                break;
                            case 409:
                                ToastUtil.showToast(getString(R.string.common_old_password_not_correct));
                                break;
                            default:
                                ToastUtil.showToast(getString(R.string.common_update_two_fail));
                                break;
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

    /**
     * 验证三个输入有无错误
     *
     * @return
     */
    private boolean valid() {
        boolean b = false;
        if (CommonUtils.isEmptyString(etNewPassword.getText().toString()) || CommonUtils.isEmptyString(etOldPassword.getText().toString())
                || CommonUtils.isEmptyString(etOnceNewPassword.getText().toString())) {
            ToastUtil.showToast(getString(R.string.common_password_not_empty));
            b = true;
        } else if (etNewPassword.length() < 6 || etNewPassword.length() > 16) {
            ToastUtil.showToast(getString(R.string.login_password_length));
        } else if (!etNewPassword.getText().toString().equals(etOnceNewPassword.getText().toString())) {
            ToastUtil.showToast(getString(R.string.update_password_inconformity));
            b = true;
        } else if (etOldPassword.getText().toString().equals(etNewPassword.getText().toString())) {
            ToastUtil.showToast(getString(R.string.update_password_new_and_old_same));
            b = true;
        }
        return b;
    }
}
