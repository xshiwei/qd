package com.qvd.smartswitch.activity.home;

import android.content.Intent;
import android.os.Bundle;
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
import com.qvd.smartswitch.utils.SnackbarUtils;
import com.qvd.smartswitch.widget.EmptyLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
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

    private EmptyLayout emptylayout;


    /**
     * 家庭列表
     */
    private List<HomeListVo.DataBean> list = new ArrayList<>();
    private HomeManageListAdapter adapter;
    private String userId;

    private boolean isHome = false;

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
        tvCommonActionbarTitle.setText("家庭管理");
        emptylayout = findViewById(R.id.emptylayout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        userId = SharedPreferencesUtil.getString(this, SharedPreferencesUtil.USER_ID);
        getHomeList();
        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new HomeManageListAdapter(this, list);
        recyclerview.setAdapter(adapter);
        adapter.setOnItemClickListener(new HomeManageListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                startActivity(new Intent(HomeManageActivity.this, HomeSettingActivity.class)
                        .putExtra("family_id", list.get(position).getFamily_id())
                        .putExtra("family_num", list.size()));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }

            @Override
            public void onItemLongClickListener(View view, int position) {

            }
        });
    }

    @Override
    public void onNetChange(int netMobile) {
        super.onNetChange(netMobile);
        if (netMobile == -1) {
            isHome = false;
        } else {
            isHome = true;
            getHomeList();
        }
    }

    /**
     * 获取家庭列表
     */
    private void getHomeList() {
        CacheSetting.getCache().getHomeManger(RetrofitService.qdoApi.getFamilyList(ConfigUtils.user_id),
                new DynamicKey(userId), new EvictDynamicKey(isHome))
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
                                    emptylayout.hide();
                                }
                            } else {
                                emptylayout.showError();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        emptylayout.showError();
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
