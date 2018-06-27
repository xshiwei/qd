package com.qvd.smartswitch.activity.device;


import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.clj.fastble.data.BleDevice;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.adapter.DeviceLogListAdapter;
import com.qvd.smartswitch.db.DeviceDaoOpe;
import com.qvd.smartswitch.model.DeviceLogVo;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.widget.EmptyLayout;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/4/13 0013.
 */

public class DeviceLogActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.tv_device_log_edit)
    TextView tvDeviceLogEdit;
    @BindView(R.id.tv_device_log_bottom_select_num)
    TextView tvDeviceLogBottomSelectNum;
    @BindView(R.id.btn_device_log_bottom_delete)
    Button btnDeviceLogBottomDelete;
    @BindView(R.id.select_device_log_bottom_all)
    Button selectDeviceLogBottomAll;
    @BindView(R.id.ll_device_log_bottom)
    LinearLayout llDeviceLogBottom;

    private EmptyLayout emptyLayout;
    private BleDevice bledevice;
    private List<DeviceLogVo> list = new ArrayList<>();
    private List<DeviceLogVo> addMessageList = new ArrayList<>();
    private DeviceLogListAdapter adapter;
    private int page = 0;
    private int pageNum = 10;
    private int position;

    private static final int MYLIVE_MODE_CHECK = 0;
    private static final int MYLIVE_MODE_EDIT = 1;
    private int mEditMode = MYLIVE_MODE_CHECK;
    private boolean isSelectAll = false;
    private boolean editorStatus = false;
    private int index = 0;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_device_log;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.app_color).init();
    }

    @Override
    protected void initData() {
        super.initData();
        tvCommonActionbarTitle.setText(R.string.device_log_title);
        bledevice = getIntent().getParcelableExtra("bledevice");
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DeviceLogListAdapter(this, list);
        recyclerview.setAdapter(adapter);
        refreshLayout.setHeaderHeight(100);
        refreshLayout.setFooterHeight(100);
        refreshLayout.setEnableLoadmoreWhenContentNotFull(true);//是否在列表不满一页时候开启上拉加载功能
        refreshLayout.setRefreshHeader(new ClassicsHeader(this));
        refreshLayout.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale));
        refreshlayout();
    }

    /**
     * 刷新数据
     */
    private void refreshlayout() {
        //刷新数据
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                //传入true表示刷新成功，false表示刷新失败
                if (list.size() > 0) {
                    list.clear();
                    page = 0;
                    getData();
                }
                refreshlayout.finishRefresh(2000, true);
            }
        });
        //加载数据
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                page++;
                getMoreData();
                refreshlayout.finishLoadmore(2000, true);
            }
        });
    }


    /**
     * 加载更多
     */
    private void getMoreData() {
        List<DeviceLogVo> deviceLogVos = DeviceDaoOpe.queryPaging(page, pageNum, this, CommonUtils.getMac(bledevice.getMac()));
        if (deviceLogVos == null || deviceLogVos.size() <= 0) {
            refreshLayout.finishLoadmoreWithNoMoreData();//完成加载并标记没有更多数据
        } else {
            addMessageList.clear();
            addMessageList.addAll(deviceLogVos);
            position = list.size();
            list.addAll(position, addMessageList);
            adapter.notifyItemInserted(position);
            emptyLayout.hide();
        }
    }

    @Override
    protected void getData() {
        super.getData();
        List<DeviceLogVo> deviceLogVos = DeviceDaoOpe.queryPaging(page, pageNum, this, CommonUtils.getMac(bledevice.getMac()));
        if (deviceLogVos != null && deviceLogVos.size() > 0) {
            list.clear();
            list.addAll(deviceLogVos);
            adapter.notifyAdapter(list, false);
            emptyLayout.hide();
        } else {
            emptyLayout.showEmpty();
        }
    }

    /**
     * 根据选择的数量是否为0来判断按钮的是否可点击.
     *
     * @param size
     */
    private void setBtnBackground(int size) {
        if (size != 0) {
            btnDeviceLogBottomDelete.setBackgroundResource(R.drawable.device_log_button_selete);
            btnDeviceLogBottomDelete.setEnabled(true);
            btnDeviceLogBottomDelete.setTextColor(Color.WHITE);
        } else {
            btnDeviceLogBottomDelete.setBackgroundResource(R.drawable.device_log_button_unselete);
            btnDeviceLogBottomDelete.setEnabled(false);
            btnDeviceLogBottomDelete.setTextColor(ContextCompat.getColor(this, R.color.app_color));
        }
    }


    @Override
    protected void initView() {
        super.initView();
        emptyLayout = findViewById(R.id.empty_view);
        adapter.setOnItemClickListener(new DeviceLogListAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(int pos, List<DeviceLogVo> deviceLogVoList) {
                if (editorStatus) {
                    DeviceLogVo deviceLogVo = deviceLogVoList.get(pos);
                    boolean isSelect = deviceLogVo.isSelete();
                    if (!isSelect) {
                        index++;
                        deviceLogVo.setSelete(true);
                        if (index == deviceLogVoList.size()) {
                            isSelectAll = true;
                            selectDeviceLogBottomAll.setText(R.string.device_log_cancle_allselete);
                        }
                    } else {
                        deviceLogVo.setSelete(false);
                        index--;
                        isSelectAll = false;
                        selectDeviceLogBottomAll.setText(R.string.device_log_allselete);
                    }
                    setBtnBackground(index);
                    tvDeviceLogBottomSelectNum.setText(String.valueOf(index));
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }


    @OnClick({R.id.iv_common_actionbar_goback, R.id.tv_device_log_edit, R.id.btn_device_log_bottom_delete, R.id.select_device_log_bottom_all})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.tv_device_log_edit:
                updataEditMode();
                break;
            case R.id.btn_device_log_bottom_delete:
                deleteVideo();
                break;
            case R.id.select_device_log_bottom_all:
                selectAllMain();
                break;
        }
    }

    /**
     * 全选和反选
     */
    private void selectAllMain() {
        if (adapter == null) return;
        if (!isSelectAll) {
            for (int i = 0, j = adapter.getMyLiveList().size(); i < j; i++) {
                adapter.getMyLiveList().get(i).setSelete(true);
            }
            index = adapter.getMyLiveList().size();
            btnDeviceLogBottomDelete.setEnabled(true);
            selectDeviceLogBottomAll.setText(R.string.device_log_cancle_allselete);
            isSelectAll = true;
        } else {
            for (int i = 0, j = adapter.getMyLiveList().size(); i < j; i++) {
                adapter.getMyLiveList().get(i).setSelete(false);
            }
            index = 0;
            btnDeviceLogBottomDelete.setEnabled(false);
            selectDeviceLogBottomAll.setText(R.string.device_log_allselete);
            isSelectAll = false;
        }
        adapter.notifyDataSetChanged();
        setBtnBackground(index);
        tvDeviceLogBottomSelectNum.setText(String.valueOf(index));
    }

    /**
     * 删除逻辑
     */
    private void deleteVideo() {
        if (index == 0) {
            btnDeviceLogBottomDelete.setEnabled(false);
            return;
        }
        final AlertDialog builder = new AlertDialog.Builder(this)
                .create();
        builder.show();
        if (builder.getWindow() == null) return;
        builder.getWindow().setContentView(R.layout.popuwindow_device_log);//设置弹出框加载的布局
        TextView msg = (TextView) builder.findViewById(R.id.tv_msg);
        Button cancle = (Button) builder.findViewById(R.id.btn_cancle);
        Button sure = (Button) builder.findViewById(R.id.btn_sure);
        if (msg == null || cancle == null || sure == null) return;
        if (index == 1) {
            msg.setText(R.string.device_log_delete_one);
        } else {
            msg.setText(getString(R.string.device_log_delete_all_one) + index + getString(R.string.device_log_delete_all_two));
        }
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = adapter.getMyLiveList().size(), j = 0; i > j; i--) {
                    DeviceLogVo myLive = adapter.getMyLiveList().get(i - 1);
                    if (myLive.isSelete()) {
                        adapter.getMyLiveList().remove(myLive);
                        index--;
                        DeviceDaoOpe.deleteData(DeviceLogActivity.this, myLive);
                    }
                }
                index = 0;
                tvDeviceLogBottomSelectNum.setText(String.valueOf(0));
                setBtnBackground(index);
                if (adapter.getMyLiveList().size() == 0) {
                    llDeviceLogBottom.setVisibility(View.GONE);
                }
                adapter.notifyDataSetChanged();
                builder.dismiss();
            }
        });
    }

    private void updataEditMode() {
        mEditMode = mEditMode == MYLIVE_MODE_CHECK ? MYLIVE_MODE_EDIT : MYLIVE_MODE_CHECK;
        if (mEditMode == MYLIVE_MODE_EDIT) {
            tvDeviceLogEdit.setText(R.string.device_log_cancle);
            llDeviceLogBottom.setVisibility(View.VISIBLE);
            editorStatus = true;
        } else {
            tvDeviceLogEdit.setText(R.string.device_log_edit);
            llDeviceLogBottom.setVisibility(View.GONE);
            editorStatus = false;
            clearAll();
        }
        adapter.setEditMode(mEditMode);
    }


    private void clearAll() {
        tvDeviceLogBottomSelectNum.setText(String.valueOf(0));
        isSelectAll = false;
        selectDeviceLogBottomAll.setText(R.string.device_log_allselete);
        setBtnBackground(0);
    }
}
