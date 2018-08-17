package com.qvd.smartswitch.activity.home;

import android.content.Intent;
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
import com.orhanobut.logger.Logger;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.MainActivity;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.adapter.HomeSettingPicAdapter;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.home.HomeBackgroundVo;
import com.qvd.smartswitch.model.home.HomeDetailsVo;
import com.qvd.smartswitch.model.login.MessageVo;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.ConfigUtils;
import com.qvd.smartswitch.utils.SnackbarUtils;
import com.qvd.smartswitch.utils.ToastUtil;
import com.qvd.smartswitch.widget.MyPopupWindowOne;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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
    private MyPopupWindowOne popupwindowDelete;

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
    //传来的family_id
    private String family_id;

    /**
     * 家庭数量
     */
    private int family_num;

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
        family_id = getIntent().getStringExtra("family_id");
        family_num = getIntent().getIntExtra("family_num", -1);
        tvCommonActionbarTitle.setText("家庭管理");
        getFamilyData();
        setRecycleView();
        //初始化数据
        mPicker.init(this);
    }

    /**
     * 初始化时获取家庭的基本信息
     */
    private void getFamilyData() {
        RetrofitService.qdoApi.getFamily(family_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HomeDetailsVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HomeDetailsVo homeDetailsVo) {
                        if (homeDetailsVo != null) {
                            if (homeDetailsVo.getCode() == 200) {
                                tvLocation.setText(homeDetailsVo.getData().getFamily_location());
                                tvName.setText(homeDetailsVo.getData().getFamily_name());
                                name = tvName.getText().toString();
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
                //退出时保存一下家庭信息
                updateFamily();
                finish();
                break;
            case R.id.rl_home_name:
                //更改家庭的名字
                showPopupwindowName();
                popupWindowName.showAtLocation(view, Gravity.BOTTOM, 0, 20);
                break;
            case R.id.rl_home_location:
                //更改家庭的位置
                showCityView();
                break;
            case R.id.rl_delete:
                //删除家庭
                showPopupwindowDelete();
                popupwindowDelete.showPopupWindow(view);
                break;
        }
    }

    /**
     * 保存家庭信息
     */
    private void updateFamily() {
        RetrofitService.qdoApi.updateFamily(tvName.getText().toString(), tvLocation.getText().toString(), family_id, "https://www.qq745.com/uploads/allimg/141009/1-14100ZT451-51.jpg")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MessageVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MessageVo messageVo) {
                        if (messageVo.getCode() == 400) {
                            SnackbarUtils.Short(tvCommonActionbarTitle, "修改失败").show();
                        } else if (messageVo.getCode() == 800) {
                            SnackbarUtils.Short(tvCommonActionbarTitle, "连接超时").show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
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
        popupwindowDelete = new MyPopupWindowOne(this, "删除家庭后，所有已设置的信息将全部清除，不可恢复。是否确认删除家庭?", "取消", "确定", new MyPopupWindowOne.IPopupWindowListener() {
            @Override
            public void cancel() {
                popupwindowDelete.dismiss();
            }

            @Override
            public void confirm() {
                deleteFamily();
                popupwindowDelete.dismiss();
            }
        });
    }

    /**
     * 删除家庭
     */
    private void deleteFamily() {
        if (family_num == 1) {
            SnackbarUtils.Short(tvCommonActionbarTitle, "必须保留一个默认家庭,不能删除").show();
        } else {
            RetrofitService.qdoApi.deleteFamily(family_id, ConfigUtils.user_id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<MessageVo>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(MessageVo messageVo) {
                            if (messageVo.getCode() == 200) {
                                startActivity(new Intent(HomeSettingActivity.this, MainActivity.class));
                                overridePendingTransition(R.anim.push_left_out, R.anim.push_bottom_in);
                                finish();
                            } else if (messageVo.getCode() == 400) {
                                SnackbarUtils.Short(tvCommonActionbarTitle, "删除失败").show();
                            } else {
                                SnackbarUtils.Short(tvCommonActionbarTitle, "连接超时").show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Logger.e(e.getMessage());
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
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
        editText.setHint(name);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        updateFamily();
    }
}
