package com.qvd.smartswitch.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.MainActivity;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.utils.CommonUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    @Override
    protected int setLayoutId() {
        return R.layout.activity_set_password;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.add_home_background).init();
    }

    @Override
    protected void initData() {
        super.initData();
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
                startActivity(new Intent(this, MainActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
        }
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
