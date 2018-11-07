package com.qvd.smartswitch.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseFragment;
import com.qvd.smartswitch.activity.device.DeviceShareManageActivity;
import com.qvd.smartswitch.adapter.ShareDeviceListAdapter;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.user.UserShareDeviceListVo;
import com.qvd.smartswitch.utils.ConfigUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/6/14 0014.
 */

public class ShareFragment extends BaseFragment {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.smart_refresh)
    SmartRefreshLayout smartRefresh;

    private ShareDeviceListAdapter adapter;
    private final List<UserShareDeviceListVo.DataBean> list = new ArrayList<>();

    public static ShareFragment newInstance(String param1) {
        ShareFragment fragment = new ShareFragment();
        Bundle args = new Bundle();
        args.putString("agrs1", param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_share;
    }

    @Override
    protected void initData() {
        super.initData();
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        adapter = new ShareDeviceListAdapter(list);
        adapter.openLoadAnimation();
        adapter.isFirstOnly(false);
        adapter.setHasStableIds(true);
        recyclerview.setAdapter(adapter);

        smartRefresh.setHeaderHeight(100);
        smartRefresh.setEnableHeaderTranslationContent(true);
        smartRefresh.setRefreshHeader(new ClassicsHeader(Objects.requireNonNull(getActivity())));
        smartRefresh.setOnRefreshListener(refreshlayout -> {
            getData();
            smartRefresh.finishRefresh(1500, true);
        });

        adapter.setOnItemClickListener((adapter, view, position) -> {
            startActivity(new Intent(getActivity(), DeviceShareManageActivity.class)
                    .putExtra("device_id", list.get(position).getDevice_id())
                    .putExtra("device_type", list.get(position).getTable_type()));
            getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        });
        myEmptyLayout.setTextViewMessage(getString(R.string.share_empty));
        myErrorLayout.setOnClickListener(v -> getData());
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    /**
     * 获取数据
     */
    private void getData() {
        adapter.setEmptyView(myLoadingLayout);
        RetrofitService.qdoApi.getUserShareDeviceList(ConfigUtils.user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UserShareDeviceListVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(UserShareDeviceListVo userShareDeviceListVo) {
                        list.clear();
                        if (userShareDeviceListVo.getCode() == 200) {
                            if (userShareDeviceListVo.getData() != null) {
                                list.addAll(userShareDeviceListVo.getData());
                                smartRefresh.setEnableRefresh(true);
                            } else {
                                adapter.setEmptyView(myEmptyLayout);
                                smartRefresh.setEnableRefresh(true);
                            }
                        } else {
                            adapter.setEmptyView(myErrorLayout);
                            smartRefresh.setEnableRefresh(false);
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

}
