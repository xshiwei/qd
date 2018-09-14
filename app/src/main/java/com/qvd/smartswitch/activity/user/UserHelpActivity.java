package com.qvd.smartswitch.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.activity.device.DeviceCommonQuestionActivity;
import com.qvd.smartswitch.adapter.HelpFeedBackListAdapter;
import com.qvd.smartswitch.api.CacheSetting;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.user.HelpFeedbackListVo;
import com.qvd.smartswitch.model.user.UserInfoVo;
import com.qvd.smartswitch.utils.ConfigUtils;

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
 * Created by Administrator on 2018/6/15 0015.
 */

public class UserHelpActivity extends BaseActivity {


    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.iv_message)
    ImageView ivMessage;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.recyclerview_two)
    RecyclerView recyclerviewTwo;

    private HelpFeedBackListAdapter adapter;
    private HelpFeedBackListAdapter adapter2;
    private List<HelpFeedbackListVo.DataBeanX.DataBean> list = new ArrayList<>();
    private List<HelpFeedbackListVo.DataBeanX.DataBean> list2 = new ArrayList<>();

    @Override
    protected int setLayoutId() {
        return R.layout.activity_user_help;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }

    @Override
    protected void initData() {
        super.initData();

        recyclerview.setLayoutManager(new GridLayoutManager(this, 3, LinearLayoutManager.VERTICAL, false));
        recyclerview.setNestedScrollingEnabled(false);
        adapter = new HelpFeedBackListAdapter(this, list);
        recyclerview.setAdapter(adapter);

        recyclerviewTwo.setLayoutManager(new GridLayoutManager(this, 3, LinearLayoutManager.VERTICAL, false));
        recyclerviewTwo.setNestedScrollingEnabled(false);
        adapter2 = new HelpFeedBackListAdapter(this, list2);
        recyclerviewTwo.setAdapter(adapter2);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
        adapter.setOnItemClickListener(new HelpFeedBackListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                startActivity(new Intent(UserHelpActivity.this, DeviceCommonQuestionActivity.class)
                        .putExtra("type", list.get(position).getDevice_no()));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }

            @Override
            public void onItemLongClickListener(View view, int position) {

            }
        });
        adapter2.setOnItemClickListener(new HelpFeedBackListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                startActivity(new Intent(UserHelpActivity.this, DeviceCommonQuestionActivity.class)
                        .putExtra("type", list2.get(position).getDevice_no()));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }

            @Override
            public void onItemLongClickListener(View view, int position) {

            }
        });
    }

    /**
     * 获取数据
     */
    private void getData() {
        CacheSetting.getCache().getUserHelpFeedbackInfo(RetrofitService.qdoApi.getUserFeedbackCategoryInfo(ConfigUtils.user_id), new DynamicKey(ConfigUtils.user_id), new EvictDynamicKey(true))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HelpFeedbackListVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HelpFeedbackListVo helpFeedbackListVo) {
                        if (helpFeedbackListVo.getCode() == 200) {
                            if (helpFeedbackListVo.getData() != null) {
                                List<HelpFeedbackListVo.DataBeanX> data = helpFeedbackListVo.getData();
                                List<HelpFeedbackListVo.DataBeanX.DataBean> data1 = data.get(0).getData();
                                List<HelpFeedbackListVo.DataBeanX.DataBean> data2 = data.get(1).getData();
                                if (data1 != null) {
                                    list.clear();
                                    list.addAll(data1);
                                    adapter.notifyDataSetChanged();
                                }
                                if (data2 != null) {
                                    list2.clear();
                                    list2.addAll(data2);
                                    adapter2.notifyDataSetChanged();
                                }
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


    @OnClick({R.id.iv_common_actionbar_goback, R.id.iv_message})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.iv_message:
                startActivity(new Intent(this, UserFeedbackListActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
        }
    }
}
