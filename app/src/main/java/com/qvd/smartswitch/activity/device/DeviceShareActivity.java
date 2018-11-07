package com.qvd.smartswitch.activity.device;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.adapter.RecentSharePeopleListAdapter;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.user.RecentSharePeopleListVo;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.ConfigUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DeviceShareActivity extends BaseActivity {

    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.tv_manger)
    TextView tvManger;
    @BindView(R.id.ll_qevdo_account)
    LinearLayout llQevdoAccount;
    @BindView(R.id.ll_family)
    LinearLayout llFamily;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;


    private PopupWindow popupWindow;
    private View decorView;

    private String deviceId;
    private String device_type;

    private RecentSharePeopleListAdapter adapter;
    private final List<RecentSharePeopleListVo.DataBean> list = new ArrayList<>();
    private RecentSharePeopleListVo.DataBean dataBean;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_device_share;
    }

    @Override
    protected void initData() {
        super.initData();
        decorView = getWindow().getDecorView();
        deviceId = getIntent().getStringExtra("device_id");
        device_type = getIntent().getStringExtra("device_type");
        tvCommonActionbarTitle.setText(CommonUtils.getDeviceName(device_type));

        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new RecentSharePeopleListAdapter(list);
        adapter.openLoadAnimation();
        adapter.isFirstOnly(false);
        adapter.setHasStableIds(true);
        recyclerview.setAdapter(adapter);

        adapter.setOnItemClickListener((adapter, view, position) -> {
            dataBean = list.get(position);
            showPopupWindow(new DeviceShareQevdoAccountFinallyActivity(), 2);
            popupWindow.showAtLocation(decorView, Gravity.BOTTOM, 0, 0);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }

    @OnClick({R.id.iv_common_actionbar_goback, R.id.tv_manger, R.id.ll_qevdo_account, R.id.ll_family})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.tv_manger:
                //管理
                startActivity(new Intent(this, DeviceShareManageActivity.class)
                        .putExtra("device_id", deviceId)
                        .putExtra("device_type", device_type));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.ll_qevdo_account:
                //科微多账号
                showPopupWindow(new DeviceShareQevdoAccountActivity(), 1);
                popupWindow.showAtLocation(decorView, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.ll_family:
                //家庭账号
                startActivity(new Intent(this, DeviceShareFamilyAccountActivity.class)
                        .putExtra("device_id", deviceId)
                        .putExtra("device_type", device_type));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
        }
    }

    /**
     * 获取数据
     */
    private void getData() {
        RetrofitService.qdoApi.getShareObjectUserInfo(ConfigUtils.user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RecentSharePeopleListVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(RecentSharePeopleListVo recentSharePeopleListVo) {
                        if (recentSharePeopleListVo.getCode() == 200) {
                            if (recentSharePeopleListVo.getData() != null) {
                                list.clear();
                                list.addAll(recentSharePeopleListVo.getData());
                                adapter.notifyDataSetChanged();
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

    private void showPopupWindow(AppCompatActivity activity, int type) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.popupwindow_device_share_selete, null, false);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, false);
        // 需要设置一下此参数，点击外边可消失
        CommonUtils.setBackgroundAlpha(this, 0.5f);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // 设置点击窗口外边窗口消失
        popupWindow.setOutsideTouchable(true);
        // 设置此参数获得焦点，否则无法点击
        popupWindow.setFocusable(true);

        RelativeLayout rl_one = view.findViewById(R.id.rl_one);
        RelativeLayout rl_two = view.findViewById(R.id.rl_two);
        ImageView iv_one = view.findViewById(R.id.iv_one);
        ImageView iv_two = view.findViewById(R.id.iv_two);

        popupWindow.setOnDismissListener(() -> CommonUtils.setBackgroundAlpha(DeviceShareActivity.this, 1.0f));

        rl_one.setOnClickListener(v -> {
            iv_one.setVisibility(View.VISIBLE);
            iv_two.setVisibility(View.INVISIBLE);
            if (type == 1) {
                startActivity(new Intent(DeviceShareActivity.this, activity.getClass())
                        .putExtra("device_id", deviceId)
                        .putExtra("device_type", device_type)
                        .putExtra("is_control", 1));
            } else {
                startActivity(new Intent(DeviceShareActivity.this, activity.getClass())
                        .putExtra("person_info", dataBean)
                        .putExtra("is_control", 1)
                        .putExtra("device_id", deviceId)
                        .putExtra("device_type", device_type)
                        .putExtra("type", dataBean.getPeople_type()));
            }
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            popupWindow.dismiss();
        });

        rl_two.setOnClickListener(v -> {
            iv_one.setVisibility(View.INVISIBLE);
            iv_two.setVisibility(View.VISIBLE);
            if (type == 1) {
                startActivity(new Intent(DeviceShareActivity.this, activity.getClass())
                        .putExtra("device_id", deviceId)
                        .putExtra("device_type", device_type)
                        .putExtra("is_control", 0));
            } else {
                startActivity(new Intent(DeviceShareActivity.this, activity.getClass())
                        .putExtra("person_info", dataBean)
                        .putExtra("is_control", 0)
                        .putExtra("device_id", deviceId)
                        .putExtra("device_type", device_type)
                        .putExtra("type", dataBean.getPeople_type()));
            }
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            popupWindow.dismiss();
        });
    }


}
