package com.qvd.smartswitch.activity.user;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.activity.device.DeviceShareFamilyActivity;
import com.qvd.smartswitch.adapter.FamilyShareDeviceListAdapter;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.device.FamilyDetailsVo;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.ConfigUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class FamilyShareDetailsActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.civ_portrait)
    CircleImageView civPortrait;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_share_num)
    TextView tvShareNum;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.ll_add_share)
    LinearLayout llAddShare;


    private String family_id;
    private FamilyShareDeviceListAdapter adapter;
    private final List<FamilyDetailsVo.DataBean.ShareDevicesDataBean> list = new ArrayList<>();


    @Override
    protected int setLayoutId() {
        return R.layout.activity_family_share_details;
    }

    @Override
    protected void initData() {
        super.initData();
        tvCommonActionbarTitle.setText(R.string.family_share_details_title);
        family_id = getIntent().getStringExtra("family_id");
        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new FamilyShareDeviceListAdapter(list);
        adapter.isFirstOnly(true);
        adapter.openLoadAnimation();
        recyclerview.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    /**
     * 获取共享设备列表
     */
    private void getData() {
        RetrofitService.qdoApi.addGetFamilyMembersDetailInfo(family_id, ConfigUtils.user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<FamilyDetailsVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(FamilyDetailsVo familyDetailsVo) {
                        if (familyDetailsVo.getCode() == 200) {
                            if (familyDetailsVo.getData() != null) {
                                tvName.setText(familyDetailsVo.getData().getUser_name());
                                tvShareNum.setText("已共享" + familyDetailsVo.getData().getShare_devices_count() + "个设备");
                                if (!CommonUtils.isEmptyString(familyDetailsVo.getData().getUser_avatar())) {
                                    Glide.with(FamilyShareDetailsActivity.this).load(familyDetailsVo.getData().getUser_avatar()).into(civPortrait);
                                }
                                if (familyDetailsVo.getData().getShare_devices_data() != null) {
                                    list.addAll(familyDetailsVo.getData().getShare_devices_data());
                                    adapter.notifyDataSetChanged();
                                }
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

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }

    @OnClick({R.id.iv_common_actionbar_goback, R.id.ll_add_share})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.ll_add_share:
                startActivity(new Intent(this, DeviceShareFamilyActivity.class)
                        .putExtra("family_id", family_id));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
        }
    }
}
