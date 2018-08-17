package com.qvd.smartswitch.activity.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.MainActivity;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.login.MessageVo;
import com.qvd.smartswitch.model.login.RegisterVo;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.ConfigUtils;
import com.qvd.smartswitch.utils.SharedPreferencesUtil;
import com.qvd.smartswitch.utils.SnackbarUtils;
import com.qvd.smartswitch.widget.MyProgressDialog;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2018-7-7.
 */

public class SetPasswordActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.iv_password)
    ImageView ivPassword;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.iv_repassword)
    ImageView ivRepassword;
    @BindView(R.id.et_repassword)
    EditText etRepassword;
    @BindView(R.id.btn_complete)
    TextView btnComplete;
    @BindView(R.id.tv_password_error)
    TextView tvPasswordError;

    /**
     * 注册账号
     */
    private String userName;
    /**
     * 登录标识
     */
    private String identity_type;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_set_password;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }

    @Override
    protected void initData() {
        super.initData();
        userName = getIntent().getStringExtra("userName");
        identity_type = getIntent().getStringExtra("identity_type");
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tvPasswordError.setVisibility(View.GONE);
            }
        });

        etRepassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tvPasswordError.setVisibility(View.GONE);
            }
        });
    }

    @OnClick({R.id.iv_common_actionbar_goback, R.id.btn_complete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.btn_complete:
                if (!validate()) {
                    return;
                }
                register();
                break;
        }
    }

    /**
     * 注册
     */
    private void register() {
        final MyProgressDialog progressDialog = MyProgressDialog.createProgressDialog(this, 5000,
                new MyProgressDialog.OnTimeOutListener() {
                    @Override
                    public void onTimeOut(ProgressDialog dialog) {
                        dialog.dismiss();
                        SnackbarUtils.Short(tvPasswordError, "登录失败,请重试");
                    }
                });
        progressDialog.setMessage("登录中");
        progressDialog.show();
        RetrofitService.qdoApi.register(userName, etPassword.getText().toString().trim(), etRepassword.getText().toString().trim(), identity_type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RegisterVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(RegisterVo messageVo) {
                        if (messageVo != null) {
                            if (messageVo.getMap_string() != null && messageVo.getCode() == 200) {
                                SharedPreferencesUtil.putString(SetPasswordActivity.this, SharedPreferencesUtil.USER_ID, messageVo.getMap_string().getUser_id());
                                SharedPreferencesUtil.putString(SetPasswordActivity.this, SharedPreferencesUtil.IDENTIFIER, messageVo.getMap_string().getIdentifier());
                                SharedPreferencesUtil.putString(SetPasswordActivity.this, SharedPreferencesUtil.PASSWORD, messageVo.getMap_string().getPassword());
                                ConfigUtils.user_id = messageVo.getMap_string().getUser_id();
                                startActivity(new Intent(SetPasswordActivity.this, MainActivity.class));
                                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                            } else if (messageVo.getCode() == 400) {
                                SnackbarUtils.Short(btnComplete, "注册失败").show();
                            } else if (messageVo.getCode() == 800) {
                                SnackbarUtils.Short(btnComplete, "连接超时").show();
                            } else if (messageVo.getCode() == 402) {
                                SnackbarUtils.Short(btnComplete, "您当前邮箱未验证").show();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e("register->" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 验证密码
     *
     * @return
     */
    private boolean validate() {
        boolean vaild = true;
        String password = etPassword.getText().toString().trim();
        String rePassword = etRepassword.getText().toString().trim();
        if (CommonUtils.isEmptyString(password) || CommonUtils.isEmptyString(rePassword)) {
            vaild = false;
            tvPasswordError.setVisibility(View.VISIBLE);
            tvPasswordError.setText("密码不能为空");
        } else if (password.length() > 16 || password.length() < 6 || rePassword.length() > 16 || rePassword.length() < 6) {
            vaild = false;
            tvPasswordError.setVisibility(View.VISIBLE);
            tvPasswordError.setText("密码长度不少于6个字符，不大于16个字符");
        } else if (!password.equals(rePassword)) {
            vaild = false;
            tvPasswordError.setVisibility(View.VISIBLE);
            tvPasswordError.setText("两次密码不一致");
        }
        return vaild;
    }

}
