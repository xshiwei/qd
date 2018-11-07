package com.qvd.smartswitch.activity.home;

import android.content.Intent;
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
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.home.AddRomeVo;
import com.qvd.smartswitch.model.home.RoomListVo;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.ConfigUtils;
import com.qvd.smartswitch.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RoomListSeleteActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.iv_add_room)
    ImageView ivAddRoom;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;

    private final List<RoomListVo.DataBean> list = new ArrayList<>();
    private RoomManageListAdapter adapter;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_selete_room_to_set;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }

    @Override
    protected void initData() {
        super.initData();
        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new RoomManageListAdapter(list);
        adapter.openLoadAnimation();
        adapter.isFirstOnly(false);
        adapter.setHasStableIds(true);
        recyclerview.setAdapter(adapter);
        adapter.setOnItemClickListener((adapter, view, position) -> {
            Intent intent = new Intent();
            intent.putExtra("room_id", list.get(position).getRoom_id());
            intent.putExtra("room_name", list.get(position).getRoom_name());
            setResult(2, intent);
            finish();
        });
        myEmptyLayout.setTextViewMessage(getString(R.string.room_list_selete_empty));
        myErrorLayout.setOnClickListener(v -> getRoomList());
        getRoomList();
    }

    @OnClick({R.id.iv_common_actionbar_goback, R.id.iv_add_room})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.iv_add_room:
                showAddRoomPopupWindow();
                break;
        }
    }

    /**
     * 展示添加房间
     */
    private void showAddRoomPopupWindow() {
        new MaterialDialog.Builder(this)
                .content(R.string.room_list_sleete_add_room)
                .negativeText(R.string.common_cancel)
                .positiveText(R.string.common_confirm)
                .inputRange(1, 20, getResources().getColor(R.color.red))
                .input(null, null, false, (dialog, input) -> {
                })
                .onNegative((dialog, which) -> CommonUtils.closeSoftKeyboard(RoomListSeleteActivity.this))
                .onPositive((dialog, which) -> {
                    addRoom(Objects.requireNonNull(dialog.getInputEditText()).getText().toString());
                    getRoomList();
                    CommonUtils.closeSoftKeyboard(RoomListSeleteActivity.this);
                })
                .show();
    }


    /**
     * 添加房间
     */
    private void addRoom(String name) {
        RetrofitService.qdoApi.addRoom(name, "http://pckgfzc5s.bkt.clouddn.com/h.png", ConfigUtils.family_locate.getFamily_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AddRomeVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(AddRomeVo messageVo) {
                        if (messageVo!=null){
                            if (messageVo.getCode()==200){
                                ToastUtil.showToast(getString(R.string.common_add_success));
                                getRoomList();
                            }else {
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

    /**
     * 获取房间列表
     */
    private void getRoomList() {
        RetrofitService.qdoApi.getRoomList(ConfigUtils.family_locate.getFamily_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RoomListVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(RoomListVo roomListVo) {
                        if (roomListVo != null) {
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
