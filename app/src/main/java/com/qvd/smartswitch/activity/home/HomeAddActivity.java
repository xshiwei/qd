package com.qvd.smartswitch.activity.home;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.lljjcoder.Interface.OnCityItemClickListener;
import com.lljjcoder.bean.CityBean;
import com.lljjcoder.bean.DistrictBean;
import com.lljjcoder.bean.ProvinceBean;
import com.lljjcoder.citywheel.CityConfig;
import com.lljjcoder.style.citypickerview.CityPickerView;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.MainActivity;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.adapter.HomeSettingPicAdapter;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.home.AddHomeVo;
import com.qvd.smartswitch.model.home.HomeBackgroundVo;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.ConfigUtils;
import com.qvd.smartswitch.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/6/13 0013.
 */

public class HomeAddActivity extends BaseActivity {
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
     * 声明城市选择器
     */
    private final CityPickerView mPicker = new CityPickerView();
    private String provinceName;   //省
    private String cityName;       //市
    private String districtName;   //区

    /**
     * 图片集合
     */
    private List<HomeBackgroundVo> list = new ArrayList<>();
    private HomeSettingPicAdapter picAdapter;

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
        tvCommonActionbarTitle.setText(R.string.home_add_title);
        setRecycleView();
        mPicker.init(this);
    }

    @OnClick({R.id.iv_common_actionbar_goback, R.id.rl_home_name, R.id.rl_home_location, R.id.tv_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                if (!CommonUtils.isEmptyString(tvName.getText().toString()) || !CommonUtils.isEmptyString(tvLocation.getText().toString())) {
                    showPopupWindowBack();
                } else {
                    finish();
                }
                break;
            case R.id.rl_home_name:
                //设置名字
                showPopupWindowName();
                break;
            case R.id.rl_home_location:
                //设置位置
                showCityView();
                break;
            case R.id.tv_confirm:
                //确认
                //判断名字或者地址有没有填
                if (CommonUtils.isEmptyString(tvName.getText().toString()) || CommonUtils.isEmptyString(tvLocation.getText().toString())) {
                    ToastUtil.showToast(getString(R.string.home_add_not_empty));
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
        new MaterialDialog.Builder(this)
                .content(R.string.home_add_abandon_edit_and_exit)
                .negativeText(R.string.common_exit)
                .positiveText(R.string.common_continue_edit)
                .onNegative((dialog, which) -> finish())
                .onPositive((dialog, which) -> {

                })
                .show();
    }

    /**
     * 设置图片列表
     */
    private void setRecycleView() {
        list.add(new HomeBackgroundVo(R.color.black));
        list.add(new HomeBackgroundVo(R.color.red));
        list.add(new HomeBackgroundVo(R.color.orange));
        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        picAdapter = new HomeSettingPicAdapter(list);
        recyclerview.setAdapter(picAdapter);
        picAdapter.setOnItemClickListener((adapter, view, position) -> picAdapter.setSelection(position));
    }

    /**
     * 添加家庭
     */
    private void addFamily() {
        RetrofitService.qdoApi.addFamily(tvName.getText().toString(), tvLocation.getText().toString(), ConfigUtils.user_id, "https://www.qq745.com/uploads/allimg/141009/1-14100ZT451-51.jpg")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AddHomeVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(AddHomeVo messageVo) {
                        //判断有没有添加成功，添加成功则跳出一个弹窗提示。
                        if (messageVo != null) {
                            if (messageVo.getCode() == 200) {
                                showPopupWindowConfirm(messageVo);
                            } else {
                                ToastUtil.showToast(getString(R.string.common_save_fail));
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showToast(getString(R.string.common_save_fail));
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
    private void showPopupWindowConfirm(AddHomeVo addHomeVo) {
        new MaterialDialog.Builder(this)
                .title("已成功创建- " + tvName.getText().toString())
                .content(R.string.home_add_immediately_set_family)
                .negativeText(R.string.common_talk_about_later)
                .positiveText(R.string.common_immediately_setting)
                .onNegative((dialog, which) -> {
                    startActivity(new Intent(HomeAddActivity.this, MainActivity.class));
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    finish();
                })
                .onPositive((dialog, which) -> {
                    startActivity(new Intent(HomeAddActivity.this, RoomAddListActivity.class)
                            .putExtra("family_id", addHomeVo.getFamily_id()));
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    finish();
                })
                .show();
    }


    /**
     * 显示更换名字的popupwindow
     */
    private void showPopupWindowName() {
        new MaterialDialog.Builder(this)
                .content(R.string.home_add_set_family_name)
                .negativeText(R.string.common_cancel)
                .positiveText(R.string.common_confirm)
                .inputRange(1, 20, getResources().getColor(R.color.red))
                .input(tvName.getText().toString(), null, false, (dialog, input) -> {

                })
                .onNegative((dialog, which) -> CommonUtils.closeSoftKeyboard(HomeAddActivity.this))
                .onPositive((dialog, which) -> {
                    tvName.setText(Objects.requireNonNull(dialog.getInputEditText()).getText().toString());
                    CommonUtils.closeSoftKeyboard(HomeAddActivity.this);
                })
                .show();
    }

    @Override
    public void onBackPressed() {
        if (!CommonUtils.isEmptyString(tvName.getText().toString()) || !CommonUtils.isEmptyString(tvLocation.getText().toString())) {
            showPopupWindowBack();
        } else {
            finish();
        }
    }
}
