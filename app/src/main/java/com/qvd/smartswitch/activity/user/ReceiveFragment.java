package com.qvd.smartswitch.activity.user;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
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
    private final List<UserReceiverDeviceListVo.DataBean> list = new ArrayList<>();

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
        adapter.setHasStableIds(true);
        recyclerview.setAdapter(adapter);
        smartRefresh.setHeaderHeight(100);
        smartRefresh.setEnableHeaderTranslationContent(true);
        smartRefresh.setRefreshHeader(new ClassicsHeader(Objects.requireNonNull(getActivity())));
        smartRefresh.setOnRefreshListener(refreshlayout -> {
            getData();
            smartRefresh.finishRefresh(1500, true);
        });

        myEmptyLayout.setTextViewMessage(getString(R.string.receive_empty));
        myErrorLayout.setOnClickListener(v -> getData());

        adapter.setOnItemChildClickListener((adapter, view, position) -> {
            //接受
            acceptShare(position);
        });

        adapter.setOnItemLongClickListener((adapter, view, position) -> {
            showDeleteDialog(position);
            return true;
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    /**
     * 显示删除的dialog
     */
    private void showDeleteDialog(int position) {
        new MaterialDialog.Builder(Objects.requireNonNull(getActivity()))
                .content(R.string.receive_delete_the_message)
                .negativeText(R.string.common_cancel)
                .positiveText(R.string.common_confirm)
                .onNegative((dialog, which) -> {
                })
                .onPositive((dialog, which) -> deleteReceiver(position))
                .show();
    }

    /**
     * 删除接受共享的消息
     *
     * @param position
     */
    private void deleteReceiver(int position) {
        Gson gson = new Gson();
        DeleteReceiveShareVo feedBackVo = new DeleteReceiveShareVo();
        List<String> list1 = new ArrayList<>();
        list1.add(list.get(position).getDevice_share_id());
        feedBackVo.setDevice_share_id(list1);
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
                            list.get(position).setIs_share(1);
                            adapter.notifyItemChanged(position);
                        } else {
                            ToastUtil.showToast(getString(R.string.receive_receive_fail));
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
