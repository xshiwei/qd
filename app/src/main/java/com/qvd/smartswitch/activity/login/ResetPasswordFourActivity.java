package com.qvd.smartswitch.activity.login;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.login.MessageVo;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.RegexpUtils;
import com.qvd.smartswitch.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ResetPasswordFourActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.et_new_password)
    EditText etNewPassword;
    @BindView(R.id.et_confirm_password)
    EditText etConfirmPassword;
    @BindView(R.id.tv_commit)
    TextView tvCommit;


    private int type;
    private String account;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_reset_password_four;
    }

    @Override
    protected void initData() {
        super.initData();
        type = getIntent().getIntExtra("type", -1);
        account = getIntent().getStringExtra("account");
        tvCommonActionbarTitle.setText("科微多账号-重置密码");
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.add_room_background).init();
    }


    @OnClick({R.id.iv_common_actionbar_goback, R.id.tv_commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.tv_commit:
                if (vaild()) {
                    return;
                } else {
                    setPassword();
                }
                break;
        }
    }

    /**
     * 设置密码
     */
    private void setPassword() {
        if (CommonUtils.isEmptyString(etNewPassword.getText().toString()) || CommonUtils.isEmptyString(etConfirmPassword.getText().toString())) {
            ToastUtil.showToast("密码不能为空");
        } else {
            RetrofitService.qdoApi.userSetNewPassword(account, etNewPassword.getText().toString(), etConfirmPassword.getText().toString())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<MessageVo>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(MessageVo messageVo) {
                            if (messageVo.getCode() == 200) {
                                startActivity(new Intent(ResetPasswordFourActivity.this, LoginActivity.class));
                                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                ToastUtil.showToast("重置密码成功");
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
    }

    /**
     * 密码验证
     */
    private boolean vaild() {
        boolean b = false;
        if (!etNewPassword.getText().toString().equals(etConfirmPassword.getText().toString())) {
            b = true;
            ToastUtil.showToast("两次输入的密码不一致");
        } else if (!RegexpUtils.isLetterDigit(etNewPassword.getText().toString())) {
            b = true;
            ToastUtil.showToast("密码中必须同时包含数字和字母");
        } else if (etNewPassword.getText().toString().length() < 8) {
            b = true;
            ToastUtil.showToast("密码长度不能小于8");
        }
        return b;
    }
}
