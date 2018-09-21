package com.qvd.smartswitch.activity.login;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.RegexpUtils;
import com.qvd.smartswitch.utils.ToastUtil;

import butterknife.BindView;
import butterknife.OnClick;

public class ResetPasswordActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.et_account)
    EditText etAccount;
    @BindView(R.id.tv_help)
    TextView tvHelp;
    @BindView(R.id.tv_next)
    TextView tvNext;


    /**
     * 表示类型 1代表邮箱 2代表手机号码
     */
    private int type = 2;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_reset_password;
    }

    @Override
    protected void initData() {
        super.initData();
        tvCommonActionbarTitle.setText("科微多账号-重置密码");
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.add_room_background).init();
    }

    @OnClick({R.id.iv_common_actionbar_goback, R.id.tv_help, R.id.tv_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.tv_help:
                ToastUtil.showToast("功能正在开发中。。。");
                break;
            case R.id.tv_next:
                if (vaild()) {
                    return;
                } else {
                    isAccountType();
                    startActivity(new Intent(this, ResetPasswordTwoActivity.class)
                            .putExtra("type", type)
                            .putExtra("account", etAccount.getText().toString()));
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
                break;
        }
    }

    private void isAccountType() {
        if (RegexpUtils.isEmailNO(etAccount.getText().toString())) {
            type = 1;
        } else if (RegexpUtils.isMobileNO(etAccount.getText().toString())) {
            type = 2;
        }
//        else {
//            type=3;
//        }
    }

    /**
     * 输入判断
     *
     * @return
     */
    private boolean vaild() {
        boolean b = false;
        if (CommonUtils.isEmptyString(etAccount.getText().toString())) {
            b = true;
            ToastUtil.showToast("账号不能为空");
        } else if (!RegexpUtils.isEmailNO(etAccount.getText().toString()) && !RegexpUtils.isMobileNO(etAccount.getText().toString()) && etAccount.getText().toString().length() != 10) {
            b = true;
            ToastUtil.showToast("账号格式不正确");
        }
        return b;
    }
}
