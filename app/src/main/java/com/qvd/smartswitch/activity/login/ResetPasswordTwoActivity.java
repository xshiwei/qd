package com.qvd.smartswitch.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ResetPasswordTwoActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.tv_text)
    TextView tvText;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.tv_send_msg)
    TextView tvSendMsg;
    @BindView(R.id.tv_other_vaild)
    TextView tvOtherVaild;


    private int type;
    private String account;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_reset_password_two;
    }

    @Override
    protected void initData() {
        super.initData();
        type = getIntent().getIntExtra("type", -1);
        account = getIntent().getStringExtra("account");
        tvCommonActionbarTitle.setText("科微多账号-科微多安全验证");
        if (type == 1) {
            tvText.setText("为了保护账号安全，需要验证邮箱有效性");
            tvContent.setText(Html.fromHtml("点击发送邮件按钮，我们将发送一条有验证码的邮件至邮箱<font color='DE5484'>" + account));
            tvSendMsg.setText("发送邮件");
        } else if (type == 2) {
            tvText.setText("为了保护账号安全，需要验证手机有效性");
            tvContent.setText(Html.fromHtml("点击发送短信按钮，我们将发送一条有验证码的短信至手机<font color='DE5484'>" + account));
            tvSendMsg.setText("发送短信");
        }
// else if (type == 3) {
//            tvText.setText("为了保护账号安全，需要验证账号有效性");
//            tvContent.setText(Html.fromHtml("点击发送验证码按钮，我们将发送一条有验证码的短信至该账号绑定的手机或者邮箱<font color='DE5484'>" + account));
//            tvSendMsg.setText("发送验证码");
//        }
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.add_room_background).init();
    }

    @OnClick({R.id.iv_common_actionbar_goback, R.id.tv_send_msg, R.id.tv_other_vaild})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.tv_send_msg:
                startActivity(new Intent(this, ResetPasswordThreeActivity.class)
                        .putExtra("type", type)
                        .putExtra("account", account));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.tv_other_vaild:
                break;
        }
    }
}
