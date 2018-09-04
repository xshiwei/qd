package com.qvd.smartswitch.activity.home;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
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

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
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
import com.qvd.smartswitch.utils.SharedPreferencesUtil;
import com.qvd.smartswitch.utils.SnackbarUtils;
import com.qvd.smartswitch.utils.ToastUtil;

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
        tvCommonActionbarTitle.setText("家庭设置");
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
        new MaterialDialog.Builder(this)
                .content("删除家庭后，所有已设置的信息将全部清除，不可恢复。是否确认删除家庭?")
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
                        deleteFamily();
                    }
                })
                .show();
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
                                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
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
        new MaterialDialog.Builder(this)
                .title("设置家庭名称")
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
                        CommonUtils.closeSoftKeyboard(HomeSettingActivity.this);
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        tvName.setText(dialog.getInputEditText().getText().toString());
                        CommonUtils.closeSoftKeyboard(HomeSettingActivity.this);
                    }
                })
                .show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        updateFamily();
    }
}
