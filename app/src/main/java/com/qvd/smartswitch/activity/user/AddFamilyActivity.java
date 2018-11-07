package com.qvd.smartswitch.activity.user;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.login.MessageVo;
import com.qvd.smartswitch.model.user.ShareObjectInfoVo;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.ConfigUtils;
import com.qvd.smartswitch.utils.RegexpUtils;
import com.qvd.smartswitch.utils.ToastUtil;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class AddFamilyActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.et_input_relation)
    EditText etInputRelation;
    @BindView(R.id.et_input_account)
    EditText etInputAccount;
    @BindView(R.id.tv_add)
    TextView tvAdd;


    @Override
    protected int setLayoutId() {
        return R.layout.activity_add_family;
    }

    @Override
    protected void initData() {
        super.initData();
        tvCommonActionbarTitle.setText(R.string.add_family_title);
        String name = getIntent().getStringExtra("name");
        etInputRelation.setText(name + "");
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }

    @OnClick({R.id.iv_common_actionbar_goback, R.id.tv_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.tv_add:
                if (vaild()) {
                    return;
                } else {
                    addFamily();
                }
                break;
        }
    }

    /**
     * 添加家庭成员
     */
    private void addFamily() {
        RetrofitService.qdoApi.getAddShareObjectUserInfo(etInputAccount.getText().toString())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .filter(shareObjectInfoVo -> shareObjectInfoVo.getCode() == 200)
                .flatMap((Function<ShareObjectInfoVo, ObservableSource<MessageVo>>) shareObjectInfoVo -> RetrofitService.qdoApi.addFamilyMember(ConfigUtils.user_id, shareObjectInfoVo.getData().getUser_id(), etInputRelation.getText().toString()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MessageVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MessageVo messageVo) {
                        switch (messageVo.getCode()) {
                            case 200:
                                ToastUtil.showToast(getString(R.string.add_family_send_invite_success));
                                finish();
                                break;
                            case 203:
                                ToastUtil.showToast(getString(R.string.add_family_not_add_self));
                                break;
                            case 205:
                                ToastUtil.showToast(getString(R.string.add_family_the_account_be_send_invite));
                                break;
                            default:
                                ToastUtil.showToast(getString(R.string.add_family_send_invite_fail));
                                break;
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
     * 输入判断
     *
     * @return
     */
    private boolean vaild() {
        boolean b = false;
        if (CommonUtils.isEmptyString(etInputRelation.getText().toString())) {
            b = true;
            ToastUtil.showToast(getString(R.string.add_family_family_name_not_empty));
        } else if (CommonUtils.isEmptyString(etInputAccount.getText().toString())) {
            b = true;
            ToastUtil.showToast(getString(R.string.add_family_account_not_empty));
        } else if (!RegexpUtils.isEmailNO(etInputAccount.getText().toString()) && !RegexpUtils.isMobileNO(etInputAccount.getText().toString()) && etInputAccount.getText().toString().length() != 10) {
            b = true;
            ToastUtil.showToast(getString(R.string.add_family_account_type_not_correct));
        }
        return b;
    }

}
