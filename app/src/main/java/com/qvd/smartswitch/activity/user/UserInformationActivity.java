package com.qvd.smartswitch.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.activity.home.HomeAddActivity;
import com.qvd.smartswitch.activity.login.LoginActivity;
import com.qvd.smartswitch.api.CacheSetting;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.login.MessageVo;
import com.qvd.smartswitch.model.user.UserInfoVo;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.ConfigUtils;
import com.qvd.smartswitch.utils.SharedPreferencesUtil;
import com.qvd.smartswitch.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.EvictDynamicKey;

/**
 * Created by Administrator on 2018/6/15 0015.
 */

public class UserInformationActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.tv_logout)
    TextView tvLogout;
    @BindView(R.id.iv_one)
    ImageView ivOne;
    @BindView(R.id.tv_account)
    TextView tvAccount;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.rl_name)
    RelativeLayout rlName;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.rl_phone)
    RelativeLayout rlPhone;
    private MaterialDialog dialog;


    private boolean isUpdate = false;
    private String userId;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_user_information;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }

    @Override
    protected void initData() {
        super.initData();
        userId = SharedPreferencesUtil.getString(this, SharedPreferencesUtil.USER_ID);
        tvCommonActionbarTitle.setText("个人信息");
        getUserInfo();
    }

    private void getUserInfo() {
        CacheSetting.getCache().getUserInfo(RetrofitService.qdoApi.getUserInfo(ConfigUtils.user_id),
                new DynamicKey(userId), new EvictDynamicKey(false))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UserInfoVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(UserInfoVo userInfoVo) {
                        if (userInfoVo != null) {
                            if (userInfoVo.getCode() == 200) {
                                if (userInfoVo.getData() != null) {
                                    tvName.setText(userInfoVo.getData().getUser_name());
                                    tvAccount.setText(userInfoVo.getData().getUser_account());
                                    if (tvPhone.equals("null")) {
                                        tvPhone.setText(userInfoVo.getData().getUser_phone() + "");
                                    } else {
                                        tvPhone.setText("绑定手机号");
                                    }
                                } else {
                                    tvName.setHint("获取失败");
                                    tvAccount.setHint("获取失败");
                                    tvPhone.setHint("获取失败");
                                }
                            }
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
     * 保存个人信息
     */
    private void saveUserInfo() {
        RetrofitService.qdoApi.updateuserInfo(ConfigUtils.user_id, tvName.getText().toString(), tvPhone.getText().toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MessageVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MessageVo messageVo) {
                        if (messageVo.getCode() == 200) {
                            ToastUtil.showToast("更新成功");
                        } else {
                            ToastUtil.showToast("更新失败");
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

    @OnClick({R.id.iv_common_actionbar_goback, R.id.tv_logout, R.id.rl_name, R.id.rl_phone})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                if (ConfigUtils.user_id != null) {
                    if (isUpdate) {
                        saveUserInfo();
                        finish();
                    } else {
                        finish();
                    }
                } else {
                    finish();
                }
                break;
            case R.id.tv_logout:
                dialog = new MaterialDialog.Builder(this)
                        .content("正在注销")
                        .progress(true, 0)
                        .autoDismiss(false)
                        .show();
                SharedPreferencesUtil.putString(this, SharedPreferencesUtil.IDENTIFIER, "");
                SharedPreferencesUtil.putString(this, SharedPreferencesUtil.PASSWORD, "");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        startActivity(new Intent(UserInformationActivity.this, LoginActivity.class));
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                        ConfigUtils.user_id = "";
                        finish();
                    }
                }, 3000);
                break;
            case R.id.rl_name:
                showUpdateNameDialog();
                break;
            case R.id.rl_phone:
                showUpdatePhoneDialog();
                break;
        }
    }

    private void showUpdateNameDialog() {
        new MaterialDialog.Builder(this)
                .title("设置用户名称")
                .negativeText("取消")
                .positiveText("确定")
                .inputRange(1, 20, getResources().getColor(R.color.red))
                .input(tvPhone.getText().toString(), null, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        CommonUtils.closeSoftKeyboard(UserInformationActivity.this);
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        tvPhone.setText(dialog.getInputEditText().getText().toString());
                        CommonUtils.closeSoftKeyboard(UserInformationActivity.this);
                        isUpdate = true;
                    }
                })
                .show();
    }

    private void showUpdatePhoneDialog() {
        new MaterialDialog.Builder(this)
                .title("设置用户手机号")
                .negativeText("取消")
                .positiveText("确定")
                .inputType(InputType.TYPE_CLASS_PHONE)
                .inputRange(13, 20, getResources().getColor(R.color.red))
                .input(tvName.getText().toString(), null, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        CommonUtils.closeSoftKeyboard(UserInformationActivity.this);
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        tvName.setText(dialog.getInputEditText().getText().toString());
                        CommonUtils.closeSoftKeyboard(UserInformationActivity.this);
                        isUpdate = true;
                    }
                })
                .show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (ConfigUtils.user_id != null) {
            if (isUpdate) {
                saveUserInfo();
                finish();
            } else {
                finish();
            }
        } else {
            finish();
        }
    }
}
