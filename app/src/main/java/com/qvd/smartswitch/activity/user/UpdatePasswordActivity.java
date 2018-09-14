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
                        if (messageVo.getCode() == 200) {
                            ToastUtil.showToast("修改成功");
                            finish();
                        } else if (messageVo.getCode() == 409) {
                            ToastUtil.showToast("旧密码不正确");
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

    /**
     * 验证三个输入有无错误
     *
     * @return
     */
    private boolean valid() {
        boolean b = false;
        if (CommonUtils.isEmptyString(etNewPassword.getText().toString()) || CommonUtils.isEmptyString(etOldPassword.getText().toString())
                || CommonUtils.isEmptyString(etOnceNewPassword.getText().toString())) {
            ToastUtil.showToast("密码不能为空");
            b = true;
        } else if (!etOldPassword.getText().toString().equals(etOnceNewPassword.getText().toString())) {
            ToastUtil.showToast("两次输入的新密码不一致");
            b = true;
        } else if (!etOldPassword.getText().toString().equals(etNewPassword.getText().toString())) {
            ToastUtil.showToast("新密码不能和旧密码相同");
            b = true;
        }
        return b;
    }
}
