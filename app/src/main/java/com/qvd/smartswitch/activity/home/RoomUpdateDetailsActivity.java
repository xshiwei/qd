package com.qvd.smartswitch.activity.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.adapter.UpdateRoomDeviceListAdapter;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.device.RoomDeviceListVo;
import com.qvd.smartswitch.model.device.UpdateDeviceRoomVo;
import com.qvd.smartswitch.model.home.RoomListVo;
import com.qvd.smartswitch.model.login.MessageVo;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.ConfigUtils;
import com.qvd.smartswitch.utils.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by Administrator on 2018/6/13 0013.
 */

public class RoomUpdateDetailsActivity extends BaseActivity {
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.tv_save)
    TextView tvSave;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.rl_room_name)
    RelativeLayout rlRoomName;
    @BindView(R.id.iv_pic)
    ImageView ivPic;
    @BindView(R.id.rl_room_pic)
    RelativeLayout rlRoomPic;
    @BindView(R.id.tv_text)
    TextView tvText;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;

    private final List<RoomDeviceListVo.DataBean> list = new ArrayList<>();
    private UpdateRoomDeviceListAdapter adapter;

    /**
     * 计算有几个被选中
     */
    private int index;

    /**
     * 判断是否改动
     */
    private boolean isUpdate = false;

    private RoomListVo.DataBean roomVo;
    private String pic;


    @Override
    protected int setLayoutId() {
        return R.layout.activity_update_room_details;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }

    @Override
    protected void initData() {
        super.initData();
        Bundle bundle = getIntent().getBundleExtra("bundle");
        roomVo = (RoomListVo.DataBean) bundle.getSerializable("room");
        tvName.setText(Objects.requireNonNull(roomVo).getRoom_name());
        Glide.with(this).load(roomVo.getRoom_pic()).into(ivPic);
        pic = roomVo.getRoom_pic();

        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new UpdateRoomDeviceListAdapter(list, roomVo.getRoom_name());
        adapter.openLoadAnimation();
        adapter.isFirstOnly(false);
        adapter.setHasStableIds(true);
        recyclerview.setAdapter(adapter);
        adapter.setOnItemClickListener((adapter, view, position) -> {
            RoomDeviceListVo.DataBean dataBean = (RoomDeviceListVo.DataBean) adapter.getData().get(position);
            boolean selete = dataBean.getIs_selete();
            if (selete) {
                index++;
                dataBean.setIs_selete(false);
            } else {
                index--;
                dataBean.setIs_selete(true);
            }
            tvText.setText("已选中" + index + "个设备");
            adapter.notifyDataSetChanged();
            isUpdate = true;
            tvSave.setTextColor(RoomUpdateDetailsActivity.this.getResources().getColor(R.color.white));
            tvSave.setBackground(RoomUpdateDetailsActivity.this.getResources().getDrawable(R.drawable.seletor_circle_green_gray_five));
        });
        myEmptyLayout.setTextViewMessage(getString(R.string.room_update_details_empty));
        myErrorLayout.setOnClickListener(v -> getDeviceList());
        getDeviceList();
    }

    /**
     * 获取设备列表
     */
    private void getDeviceList() {
        RetrofitService.qdoApi.getRoomDeviceList(ConfigUtils.user_id, roomVo.getRoom_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RoomDeviceListVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(RoomDeviceListVo deviceListVo) {
                        if (deviceListVo.getCode() == 200) {
                            if (deviceListVo.getData() != null && deviceListVo.getData().size() > 0) {
                                for (RoomDeviceListVo.DataBean dataBean : deviceListVo.getData()) {
                                    dataBean.setIs_selete(true);
                                    list.add(dataBean);
                                }
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

    @OnClick({R.id.tv_cancel, R.id.tv_save, R.id.rl_room_name, R.id.rl_room_pic})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                //取消
                if (isUpdate) {
                    showPopupwindowDelete();
                } else {
                    finish();
                }
                break;
            case R.id.tv_save:
                //保存
                if (isUpdate) {
                    updateRoom();
                }
                break;
            case R.id.rl_room_name:
                //房间名字
                showPopupwindowName();
                break;
            case R.id.rl_room_pic:
                //更换图标
                isUpdate = true;
                tvSave.setEnabled(true);
                tvSave.setTextColor(getResources().getColor(R.color.white));
                tvSave.setBackground(getResources().getDrawable(R.drawable.seletor_circle_green_gray_five));
                startActivityForResult(new Intent(this, RoomPicListActivity.class), 1);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
        }
    }

    /**
     * 更新房间信息
     */
    private void updateRoom() {
        Map<String, String> map = new HashMap<>();
        map.put("room_id", roomVo.getRoom_id());
        map.put("room_name", tvName.getText().toString());
        map.put("room_pic", pic);
        RetrofitService.qdoApi.updateRoom(map)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .filter(messageVo -> messageVo.getCode() == 200)
                .observeOn(Schedulers.io())
                .concatMap((Function<MessageVo, ObservableSource<MessageVo>>) messageVo -> {
                    Gson gson = new Gson();
                    List<String> list = new ArrayList<>();
                    for (RoomDeviceListVo.DataBean dataBean : getSeleteList()) {
                        list.add(dataBean.getDevice_id());
                    }
                    UpdateDeviceRoomVo updateDeviceRoomVo = new UpdateDeviceRoomVo();
                    updateDeviceRoomVo.setRoom_id(ConfigUtils.defaultRoomId);
                    updateDeviceRoomVo.setDevice_id(list);
                    String body = gson.toJson(updateDeviceRoomVo);
                    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type: application/json"), body);
                    return RetrofitService.qdoApi.updateDeviceRoom(requestBody);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MessageVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MessageVo messageVo) {
                        if (messageVo != null) {
                            if (messageVo.getCode() == 200) {
                                ToastUtil.showToast(getString(R.string.common_room_save_success));
                                finish();
                            } else {
                                ToastUtil.showToast(getString(R.string.common_room_save_success_and_device_move_fail));
                                finish();
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
     * 显示取消的popupwindow
     */
    private void showPopupwindowDelete() {
        new MaterialDialog.Builder(this)
                .title(R.string.room_add_details_edit_not_save)
                .content(R.string.room_add_details_confirm_save_content)
                .negativeText(R.string.common_cancel)
                .positiveText(R.string.common_confirm)
                .onNegative((dialog, which) -> finish())
                .onPositive((dialog, which) -> {
                    ToastUtil.showToast(getString(R.string.common_save_success));
                    finish();
                })
                .show();
    }

    /**
     * 显示更换名字的popupwindow
     */
    private void showPopupwindowName() {
        new MaterialDialog.Builder(this)
                .content(R.string.room_add_details_set_room_name)
                .negativeText(R.string.common_cancel)
                .positiveText(R.string.common_confirm)
                .inputRange(1, 20, getResources().getColor(R.color.red))
                .input(tvName.getText().toString(), null, false, (dialog, input) -> {

                })
                .onNegative((dialog, which) -> {
                    isUpdate = false;
                    CommonUtils.closeSoftKeyboard(RoomUpdateDetailsActivity.this);
                })
                .onPositive((dialog, which) -> {
                    tvName.setText(Objects.requireNonNull(dialog.getInputEditText()).getText().toString());
                    isUpdate = true;
                    tvSave.setTextColor(getResources().getColor(R.color.white));
                    tvSave.setBackground(getResources().getDrawable(R.drawable.seletor_circle_green_gray_five));
                    CommonUtils.closeSoftKeyboard(RoomUpdateDetailsActivity.this);
                })
                .show();
    }

    /**
     * 获取未选中的所有条目
     */
    private List<RoomDeviceListVo.DataBean> getSeleteList() {
        List<RoomDeviceListVo.DataBean> roomDeviceList = new ArrayList<>();
        for (int i = 0; i < adapter.getData().size(); i++) {
            if (!adapter.getData().get(i).getIs_selete()) {
                roomDeviceList.add(adapter.getData().get(i));
            }
        }
        return roomDeviceList;
    }

    @Override
    public void onBackPressed() {
        //取消
        if (isUpdate) {
            showPopupwindowDelete();
        } else {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 2) {
             pic = data.getStringExtra("pic");
            Glide.with(this).load(pic).into(ivPic);
        }
    }
}
