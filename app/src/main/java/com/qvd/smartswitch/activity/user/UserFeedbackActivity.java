package com.qvd.smartswitch.activity.user;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.login.MessageVo;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.ConfigUtils;
import com.qvd.smartswitch.utils.RegexpUtils;
import com.qvd.smartswitch.utils.ToastUtil;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/6/19 0019.
 */

public class UserFeedbackActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.et_common_contract)
    EditText etCommonContract;
    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;

    private String type;

    private boolean b1 = false;
    private boolean b2 = false;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_user_feedback;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }

    @Override
    protected void initData() {
        super.initData();
        type = getIntent().getStringExtra("type");
        tvName.setText(CommonUtils.getDeviceName(type));
        tvCommonActionbarTitle.setText(R.string.user_feedback_title);
        RippleView rippleView = findViewById(R.id.riv_view);
        rippleView.setOnClickListener(v -> {
            if (!vaild()) {
                addFeedback();
            }
        });
        etCommonContract.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!CommonUtils.isEmptyString(s.toString())) {
                    b1 = true;
                    if (b2) {
                        tvConfirm.setTextColor(getResources().getColor(R.color.home_setting_text));
                    }
                } else {
                    b1 = false;
                    tvConfirm.setTextColor(getResources().getColor(R.color.home_setting_text_two));
                }
            }
        });
        etContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!CommonUtils.isEmptyString(s.toString())) {
                    b2 = true;
                    if (b1) {
                        tvConfirm.setTextColor(getResources().getColor(R.color.home_setting_text));
                    }
                } else {
                    b2 = false;
                    tvConfirm.setTextColor(getResources().getColor(R.color.home_setting_text_two));
                }
            }
        });
    }


    @OnClick({R.id.iv_common_actionbar_goback})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
        }
    }

    private void addFeedback() {
        RetrofitService.qdoApi.addUserFeedbackInfo(ConfigUtils.user_id, etCommonContract.getText().toString(), etContent.getText().toString(), type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MessageVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MessageVo messageVo) {
                        if (messageVo.getCode() == 200) {
                            ToastUtil.showToast(getString(R.string.common_feedback_success));
                            finish();
                        } else {
                            ToastUtil.showToast(getString(R.string.common_feedback_fail));
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

    private boolean vaild() {
        boolean b = false;
        if (CommonUtils.isEmptyString(etCommonContract.getText().toString())) {
            Toast.makeText(this, R.string.common_contract_not_empty, Toast.LENGTH_SHORT).show();
            b = true;
        } else if (CommonUtils.isEmptyString(etContent.getText().toString())) {
            Toast.makeText(this, R.string.common_content_not_empty, Toast.LENGTH_SHORT).show();
            b = true;
        } else if (!RegexpUtils.isEmailNO(etCommonContract.getText().toString()) && !RegexpUtils.isMobileNO(etCommonContract.getText().toString()) && etCommonContract.getText().toString().length() != 10) {
            b = true;
            ToastUtil.showToast(getString(R.string.reset_password_type_error));
        }
        return b;
    }
}
