package com.qvd.smartswitch.activity.device;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.MainActivity;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.activity.home.RoomListSeleteActivity;
import com.qvd.smartswitch.activity.qsTwo.QsTwoControlActivity;
import com.qvd.smartswitch.activity.qsThree.QsThreeControlActivity;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.device.ScanResultVo;
import com.qvd.smartswitch.model.device.UpdateDeviceRoomVo;
import com.qvd.smartswitch.model.login.MessageVo;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.ConfigUtils;
import com.qvd.smartswitch.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class DeviceSetRoomActivity extends BaseActivity {


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

    /**
     * 是否设置成常用
     */
    private int isCommon = 1;
    /**
     * roomId
     */
    private String roomId;
    /**
     * 表类型
     */
    private String tableType;

    private boolean isSelete = true;

    private ScanResultVo resultVo;


    @Override
    protected int setLayoutId() {
        return R.layout.activity_set_device_to_room;
    }

    @Override
    protected void initData() {
        super.initData();
        resultVo = (ScanResultVo) getIntent().getSerializableExtra("scanResult");
        roomId = ConfigUtils.defaultRoomId;
        initDevice();
    }

    /**
     * 初始化设备图片
     */
    private void initDevice() {
        tvName.setText(CommonUtils.getDeviceName(resultVo.getDeviceNo()));
        tableType = resultVo.getDeviceNo();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(false).transparentBar().init();
    }

    @OnClick({R.id.iv_common_actionbar_goback, R.id.rl_device_name, R.id.iv_set_common, R.id.tv_complete, R.id.rl_selete_room})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                showCancelPopupWindow();
                break;
            case R.id.rl_device_name:
                showPopupWindowName();
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
                startActivityForResult(new Intent(this, RoomListSeleteActivity.class), 1);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
        }
    }

    /**
     * 显示更换名字的popupwindow
     */
    private void showPopupWindowName() {
        new MaterialDialog.Builder(this)
                .title("设置设备名称")
                .negativeText("取消")
                .positiveText("确定")
                .inputRange(1, 20, getResources().getColor(R.color.red))
                .input(tvName.getText().toString(), null, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        CommonUtils.closeSoftKeyboard(DeviceSetRoomActivity.this);
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        tvName.setText(dialog.getInputEditText().getText().toString());
                        CommonUtils.closeSoftKeyboard(DeviceSetRoomActivity.this);
                    }
                })
                .show();
    }

    /**
     * 取消的popupWindow
     */
    private void showCancelPopupWindow() {
        new MaterialDialog.Builder(this)
                .content("是否保存本次修改")
                .negativeText("取消")
                .positiveText("确定")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        update(2);
                        startActivity(new Intent(DeviceSetRoomActivity.this, MainActivity.class));
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                        finish();
                    }
                })
                .show();
    }


    /**
     * 保存
     */
    private void update(int i) {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .content("正在保存相关配置")
                .progress(true, 0)
                .show();
        RetrofitService.qdoApi.updateSpecificDeviceName(resultVo.getDeviceId(), tvName.getText().toString(), tableType)
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
                        Gson gson = new Gson();
                        List<String> list = new ArrayList<>();
                        list.add(resultVo.getDeviceId());
                        UpdateDeviceRoomVo updateDeviceRoomVo = new UpdateDeviceRoomVo();
                        updateDeviceRoomVo.setRoom_id(roomId);
                        updateDeviceRoomVo.setDevice_id(list);
                        String body = gson.toJson(updateDeviceRoomVo);
                        RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type: application/json"), body);
                        return RetrofitService.qdoApi.updateDeviceRoom(requestBody);
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
                        return RetrofitService.qdoApi.setCommonDevice(resultVo.getDeviceId(), isCommon);
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
                            dialog.dismiss();
                            if (i == 1) {
                                resultVo.setIsFirstConnect(1);
                                switch (resultVo.getDeviceNo()) {
                                    case "qs02":
                                        startActivity(new Intent(DeviceSetRoomActivity.this, QsTwoControlActivity.class)
                                                .putExtra("scanResult", resultVo));
                                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                        finish();
                                        break;
                                    case "qs03":
                                        startActivity(new Intent(DeviceSetRoomActivity.this, QsThreeControlActivity.class)
                                                .putExtra("scanResult", resultVo));
                                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                        finish();
                                        break;
                                }
                            } else {
                                ToastUtil.showToast("保存成功");
                            }
                        } else {
                            ToastUtil.showToast("保存失败");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        dialog.dismiss();
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

    @Override
    public void onBackPressed() {
        showCancelPopupWindow();
    }
}
