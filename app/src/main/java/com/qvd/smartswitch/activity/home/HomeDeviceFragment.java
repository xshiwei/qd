/*
 * Copyright (C) 2017
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.qvd.smartswitch.activity.home;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.TextOutsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.orhanobut.logger.Logger;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.device.DeviceLogActivity;
import com.qvd.smartswitch.activity.device.DeviceSettingActivity;
import com.qvd.smartswitch.activity.device.DeviceSoundControlActivity;
import com.qvd.smartswitch.activity.qsThree.QsThreeControlActivity;
import com.qvd.smartswitch.activity.qsTwo.QsTwoControlActivity;
import com.qvd.smartswitch.activity.qsTwo.QsTwoTimingActivity;
import com.qvd.smartswitch.adapter.HomeDeviceListAdapter;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.device.RoomDeviceListVo;
import com.qvd.smartswitch.model.device.ScanResultVo;
import com.qvd.smartswitch.model.device.UpdateDeviceRoomVo;
import com.qvd.smartswitch.model.home.HomeLeftListVo;
import com.qvd.smartswitch.model.home.RoomListVo;
import com.qvd.smartswitch.model.home.Test2Vo;
import com.qvd.smartswitch.model.login.MessageVo;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.ConfigUtils;
import com.qvd.smartswitch.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class HomeDeviceFragment extends Fragment {
    private RecyclerView recycleView;
    private RelativeLayout rl_empty;
    private View view;
    private TextView layout;
    private HomeDeviceListAdapter adapter;
    private NestedScrollView scrollview;
    private List<RoomDeviceListVo.DataBean> list = new ArrayList<>();
    private BoomMenuButton bmb;

    public HomeLeftListVo.DataBean dataBean;

    /**
     * 当前被选中的设备
     */
    private RoomDeviceListVo.DataBean seleteDevice;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_page, container, false);
        recycleView = view.findViewById(R.id.my_recycler_view);
        rl_empty = view.findViewById(R.id.rl_empty);
        layout = view.findViewById(R.id.textViewMessage);
        scrollview = view.findViewById(R.id.scrollview);
        bmb = view.findViewById(R.id.bmb);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBean = (HomeLeftListVo.DataBean) getArguments().getSerializable("agrs");
    }

    /**
     * 显示长按列表展示的dialog（设置常用）
     */
    private void showDialog() {
        new MaterialDialog.Builder(getActivity())
                .title("功能选择")
                .items(R.array.home_device_dialog)
                .itemsColor(getResources().getColor(R.color.app_red_color))
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                        switch (position) {
                            case 0:
                                showMoveRoomDialog();
                                break;
                            case 1:
                                showRetryNameDialog();
                                break;
                            case 2:
                                showDeleteDialog();
                                break;
                            case 3:
                                showCommonDevice(1);
                                break;
                        }
                    }
                })
                .negativeText("取消")
                .show();
    }


    /**
     * 设置和取消常用设备
     */
    private void showCommonDevice(int isCommon) {
        RetrofitService.qdoApi.setCommonDevice(seleteDevice.getDevice_id(), isCommon)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MessageVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MessageVo messageVo) {
                        if (messageVo.getCode() == 200) {
                            ToastUtil.showToast("设置成功");
                            setData();
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
     * 显示长按列表展示的dialog（取消常用）
     */
    private void showDialogTwo() {
        new MaterialDialog.Builder(getActivity())
                .title("功能选择")
                .items(R.array.home_device_dialog_two)
                .itemsColor(getResources().getColor(R.color.app_red_color))
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                        switch (position) {
                            case 0:
                                showMoveRoomDialog();
                                break;
                            case 1:
                                showRetryNameDialog();
                                break;
                            case 2:
                                showDeleteDialog();
                                break;
                            case 3:
                                showCommonDevice(0);
                                break;
                        }
                    }
                })
                .negativeText("取消")
                .show();
    }


    /**
     * 显示删除设备的dialog
     */
    private void showDeleteDialog() {
        new MaterialDialog.Builder(getActivity())
                .title("您确定要删除该设备吗")
                .negativeText("取消")
                .positiveText("确定")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        RetrofitService.qdoApi.deleteDevice(seleteDevice.getDevice_id())
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
                                            setData();
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
                })
                .show();
    }

    /**
     * 显示重命名的dialog
     */
    private void showRetryNameDialog() {
        new MaterialDialog.Builder(getActivity())
                .title("设置设备名称")
                .inputRange(1, 20, getResources().getColor(R.color.red))
                .input(seleteDevice.getDevice_name(), null, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                    }
                })
                .negativeText("取消")
                .positiveText("确定")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        CommonUtils.closeSoftKeyboard(getActivity());
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        //修改设备名称
                        EditText inputEditText = dialog.getInputEditText();
                        RetrofitService.qdoApi.updateSpecificDeviceName(seleteDevice.getDevice_id(), inputEditText.getText().toString(), seleteDevice.getTable_type())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<MessageVo>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onNext(MessageVo messageVo) {
                                        if (messageVo.getCode() == 200) {
                                            ToastUtil.showToast("修改成功");
                                            setData();
                                        } else {
                                            ToastUtil.showToast("网络失败");
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        ToastUtil.showToast("网络失败");
                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                });
                    }
                })
                .show();
    }

    /**
     * 显示移动设备到别的房间的dialog
     */
    private void showMoveRoomDialog() {
        RetrofitService.qdoApi.getRoomList(ConfigUtils.family_locate.getFamily_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RoomListVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(RoomListVo roomListVo) {
                        if (roomListVo.getCode() == 200) {
                            if (roomListVo.getData() != null && roomListVo.getData().size() > 0) {
                                List<String> strList = new ArrayList<>();
                                for (RoomListVo.DataBean dataBean : roomListVo.getData()) {
                                    strList.add(dataBean.getRoom_name());
                                }
                                new MaterialDialog.Builder(getActivity())
                                        .title("移动设备")
                                        .items(strList)
                                        .itemsColor(getResources().getColor(R.color.app_red_color))
                                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                                            @Override
                                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                                return true;
                                            }
                                        })
                                        .positiveText("确定")
                                        .negativeText("取消")
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                int selectedIndex = dialog.getSelectedIndex();
                                                Gson gson = new Gson();
                                                List<String> list = new ArrayList<>();
                                                list.add(seleteDevice.getDevice_id());
                                                UpdateDeviceRoomVo updateDeviceRoomVo = new UpdateDeviceRoomVo();
                                                updateDeviceRoomVo.setRoom_id(roomListVo.getData().get(selectedIndex).getRoom_id());
                                                updateDeviceRoomVo.setDevice_id(list);
                                                String body = gson.toJson(updateDeviceRoomVo);
                                                RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type: application/json"), body);
                                                RetrofitService.qdoApi.updateDeviceRoom(requestBody)
                                                        .subscribeOn(Schedulers.io())
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .subscribe(new Observer<MessageVo>() {
                                                            @Override
                                                            public void onSubscribe(Disposable d) {

                                                            }

                                                            @Override
                                                            public void onNext(MessageVo messageVo) {
                                                                if (messageVo.getCode() == 200) {
                                                                    ToastUtil.showToast("移动成功");
                                                                    setData();
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
                                        })
                                        .show();
                            }
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


    private void setData() {
        switch (dataBean.getType()) {
            case 1:
                //常用
                getCommonDevice();
                break;
            case 2:
                //房间
                getRoomListDevice(dataBean.getRoom_id());
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setData();
    }

    public static HomeDeviceFragment newInstance(HomeLeftListVo.DataBean dataBean) {
        HomeDeviceFragment fragment = new HomeDeviceFragment();
        Bundle args = new Bundle();
        args.putSerializable("agrs", dataBean);
        fragment.setArguments(args);
        return fragment;
    }


    /**
     * 获取房间设备
     */
    private void getRoomListDevice(String roomid) {
        scrollview.setVisibility(View.GONE);
        recycleView.setVisibility(View.VISIBLE);
        list.clear();
        RetrofitService.qdoApi.getRoomDeviceList(ConfigUtils.user_id, roomid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RoomDeviceListVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(RoomDeviceListVo roomDeviceListVo) {
                        if (roomDeviceListVo != null) {
                            if (roomDeviceListVo.getCode() == 200) {
                                if (roomDeviceListVo.getData() != null && roomDeviceListVo.getData().size() > 0) {
                                    //获取右侧家庭设备数据
                                    list.addAll(roomDeviceListVo.getData());
                                    adapter = new HomeDeviceListAdapter(getActivity(), list);
                                    recycleView.setLayoutManager(new GridLayoutManager(getActivity(), 3, LinearLayoutManager.VERTICAL, false));
                                    recycleView.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                    setOnItemClick();
                                } else {
                                    scrollview.setVisibility(View.VISIBLE);
                                    recycleView.setVisibility(View.GONE);
                                }
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
     * 获取常用设备
     */
    private void getCommonDevice() {
        scrollview.setVisibility(View.GONE);
        recycleView.setVisibility(View.VISIBLE);
        list.clear();
        RetrofitService.qdoApi.getCommonDeviceList(ConfigUtils.user_id, ConfigUtils.family_locate.getFamily_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RoomDeviceListVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(RoomDeviceListVo commonDeviceListVo) {
                        if (commonDeviceListVo != null) {
                            if (commonDeviceListVo.getCode() == 200) {
                                if (commonDeviceListVo.getData() != null && commonDeviceListVo.getData().size() > 0) {
                                    //获取右侧家庭设备数据
                                    list.addAll(commonDeviceListVo.getData());
                                    adapter = new HomeDeviceListAdapter(getActivity(), list);
                                    recycleView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                                    recycleView.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                    setOnItemClick();
                                } else {
                                    scrollview.setVisibility(View.VISIBLE);
                                    recycleView.setVisibility(View.GONE);
                                }
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

    private void setOnItemClick() {
        //家庭内容区域设备点击事件
        adapter.setOnItemClickListener(new HomeDeviceListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                RoomDeviceListVo.DataBean dataBean = list.get(position);
                switch (dataBean.getDevice_no()) {
                    case "qs02":
                        startActivity(new Intent(getActivity(), QsTwoControlActivity.class)
                                .putExtra("scanResult", new ScanResultVo(dataBean.getDevice_no(),
                                        CommonUtils.getDeviceName(dataBean.getDevice_no()), dataBean.getDevice_mac(),
                                        dataBean.getConnect_type()))
                                .putExtra("isFirstConnect", dataBean.getIs_first_connect())
                                .putExtra("deviceId", dataBean.getDevice_id()));
                        getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                        break;
                    case "qs03":
                        startActivity(new Intent(getActivity(), QsThreeControlActivity.class)
                                .putExtra("isFirstConnect", 1)
                                .putExtra("deviceId", dataBean.getDevice_id()));
                        getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                        break;
                }
            }

            @Override
            public void onItemLongClickListener(View view, int position) {
                seleteDevice = list.get(position);
                if (seleteDevice.getIs_common() == 1) {
                    showDialogTwo();
                } else {
                    showDialog();
                }
            }

            @Override
            public void onToggleButtonClickListener(boolean state, int position) {

            }
        });
    }
}
