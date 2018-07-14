package com.qvd.smartswitch.activity.device;


import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.clj.fastble.data.BleDevice;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.db.DeviceNickNameDaoOpe;
import com.qvd.smartswitch.model.DeviceNickNameVo;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.SnackbarUtils;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2018/4/17 0017.
 */

public class DeviceSetTypeActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.flowLayout)
    TagFlowLayout flowLayout;
    @BindView(R.id.btn_device_set_type_save)
    Button btnDeviceSetTypeSave;

    private List<String> list = new ArrayList<>();
    private String type = "";
    private BleDevice bledevice;
    private Disposable notify;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_device_set_type;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.app_color).init();
    }

    @Override
    protected void initData() {
        super.initData();
        bledevice = getIntent().getParcelableExtra("bledevice");
        notify = CommonUtils.getNotify(this, bledevice);
        list.add("开关");
        list.add("机器人");
        list.add("扫地");
        list.add("耳机");
        final LayoutInflater inflater = LayoutInflater.from(this);
        tvCommonActionbarTitle.setText("设置类型");
        flowLayout.setAdapter(new TagAdapter<String>(list) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView textView = (TextView) inflater.inflate(R.layout.textview_flowlayout, flowLayout, false);
                textView.setText(s);
                return textView;
            }
        });
        flowLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                type = list.get(position);
                return true;
            }
        });
    }

    @OnClick({R.id.iv_common_actionbar_goback, R.id.btn_device_set_type_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.btn_device_set_type_save:
                DeviceNickNameVo deviceNickNameVo = DeviceNickNameDaoOpe.queryOne(this, CommonUtils.getMac(bledevice.getMac()));
                DeviceNickNameVo deviceNickNameVo1 = new DeviceNickNameVo(deviceNickNameVo.getId(), deviceNickNameVo.getDeviceId(), deviceNickNameVo.getDeviceName(),
                        deviceNickNameVo.getDate(), deviceNickNameVo.getDeviceNickname(), deviceNickNameVo.getPic(), type);
                DeviceNickNameDaoOpe.updateData(this, deviceNickNameVo1);
                SnackbarUtils.Short(btnDeviceSetTypeSave, "设置成功").show();
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (notify != null) {
            notify.dispose();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (notify != null) {
            notify.dispose();
        }
    }
}
