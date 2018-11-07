package com.qvd.smartswitch.activity.device;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseFragment;
import com.qvd.smartswitch.activity.device.Ble.DeviceSearchBleActivity;
import com.qvd.smartswitch.activity.device.wifi.ConfirmLightFlickerActivity;
import com.qvd.smartswitch.activity.user.UserFragment;
import com.qvd.smartswitch.adapter.ClassifyDetailAdapter;
import com.qvd.smartswitch.model.device.AddDeviceListVo;
import com.qvd.smartswitch.model.home.RightBean;
import com.qvd.smartswitch.widget.CheckListener;
import com.qvd.smartswitch.widget.ItemHeaderDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;


public class DeviceSortDetailFragment extends BaseFragment implements CheckListener {
    @BindView(R.id.rv)
    RecyclerView mRv;
    private GridLayoutManager mManager;
    private final List<RightBean> mDatas = new ArrayList<>();
    private boolean move = false;
    private int mIndex = 0;
    private CheckListener checkListener;

    public static DeviceSortDetailFragment newInstance(String param1) {
        DeviceSortDetailFragment fragment = new DeviceSortDetailFragment();
        Bundle args = new Bundle();
        args.putString("agrs1", param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_sort_detail;
    }

    @Override
    protected void initData() {
        mRv.addOnScrollListener(new RecyclerViewListener());
        mManager = new GridLayoutManager(getActivity(), 3);
        //通过isTitle的标志来判断是否是title
        mManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return mDatas.get(position).isTitle() ? 3 : 1;
            }
        });
        mRv.setLayoutManager(mManager);
        //当前点击事件
        ClassifyDetailAdapter mAdapter = new ClassifyDetailAdapter(getActivity(), mDatas, (id, position) -> {
            switch (id) {
                case R.id.root:
                    break;
                case R.id.content:
                    String deviceNo = mDatas.get(position).getDeviceNo();
                    switchToActivity(deviceNo, mDatas.get(position).getConnectType());
                    break;
            }

        });

        mRv.setAdapter(mAdapter);
        ItemHeaderDecoration mDecoration = new ItemHeaderDecoration(Objects.requireNonNull(getActivity()), mDatas);
        mRv.addItemDecoration(mDecoration);
        mDecoration.setCheckListener(checkListener);

        AddDeviceListVo addDeviceListVo = (AddDeviceListVo) Objects.requireNonNull(getArguments()).getSerializable("right");
        List<AddDeviceListVo.DataBean> rightList = Objects.requireNonNull(addDeviceListVo).getData();
        for (int i = 0; i < rightList.size(); i++) {
            RightBean head = new RightBean(rightList.get(i).getDevice_type());
            //头部设置为true
            head.setTitle(true);
            head.setTitleName(rightList.get(i).getDevice_type());
            head.setTag(String.valueOf(i));
            mDatas.add(head);
            List<AddDeviceListVo.DataBean.DeviceDetailListBean> categoryTwoArray = rightList.get(i).getDevice_detail_list();
            for (int j = 0; j < categoryTwoArray.size(); j++) {
                RightBean body = new RightBean(categoryTwoArray.get(j).getDevice_name());
                body.setTag(String.valueOf(i));
                String name = rightList.get(i).getDevice_type();
                body.setTitleName(name);
                body.setDeviceNo(categoryTwoArray.get(j).getDevice_no());
                body.setConnectType(categoryTwoArray.get(j).getConnect_type());
                body.setImgsrc(categoryTwoArray.get(j).getDevice_pic());
                mDatas.add(body);
            }
        }
        mAdapter.notifyDataSetChanged();
        mDecoration.setData(mDatas);
    }

    /**
     * 根据选择的产品跳转到对应的activity
     */
    private void switchToActivity(String deviceNo, int type) {
        switch (type) {
            case 1:
                startActivity(new Intent(getActivity(), DeviceSearchBleActivity.class)
                        .putExtra("deviceNo", deviceNo));
                Objects.requireNonNull(getActivity()).overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case 2:
                startActivity(new Intent(getActivity(), ConfirmLightFlickerActivity.class)
                        .putExtra("wifi_ssid", deviceNo));
                Objects.requireNonNull(getActivity()).overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
        }
    }

    public void setData(int n) {
        mIndex = n;
        mRv.stopScroll();
        smoothMoveToPosition(n);
    }

    public void setListener(CheckListener listener) {
        this.checkListener = listener;
    }


    private void smoothMoveToPosition(int n) {
        int firstItem = mManager.findFirstVisibleItemPosition();
        int lastItem = mManager.findLastVisibleItemPosition();
        Log.d("first--->", String.valueOf(firstItem));
        Log.d("last--->", String.valueOf(lastItem));
        if (n <= firstItem) {
            mRv.scrollToPosition(n);
        } else if (n <= lastItem) {
            Log.d("pos---->", String.valueOf(n) + "VS" + firstItem);
            int top = mRv.getChildAt(n - firstItem).getTop();
            Log.d("top---->", String.valueOf(top));
            mRv.scrollBy(0, top);
        } else {
            mRv.scrollToPosition(n);
            move = true;
        }
    }


    @Override
    public void check(int position, boolean isScroll) {
        checkListener.check(position, isScroll);
    }


    private class RecyclerViewListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (move && newState == RecyclerView.SCROLL_STATE_IDLE) {
                move = false;
                int n = mIndex - mManager.findFirstVisibleItemPosition();
                Log.d("n---->", String.valueOf(n));
                if (0 <= n && n < mRv.getChildCount()) {
                    int top = mRv.getChildAt(n).getTop();
                    Log.d("top--->", String.valueOf(top));
                    mRv.smoothScrollBy(0, top);
                }
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (move) {
                move = false;
                int n = mIndex - mManager.findFirstVisibleItemPosition();
                if (0 <= n && n < mRv.getChildCount()) {
                    int top = mRv.getChildAt(n).getTop();
                    mRv.scrollBy(0, top);
                }
            }
        }
    }


}
