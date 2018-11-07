package com.qvd.smartswitch.activity.device;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.activity.user.UserFamilyActivity;
import com.qvd.smartswitch.adapter.FamilyAccountListAdapter;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.user.FamilyListCommonVo;
import com.qvd.smartswitch.model.user.FamilyListVo;
import com.qvd.smartswitch.model.user.RecentSharePeopleListVo;
import com.qvd.smartswitch.utils.ConfigUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DeviceShareFamilyAccountActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    private FamilyAccountListAdapter adapter;
    private final List<FamilyListCommonVo> list = new ArrayList<>();
    private String device_id;
    private String device_type;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_device_share_family_account;
    }

    @Override
    protected void initData() {
        super.initData();
        tvCommonActionbarTitle.setText(R.string.device_share_family_account_title);
        device_id = getIntent().getStringExtra("device_id");
        device_type = getIntent().getStringExtra("device_type");
        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new FamilyAccountListAdapter(list);
        adapter.openLoadAnimation();
        adapter.isFirstOnly(false);
        recyclerview.setAdapter(adapter);

        myEmptyLayout.setTextViewMessage(getString(R.string.device_share_family_account_empty));
        myEmptyLayout.setOnClickListener(v -> {
            startActivity(new Intent(DeviceShareFamilyAccountActivity.this, UserFamilyActivity.class));
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        });
        myErrorLayout.setOnClickListener(v -> getData());

        refreshLayout.setHeaderHeight(100);
        refreshLayout.setEnableHeaderTranslationContent(true);
        refreshLayout.setRefreshHeader(new ClassicsHeader(this));
        refreshLayout.setOnRefreshListener(refreshlayout -> getData());

        adapter.setOnItemClickListener((adapter, view, position) -> {
            FamilyListCommonVo commonVo = list.get(position);
            RecentSharePeopleListVo.DataBean dataBean = new RecentSharePeopleListVo.DataBean();
            dataBean.setShare_object_userid(commonVo.getFamily_userid());
            dataBean.setUser_avatar(commonVo.getFamily_avatar());
            dataBean.setUser_name(commonVo.getFamily_name());
            startActivity(new Intent(DeviceShareFamilyAccountActivity.this, DeviceShareQevdoAccountFinallyActivity.class)
                    .putExtra("person_info", dataBean)
                    .putExtra("is_control", 1)
                    .putExtra("device_id", device_id)
                    .putExtra("device_type", device_type)
                    .putExtra("type", 1));
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        });

        getData();
    }

    /**
     * 获取家人数据
     */
    private void getData() {
        adapter.setEmptyView(myLoadingLayout);
        RetrofitService.qdoApi.getAllFamilyMembers(ConfigUtils.user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<FamilyListVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(FamilyListVo familyListVo) {
                        if (familyListVo.getCode() == 200) {
                            refreshLayout.finishRefresh(true);
                            list.clear();
                            if (familyListVo.getData() != null) {
                                List<FamilyListVo.DataBean.MasterFamilyMembersBean> master_family_members = familyListVo.getData().getMaster_family_members();
                                List<FamilyListVo.DataBean.SlaveFamilyMembersBean> slave_family_members = familyListVo.getData().getSlave_family_members();
                                if (master_family_members != null) {
                                    for (FamilyListVo.DataBean.MasterFamilyMembersBean masterFamilyMembersBean : master_family_members) {
                                        if (masterFamilyMembersBean.getIs_agree() == 1) {
                                            list.add(new FamilyListCommonVo(masterFamilyMembersBean.getFamily_members_id(), masterFamilyMembersBean.getFamily_members_relation(),
                                                    masterFamilyMembersBean.getFamily_members_userid(), masterFamilyMembersBean.getUser_avatar(),
                                                    masterFamilyMembersBean.getUser_name()));
                                        }
                                    }
                                }

                                if (slave_family_members != null) {
                                    for (FamilyListVo.DataBean.SlaveFamilyMembersBean slaveFamilyMembersBean : slave_family_members) {
                                        if (slaveFamilyMembersBean.getIs_agree() == 1) {
                                            list.add(new FamilyListCommonVo(slaveFamilyMembersBean.getFamily_members_id(), "您是他/她的" + slaveFamilyMembersBean.getFamily_members_relation(),
                                                    slaveFamilyMembersBean.getUser_id(), slaveFamilyMembersBean.getUser_avatar(),
                                                    slaveFamilyMembersBean.getUser_name()));
                                        }
                                    }
                                }
                            } else {
                                adapter.setEmptyView(myEmptyLayout);
                                refreshLayout.setEnableRefresh(false);
                            }
                        } else {
                            adapter.setEmptyView(myErrorLayout);
                            refreshLayout.setEnableRefresh(false);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {
                        adapter.setEmptyView(myErrorLayout);
                        refreshLayout.setEnableRefresh(false);
                        adapter.notifyDataSetChanged();
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

    @OnClick(R.id.iv_common_actionbar_goback)
    public void onViewClicked() {
        finish();
    }
}
