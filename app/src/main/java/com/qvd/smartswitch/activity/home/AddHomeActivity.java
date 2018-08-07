package com.qvd.smartswitch.activity.home;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.qvd.smartswitch.model.login.MessageVo;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.ConfigUtils;
import com.qvd.smartswitch.utils.SnackbarUtils;
import com.qvd.smartswitch.widget.MyPopupWindowOne;
import com.qvd.smartswitch.widget.MyPopupWindowThree;
import com.qvd.smartswitch.widget.MyPopupWindowTwo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;

    /**
     * 更改名称的popupwindow
     */
    private MyPopupWindowThree popupWindowName;

    /**
     * 删除家庭的popupwindow
     */
    private MyPopupWindowTwo popupwindowConfirm;

    /**
     * 声明城市选择器
     */
    private CityPickerView mPicker = new CityPickerView();
    private String provinceName;   //省
    private String cityName;       //市
    private String districtName;   //区

    /**
     * 图片集合
     */
    private List<HomeBackgroundVo> list = new ArrayList<>();
    private HomeSettingPicAdapter adapter;
    /**
     * 当未保存时弹出该提示框
     */
    private MyPopupWindowOne popupWindowBack;

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
        setRecycleView();
        mPicker.init(this);
    }

    @OnClick({R.id.iv_common_actionbar_goback, R.id.rl_home_name, R.id.rl_home_location, R.id.tv_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                if (!CommonUtils.isEmptyString(tvName.getText().toString()) || !CommonUtils.isEmptyString(tvLocation.getText().toString())) {
                    showPopupWindowBack();
                    popupWindowBack.showPopupWindow(tvCommonActionbarTitle);
                } else {
                    finish();
                }
                break;
            case R.id.rl_home_name:
                //设置名字
                showPopupWindowName();
                popupWindowName.showPopupWindow(view);
                break;
            case R.id.rl_home_location:
                //设置位置
                showCityView();
                break;
            case R.id.tv_confirm:
                //确认
                //判断名字或者地址有没有填
                if (CommonUtils.isEmptyString(tvName.getText().toString()) || CommonUtils.isEmptyString(tvLocation.getText().toString())) {
                    SnackbarUtils.Short(tvConfirm, "名字或位置不能为空").show();
                } else {
                    addFamily();
                }
                break;
        }
    }

    /**
     * 创建退出时提示用户还未创建成功的popupwindow
     */
    private void showPopupWindowBack() {
        popupWindowBack = new MyPopupWindowOne(this, "您当前创建的家庭还没有保存，是否放弃编辑并退出？", "继续编辑", "退出", new MyPopupWindowOne.IPopupWindowListener() {
            @Override
            public void cancel() {
                popupWindowBack.dismiss();
            }

            @Override
            public void confirm() {
                popupWindowBack.dismiss();
                finish();
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

    /**
     * 添加家庭
     */
    private void addFamily() {
        RetrofitService.qdoApi.addFamily(tvName.getText().toString(), tvLocation.getText().toString(), ConfigUtils.user_id, "https://www.qq745.com/uploads/allimg/141009/1-14100ZT451-51.jpg")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MessageVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MessageVo messageVo) {
                        //判断有没有添加成功，添加成功则跳出一个弹窗提示。
                        if (messageVo.getCode() == 200) {
                            showPopupWindowConfirm();
                            popupwindowConfirm.showPopupWindow(tvCommonActionbarTitle);
                        } else if (messageVo.getCode() == 400) {
                            SnackbarUtils.Short(tvCommonActionbarTitle, "保存失败");
                        } else {
                            SnackbarUtils.Short(tvCommonActionbarTitle, "网络连接超时");
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
     * 显示popupwindow
     */
    private void showPopupWindowConfirm() {
        popupwindowConfirm = new MyPopupWindowTwo(this, "已成功创建- 二宝", "是否现在立刻去设置新家庭", "稍后再说", "立即设置", new MyPopupWindowTwo.IPopupWindowListener() {
            @Override
            public void cancel() {
                popupwindowConfirm.dismiss();
                startActivity(new Intent(AddHomeActivity.this, MainActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                finish();
            }

            @Override
            public void confirm() {
                popupwindowConfirm.dismiss();
                startActivity(new Intent(AddHomeActivity.this, AddRoomListActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                finish();
            }
        });
    }


    /**
     * 显示更换名字的popupwindow
     */
    private void showPopupWindowName() {

        popupWindowName = new MyPopupWindowThree(this, "设置家庭名称", tvName.getText().toString(), new MyPopupWindowThree.IPopupWindowListener() {
            @Override
            public void cancel() {
                popupWindowName.dismiss();
                CommonUtils.closeSoftKeyboard(AddHomeActivity.this);
            }

            @Override
            public void confirm() {
                EditText etEditText = popupWindowName.getEtEditText();
                tvName.setText(etEditText.getText().toString());
                popupWindowName.dismiss();
                CommonUtils.closeSoftKeyboard(AddHomeActivity.this);
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

    @Override
    public void onBackPressed() {
        if (!CommonUtils.isEmptyString(tvName.getText().toString()) || !CommonUtils.isEmptyString(tvLocation.getText().toString())) {
            showPopupWindowBack();
            popupWindowBack.showPopupWindow(tvCommonActionbarTitle);
        } else {
            finish();
        }
    }
}
