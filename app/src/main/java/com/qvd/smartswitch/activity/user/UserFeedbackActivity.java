package com.qvd.smartswitch.activity.user;

import android.os.Bundle;
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
import com.qvd.smartswitch.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
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

    private RippleView rippleView;

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
        tvCommonActionbarTitle.setText("反馈");
        rippleView = findViewById(R.id.riv_view);
        rippleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!vaild()) {
                    addFeedback();
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
                            ToastUtil.showToast("反馈成功");
                            finish();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showToast("网络出错了");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private boolean vaild() {
        boolean b = false;
        if (CommonUtils.isEmptyString(etCommonContract.getText().toString())) {
            Toast.makeText(this, "联系方式不能为空", Toast.LENGTH_SHORT).show();
            b = true;
        } else if (CommonUtils.isEmptyString(etContent.getText().toString())) {
            Toast.makeText(this, "反馈内容不能为空", Toast.LENGTH_SHORT).show();
            b = true;
        }
        return b;
    }
}
