package com.qvd.smartswitch.activity.device;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.orhanobut.logger.Logger;
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
import butterknife.ButterKnife;
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
    private List<DeviceListVo.DataBean> list = new ArrayList<>();
    private String family_id;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_device_share_family;
    }

    @Override
    protected void initData() {
        super.initData();
        tvCommonActionbarTitle.setText("分享给家人");
        family_id = getIntent().getStringExtra("family_id");
        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new ShareDeviceFamilyListAdapter(list);
        adapter.openLoadAnimation();
        adapter.isFirstOnly(false);
        recyclerview.setAdapter(adapter);
        myEmptyLayout.setTextViewMessage("您还没有添加设备");
        myErrorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceList();
            }
        });

        getDeviceList();

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                showShareDialog(position);
            }
        });
    }

    /**
     * 展示分享的dialog
     */
    private void showShareDialog(int position) {
        new MaterialDialog.Builder(this)
                .title("您要把该设备分享出去吗")
                .negativeText("取消")
                .positiveText("确定")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        addFamilyDevice(position);
                    }
                }).show();
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
                        if (messageVo.getCode() == 200) {
                            ToastUtil.showToast("分享成功");
                            finish();
                        } else if (messageVo.getCode() == 203) {
                            ToastUtil.showToast("不能分享给自己");
                        } else if (messageVo.getCode() == 205) {
                            if (messageVo.getIs_share() == 0) {
                                ToastUtil.showToast("该设备已分享，请等待对方同意");
                            } else {
                                ToastUtil.showToast("该设备已分享");
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showToast("分享失败");
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
    public void getDeviceList() {
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
