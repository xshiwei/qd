package com.qvd.smartswitch.activity.user;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.adapter.MyFamilyMasterListAdapter;
import com.qvd.smartswitch.adapter.MyFamilySlaveListAdapter;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.login.MessageVo;
import com.qvd.smartswitch.model.user.FamilyListVo;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.ConfigUtils;
import com.qvd.smartswitch.utils.ToastUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/6/15 0015.
 */

public class UserFamilyActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.recyclerview_one)
    RecyclerView recyclerviewOne;
    @BindView(R.id.recyclerview_two)
    RecyclerView recyclerviewTwo;
    @BindView(R.id.smart_refresh)
    SmartRefreshLayout smartRefresh;
    @BindView(R.id.tv_father)
    TextView tvFather;
    @BindView(R.id.tv_mather)
    TextView tvMather;
    @BindView(R.id.tv_son)
    TextView tvSon;
    @BindView(R.id.tv_daughter)
    TextView tvDaughter;
    @BindView(R.id.tv_friend)
    TextView tvFriend;
    @BindView(R.id.tv_other)
    TextView tvOther;

    private MyFamilyMasterListAdapter masterListAdapter;
    private MyFamilySlaveListAdapter slaveListAdapter;

    private List<FamilyListVo.DataBean.MasterFamilyMembersBean> list = new ArrayList<>();
    private List<FamilyListVo.DataBean.SlaveFamilyMembersBean> list2 = new ArrayList<>();
    private PopupWindow popupWindow;
    private String name;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_user_family;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }

    @Override
    protected void initData() {
        super.initData();
        name = getIntent().getStringExtra("name");
        tvCommonActionbarTitle.setText("我的家人");
        recyclerviewOne.setLayoutManager(new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false));
        recyclerviewOne.setNestedScrollingEnabled(true);
        masterListAdapter = new MyFamilyMasterListAdapter(list);
        masterListAdapter.setHasStableIds(true);
        recyclerviewOne.setAdapter(masterListAdapter);

        recyclerviewTwo.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerviewOne.setNestedScrollingEnabled(true);
        slaveListAdapter = new MyFamilySlaveListAdapter(list2);
        slaveListAdapter.setHasStableIds(true);
        recyclerviewTwo.setAdapter(slaveListAdapter);

        smartRefresh.setHeaderHeight(100);
        smartRefresh.setEnableHeaderTranslationContent(true);
        smartRefresh.setRefreshHeader(new ClassicsHeader(this));
        smartRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getData();
                smartRefresh.finishRefresh(1500, true);
            }
        });

        slaveListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (list2.get(position).getIs_agree() == 0) {
                    //还未同意
                    showAgreeDialog(position);
                } else {
                    startActivity(new Intent(UserFamilyActivity.this, FamilyShareDetailsActivity.class)
                            .putExtra("family_id", list2.get(position).getUser_id()));
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
            }
        });

        slaveListAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                if (list2.get(position).getIs_agree() == 1) {
                    showDialogTwo(list2.get(position).getFamily_members_id(), list2.get(position).getFamily_members_relation());
                    popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
                }
                return true;
            }
        });

        masterListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (position == 0) {
                    return;
                } else {
                    if (list.get(position).getIs_agree() == 0) {
                        ToastUtil.showToast("对方还未同意你的邀请");
                    } else {
                        startActivity(new Intent(UserFamilyActivity.this, FamilyShareDetailsActivity.class)
                                .putExtra("family_id", list.get(position).getFamily_members_userid()));
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    }
                }
            }
        });

        masterListAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                if (list.get(position).getIs_agree() == 1) {
                    showDialogTwo(list.get(position).getFamily_members_id(), list.get(position).getFamily_members_relation());
                    popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
                }
                return true;
            }
        });
    }

    /**
     * 展示dialog
     */
    private void showDialogTwo(String id, String name) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.popopwindow_share_family, null, false);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, false);
        // 需要设置一下此参数，点击外边可消失
        CommonUtils.setBackgroundAlpha(this, 0.5f);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // 设置点击窗口外边窗口消失
        popupWindow.setOutsideTouchable(true);
        // 设置此参数获得焦点，否则无法点击
        popupWindow.setFocusable(true);

        TextView tv_one = view.findViewById(R.id.tv_one);
        TextView tv_two = view.findViewById(R.id.tv_two);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                CommonUtils.setBackgroundAlpha(UserFamilyActivity.this, 1.0f);
            }
        });

        tv_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialog(id);
                popupWindow.dismiss();
            }
        });

        tv_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUpdateDialog(id, name);
                popupWindow.dismiss();
            }
        });
    }

    /**
     * 显示修改关系的dialog
     */
    private void showUpdateDialog(String id, String name) {
        new MaterialDialog.Builder(this)
                .title("修改家人关系")
                .inputRange(1, 20, getResources().getColor(R.color.red))
                .input(name, null, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                    }
                })
                .positiveText("确定")
                .negativeText("取消")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        UpdateRelation(id, dialog.getInputEditText().getText().toString());
                    }
                }).show();
    }


    /**
     * 更新关系
     */
    private void UpdateRelation(String id, String name) {
        RetrofitService.qdoApi.updateFamilyMembers(id, name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MessageVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MessageVo messageVo) {
                        if (messageVo.getCode() == 200) {
                            ToastUtil.showToast("修改成功");
                            getData();
                        } else {
                            ToastUtil.showToast("修改失败");
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

    /**
     * 显示解除的dialog
     */
    private void showDeleteDialog(String id) {
        new MaterialDialog.Builder(this)
                .title("你确定要解除和他/她的关系吗")
                .negativeText("取消")
                .positiveText("确定")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        deleteFamily(id);
                    }
                }).show();
    }

    /**
     * 解除家人关系
     */
    private void deleteFamily(String id) {
        RetrofitService.qdoApi.relieveFamilyMembers(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MessageVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MessageVo messageVo) {
                        if (messageVo.getCode() == 200) {
                            getData();
                        } else {
                            ToastUtil.showToast("解除失败");
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

    /**
     * 展示同意的dialog
     */
    private void showAgreeDialog(int position) {
        new MaterialDialog.Builder(this)
                .title("你确定同意这个邀请吗")
                .negativeText("取消")
                .positiveText("确定")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        agreeFamilyRelation(position);
                    }
                }).show();
    }

    /**
     * 同意成为对方家人
     *
     * @param position
     */
    private void agreeFamilyRelation(int position) {
        RetrofitService.qdoApi.agreeFamilyMembersRelation(list2.get(position).getFamily_members_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MessageVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MessageVo messageVo) {
                        if (messageVo.getCode() == 200) {
                            getData();
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
    protected void onResume() {
        super.onResume();
        getData();
    }

    /**
     * 获取数据
     */
    private void getData() {
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
                            list.clear();
                            list2.clear();
                            FamilyListVo.DataBean.MasterFamilyMembersBean membersBean = new FamilyListVo.DataBean.MasterFamilyMembersBean();
                            membersBean.setUser_name(name);
                            membersBean.setIs_agree(1);
                            list.add(membersBean);
                            if (familyListVo.getData() != null) {
                                if (familyListVo.getData().getMaster_family_members() != null) {
                                    list.addAll(familyListVo.getData().getMaster_family_members());
                                }
                                if (familyListVo.getData().getSlave_family_members() != null) {
                                    list2.addAll(familyListVo.getData().getSlave_family_members());
                                }
                            }
                            masterListAdapter.notifyDataSetChanged();
                            slaveListAdapter.notifyDataSetChanged();
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

    @OnClick({R.id.iv_common_actionbar_goback, R.id.tv_father, R.id.tv_mather, R.id.tv_son, R.id.tv_daughter, R.id.tv_friend, R.id.tv_other})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.tv_father:
                startActivity(new Intent(this, AddFamilyActivity.class)
                        .putExtra("name", "爸爸"));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.tv_mather:
                startActivity(new Intent(this, AddFamilyActivity.class)
                        .putExtra("name", "妈妈"));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.tv_son:
                startActivity(new Intent(this, AddFamilyActivity.class)
                        .putExtra("name", "儿子"));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.tv_daughter:
                startActivity(new Intent(this, AddFamilyActivity.class)
                        .putExtra("name", "女儿"));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.tv_friend:
                startActivity(new Intent(this, AddFamilyActivity.class)
                        .putExtra("name", "同居密友"));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.tv_other:
                startActivity(new Intent(this, AddFamilyActivity.class)
                        .putExtra("name", ""));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
        }
    }
}
