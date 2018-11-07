package com.qvd.smartswitch.activity.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.orhanobut.logger.Logger;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.adapter.RoomManageListAdapter;
import com.qvd.smartswitch.api.CacheSetting;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.home.RoomListVo;
import com.qvd.smartswitch.model.login.MessageVo;
import com.qvd.smartswitch.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.EvictDynamicKey;

/**
 * Created by Administrator on 2018/6/13 0013.
 */

public class RoomManageActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.tv_add_room)
    TextView tvAddRoom;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;

    private final List<RoomListVo.DataBean> list = new ArrayList<>();
    private RoomManageListAdapter adapter;

    /**
     * 家庭id
     */
    private String family_id;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_room_manage;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }

    @Override
    protected void initData() {
        super.initData();
        family_id = getIntent().getStringExtra("family_id");
    }

    @Override
    protected void onResume() {
        super.onResume();
        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new RoomManageListAdapter(list);
        adapter.openLoadAnimation();
        adapter.isFirstOnly(false);
        adapter.setHasStableIds(true);
        recyclerview.setAdapter(adapter);
        adapter.setOnItemClickListener((adapter, view, position) -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("room", list.get(position));
            startActivity(new Intent(RoomManageActivity.this, RoomUpdateDetailsActivity.class)
                    .putExtra("bundle", bundle));
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        });
        adapter.setOnItemLongClickListener((adapter, view, position) -> {
            showDeleteRoomPopouWindow(position);
            return true;
        });
        myEmptyLayout.setTextViewMessage(getString(R.string.room_list_selete_empty));
        myErrorLayout.setOnClickListener(v -> {
            getRoomList();
        });
        getRoomList();
    }

    /**
     * 获取房间列表
     */
    private void getRoomList() {
        RetrofitService.qdoApi.getRoomList(family_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RoomListVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(RoomListVo roomListVo) {
                        if (roomListVo != null) {
                            if (roomListVo.getCode() == 200) {
                                if (roomListVo.getData() != null) {
                                    list.clear();
                                    list.addAll(roomListVo.getData());
                                    adapter.notifyDataSetChanged();
                                } else {
                                    adapter.setEmptyView(myEmptyLayout);
                                }
                            } else {
                                adapter.setEmptyView(myErrorLayout);
                            }
                        }
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

    /**
     * 显示删除房间的popupwindow
     */
    private void showDeleteRoomPopouWindow(int position) {
        new MaterialDialog.Builder(this)
                .content(R.string.room_manage_delete_room_content)
                .negativeText(R.string.common_cancel)
                .positiveText(R.string.common_confirm)
                .onNegative((dialog, which) -> {

                })
                .onPositive((dialog, which) -> deleteRoom(position))
                .show();
    }

    /**
     * 删除房间
     */
    private void deleteRoom(int position) {
        RetrofitService.qdoApi.deleteRoom(list.get(position).getRoom_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MessageVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MessageVo messageVo) {
                        if (messageVo != null) {
                            if (messageVo.getCode() == 200) {
                                ToastUtil.showToast(getString(R.string.common_delete_success));
                                getRoomList();
                            } else {
                                ToastUtil.showToast(getString(R.string.common_server_error));
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @OnClick({R.id.iv_common_actionbar_goback, R.id.tv_add_room})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.tv_add_room:
                startActivity(new Intent(this, RoomAddListActivity.class).putExtra("family_id", family_id));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
        }
    }

}
