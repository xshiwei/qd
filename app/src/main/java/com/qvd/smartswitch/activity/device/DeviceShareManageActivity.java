package com.qvd.smartswitch.activity.device;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.adapter.DeviceShareManageListAdapter;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.login.MessageVo;
import com.qvd.smartswitch.model.user.DeviceShareManageListVo;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.ConfigUtils;
import com.qvd.smartswitch.utils.ToastUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DeviceShareManageActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.smart_refresh)
    SmartRefreshLayout smartRefresh;
    @BindView(R.id.rl_add_share)
    RelativeLayout rlAddShare;


    private String device_id;
    private DeviceShareManageListAdapter adapter;
    private final List<DeviceShareManageListVo.DataBean> list = new ArrayList<>();
    private String device_type;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_device_share_manage;
    }

    @Override
    protected void initData() {
        super.initData();
        device_id = getIntent().getStringExtra("device_id");
        device_type = getIntent().getStringExtra("device_type");
        tvCommonActionbarTitle.setText(CommonUtils.getDeviceName(device_type) + "共享管理");
        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new DeviceShareManageListAdapter(list);
        adapter.openLoadAnimation();
        adapter.isFirstOnly(false);
        adapter.setHasStableIds(true);
        recyclerview.setAdapter(adapter);

        smartRefresh.setHeaderHeight(100);
        smartRefresh.setEnableHeaderTranslationContent(true);
        smartRefresh.setRefreshHeader(new ClassicsHeader(this));
        smartRefresh.setOnRefreshListener(refreshlayout -> {
            getData();
            smartRefresh.finishRefresh(1000, true);
        });

        myErrorLayout.setOnClickListener(v -> getData());
        myEmptyLayout.setTextViewMessage(getString(R.string.device_share_manage_not_share_device));

        adapter.setOnItemChildClickListener((adapter, view, position) -> showDeleteDialog(position));
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    private void showDeleteDialog(int position) {
        new MaterialDialog.Builder(this)
                .content(R.string.device_share_manage_cancel_accredit)
                .negativeText(R.string.common_cancel)
                .positiveText(R.string.common_confirm)
                .onNegative((dialog, which) -> {

                })
                .onPositive((dialog, which) -> deleteShareObject(position)).show();
    }

    private void deleteShareObject(int position) {
        RetrofitService.qdoApi.cancelDevicesShare(list.get(position).getDevice_share_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MessageVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MessageVo messageVo) {
                        if (messageVo.getCode() == 200) {
                            ToastUtil.showToast(getString(R.string.common_delete_success));
                            getData();
                        } else {
                            ToastUtil.showToast(getString(R.string.common_delete_fail));
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
     * 获取数据
     */
    private void getData() {
        adapter.setEmptyView(myLoadingLayout);
        RetrofitService.qdoApi.getDeviceShareUserInfo(ConfigUtils.user_id, device_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DeviceShareManageListVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(DeviceShareManageListVo deviceShareManageListVo) {
                        list.clear();
                        switch (deviceShareManageListVo.getCode()) {
                            case 200:
                                if (deviceShareManageListVo.getData() != null) {
                                    list.addAll(deviceShareManageListVo.getData());
                                    smartRefresh.setEnableRefresh(true);
                                } else {
                                    smartRefresh.setEnableRefresh(true);
                                    adapter.setEmptyView(myEmptyLayout);
                                }
                                break;
                            case 201:
                                adapter.setEmptyView(myEmptyLayout);
                                smartRefresh.setEnableRefresh(false);
                                break;
                            default:
                                adapter.setEmptyView(myErrorLayout);
                                smartRefresh.setEnableRefresh(false);
                                break;
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {
                        adapter.setEmptyView(myErrorLayout);
                        smartRefresh.setEnableRefresh(false);
                        adapter.notifyDataSetChanged();
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

    @OnClick({R.id.iv_common_actionbar_goback, R.id.rl_add_share})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.rl_add_share:
                startActivity(new Intent(this, DeviceShareActivity.class)
                        .putExtra("device_id", device_id)
                        .putExtra("device_type", device_type));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
        }
    }
}
