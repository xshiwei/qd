package com.qvd.smartswitch.activity.home;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.adapter.AddRoomListAdapter;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.home.RoomPicListVo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
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

    private List<RoomPicListVo.DataBean> list = new ArrayList<>();
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
        tvCommonActionbarTitle.setText("更换图标");
        getData();
        recyclerview.setLayoutManager(new GridLayoutManager(this, 4, LinearLayoutManager.VERTICAL, false));
        adapter = new AddRoomListAdapter(this, list);
        recyclerview.setAdapter(adapter);
        adapter.setOnItemClickListener(new AddRoomListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position == list.size() - 1) {
                    //说明点击了最后一个
                } else {
                    setResult(2, new Intent().putExtra("pic", list.get(position).getRoom_min_pic()));
                    finish();
                }
                finish();
            }

            @Override
            public void onItemLongClickListener(View view, int position) {

            }
        });
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
                        list.clear();
                        list.addAll(roomPicListVo.getData());
                        adapter.notifyDataSetChanged();
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
