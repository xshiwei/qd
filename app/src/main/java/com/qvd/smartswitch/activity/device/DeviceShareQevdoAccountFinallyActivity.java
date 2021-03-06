package com.qvd.smartswitch.activity.device;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.device.ShareSuccessVo;
import com.qvd.smartswitch.model.user.RecentSharePeopleListVo;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.ConfigUtils;
import com.qvd.smartswitch.utils.ToastUtil;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DeviceShareQevdoAccountFinallyActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.iv_device_pic)
    CircleImageView ivDevicePic;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_text)
    TextView tvText;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;


    private RecentSharePeopleListVo.DataBean dataBean;
    private String deviceId;
    private String device_type;
    private int is_control;

    /**
     * 0为普通账户 1为家人
     */
    private int type;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_device_share_qevdo_account_finally;
    }

    @Override
    protected void initData() {
        super.initData();
        tvCommonActionbarTitle.setText(R.string.device_share_qevdo_account_finally_title);
        dataBean = (RecentSharePeopleListVo.DataBean) getIntent().getSerializableExtra("person_info");
        deviceId = getIntent().getStringExtra("device_id");
        device_type = getIntent().getStringExtra("device_type");
        is_control = getIntent().getIntExtra("is_control", -1);
        type = Integer.parseInt(getIntent().getStringExtra("type"));
        if (dataBean != null) {
            if (!CommonUtils.isEmptyString(dataBean.getUser_avatar())) {
                Glide.with(this).load(dataBean.getUser_avatar()).into(ivDevicePic);
            }
            tvName.setText(dataBean.getUser_name());
            if (is_control == 1) {
                tvText.setText("允许控制 [" + CommonUtils.getDeviceName(device_type) + " ]");
            } else {
                tvText.setText("仅可查看 [" + CommonUtils.getDeviceName(device_type) + " ]");
            }
        }
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
                if (type == 0) {
                    addShareDevice();
                } else {
                    addFamilyDevice();
                }
                break;
        }
    }

    /**
     * 添加家人共享设备
     */
    private void addFamilyDevice() {
        RetrofitService.qdoApi.addShareFamilymembersOfDevice(deviceId, dataBean.getShare_object_userid(), ConfigUtils.user_id,
                device_type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ShareSuccessVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ShareSuccessVo messageVo) {
                        switch (messageVo.getCode()) {
                            case 200:
                                ToastUtil.showToast(getString(R.string.common_share_success));
                                finish();
                                break;
                            case 203:
                                ToastUtil.showToast(getString(R.string.common_not_share_self));
                                break;
                            case 205:
                                if (messageVo.getIs_share() == 0) {
                                    ToastUtil.showToast(getString(R.string.device_share_family_wait_agree));
                                } else {
                                    ToastUtil.showToast(getString(R.string.device_share_family_have_to_share));
                                }
                                break;
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showToast(getString(R.string.common_share_fail));
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 添加共享设备
     */
    private void addShareDevice() {
        RetrofitService.qdoApi.addShareDevicesInfo(deviceId, dataBean.getShare_object_userid(), ConfigUtils.user_id,
                device_type, is_control)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ShareSuccessVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ShareSuccessVo messageVo) {
                        switch (messageVo.getCode()) {
                            case 200:
                                ToastUtil.showToast(getString(R.string.common_share_success));
                                finish();
                                break;
                            case 203:
                                ToastUtil.showToast(getString(R.string.common_not_share_self));
                                break;
                            case 205:
                                if (messageVo.getIs_share() == 0) {
                                    ToastUtil.showToast(getString(R.string.device_share_family_wait_agree));
                                } else {
                                    ToastUtil.showToast(getString(R.string.device_share_family_have_to_share));
                                }
                                break;
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showToast(getString(R.string.common_share_fail));
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
