package com.qvd.smartswitch.activity.device;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.adapter.ShareDeviceFamilyListAdapter;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.device.ShareSuccessVo;
import com.qvd.smartswitch.model.home.DeviceListVo;
import com.qvd.smartswitch.utils.ConfigUtils;
import com.qvd.smartswitch.utils.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DeviceShareFamilyActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;

    private ShareDeviceFamilyListAdapter adapter;
    private final List<DeviceListVo.DataBean> list = new ArrayList<>();
    private String family_id;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_device_share_family;
    }

    @Override
    protected void initData() {
        super.initData();
        tvCommonActionbarTitle.setText(R.string.device_share_family_title);
        family_id = getIntent().getStringExtra("family_id");
        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new ShareDeviceFamilyListAdapter(list);
        adapter.openLoadAnimation();
        adapter.isFirstOnly(false);
        recyclerview.setAdapter(adapter);
        myEmptyLayout.setTextViewMessage(getString(R.string.device_share_family_empty));
        myErrorLayout.setOnClickListener(v -> getDeviceList());

        getDeviceList();

        adapter.setOnItemClickListener((adapter, view, position) -> showShareDialog(position));
    }

    /**
     * 展示分享的dialog
     */
    private void showShareDialog(int position) {
        new MaterialDialog.Builder(this)
                .content(R.string.device_share_family_share_device)
                .negativeText(R.string.common_cancel)
                .positiveText(R.string.common_confirm)
                .onPositive((dialog, which) -> addFamilyDevice(position)).show();
    }

    /**
     * 添加家人共享设备
     */
    private void addFamilyDevice(int position) {
        RetrofitService.qdoApi.addShareFamilymembersOfDevice(list.get(position).getDevice_id(), family_id, ConfigUtils.user_id,
                list.get(position).getTable_type())
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

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }


    @OnClick(R.id.iv_common_actionbar_goback)
    public void onViewClicked() {
        finish();
    }

    /**
     * 获取设备列表
     */
    private void getDeviceList() {
        adapter.setEmptyView(myLoadingLayout);
        Map<String, String> map = new HashMap<>();
        map.put("user_id", ConfigUtils.user_id);
        map.put("family_id", ConfigUtils.family_locate.getFamily_id());
        RetrofitService.qdoApi.getDeviceList(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DeviceListVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(DeviceListVo deviceListVo) {
                        if (deviceListVo.getCode() == 200) {
                            if (deviceListVo.getData() != null) {
                                list.addAll(deviceListVo.getData());
                            } else {
                                adapter.setEmptyView(myEmptyLayout);
                            }
                        } else if (deviceListVo.getCode() == 400) {
                            adapter.setEmptyView(myErrorLayout);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {
                        adapter.setEmptyView(myErrorLayout);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
