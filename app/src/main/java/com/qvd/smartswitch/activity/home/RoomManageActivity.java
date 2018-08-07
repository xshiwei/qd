package com.qvd.smartswitch.activity.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.adapter.RoomManageListAdapter;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.home.RoomListVo;
import com.qvd.smartswitch.model.login.MessageVo;
import com.qvd.smartswitch.utils.SnackbarUtils;
import com.qvd.smartswitch.utils.ToastUtil;
import com.qvd.smartswitch.widget.EmptyLayout;
import com.qvd.smartswitch.widget.MyPopupWindowOne;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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
    @BindView(R.id.emptylayout)
    EmptyLayout emptylayout;

    private List<RoomListVo.DataBean> list = new ArrayList<>();
    private RoomManageListAdapter adapter;
    /**
     * 删除房间
     */
    private MyPopupWindowOne popupWindowDeleteRoom;
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
        getRoomList();
        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new RoomManageListAdapter(this, list);
        recyclerview.setAdapter(adapter);
        adapter.setOnItemClickListener(new RoomManageListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("room", list.get(position));
                startActivity(new Intent(RoomManageActivity.this, UpdateRoomDetailsActivity.class)
                        .putExtra("bundle", bundle));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }

            @Override
            public void onItemLongClickListener(View view, int position) {
                showDeleteRoomPopouWindow(position);
                popupWindowDeleteRoom.showPopupWindow(view);
            }
        });
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
                                emptylayout.hide();
                                list.clear();
                                list.addAll(roomListVo.getData());
                                adapter.notifyDataSetChanged();
                            } else if (roomListVo.getCode() == 400) {
                                emptylayout.showEmpty();
                            } else {
                                emptylayout.showEmpty();
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

    /**
     * 显示删除房间的popupwindow
     */
    private void showDeleteRoomPopouWindow(int position) {
        popupWindowDeleteRoom = new MyPopupWindowOne(this, "删除房间后，原房间内设备将被移入默认房间，是否确认删除？", "取消", "删除", new MyPopupWindowOne.IPopupWindowListener() {
            @Override
            public void cancel() {
                popupWindowDeleteRoom.dismiss();
            }

            @Override
            public void confirm() {
                //删除房间
                popupWindowDeleteRoom.dismiss();
                deleteRoom(position);
            }
        });
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
                        if (messageVo.getCode() == 200) {
                            SnackbarUtils.Short(tvCommonActionbarTitle, "删除成功").show();
                            getRoomList();
                        } else if (messageVo.getCode() == 400) {
                            SnackbarUtils.Short(tvCommonActionbarTitle, "删除失败").show();
                        } else {
                            SnackbarUtils.Short(tvCommonActionbarTitle, "连接超时").show();
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
                startActivity(new Intent(this, AddRoomListActivity.class).putExtra("family_id", family_id));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
        }
    }

}
