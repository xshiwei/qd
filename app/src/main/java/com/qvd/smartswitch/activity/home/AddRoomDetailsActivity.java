package com.qvd.smartswitch.activity.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.adapter.AddRoomDeviceListAdapter;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.home.DeviceListVo;
import com.qvd.smartswitch.model.login.MessageVo;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.ConfigUtils;
import com.qvd.smartswitch.utils.SnackbarUtils;
import com.qvd.smartswitch.utils.ToastUtil;
import com.qvd.smartswitch.widget.EmptyLayout;
import com.qvd.smartswitch.widget.MyPopupWindowOne;
import com.qvd.smartswitch.widget.MyPopupWindowThree;
import com.qvd.smartswitch.widget.MyPopupWindowTwo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class AddRoomDetailsActivity extends BaseActivity {
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
    @BindView(R.id.emptylayout)
    EmptyLayout emptylayout;

    private List<DeviceListVo.DataBean> list = new ArrayList<>();
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
     * 更换名字
     */
    private MyPopupWindowThree popupWindowName;
    /**
     * 名字
     */
    private String name;
    /**
     * 图标
     */
    private String pic;
    /**
     * 取消
     */
    private MyPopupWindowTwo popupwindowDelete;
    /**
     * 确定移动设备
     */
    private MyPopupWindowOne popupWindowConfirm;
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
        name = getIntent().getStringExtra("name");
        pic = getIntent().getStringExtra("pic");
        family_id = getIntent().getStringExtra("family_id");
        tvHomeName.setText(ConfigUtils.family_locate.getFamily_name());
        Picasso.with(this).load(pic).into(ivPic);
        getDeviceList();
        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new AddRoomDeviceListAdapter(this, list);
        recyclerview.setAdapter(adapter);
        adapter.setOnItemClickListener((pos, myLiveList) -> {
            DeviceListVo.DataBean bean = myLiveList.get(pos);
            boolean selete = bean.isIs_selete();
            if (!selete) {
                showPopupwindowConfirm(bean);
                popupWindowConfirm.showPopupWindow(tvCommonActionbarTitle);
            } else {
                index--;
                bean.setIs_selete(false);
            }
            tvText.setText("已选中" + index + "个设备");
            adapter.notifyDataSetChanged();
            isUpdate = true;
        });
    }

    /**
     * 显示是否将该设备移至当前房间中
     */
    private void showPopupwindowConfirm(DeviceListVo.DataBean dataBean) {
        String title = "该设备已关联到" + dataBean.getRoom_name() + "确定要移动到[" + tvName.getText().toString() + "]吗？";
        popupWindowConfirm = new MyPopupWindowOne(this, title, "取消", "确定", new MyPopupWindowOne.IPopupWindowListener() {
            @Override
            public void cancel() {
                popupWindowConfirm.dismiss();
            }

            @Override
            public void confirm() {
                index++;
                dataBean.setIs_selete(true);
                popupWindowConfirm.dismiss();
            }
        });
    }

    /**
     * 获取设备列表
     */
    public void getDeviceList() {
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
                            if (deviceListVo.getData() != null) {
                                for (DeviceListVo.DataBean dataBean : deviceListVo.getData()) {
                                    list.add(dataBean);
                                }
                                adapter.notifyDataSetChanged();
                            }
                        } else if (deviceListVo.getCode() == 400) {
                            ToastUtil.showToast("连接失败");
                        } else {
                            ToastUtil.showToast("连接超时");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showToast("网络连接错误");
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
                    popupwindowDelete.showPopupWindow(view);
                } else {
                    finish();
                }
                break;
            case R.id.tv_save:
                //保存
                addRoom();
                break;
            case R.id.rl_room_name:
                //房间名字
                showPopupwindowName();
                popupWindowName.showPopupWindow(view);
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
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MessageVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }
                    @Override
                    public void onNext(MessageVo messageVo) {
                        if (messageVo.getCode() == 200) {
                            ToastUtil.showToast("添加成功");
                            finish();
                        } else if (messageVo.getCode() == 400) {
                            ToastUtil.showToast("添加失败");
                        } else {
                            ToastUtil.showToast("添加超时");
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
     * 显示删除家庭的popupwindow
     */
    private void showPopupwindowDelete() {
        popupwindowDelete = new MyPopupWindowTwo(this, "房间编辑信息未保存", "房间被编辑还未保存，请确认是否要保存编辑信息", "取消", "确定", new MyPopupWindowTwo.IPopupWindowListener() {
            @Override
            public void cancel() {
                popupwindowDelete.dismiss();
                finish();
            }

            @Override
            public void confirm() {
                popupwindowDelete.dismiss();
                SnackbarUtils.Short(tvCommonActionbarTitle, "保存成功").show();
                finish();
            }
        });
    }

    /**
     * 显示房间名字的popupwindow
     */
    private void showPopupwindowName() {
        popupWindowName = new MyPopupWindowThree(this, "设置房间名称", tvName.getText().toString(), new MyPopupWindowThree.IPopupWindowListener() {
            @Override
            public void cancel() {
                popupWindowName.dismiss();
                CommonUtils.closeSoftKeyboard(AddRoomDetailsActivity.this);
            }

            @Override
            public void confirm() {
                EditText etEditText = popupWindowName.getEtEditText();
                isUpdate = true;
                tvName.setText(etEditText.getText().toString());
                popupWindowName.dismiss();
                CommonUtils.closeSoftKeyboard(AddRoomDetailsActivity.this);
            }
        });
        final EditText etEditText = popupWindowName.getEtEditText();
        final TextView tvConfirm = popupWindowName.getTvConfirm();
        final TextView tvError = popupWindowName.getTvError();

        if (etEditText.getText().toString().equals(name)) {
            tvConfirm.setEnabled(false);
            tvConfirm.setTextColor(getResources().getColor(R.color.home_setting_text_three));
        }
        etEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals(name)) {
                    tvError.setVisibility(View.VISIBLE);
                    tvError.setText("不能和之前名字一致");
                    tvConfirm.setEnabled(false);
                    tvConfirm.setTextColor(getResources().getColor(R.color.home_setting_text_three));
                } else if (s.toString().length() > 10) {
                    tvError.setVisibility(View.VISIBLE);
                    tvError.setText("长度超过最大");
                    tvConfirm.setEnabled(false);
                    tvConfirm.setTextColor(getResources().getColor(R.color.home_setting_text_three));
                } else if (s.toString().length() == 0) {
                    tvConfirm.setEnabled(false);
                    tvConfirm.setTextColor(getResources().getColor(R.color.home_setting_text_three));
                    etEditText.setCursorVisible(false);
                } else {
                    tvError.setVisibility(View.GONE);
                    tvConfirm.setEnabled(true);
                    tvConfirm.setTextColor(getResources().getColor(R.color.popupwindow_confirm_text));
                }
            }
        });
    }

    /**
     * 获取所选中的所有条目
     */
    private List<DeviceListVo.DataBean> getSeleteList() {
        List<DeviceListVo.DataBean> roomDeviceList = new ArrayList<>();
        for (int i = 0; i < adapter.getAllList().size(); i++) {
            if (adapter.getAllList().get(i).isIs_selete()) {
                roomDeviceList.add(adapter.getAllList().get(i));
            }
        }
        return roomDeviceList;
    }

    @Override
    public void onBackPressed() {
        //取消
        if (isUpdate) {
            showPopupwindowDelete();
            popupwindowDelete.showPopupWindow(recyclerview);
        } else {
            SnackbarUtils.Short(tvSave, "保存成功").show();
            finish();
        }
    }

}
