package com.qvd.smartswitch.activity.user;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.MainActivity;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.adapter.LanguageSeleteAdapter;
import com.qvd.smartswitch.model.home.TestVo;
import com.qvd.smartswitch.utils.LanguageUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

public class LanguageSeleteActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;

    private LanguageSeleteAdapter adapter;
    private final List<TestVo> list = new ArrayList<>();

    @Override
    protected int setLayoutId() {
        return R.layout.activity_language_selete;
    }

    @Override
    protected void initData() {
        super.initData();
        tvCommonActionbarTitle.setText(R.string.language_selete_title);
        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new LanguageSeleteAdapter(list);
        adapter.openLoadAnimation();
        adapter.isFirstOnly(false);
        adapter.setHasStableIds(true);
        recyclerview.setAdapter(adapter);

        myErrorLayout.setOnClickListener(v -> getData());
        getData();
        adapter.setOnItemClickListener((adapter, view, position) -> setLocale(Locale.ENGLISH));
    }

    private void setLocale(Locale myLocale) {
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        LanguageUtils.saveLanguageSetting(this, myLocale);
        refreshSelf();
    }


    private void refreshSelf() {
        finish();
        Intent refresh = new Intent(this, MainActivity.class);
        refresh.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(refresh);
    }

    private void getData() {
        list.clear();
        list.add(new TestVo("简体中文（默认）"));
        list.add(new TestVo("英语"));
        list.add(new TestVo("日语"));
        list.add(new TestVo("俄语"));
        list.add(new TestVo("韩语"));
        list.add(new TestVo("德语"));
        adapter.notifyDataSetChanged();
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
