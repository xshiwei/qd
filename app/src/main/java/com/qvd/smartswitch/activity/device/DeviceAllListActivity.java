package com.qvd.smartswitch.activity.device;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.adapter.ShareDeviceFamilyListAdapter;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.home.DeviceListVo;
import com.qvd.smartswitch.utils.ConfigUtils;

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

public class DeviceAllListActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;

    private ShareDeviceFamilyListAdapter adapter;
    private final List<DeviceListVo.DataBean> list = new ArrayList<>();

    @Override
    protected int setLayoutId() {
        return R.layout.activity_device_share_family;
    }

    @Override
    protected void initData() {
        super.initData();
        tvCommonActionbarTitle.setText(R.string.device_all_list_title);
        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new ShareDeviceFamilyListAdapter(list);
        adapter.openLoadAnimation();
        adapter.isFirstOnly(false);
        recyclerview.setAdapter(adapter);
        myEmptyLayout.setTextViewMessage(getString(R.string.common_not_add_device));
        myErrorLayout.setOnClickListener(v -> getDeviceList());
        getDeviceList();
        adapter.setOnItemClickListener((BaseQuickAdapter adapter, View view, int position) -> {
            startActivity(new Intent(this, DeviceLogActivity.class)
                    .putExtra("device_id", list.get(position).getDevice_id())
                    .putExtra("device_type", list.get(position).getDevice_no()));
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
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
