package com.qvd.smartswitch.activity.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.google.gson.Gson;
import com.isupatches.wisefy.WiseFy;
import com.isupatches.wisefy.callbacks.GetNearbyAccessPointsCallbacks;
import com.isupatches.wisefy.callbacks.SearchForAccessPointsCallbacks;
import com.isupatches.wisefy.callbacks.SearchForSSIDsCallbacks;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.activity.wifi.DeviceWifiSettingActivity;
import com.qvd.smartswitch.adapter.AddDeviceSortAdapter;
import com.qvd.smartswitch.model.device.ScanResultVo;
import com.qvd.smartswitch.model.home.SortBean;
import com.qvd.smartswitch.utils.BleManageUtils;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.PermissionUtils;
import com.qvd.smartswitch.utils.ToastUtil;
import com.qvd.smartswitch.widget.CheckListener;
import com.qvd.smartswitch.widget.ItemHeaderDecoration;
import com.qvd.smartswitch.widget.MyPopupWindowOne;
import com.qvd.smartswitch.widget.RandomTextView;
import com.yanzhenjie.permission.Permission;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/6/11 0011.
 */

public class AddDeviceActivity extends BaseActivity implements CheckListener {
    @BindView(R.id.iv_add_device_back)
    ImageView ivAddDeviceBack;
    @BindView(R.id.tv_add_device_nearby)
    TextView tvAddDeviceNearby;
    @BindView(R.id.tv_add_device_manual)
    TextView tvAddDeviceManual;
    @BindView(R.id.iv_add_device_search)
    ImageView ivAddDeviceSearch;
    @BindView(R.id.rv_sort)
    RecyclerView rvSort;
    @BindView(R.id.lin_fragment)
    FrameLayout linFragment;
    @BindView(R.id.ll_add_device_list)
    LinearLayout llAddDeviceList;
    @BindView(R.id.rl_add_device_nearby)
    RelativeLayout rlAddDeviceNearby;

    /**
     * 列表适配器
     */
    private AddDeviceSortAdapter mAddDeviceSortAdapter;
    /**
     * 右侧的Fragment
     */
    private SortDetailFragment mSortDetailFragment;
    private LinearLayoutManager mLinearLayoutManager;
    /**
     * 点击左边某一个具体的item的位置
     */
    private int targetPosition;
    /**
     * 判断是否移出
     */
    private boolean isMoved;
    /**
     * 后台数据实体类
     */
    private SortBean mSortBean;

    /**
     * wifi控制实例
     */
    private WiseFy wiseFy;

    private MyPopupWindowOne popupWindowOpenWifi;

    private RandomTextView randomTextview;

    private static final int REQUEST_CODE_OPEN_GPS = 1;

    public final int REQUEST_ENABLE_BT = 110;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    randomTextview.show();
                    break;
            }
        }
    };

    @Override
    protected int setLayoutId() {
        return R.layout.activity_add_device;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }

    @Override
    protected void initData() {
        super.initData();
        randomTextview = findViewById(R.id.random_textview);
        wiseFy = new WiseFy.brains(this).logging(true).getSmarts();
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //判断是否支持蓝牙功能
        if (bluetoothAdapter == null) {
            ToastUtil.showToast("您的手机不支持蓝牙功能");
            return;
        }
        /**
         * 去打开蓝牙，否则直接扫描设备
         */
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        initWifi();
        PermissionUtils.requestPermission(this, Permission.Group.LOCATION);
        initNearby();
        initManual();
    }

    /**
     * 附近设备添加
     */
    private void initNearby() {
        //初始化设备管理器
        BleManageUtils.getInstance().initBleManage();
        randomTextview.setOnRippleViewClickListener(position -> {
            //搜索的设备跳转到连接页面
            ScanResultVo resultVo = randomTextview.getKeyWords().get(position);
            switch (resultVo.getDeviceNo()) {
                case "qevdo_qs03":
                    startActivity(new Intent(AddDeviceActivity.this, DeviceConnectActivity.class)
                            .putExtra("ScanResultVo", resultVo));
                    break;
                case "QS02":
                    startActivity(new Intent(AddDeviceActivity.this, DeviceWifiSettingActivity.class));
                    break;
            }
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        });
    }

    /**
     * 开始获得扫描设备
     */
    private void getNearByData() {
        checkPermission();
        startScanWifi();
    }

    /**
     * 初始化WiFi
     */
    private void initWifi() {
        //检查当前设备是否开启wifi
        if (!wiseFy.isWifiEnabled()) {
            showOpenWifiPopupWindow();
            popupWindowOpenWifi.showPopupWindow(tvAddDeviceManual);
        }
    }


    /**
     * 开始扫描wifi
     */
    private void startScanWifi() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            PermissionUtils.requestPermission(this, Permission.Group.LOCATION);
        }
        wiseFy.getNearbyAccessPoints(false, new GetNearbyAccessPointsCallbacks() {
            @Override
            public void getNearbyAccessPointsWiseFyFailure(int i) {

            }

            @Override
            public void retrievedNearbyAccessPoints(List<ScanResult> list) {
                for (ScanResult result : list) {
                    switch (result.SSID) {
                        case "qevdo_qs03":
                            ScanResultVo resultVo = new ScanResultVo(result.SSID, CommonUtils.getDeviceName(result.SSID), result.BSSID, 2);
                            randomTextview.addKeyWord(resultVo);
                            handler.sendEmptyMessage(1);
                            break;
                    }
                }
            }
        });
    }

    /**
     * 展示打开Wifi的popouwindow
     */
    private void showOpenWifiPopupWindow() {
        popupWindowOpenWifi = new MyPopupWindowOne(this, "您当前Wi-Fi网络未开启，不能扫描连接Wi_Fi设备,点击确定开启Wi-Fi。", "取消", "确定", new MyPopupWindowOne.IPopupWindowListener() {
            @Override
            public void cancel() {
                popupWindowOpenWifi.dismiss();
            }

            @Override
            public void confirm() {
                popupWindowOpenWifi.dismiss();
                wiseFy.enableWifi();
            }
        });
    }

    /**
     * 检查扫描需要的权限
     */
    private void checkPermission() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //判断是否支持蓝牙功能
        if (bluetoothAdapter == null) {
            ToastUtil.showToast("您的手机不支持蓝牙功能");
            return;
        }
        /**
         * 去打开蓝牙，否则直接扫描设备
         */
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            BleManageUtils.getInstance().setScanRule();
            startScan();
        }
    }

    /**
     * 开始扫描
     */
    private void startScan() {
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {

            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                ScanResultVo resultVo = new ScanResultVo(bleDevice.getName(), CommonUtils.getDeviceName(bleDevice.getName()), bleDevice.getMac(), 1);
                randomTextview.addKeyWord(resultVo);
                handler.sendEmptyMessage(1);
            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_OPEN_GPS) {
            if (BleManageUtils.getInstance().checkGPSIsOpen(this)) {
                BleManageUtils.getInstance().setScanRule();
                startScan();
            }
        }
    }

    /**
     * 手动添加
     */
    private void initManual() {
        mLinearLayoutManager = new LinearLayoutManager(this);
        rvSort.setLayoutManager(mLinearLayoutManager);

        //获取asset目录下的资源文件
        String assetsData = getAssetsData("sort.json");
        Gson gson = new Gson();
        mSortBean = gson.fromJson(assetsData, SortBean.class);
        List<SortBean.CategoryOneArrayBean> categoryOneArray = mSortBean.getCategoryOneArray();
        List<String> list = new ArrayList<>();
        //初始化左侧列表数据
        for (int i = 0; i < categoryOneArray.size(); i++) {
            list.add(categoryOneArray.get(i).getName());
        }
        mAddDeviceSortAdapter = new AddDeviceSortAdapter(this, list, (id, position) -> {
            if (mSortDetailFragment != null) {
                isMoved = true;
                targetPosition = position;
                setChecked(position, true);
            }
        });
        rvSort.setAdapter(mAddDeviceSortAdapter);
        createFragment();
    }

    /**
     * 从资源文件中获取分类json
     *
     * @param path
     * @return
     */
    private String getAssetsData(String path) {
        String result = "";
        try {
            //获取输入流
            InputStream mAssets = getAssets().open(path);
            //获取文件的字节数
            int lenght = mAssets.available();
            //创建byte数组
            byte[] buffer = new byte[lenght];
            //将文件中的数据写入到字节数组中
            mAssets.read(buffer);
            mAssets.close();
            result = new String(buffer);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("fuck", e.getMessage());
            return result;
        }
    }

    /**
     * 创建Fragment
     */
    public void createFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        mSortDetailFragment = new SortDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("right", mSortBean.getCategoryOneArray());
        mSortDetailFragment.setArguments(bundle);
        mSortDetailFragment.setListener(this);
        fragmentTransaction.add(R.id.lin_fragment, mSortDetailFragment);
        fragmentTransaction.commit();
    }

    /**
     * 设置哪个分类被点击
     *
     * @param position
     * @param isLeft
     */
    private void setChecked(int position, boolean isLeft) {
        if (isLeft) {
            mAddDeviceSortAdapter.setCheckedPosition(position);
            //此处的位置需要根据每个分类的集合来进行计算
            int count = 0;
            for (int i = 0; i < position; i++) {
                count += mSortBean.getCategoryOneArray().get(i).getCategoryTwoArray().size();
            }
            count += position;
            mSortDetailFragment.setData(count);
            ItemHeaderDecoration.setCurrentTag(String.valueOf(targetPosition));//凡是点击左边，将左边点击的位置作为当前的tag
        } else {
            if (isMoved) {
                isMoved = false;
            } else
                mAddDeviceSortAdapter.setCheckedPosition(position);
            ItemHeaderDecoration.setCurrentTag(String.valueOf(position));//如果是滑动右边联动左边，则按照右边传过来的位置作为tag
        }
        moveToCenter(position);
    }

    //将当前选中的item居中
    private void moveToCenter(int position) {
        //将点击的position转换为当前屏幕上可见的item的位置以便于计算距离顶部的高度，从而进行移动居中
        View childAt = rvSort.getChildAt(position - mLinearLayoutManager.findFirstVisibleItemPosition());
        if (childAt != null) {
            int y = (childAt.getTop() - rvSort.getHeight() / 2);
            rvSort.smoothScrollBy(0, y);
        }
    }


    @OnClick({R.id.iv_add_device_back, R.id.tv_add_device_nearby, R.id.tv_add_device_manual, R.id.iv_add_device_search, R.id.rl_add_device_nearby})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_add_device_back:
                //返回
                finish();
                break;
            case R.id.tv_add_device_nearby:
                //附近设备
                rlAddDeviceNearby.setVisibility(View.VISIBLE);
                llAddDeviceList.setVisibility(View.GONE);
                tvAddDeviceNearby.setTextColor(getResources().getColor(R.color.add_device_selete));
                tvAddDeviceManual.setTextColor(getResources().getColor(R.color.add_device_title));
                getNearByData();
                break;
            case R.id.tv_add_device_manual:
                //手动添加
                BleManager.getInstance().cancelScan();
                rlAddDeviceNearby.setVisibility(View.GONE);
                llAddDeviceList.setVisibility(View.VISIBLE);
                tvAddDeviceManual.setTextColor(getResources().getColor(R.color.add_device_selete));
                tvAddDeviceNearby.setTextColor(getResources().getColor(R.color.add_device_title));
                break;
            case R.id.iv_add_device_search:
                //搜索
                break;
        }
    }


    @Override
    public void check(int position, boolean isScroll) {
        setChecked(position, isScroll);
    }

}
