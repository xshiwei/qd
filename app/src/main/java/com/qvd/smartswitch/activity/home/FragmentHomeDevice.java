package com.qvd.smartswitch.activity.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.orhanobut.logger.Logger;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.device.DeviceControlTwoActivity;
import com.qvd.smartswitch.activity.wifi.DeviceWifiControlActivity;
import com.qvd.smartswitch.adapter.HomeContentAdapter;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.device.RoomDeviceListVo;
import com.qvd.smartswitch.model.device.ScanResultVo;
import com.qvd.smartswitch.model.home.HomeLeftListVo;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.ConfigUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class FragmentHomeDevice extends Fragment {

    @BindView(R.id.rv_content)
    RecyclerView rvContent;
    Unbinder unbinder;

    /**
     * 接收的数据
     */
    private HomeLeftListVo.DataBean dataBean;

    /**
     * 房间点击后设备适配器
     */
    private HomeContentAdapter contentAdapter;

    /**
     * 服务器获取的设备数据源
     */
    private List<RoomDeviceListVo.DataBean> contentList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_device, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int position = FragmentPagerItem.getPosition(getArguments());
        Logger.e("fragment" + position);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * 获取房间设备
     */
    private void getRoomListDevice(String roomid) {
        contentList.clear();
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
                                    contentList.addAll(roomDeviceListVo.getData());
                                    contentAdapter = new HomeContentAdapter(getActivity(), contentList);
                                    rvContent.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                                    rvContent.setAdapter(contentAdapter);
                                    contentAdapter.notifyDataSetChanged();
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
                                    contentList.addAll(commonDeviceListVo.getData());
                                    contentAdapter = new HomeContentAdapter(getActivity(), contentList);
                                    rvContent.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                                    rvContent.setAdapter(contentAdapter);
                                    contentAdapter.notifyDataSetChanged();
                                    //家庭内容区域设备点击事件
                                    contentAdapter.setOnItemClickListener(new HomeContentAdapter.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(View view, int position) {
                                            RoomDeviceListVo.DataBean dataBean = contentList.get(position);
                                            switch (dataBean.getDevice_no()) {
                                                case "qs02":
                                                    startActivity(new Intent(getActivity(), DeviceControlTwoActivity.class)
                                                            .putExtra("scanResult", new ScanResultVo(dataBean.getDevice_no(),
                                                                    CommonUtils.getDeviceName(dataBean.getDevice_no()), dataBean.getDevice_mac(),
                                                                    dataBean.getConnect_type()))
                                                            .putExtra("isFirstConnect", dataBean.getIs_first_connect()));
                                                    getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                                    break;
                                                case "qs03":
                                                    startActivity(new Intent(getActivity(), DeviceWifiControlActivity.class)
                                                            .putExtra("isFirstConnect", 1));
                                                    getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                                    break;
                                            }
                                        }

                                        @Override
                                        public void onItemLongClickListener(View view, int position) {

                                        }
                                    });
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
     * 接收数据
     */
    public void setData(HomeLeftListVo.DataBean dataBean) {
        this.dataBean = dataBean;
    }
}
