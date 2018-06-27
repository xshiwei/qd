package com.qvd.smartswitch.activity.home;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.adapter.AddRoomDeviceListAdapter;
import com.qvd.smartswitch.model.home.RoomDeviceVo;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    private List<RoomDeviceVo> list = new ArrayList<>();
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
    private PopupWindow popupWindowName;
    /**
     * 名字
     */
    private String name;
    /**
     * 取消
     */
    private PopupWindow popupwindowDelete;

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
        name = "客厅";
        for (int i = 0; i < 10; i++) {
            list.add(new RoomDeviceVo("电动牙刷", false));
        }
        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new AddRoomDeviceListAdapter(this, list);
        recyclerview.setAdapter(adapter);
        adapter.setOnItemClickListener(new AddRoomDeviceListAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(int pos, List<RoomDeviceVo> myLiveList) {
                RoomDeviceVo roomDeviceVo = myLiveList.get(pos);
                boolean selete = roomDeviceVo.isSelete();
                if (!selete) {
                    index++;
                    roomDeviceVo.setSelete(true);
                } else {
                    index--;
                    roomDeviceVo.setSelete(false);
                }
                tvText.setText("已选中" + index + "个设备");
                adapter.notifyDataSetChanged();
                isUpdate = true;
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
                    popupwindowDelete.showAtLocation(view, Gravity.BOTTOM, 0, 30);
                } else {
                    finish();
                }
                break;
            case R.id.tv_save:
                //保存
                ToastUtil.showToast("保存成功");
                finish();
                break;
            case R.id.rl_room_name:
                //房间名字
                showPopupwindowName();
                popupWindowName.showAtLocation(view, Gravity.BOTTOM, 0, 30);
                break;
            case R.id.rl_room_pic:
                //更换图标
                isUpdate = true;
                break;
        }
    }

    /**
     * 显示删除家庭的popupwindow
     */
    private void showPopupwindowDelete() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.popupwindow_dialog_two, null, false);
        popupwindowDelete = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupwindowDelete.setBackgroundDrawable(new ColorDrawable());
        popupwindowDelete.setAnimationStyle(R.style.AnimBottom);
        popupwindowDelete.setOutsideTouchable(true);
        popupwindowDelete.setFocusable(true);
        CommonUtils.setBackgroundAlpha(this, 0.5f);
        popupwindowDelete.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                CommonUtils.setBackgroundAlpha(AddRoomDetailsActivity.this, 1.0f);
            }
        });

        TextView title = view.findViewById(R.id.tv_title);
        TextView cancel = view.findViewById(R.id.tv_cancel);
        TextView confirm = view.findViewById(R.id.tv_confirm);
        TextView text = view.findViewById(R.id.tv_text);
        title.setText("房间编辑信息未保存");
        text.setText("房间被编辑还未保存，请确认是否要保存编辑信息");
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupwindowDelete.dismiss();
                finish();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupwindowDelete.dismiss();
                ToastUtil.showToast("保存成功");
                finish();
            }
        });

    }

    /**
     * 显示更换名字的popupwindow
     */
    private void showPopupwindowName() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.popupwindow_edittext, null, false);
        popupWindowName = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindowName.setBackgroundDrawable(new ColorDrawable());
        popupWindowName.setAnimationStyle(R.style.AnimBottom);
        popupWindowName.setOutsideTouchable(true);
        popupWindowName.setFocusable(true);
        CommonUtils.setBackgroundAlpha(this, 0.5f);
        popupWindowName.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popupWindowName.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        popupWindowName.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                CommonUtils.setBackgroundAlpha(AddRoomDetailsActivity.this, 1.0f);
            }
        });

        TextView title = view.findViewById(R.id.tv_title);
        final EditText editText = view.findViewById(R.id.et_edittext);
        ImageView delete = view.findViewById(R.id.iv_delete);
        final TextView error = view.findViewById(R.id.tv_error);
        TextView cancel = view.findViewById(R.id.tv_cancel);
        final TextView confirm = view.findViewById(R.id.tv_confirm);

        title.setText("设置家庭名称");
        editText.setText(name);
        if (editText.getText().toString().equals(name)) {
            confirm.setEnabled(false);
            confirm.setTextColor(getResources().getColor(R.color.home_setting_text_three));
        }
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals(name)) {
                    error.setVisibility(View.VISIBLE);
                    error.setText("不能和之前名字一致");
                    confirm.setEnabled(false);
                    confirm.setTextColor(getResources().getColor(R.color.home_setting_text_three));
                } else if (s.toString().length() > 10) {
                    error.setVisibility(View.VISIBLE);
                    error.setText("长度超过最大");
                    confirm.setEnabled(false);
                    confirm.setTextColor(getResources().getColor(R.color.home_setting_text_three));
                } else if (s.toString().length() == 0) {
                    confirm.setEnabled(false);
                    confirm.setTextColor(getResources().getColor(R.color.home_setting_text_three));
                    editText.setCursorVisible(false);
                } else {
                    error.setVisibility(View.GONE);
                    confirm.setEnabled(true);
                    confirm.setTextColor(getResources().getColor(R.color.popupwindow_confirm_text));
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvName.setText(name);
                popupWindowName.dismiss();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
                editText.setCursorVisible(false);
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isUpdate = true;
                tvName.setText(editText.getText().toString());
                popupWindowName.dismiss();
            }
        });

    }


    /**
     * 获取所选中的所有条目
     */
    private List<RoomDeviceVo> getSeleteList() {
        List<RoomDeviceVo> roomDeviceList = new ArrayList<>();
        for (int i = 0; i < adapter.getAllList().size(); i++) {
            if (adapter.getAllList().get(i).isSelete()) {
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
            popupwindowDelete.showAtLocation(recyclerview, Gravity.BOTTOM, 0, 30);
        } else {
            finish();
            ToastUtil.showToast("保存成功");
        }
    }
}
