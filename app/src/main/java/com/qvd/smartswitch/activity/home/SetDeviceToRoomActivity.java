package com.qvd.smartswitch.activity.home;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.activity.device.DeviceControlTwoActivity;
import com.qvd.smartswitch.activity.wifi.DeviceWifiControlActivity;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.device.ScanResultVo;
import com.qvd.smartswitch.model.login.MessageVo;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.ConfigUtils;
import com.qvd.smartswitch.utils.SnackbarUtils;
import com.qvd.smartswitch.utils.ToastUtil;
import com.qvd.smartswitch.widget.MyPopupWindowLoading;
import com.qvd.smartswitch.widget.MyPopupWindowOne;
import com.qvd.smartswitch.widget.MyPopupWindowThree;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

public class SetDeviceToRoomActivity extends BaseActivity {


    @BindView(R.id.iv_device_pic)
    ImageView ivDevicePic;
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.rl_device_name)
    RelativeLayout rlDeviceName;
    @BindView(R.id.tv_room_name)
    TextView tvRoomName;
    @BindView(R.id.rl_selete_room)
    RelativeLayout rlSeleteRoom;
    @BindView(R.id.iv_set_common)
    ImageView ivSetCommon;
    @BindView(R.id.rl_set_common)
    RelativeLayout rlSetCommon;
    @BindView(R.id.tv_complete)
    TextView tvComplete;

    private MyPopupWindowOne mCancelPopupWindow;

    /**
     * 设备的device_id
     */
    private String device_id;
    /**
     * 是否设置成常用
     */
    private int isCommon = 0;
    /**
     * roomId
     */
    private String roomId;
    /**
     * 表类型
     */
    private String tableType;

    /**
     * 修改名字
     */
    private MyPopupWindowThree popupWindowName;

    private boolean isSelete = false;

    private MyPopupWindowLoading myPopupWindowLoading;

    private ScanResultVo resultVo;


    @Override
    protected int setLayoutId() {
        return R.layout.activity_set_device_to_room;
    }

    @Override
    protected void initData() {
        super.initData();
        device_id = getIntent().getStringExtra("device_id");
        resultVo = (ScanResultVo) getIntent().getSerializableExtra("scanResult");
        roomId = ConfigUtils.defaultRoomId;
        initDevice();
    }

    /**
     * 初始化设备图片
     */
    private void initDevice() {
        switch (resultVo.getDeviceNo()) {
            case "qs02":
                ivDevicePic.setImageResource(R.color.white);
                break;
            case "qs03":
                ivDevicePic.setImageResource(R.color.red);
                break;
        }
        tvName.setText(CommonUtils.getDeviceName(resultVo.getDeviceNo()));
        tableType = CommonUtils.getTableName(resultVo.getDeviceNo());
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }

    @OnClick({R.id.iv_common_actionbar_goback, R.id.rl_device_name, R.id.iv_set_common, R.id.tv_complete, R.id.rl_selete_room})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                showCancelPopupWindow();
                break;
            case R.id.rl_device_name:
                showPopupWindowName();
                popupWindowName.showPopupWindow(view);
                break;
            case R.id.iv_set_common:
                if (isSelete) {
                    isSelete = false;
                    isCommon = 0;
                    ivSetCommon.setImageResource(R.mipmap.register_unselete);
                } else {
                    isSelete = true;
                    isCommon = 1;
                    ivSetCommon.setImageResource(R.mipmap.register_selete);
                }
                break;
            case R.id.tv_complete:
                update(1);
                break;
            case R.id.rl_selete_room:
                startActivityForResult(new Intent(this, SeleteRoomToSetActivity.class), 1);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
        }
    }

    /**
     * 显示更换名字的popupwindow
     */
    private void showPopupWindowName() {
        popupWindowName = new MyPopupWindowThree(this, "设置设备名称", tvName.getText().toString(), new MyPopupWindowThree.IPopupWindowListener() {
            @Override
            public void cancel() {
                popupWindowName.dismiss();
                CommonUtils.closeSoftKeyboard(SetDeviceToRoomActivity.this);
            }

            @Override
            public void confirm() {
                EditText etEditText = popupWindowName.getEtEditText();
                tvName.setText(etEditText.getText().toString());
                popupWindowName.dismiss();
                CommonUtils.closeSoftKeyboard(SetDeviceToRoomActivity.this);
            }
        });
        final EditText etEditText = popupWindowName.getEtEditText();
        final TextView tvConfirm = popupWindowName.getTvConfirm();
        final TextView tvError = popupWindowName.getTvError();

        if (etEditText.getText().toString().equals("")) {
            tvConfirm.setEnabled(false);
            tvConfirm.setTextColor(etEditText.getResources().getColor(R.color.home_setting_text_three));
        }

        //判断当前输入是否符合要求
        etEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 20) {
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
     * 取消的popupWindow
     */
    private void showCancelPopupWindow() {
        mCancelPopupWindow = new MyPopupWindowOne(this, "是否保存本次修改", "取消", "保存", new MyPopupWindowOne.IPopupWindowListener() {
            @Override
            public void cancel() {
                mCancelPopupWindow.dismiss();
                finish();
            }

            @Override
            public void confirm() {
                mCancelPopupWindow.dismiss();
                update(2);
                finish();
            }
        });
        mCancelPopupWindow.showPopupWindow(ivCommonActionbarGoback);
    }


    /**
     * 保存
     */
    private void update(int i) {
        myPopupWindowLoading = new MyPopupWindowLoading(this, "正在保存相关配置");
        myPopupWindowLoading.showPopupWindow(ivCommonActionbarGoback);
        RetrofitService.qdoApi.updateSpecificDeviceName(device_id, tvName.getText().toString(), tableType)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .filter(new Predicate<MessageVo>() {
                    @Override
                    public boolean test(MessageVo messageVo) throws Exception {
                        return messageVo.getCode() == 200;
                    }
                })
                .observeOn(Schedulers.io())
                .flatMap(new Function<MessageVo, ObservableSource<MessageVo>>() {
                    @Override
                    public ObservableSource<MessageVo> apply(MessageVo messageVo) throws Exception {
                        return RetrofitService.qdoApi.updateDeviceRoom(device_id, roomId);
                    }
                })
                .filter(new Predicate<MessageVo>() {
                    @Override
                    public boolean test(MessageVo messageVo) throws Exception {
                        return messageVo.getCode() == 200;
                    }
                })
                .flatMap(new Function<MessageVo, ObservableSource<MessageVo>>() {
                    @Override
                    public ObservableSource<MessageVo> apply(MessageVo messageVo) throws Exception {
                        return RetrofitService.qdoApi.setCommonDevice(device_id, isCommon);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MessageVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MessageVo messageVo) {
                        if (messageVo.getCode() == 200) {
                            myPopupWindowLoading.dismiss();
                            if (i == 1) {
                                switch (resultVo.getDeviceNo()) {
                                    case "qs02":
                                        startActivity(new Intent(SetDeviceToRoomActivity.this, DeviceControlTwoActivity.class)
                                                .putExtra("scanResult", resultVo)
                                                .putExtra("isFirstConnect", 1));
                                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                        finish();
                                        break;
                                    case "qs03":
                                        startActivity(new Intent(SetDeviceToRoomActivity.this, DeviceWifiControlActivity.class)
                                                .putExtra("isFirstConnect", 1));
                                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                        finish();
                                        break;
                                }
                            } else {
                                ToastUtil.showToast("保存成功");
                                finish();
                            }
                        } else {
                            ToastUtil.showToast("保存失败");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        myPopupWindowLoading.dismiss();
                        ToastUtil.showToast("保存失败");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 & resultCode == 2) {
            roomId = data.getStringExtra("room_id");
            tvRoomName.setText(data.getStringExtra("room_name"));
        }
    }
}
