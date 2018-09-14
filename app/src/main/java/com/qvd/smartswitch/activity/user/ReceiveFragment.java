package com.qvd.smartswitch.activity.user;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseFragment;
import com.qvd.smartswitch.adapter.ReceiverDeviceListAdapter;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.login.MessageVo;
import com.qvd.smartswitch.model.user.DeleteReceiveShareVo;
import com.qvd.smartswitch.model.user.UserReceiverDeviceListVo;
import com.qvd.smartswitch.utils.ConfigUtils;
import com.qvd.smartswitch.utils.ToastUtil;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by Administrator on 2018/6/14 0014.
 */

public class ReceiveFragment extends BaseFragment {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.smart_refresh)
    SmartRefreshLayout smartRefresh;

    private ReceiverDeviceListAdapter adapter;
    private List<UserReceiverDeviceListVo.DataBean> list = new ArrayList<>();

    public static ReceiveFragment newInstance(String param1) {
        ReceiveFragment fragment = new ReceiveFragment();
        Bundle args = new Bundle();
        args.putString("agrs1", param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_receive;
    }

    @Override
    protected void initData() {
        super.initData();
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        adapter = new ReceiverDeviceListAdapter(list);
        adapter.openLoadAnimation();
        adapter.isFirstOnly(false);
        recyclerview.setAdapter(adapter);
        smartRefresh.setHeaderHeight(100);
        smartRefresh.setEnableHeaderTranslationContent(true);
        smartRefresh.setRefreshHeader(new MaterialHeader(getActivity()));
        smartRefresh.setOnRefreshListener(refreshlayout -> {
            getData();
            smartRefresh.finishRefresh(1500, true);
        });

        myEmptyLayout.setOnClickListener(v -> getData());
        myErrorLayout.setOnClickListener(v -> getData());
        getData();

        adapter.setOnItemChildClickListener((adapter, view, position) -> {
            //接受
            acceptShare(position);
        });

        adapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                showDeleteDialog(position);
                return true;
            }
        });

    }

    /**
     * 显示删除的dialog
     */
    private void showDeleteDialog(int position) {
        new MaterialDialog.Builder(getActivity())
                .title("要删除这一条消息吗")
                .negativeText("取消")
                .positiveText("确定")
                .onNegative((dialog, which) -> {
                })
                .onPositive((dialog, which) -> deleteReceiver(list.get(position).getDevice_share_id()))
                .show();
    }

    /**
     * 删除接受共享的消息
     *
     * @param device_share_id
     */
    private void deleteReceiver(String device_share_id) {
        Gson gson = new Gson();
        DeleteReceiveShareVo feedBackVo = new DeleteReceiveShareVo();
        List<String> list = new ArrayList<>();
        list.add(device_share_id);
        feedBackVo.setDevice_share_id(list);
        String s = gson.toJson(feedBackVo);
        RequestBody body = RequestBody.create(MediaType.parse("Content-Type: application/json"), s);
        RetrofitService.qdoApi.deleteObjectUserShareDevicesInfo(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MessageVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MessageVo messageVo) {
                        if (messageVo.getCode() == 200) {
                            ToastUtil.showToast("删除成功");
                        } else {
                            ToastUtil.showToast("删除失败");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showToast("删除失败");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    /**
     * 接受共享
     */
    private void acceptShare(int position) {
        RetrofitService.qdoApi.acceptShareDevicesInfo(list.get(position).getDevice_share_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MessageVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MessageVo messageVo) {
                        if (messageVo.getCode() == 200) {
                            adapter.notifyItemChanged(position);
                        } else {
                            ToastUtil.showToast("网络错误");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showToast("网络错误");
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
        RetrofitService.qdoApi.getObjectUserShareDeviceInfo(ConfigUtils.user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UserReceiverDeviceListVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(UserReceiverDeviceListVo userReceiverDeviceListVo) {
                        if (userReceiverDeviceListVo.getCode() == 200) {
                            if (userReceiverDeviceListVo.getData() != null) {
                                list.clear();
                                list.addAll(userReceiverDeviceListVo.getData());
                                adapter.notifyDataSetChanged();
                                smartRefresh.setEnableRefresh(true);
                            } else {
                                adapter.setEmptyView(myEmptyLayout);
                                smartRefresh.setEnableRefresh(false);
                            }
                        } else {
                            adapter.setEmptyView(myErrorLayout);
                            smartRefresh.setEnableRefresh(false);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        adapter.setEmptyView(myErrorLayout);
                        smartRefresh.setEnableRefresh(false);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
