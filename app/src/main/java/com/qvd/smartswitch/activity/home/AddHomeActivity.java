package com.qvd.smartswitch.activity.home;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lljjcoder.Interface.OnCityItemClickListener;
import com.lljjcoder.bean.CityBean;
import com.lljjcoder.bean.DistrictBean;
import com.lljjcoder.bean.ProvinceBean;
import com.lljjcoder.citywheel.CityConfig;
import com.lljjcoder.style.citypickerview.CityPickerView;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.MainActivity;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.SnackbarUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/6/13 0013.
 */

public class AddHomeActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.rl_home_name)
    RelativeLayout rlHomeName;
    @BindView(R.id.tv_location)
    TextView tvLocation;
    @BindView(R.id.rl_home_location)
    RelativeLayout rlHomeLocation;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;


    /**
     * 更改名称的popupwindow
     */
    private PopupWindow popupWindowName;

    /**
     * 删除家庭的popupwindow
     */
    private PopupWindow popupwindowConfirm;

    /**
     * 声明城市选择器
     */
    private CityPickerView mPicker = new CityPickerView();
    private String provinceName;   //省
    private String cityName;       //市
    private String districtName;   //区

    @Override
    protected int setLayoutId() {
        return R.layout.activity_add_home_list;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }

    @Override
    protected void initData() {
        super.initData();
        tvCommonActionbarTitle.setText("添加家庭");
        mPicker.init(this);
    }

    @OnClick({R.id.iv_common_actionbar_goback, R.id.rl_home_name, R.id.rl_home_location, R.id.tv_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.rl_home_name:
                //设置名字
                showPopupwindowName();
                popupWindowName.showAtLocation(view, Gravity.BOTTOM, 0, 30);
                break;
            case R.id.rl_home_location:
                //设置位置
                showCityView();
                break;
            case R.id.tv_confirm:
                //确认
                //判断名字或者地址有没有填
                if (CommonUtils.isEmptyString(tvName.getText().toString()) || CommonUtils.isEmptyString(tvLocation.getText().toString())) {
                    tvConfirm.setEnabled(false);
                    SnackbarUtils.Short(tvConfirm, "名字或位置不能为空").show();
                } else {
                    tvConfirm.setEnabled(true);
                    showPopupwindowConfirm();
                    popupwindowConfirm.showAtLocation(view, Gravity.BOTTOM, 0, 30);
                }
                break;
        }
    }

    /**
     * 显示城市选择器
     */
    private void showCityView() {
        //添加默认的配置
        CityConfig cityConfig = new CityConfig.Builder().build();
        mPicker.setConfig(cityConfig);
        //监听选择点击事件及返回结果
        mPicker.setOnCityItemClickListener(new OnCityItemClickListener() {
            @Override
            public void onSelected(ProvinceBean province, CityBean city, DistrictBean district) {

                //省份
                if (province != null) {
                    provinceName = province.getName();
                }

                //城市
                if (city != null) {
                    cityName = city.getName();
                }

                //地区
                if (district != null) {
                    districtName = district.getName();
                }
                tvLocation.setText(provinceName + " " + cityName + " " + districtName);
            }

            @Override
            public void onCancel() {
            }
        });
        mPicker.showCityPicker();
    }


    /**
     * 显示删除家庭的popupwindow
     */
    private void showPopupwindowConfirm() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.popupwindow_dialog_two, null, false);
        popupwindowConfirm = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupwindowConfirm.setBackgroundDrawable(new ColorDrawable());
        popupwindowConfirm.setAnimationStyle(R.style.AnimBottom);
        popupwindowConfirm.setOutsideTouchable(true);
        popupwindowConfirm.setFocusable(true);
        CommonUtils.setBackgroundAlpha(this, 0.5f);
        popupwindowConfirm.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                CommonUtils.setBackgroundAlpha(AddHomeActivity.this, 1.0f);
            }
        });

        TextView title = view.findViewById(R.id.tv_title);
        TextView cancel = view.findViewById(R.id.tv_cancel);
        TextView confirm = view.findViewById(R.id.tv_confirm);
        TextView text = view.findViewById(R.id.tv_text);

        cancel.setText("稍后再说");
        confirm.setText("立即设置");
        title.setText("已成功创建- 二宝");
        text.setText("是否现在立刻去设置新家庭");

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupwindowConfirm.dismiss();
                startActivity(new Intent(AddHomeActivity.this, MainActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                finish();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddHomeActivity.this, AddRoomListActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                popupwindowConfirm.dismiss();
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
                CommonUtils.setBackgroundAlpha(AddHomeActivity.this, 1.0f);
            }
        });

        TextView title = view.findViewById(R.id.tv_title);
        final EditText editText = view.findViewById(R.id.et_edittext);
        ImageView delete = view.findViewById(R.id.iv_delete);
        final TextView error = view.findViewById(R.id.tv_error);
        TextView cancel = view.findViewById(R.id.tv_cancel);
        final TextView confirm = view.findViewById(R.id.tv_confirm);

        title.setText("设置家庭名称");
        editText.setText("");
        if (editText.getText().toString().equals("")) {
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
                if (s.toString().length() > 10) {
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
                tvName.setText(editText.getText().toString());
                popupWindowName.dismiss();
            }
        });

    }
}
