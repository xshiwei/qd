package com.qvd.smartswitch.activity.device;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.activity.user.UserFeedbackActivity;
import com.qvd.smartswitch.adapter.CommonQuestionAdapter;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.device.DeviceCommonQuestionVo;
import com.qvd.smartswitch.utils.CommonUtils;
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

public class DeviceCommonQuestionActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;

    private List<DeviceCommonQuestionVo.DataBean> list = new ArrayList<>();
    private CommonQuestionAdapter adapter;
    private String type;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_device_common_question;
    }

    @Override
    protected void initData() {
        super.initData();
        type = getIntent().getStringExtra("type");
        tvCommonActionbarTitle.setText(CommonUtils.getDeviceName(type));
        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new CommonQuestionAdapter(list);
        adapter.openLoadAnimation();
        adapter.isFirstOnly(false);
        recyclerview.setAdapter(adapter);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                startActivity(new Intent(DeviceCommonQuestionActivity.this, DeviceCommonQuestionDetailsActivity.class)
                        .putExtra("data", list.get(position)));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
        myEmptyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getQuestion();
            }
        });
        myErrorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getQuestion();
            }
        });
        getQuestion();
    }

    /**
     * 获取常见问题列表
     */
    private void getQuestion() {
        adapter.setEmptyView(myLoadingLayout);
        RetrofitService.qdoApi.getDeviceCommonQuestionList(type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DeviceCommonQuestionVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(DeviceCommonQuestionVo deviceCommonQuestionVo) {
                        if (deviceCommonQuestionVo.getCode() == 200) {
                            if (deviceCommonQuestionVo.getData() != null) {
                                list.clear();
                                list.addAll(deviceCommonQuestionVo.getData());
                                adapter.notifyDataSetChanged();
                            } else {
                                adapter.setEmptyView(myEmptyLayout);
                            }
                        } else {
                            adapter.setEmptyView(myErrorLayout);
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

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }

    @OnClick({R.id.iv_common_actionbar_goback, R.id.rl_question})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.rl_question:
                startActivity(new Intent(this, UserFeedbackActivity.class)
                        .putExtra("type", type));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
        }
    }
}
