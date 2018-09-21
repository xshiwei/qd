package com.qvd.smartswitch.activity.device;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.user.RecentSharePeopleListVo;
import com.qvd.smartswitch.model.user.ShareObjectInfoVo;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DeviceShareQevdoAccountActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.iv_device_pic)
    ImageView ivDevicePic;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.et_account)
    EditText etAccount;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;


    private String deviceId;
    private String device_type;
    private int is_control;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_device_share_qevdo_account;
    }

    @Override
    protected void initData() {
        super.initData();
        deviceId = getIntent().getStringExtra("device_id");
        device_type = getIntent().getStringExtra("device_type");
        is_control = getIntent().getIntExtra("is_control", -1);
        tvCommonActionbarTitle.setText("共享给科微多账号");
        tvName.setText(CommonUtils.getDeviceName(device_type));
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }

    @OnClick({R.id.iv_common_actionbar_goback, R.id.tv_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.tv_confirm:
                if (CommonUtils.isEmptyString(etAccount.getText().toString())) {
                    ToastUtil.showToast("账号不能为空");
                } else {
                    getShareObjectInfo();
                }
                break;
        }
    }

    /**
     * 获取被分享的人的信息
     */
    private void getShareObjectInfo() {
        RetrofitService.qdoApi.getAddShareObjectUserInfo(etAccount.getText().toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ShareObjectInfoVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ShareObjectInfoVo shareObjectInfoVo) {
                        if (shareObjectInfoVo.getCode() == 200) {
                            if (shareObjectInfoVo.getData() != null) {
                                RecentSharePeopleListVo.DataBean dataBean = new RecentSharePeopleListVo.DataBean();
                                dataBean.setShare_object_userid(shareObjectInfoVo.getData().getUser_id());
                                dataBean.setUser_account(etAccount.getText().toString());
                                dataBean.setUser_avatar(shareObjectInfoVo.getData().getUser_avatar());
                                dataBean.setUser_name(shareObjectInfoVo.getData().getUser_name());
                                startActivity(new Intent(DeviceShareQevdoAccountActivity.this, DeviceShareQevdoAccountFinallyActivity.class)
                                        .putExtra("person_info", dataBean)
                                        .putExtra("is_control", is_control)
                                        .putExtra("device_id", deviceId)
                                        .putExtra("device_type", device_type)
                                        .putExtra("type", 0));
                                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                finish();
                            } else {
                                ToastUtil.showToast("没有找到对应的用户");
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
}
