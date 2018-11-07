package com.qvd.smartswitch.activity.home;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.adapter.HomeManageListAdapter;
import com.qvd.smartswitch.api.CacheSetting;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.home.HomeListVo;
import com.qvd.smartswitch.utils.ConfigUtils;
import com.qvd.smartswitch.utils.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.EvictDynamicKey;

/**
 * Created by Administrator on 2018/6/13 0013.
 */

public class HomeManageActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.rl_add)
    RelativeLayout rlAdd;

    /**
     * 家庭列表
     */
    private List<HomeListVo.DataBean> list = new ArrayList<>();
    private HomeManageListAdapter adapter;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_home_manage;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }

    @Override
    protected void initData() {
        super.initData();

        tvCommonActionbarTitle.setText(R.string.home_manage_title);
        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new HomeManageListAdapter(list);
        recyclerview.setAdapter(adapter);
        adapter.setOnItemClickListener((adapter, view, position) -> {
            startActivity(new Intent(HomeManageActivity.this, HomeSettingActivity.class)
                    .putExtra("family_id", list.get(position).getFamily_id())
                    .putExtra("family_num", list.size()));
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        });
        myErrorLayout.setOnClickListener(v -> {
            getHomeList();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getHomeList();
    }

    /**
     * 获取家庭列表
     */
    private void getHomeList() {
        RetrofitService.qdoApi.getFamilyList(ConfigUtils.user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HomeListVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HomeListVo homeListVo) {
                        if (homeListVo != null) {
                            if (homeListVo.getCode() == 200) {
                                if (homeListVo.getData() != null) {
                                    list.clear();
                                    list.addAll(homeListVo.getData());
                                    adapter.notifyDataSetChanged();
                                } else {
                                    adapter.setEmptyView(myEmptyLayout);
                                }
                            } else {
                                adapter.setEmptyView(myErrorLayout);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        adapter.setEmptyView(myErrorLayout);
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }


    @OnClick({R.id.iv_common_actionbar_goback, R.id.rl_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.rl_add:
                //添加家庭
                startActivity(new Intent(this, HomeAddActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
        }
    }

}
