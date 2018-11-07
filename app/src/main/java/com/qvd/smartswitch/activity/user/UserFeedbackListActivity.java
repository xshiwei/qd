package com.qvd.smartswitch.activity.user;

import android.content.Intent;
import android.graphics.Canvas;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemSwipeListener;
import com.google.gson.Gson;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.adapter.UserFeedbackListAdapter;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.login.MessageVo;
import com.qvd.smartswitch.model.user.DeleteFeedBackVo;
import com.qvd.smartswitch.model.user.UserFeedbackListVo;
import com.qvd.smartswitch.utils.ConfigUtils;
import com.qvd.smartswitch.utils.ToastUtil;
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
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by Administrator on 2018/6/19 0019.
 */

public class UserFeedbackListActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.smart_refresh)
    SmartRefreshLayout smartRefresh;

    private UserFeedbackListAdapter adapter;
    private final List<UserFeedbackListVo.DataBean> list = new ArrayList<>();

    @Override
    protected int setLayoutId() {
        return R.layout.activity_user_feedback_list;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }

    @Override
    protected void initData() {
        super.initData();
        tvCommonActionbarTitle.setText(R.string.user_feedback_list_title);
        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new UserFeedbackListAdapter(list);
        adapter.openLoadAnimation();
        adapter.isFirstOnly(false);
        ItemDragAndSwipeCallback itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(adapter);
        itemDragAndSwipeCallback.setSwipeMoveFlags(ItemTouchHelper.START | ItemTouchHelper.END);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
        itemTouchHelper.attachToRecyclerView(recyclerview);
        //开启滑动删除
        adapter.enableSwipeItem();
        adapter.setOnItemSwipeListener(onItemSwipeListener);
        recyclerview.setAdapter(adapter);

        smartRefresh.setHeaderHeight(100);
        smartRefresh.setEnableHeaderTranslationContent(true);
        smartRefresh.setRefreshHeader(new ClassicsHeader(this));
        smartRefresh.setOnRefreshListener(refreshlayout -> {
            getData();
            smartRefresh.finishRefresh(1000, true);
        });

        myErrorLayout.setOnClickListener(v -> getData());

        myEmptyLayout.setOnClickListener(v -> getData());

        adapter.setOnItemClickListener((adapter, view, position) -> {
            startActivity(new Intent(UserFeedbackListActivity.this, UserFeedBackDetailsActivity.class)
                    .putExtra("data", list.get(position)));
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    /**
     * 获取数据
     */
    private void getData() {
        adapter.setEmptyView(myLoadingLayout);
        RetrofitService.qdoApi.getUserFeedbackInfo(ConfigUtils.user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UserFeedbackListVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(UserFeedbackListVo userFeedbackListVo) {
                        switch (userFeedbackListVo.getCode()) {
                            case 200:
                                if (userFeedbackListVo.getData() != null) {
                                    list.clear();
                                    list.addAll(userFeedbackListVo.getData());
                                    adapter.notifyDataSetChanged();
                                    smartRefresh.setEnableRefresh(true);
                                } else {
                                    smartRefresh.setEnableRefresh(true);
                                    adapter.setEmptyView(myEmptyLayout);
                                }
                                break;
                            case 201:
                                adapter.setEmptyView(myEmptyLayout);
                                smartRefresh.setEnableRefresh(false);
                                break;
                            default:
                                adapter.setEmptyView(myErrorLayout);
                                smartRefresh.setEnableRefresh(false);
                                break;
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        adapter.setEmptyView(myErrorLayout);
                        smartRefresh.setEnableRefresh(false);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @OnClick(R.id.iv_common_actionbar_goback)
    public void onViewClicked() {
        finish();
    }


    /**
     * 回调接口
     */
    private final OnItemSwipeListener onItemSwipeListener = new OnItemSwipeListener() {
        @Override
        public void onItemSwipeStart(RecyclerView.ViewHolder viewHolder, int pos) {

        }

        @Override
        public void clearView(RecyclerView.ViewHolder viewHolder, int pos) {
            //删除视图成功,pos返回-1
            if (pos == -1) {
                getData();
            }
        }

        @Override
        public void onItemSwiped(RecyclerView.ViewHolder viewHolder, int pos) {
            //在这里作删除，pos为当前实际位置
            deleteFeedback(list.get(pos).getFeedback_id());
        }

        @Override
        public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float dX, float dY, boolean isCurrentlyActive) {
            canvas.drawColor(ContextCompat.getColor(UserFeedbackListActivity.this, R.color.actionbar_blue));
        }
    };

    /**
     * 删除反馈信息
     */
    private void deleteFeedback(String id) {
        Gson gson = new Gson();
        DeleteFeedBackVo feedBackVo = new DeleteFeedBackVo();
        List<String> list = new ArrayList<>();
        list.add(id);
        feedBackVo.setFeedback_id(list);
        String s = gson.toJson(feedBackVo);
        RequestBody body = RequestBody.create(MediaType.parse("Content-Type: application/json"), s);
        RetrofitService.qdoApi.deleteUserFeedbackInfo(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MessageVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MessageVo messageVo) {
                        if (messageVo.getCode() == 200) {
                            ToastUtil.showToast(getString(R.string.common_delete_success));
                        } else {
                            ToastUtil.showToast(getString(R.string.common_delete_fail));
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

}
