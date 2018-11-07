package com.qvd.smartswitch.activity.home;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
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
    @BindView(R.id.rl_room_manage)
    RelativeLayout rlRoomManage;

    /**
     * 图片集合
     */
    private List<HomeBackgroundVo> list = new ArrayList<>();
    private HomeSettingPicAdapter adapter;

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
        tvCommonActionbarTitle.setText(R.string.home_setting_title);
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
        adapter = new HomeSettingPicAdapter(list);
        recyclerview.setAdapter(adapter);
        adapter.setOnItemClickListener((adapter1, view, position) -> adapter.setSelection(position));
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @OnClick({R.id.iv_common_actionbar_goback, R.id.rl_home_name, R.id.rl_home_location, R.id.rl_delete, R.id.rl_room_manage})
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
                break;
            case R.id.rl_home_location:
                //更改家庭的位置
                showCityView();
                break;
            case R.id.rl_delete:
                //删除家庭
                showPopupwindowDelete();
                break;
            case R.id.rl_room_manage:
                startActivity(new Intent(this, RoomManageActivity.class)
                        .putExtra("family_id", family_id));
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
        new MaterialDialog.Builder(this)
                .content(R.string.home_manage_delete_family_content)
                .negativeText(R.string.common_cancel)
                .positiveText(R.string.common_confirm)
                .onNegative((dialog, which) -> {

                })
                .onPositive((dialog, which) -> deleteFamily())
                .show();
    }

    /**
     * 删除家庭
     */
    private void deleteFamily() {
        if (family_num == 1) {
            ToastUtil.showToast(getString(R.string.home_manage_save_default_family));
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
                            if (messageVo != null) {
                                if (messageVo.getCode() == 200) {
                                    startActivity(new Intent(HomeSettingActivity.this, MainActivity.class));
                                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                    try {
                                        Thread.sleep(500);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    finish();
                                } else {
                                    ToastUtil.showToast(getString(R.string.common_server_error));
                                }
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
        new MaterialDialog.Builder(this)
                .content(R.string.home_manage_set_family_name)
                .negativeText(R.string.common_cancel)
                .positiveText(R.string.common_confirm)
                .inputRange(1, 20, getResources().getColor(R.color.red))
                .input(tvName.getText().toString(), null, false, (dialog, input) -> {

                })
                .onNegative((dialog, which) -> CommonUtils.closeSoftKeyboard(HomeSettingActivity.this))
                .onPositive((dialog, which) -> {
                    tvName.setText(Objects.requireNonNull(dialog.getInputEditText()).getText().toString());
                    CommonUtils.closeSoftKeyboard(HomeSettingActivity.this);
                })
                .show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        updateFamily();
    }
}
