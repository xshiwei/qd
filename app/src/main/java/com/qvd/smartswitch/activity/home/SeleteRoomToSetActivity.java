package com.qvd.smartswitch.activity.home;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.adapter.RoomManageListAdapter;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.home.RoomListVo;
import com.qvd.smartswitch.model.login.MessageVo;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.ConfigUtils;
import com.qvd.smartswitch.utils.ToastUtil;
import com.qvd.smartswitch.widget.EmptyLayout;
import com.qvd.smartswitch.widget.MyPopupWindowThree;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SeleteRoomToSetActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.iv_add_room)
    ImageView ivAddRoom;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.emptylayout)
    EmptyLayout emptylayout;

    private List<RoomListVo.DataBean> list = new ArrayList<>();
    private RoomManageListAdapter adapter;
    private MyPopupWindowThree popupWindowName;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_selete_room_to_set;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }

    @Override
    protected void initData() {
        super.initData();
        getRoomList();
        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new RoomManageListAdapter(this, list);
        recyclerview.setAdapter(adapter);
        adapter.setOnItemClickListener(new RoomManageListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent();
                intent.putExtra("room_id", list.get(position).getRoom_id());
                intent.putExtra("room_name", list.get(position).getRoom_name());
                setResult(2, intent);
                finish();
            }

            @Override
            public void onItemLongClickListener(View view, int position) {

            }
        });
    }

    @OnClick({R.id.iv_common_actionbar_goback, R.id.iv_add_room})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.iv_add_room:
                showAddRoomPopupWindow();
                popupWindowName.showPopupWindow(view);
                break;
        }
    }

    /**
     * 展示添加房间
     */
    private void showAddRoomPopupWindow() {
        popupWindowName = new MyPopupWindowThree(this, "添加属于您的房间", "", new MyPopupWindowThree.IPopupWindowListener() {
            @Override
            public void cancel() {
                popupWindowName.dismiss();
                CommonUtils.closeSoftKeyboard(SeleteRoomToSetActivity.this);
            }

            @Override
            public void confirm() {
                EditText etEditText = popupWindowName.getEtEditText();
                addRoom(etEditText.getText().toString());
                popupWindowName.dismiss();
                adapter.notifyDataSetChanged();
                CommonUtils.closeSoftKeyboard(SeleteRoomToSetActivity.this);
            }
        });
        final EditText etEditText = popupWindowName.getEtEditText();
        final TextView tvConfirm = popupWindowName.getTvConfirm();
        final TextView tvError = popupWindowName.getTvError();

        if (etEditText.getText().toString().equals("")) {
            tvConfirm.setEnabled(false);
            tvConfirm.setTextColor(etEditText.getResources().getColor(R.color.home_setting_text_three));
        }

        //判断当前输入是否符合要求
        etEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 20) {
                    tvError.setVisibility(View.VISIBLE);
                    tvError.setText("长度超过最大");
                    tvConfirm.setEnabled(false);
                    tvConfirm.setTextColor(getResources().getColor(R.color.home_setting_text_three));
                } else if (s.toString().length() == 0) {
                    tvConfirm.setEnabled(false);
                    tvConfirm.setTextColor(getResources().getColor(R.color.home_setting_text_three));
                    etEditText.setCursorVisible(false);
                } else {
                    tvError.setVisibility(View.GONE);
                    tvConfirm.setEnabled(true);
                    tvConfirm.setTextColor(getResources().getColor(R.color.popupwindow_confirm_text));
                }
            }
        });
    }


    /**
     * 添加房间
     */
    private void addRoom(String name) {
        RetrofitService.qdoApi.addRoom(name, "ssss", ConfigUtils.family_locate.getFamily_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MessageVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MessageVo messageVo) {
                        if (messageVo.getCode() == 200) {
                            ToastUtil.showToast("添加成功");
                            getRoomList();
                        } else if (messageVo.getCode() == 400) {
                            ToastUtil.showToast("添加失败");
                        } else {
                            ToastUtil.showToast("添加超时");
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

    /**
     * 获取房间列表
     */
    private void getRoomList() {
        RetrofitService.qdoApi.getRoomList(ConfigUtils.family_locate.getFamily_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RoomListVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(RoomListVo roomListVo) {
                        if (roomListVo != null) {
                            list.clear();
                            if (roomListVo.getCode() == 200) {
                                emptylayout.hide();
                                list.addAll(roomListVo.getData());
                                adapter.notifyDataSetChanged();
                            } else if (roomListVo.getCode() == 400) {
                                emptylayout.showEmpty();
                            } else {
                                emptylayout.showEmpty();
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
}
