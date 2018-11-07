package com.qvd.smartswitch.activity.home;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.adapter.AddRoomListAdapter;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.home.RoomPicListVo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RoomPicListActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.tv_text)
    TextView tvText;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;

    private final List<RoomPicListVo.DataBean> list = new ArrayList<>();
    private AddRoomListAdapter adapter;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_room_pic_list;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }

    @Override
    protected void initData() {
        super.initData();
        tvCommonActionbarTitle.setText(R.string.room_pic_list_title);
        recyclerview.setLayoutManager(new GridLayoutManager(this, 4, LinearLayoutManager.VERTICAL, false));
        adapter = new AddRoomListAdapter(list);
        adapter.openLoadAnimation();
        adapter.isFirstOnly(false);
        adapter.setHasStableIds(true);
        recyclerview.setAdapter(adapter);
        adapter.setOnItemClickListener((adapter, view, position) -> {
            if (position == list.size() - 1) {
                //说明点击了最后一个
            } else {
                setResult(2, new Intent().putExtra("pic", list.get(position).getRoom_min_pic()));
                finish();
            }
            finish();
        });
        myEmptyLayout.setTextViewMessage(getString(R.string.common_server_error));
        myErrorLayout.setOnClickListener(v -> getData());
        getData();
    }


    private void getData() {
        RetrofitService.qdoApi.getRoomPicList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RoomPicListVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(RoomPicListVo roomPicListVo) {
                        if (roomPicListVo.getCode() == 200) {
                            if (roomPicListVo.getData() != null && roomPicListVo.getData().size() > 0) {
                                list.clear();
                                list.addAll(roomPicListVo.getData());
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

    @OnClick(R.id.iv_common_actionbar_goback)
    public void onViewClicked() {
        finish();
    }
}
