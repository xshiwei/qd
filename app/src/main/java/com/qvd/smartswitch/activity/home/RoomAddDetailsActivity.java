package com.qvd.smartswitch.activity.home;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.adapter.AddRoomDeviceListAdapter;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.device.UpdateDeviceRoomVo;
import com.qvd.smartswitch.model.home.AddRomeVo;
import com.qvd.smartswitch.model.home.DeviceListVo;
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

public class RoomAddDetailsActivity extends BaseActivity {
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
    @BindView(R.id.tv_home_name)
    TextView tvHomeName;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;

    private final List<DeviceListVo.DataBean> list = new ArrayList<>();
    private AddRoomDeviceListAdapter adapter;

    /**
     * 计算有几个被选中
     */
    private int index;

    /**
     * 判断是否改动
     */
    private boolean isUpdate = true;
    /**
     * 图标
     */
    private String pic;

    /**
     * 家庭id
     */
    private String family_id;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_add_room_details;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }

    @Override
    protected void initData() {
        super.initData();
        String name = getIntent().getStringExtra("name");
        pic = getIntent().getStringExtra("pic");
        family_id = getIntent().getStringExtra("family_id");
        tvName.setText(name);
        tvHomeName.setText(ConfigUtils.family_locate.getFamily_name());
        Glide.with(this).load(pic).into(ivPic);
        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new AddRoomDeviceListAdapter(list);
        adapter.openLoadAnimation();
        adapter.isFirstOnly(false);
        adapter.setHasStableIds(true);
        recyclerview.setAdapter(adapter);
        adapter.setOnItemClickListener((adapter, view, position) -> {
            DeviceListVo.DataBean bean = (DeviceListVo.DataBean) adapter.getData().get(position);
            boolean selete = bean.isIs_selete();
            if (!selete) {
                RoomAddDetailsActivity.this.showPopupwindowConfirm(bean);
            } else {
                index--;
                bean.setIs_selete(false);
                tvText.setText("已选中" + index + "个设备");
                adapter.notifyDataSetChanged();
            }
            isUpdate = true;
        });
        myEmptyLayout.setTextViewMessage(getString(R.string.room_add_details_empty));
        myErrorLayout.setOnClickListener(v -> getDeviceList());
        getDeviceList();
    }

    /**
     * 显示是否将该设备移至当前房间中
     */
    private void showPopupwindowConfirm(DeviceListVo.DataBean dataBean) {
        String title = "该设备已关联到" + dataBean.getRoom_name() + "确定要移动到[" + tvName.getText().toString() + "]吗？";
        new MaterialDialog.Builder(this)
                .content(title)
                .negativeText(R.string.common_cancel)
                .positiveText(R.string.common_confirm)
                .onNegative((dialog, which) -> {

                })
                .onPositive((dialog, which) -> {
                    index++;
                    dataBean.setIs_selete(true);
                    tvText.setText("已选中" + index + "个设备");
                    adapter.notifyDataSetChanged();
                })
                .show();
    }

    /**
     * 获取设备列表
     */
    private void getDeviceList() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", ConfigUtils.user_id);
        map.put("family_id", family_id);
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
                            if (deviceListVo.getData() != null && deviceListVo.getData().size() > 0) {
                                list.addAll(deviceListVo.getData());
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
                        Logger.e(e.getMessage());
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
                if (CommonUtils.isEmptyString(tvName.getText().toString())) {
                    ToastUtil.showToast(getString(R.string.common_room_name_not_empty));
                } else {
                    addRoom();
                }
                break;
            case R.id.rl_room_name:
                //房间名字
                showPopupwindowName();
                break;
            case R.id.rl_room_pic:
                //更换图标
                isUpdate = true;
                startActivity(new Intent(this, RoomPicListActivity.class));
                break;
        }
    }

    /**
     * 保存房间
     */
    private void addRoom() {
        RetrofitService.qdoApi.addRoom(tvName.getText().toString(), pic, family_id)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .filter(messageVo -> messageVo.getCode() == 200)
                .observeOn(Schedulers.io())
                .concatMap((Function<AddRomeVo, ObservableSource<MessageVo>>) messageVo -> {
                    Gson gson = new Gson();
                    List<String> list = new ArrayList<>();
                    for (DeviceListVo.DataBean dataBean : getSeleteList()) {
                        list.add(dataBean.getDevice_id());
                    }
                    UpdateDeviceRoomVo updateDeviceRoomVo = new UpdateDeviceRoomVo();
                    updateDeviceRoomVo.setRoom_id(messageVo.getRoom_id());
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
                        if (messageVo.getCode() == 200) {
                            ToastUtil.showToast(getString(R.string.common_room_save_success));
                            finish();
                        } else {
                            ToastUtil.showToast(getString(R.string.common_room_save_success_and_device_move_fail));
                            finish();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e(e.getMessage());
                        ToastUtil.showToast(getString(R.string.common_server_error));
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 显示退出的popupwindow
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
     * 显示房间名字的popupwindow
     */
    private void showPopupwindowName() {
        new MaterialDialog.Builder(this)
                .content(R.string.room_add_details_set_room_name)
                .negativeText(R.string.common_cancel)
                .positiveText(R.string.common_confirm)
                .inputRange(1, 20, getResources().getColor(R.color.red))
                .input(tvName.getText().toString(), null, false, (dialog, input) -> {

                })
                .onNegative((dialog, which) -> CommonUtils.closeSoftKeyboard(RoomAddDetailsActivity.this))
                .onPositive((dialog, which) -> {
                    tvName.setText(Objects.requireNonNull(dialog.getInputEditText()).getText().toString());
                    isUpdate = true;
                    tvSave.setTextColor(getResources().getColor(R.color.white));
                    tvSave.setBackground(getResources().getDrawable(R.drawable.seletor_circle_green_gray_five));
                    CommonUtils.closeSoftKeyboard(RoomAddDetailsActivity.this);
                })
                .show();
    }

    /**
     * 获取所选中的所有条目
     */
    private List<DeviceListVo.DataBean> getSeleteList() {
        List<DeviceListVo.DataBean> roomDeviceList = new ArrayList<>();
        for (int i = 0; i < adapter.getData().size(); i++) {
            if (adapter.getData().get(i).isIs_selete()) {
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
            ToastUtil.showToast(getString(R.string.common_save_success));
            finish();
        }
    }

}
