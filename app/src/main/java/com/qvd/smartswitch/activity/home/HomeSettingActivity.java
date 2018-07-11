package com.qvd.smartswitch.activity.home;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.adapter.HomeSettingPicAdapter;
import com.qvd.smartswitch.model.home.HomeBackgroundVo;
import com.qvd.smartswitch.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/6/12 0012.
 */

public class HomeSettingActivity extends BaseActivity {
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
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.rl_delete)
    RelativeLayout rlDelete;
    @BindView(R.id.rl_layout)
    RelativeLayout rlLayout;

    /**
     * 图片集合
     */
    private List<HomeBackgroundVo> list = new ArrayList<>();
    private HomeSettingPicAdapter adapter;

    /**
     * 更改名称的popupwindow
     */
    private PopupWindow popupWindowName;

    /**
     * 删除家庭的popupwindow
     */
    private PopupWindow popupwindowDelete;

    /**
     * 获取名称
     */
    private String name;

    /**
     * 声明城市选择器
     */
    private CityPickerView mPicker = new CityPickerView();
    private String provinceName;   //省
    private String cityName;       //市
    private String districtName;   //区

    @Override
    protected int setLayoutId() {
        return R.layout.activity_home_setting;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }

    @Override
    protected void initData() {
        super.initData();
        tvCommonActionbarTitle.setText("家庭管理");
        name = tvName.getText().toString();
        setRecycleView();
        //初始化数据
        mPicker.init(this);
    }

    /**
     * 设置图片列表
     */
    private void setRecycleView() {
        list.add(new HomeBackgroundVo(R.color.black));
        list.add(new HomeBackgroundVo(R.color.red));
        list.add(new HomeBackgroundVo(R.color.orange));
        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapter = new HomeSettingPicAdapter(this, list);
        recyclerview.setAdapter(adapter);
        adapter.setOnItemClickListener(new HomeSettingPicAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                adapter.setSelection(position);
            }

            @Override
            public void onItemLongClickListener(View view, int position) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @OnClick({R.id.iv_common_actionbar_goback, R.id.rl_home_name, R.id.rl_home_location, R.id.rl_delete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.rl_home_name:
                //更改家庭的名字
                showPopupwindowName();
                popupWindowName.showAtLocation(view, Gravity.BOTTOM , 0, 30);
                break;
            case R.id.rl_home_location:
                //更改家庭的位置
                showCityView();
                break;
            case R.id.rl_delete:
                //删除家庭
                showPopupwindowDelete();
                popupwindowDelete.showAtLocation(view, Gravity.BOTTOM, 0, 30);
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
    private void showPopupwindowDelete() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.popupwindow_dialog, null, false);
        popupwindowDelete = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupwindowDelete.setBackgroundDrawable(new ColorDrawable());
        popupwindowDelete.setAnimationStyle(R.style.AnimBottom);
        popupwindowDelete.setOutsideTouchable(true);
        popupwindowDelete.setFocusable(true);
        CommonUtils.setBackgroundAlpha(this, 0.5f);
        popupwindowDelete.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                CommonUtils.setBackgroundAlpha(HomeSettingActivity.this, 1.0f);
            }
        });

        TextView title = view.findViewById(R.id.tv_title);
        TextView cancel = view.findViewById(R.id.tv_cancel);
        TextView confirm = view.findViewById(R.id.tv_confirm);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupwindowDelete.dismiss();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ToastUtil.showToast("确认删除");
                popupwindowDelete.dismiss();
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
                CommonUtils.setBackgroundAlpha(HomeSettingActivity.this, 1.0f);
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
