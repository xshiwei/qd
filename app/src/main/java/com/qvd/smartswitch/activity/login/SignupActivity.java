package com.qvd.smartswitch.activity.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.widget.MyProgressDialog;

import butterknife.BindView;
import butterknife.OnClick;

public class SignupActivity extends BaseActivity {
    private static final String TAG = "SignupActivity";
    @BindView(R.id.input_name)
    EditText inputName;
    @BindView(R.id.btn_signup)
    TextView btnSignup;
    @BindView(R.id.link_login)
    TextView linkLogin;
    @BindView(R.id.input_code)
    EditText inputCode;
    @BindView(R.id.tv_input_code)
    TextView tvInputCode;

    private final MyCountDownTimer myCountDownTimer = new MyCountDownTimer(60000, 1000);//60000
    private Drawable dr;

    @Override
    protected void initData() {
        super.initData();
        dr = getResources().getDrawable(R.mipmap.edittext_error);
        dr.setBounds(0, 0, dr.getIntrinsicWidth(), dr.getIntrinsicHeight());
    }

    @Override
    protected int setLayoutId() {
        return R.layout.activity_signup;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.init();
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        btnSignup.setEnabled(false);

        final MyProgressDialog progressDialog = MyProgressDialog.createProgressDialog(this, 5000,
                new MyProgressDialog.OnTimeOutListener() {
                    @Override
                    public void onTimeOut(ProgressDialog dialog) {
                        dialog.dismiss();
                        onSignupFailed();
                    }
                });
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String name = inputName.getText().toString();
        String code = inputCode.getText().toString();

        new Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    public void onSignupSuccess() {
        btnSignup.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        btnSignup.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = inputName.getText().toString();

        if (name.isEmpty() || name.length() < 10) {
            inputName.setError("at least 3 characters", dr);
            valid = false;
        } else {
            inputName.setError(null);
        }

//        if (password.isEmpty() || password.length() < 11) {
//            inputPassword.setError("Enter Valid Address", dr);
//            valid = false;
//        } else {
//            inputPassword.setError(null);
//        }
//
//        if (rePassword.isEmpty() || !password.equals(rePassword)) {
//            inputRepassword.setError("enter a valid email address", dr);
//            valid = false;
//        } else {
//            inputRepassword.setError(null);
//        }

        return valid;
    }

    @OnClick({R.id.tv_input_code, R.id.btn_signup, R.id.link_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_input_code:
                myCountDownTimer.start();
                getcode();
                break;
            case R.id.btn_signup:
                signup();
                break;
            case R.id.link_login:
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
        }
    }

    /**
     * 获取验证码
     */
    private void getcode() {

    }

    public class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
            //防止计时过程中重复点击
            tvInputCode.setClickable(false);
            tvInputCode.setText("" + l / 1000);
        }

        @Override
        public void onFinish() {
            //重新给Button设置文字
            tvInputCode.setText("获取");
            //设置可点击
            tvInputCode.setClickable(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myCountDownTimer.cancel();
    }
}