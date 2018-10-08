package com.qvd.smartswitch.activity.device;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.activity.qsTwo.QsTwoControlActivity;
import com.qvd.smartswitch.adapter.HomeMenuAdapter;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.device.ScanResultVo;
import com.qvd.smartswitch.model.login.MessageVo;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.ToastUtil;
import com.squareup.picasso.Picasso;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DeviceSplashActivity extends BaseActivity {
    @BindView(R.id.banner)
    Banner banner;

    private List<Integer> images = new ArrayList<>();
    private PopupWindow popupWindow;
    private ScanResultVo resultVo;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_device_splash;
    }

    private int isfirstConnect;

    @Override
    protected void initData() {
        super.initData();
        resultVo = (ScanResultVo) getIntent().getSerializableExtra("scanResult");
        setHomeBanner();
        //展示popupWindow
        View decorView = getWindow().getDecorView();
        decorView.post(new Runnable() {
            @Override
            public void run() {
                showPopupWindow();
                popupWindow.showAtLocation(decorView, Gravity.BOTTOM, 0, 30);
            }
        });
    }

    private void showPopupWindow() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.popupwindow_device_splash, null, false);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, false);
        // 需要设置一下此参数，点击外边可消失
        CommonUtils.setBackgroundAlpha(this, 0.5f);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // 设置点击窗口外边窗口消失
        popupWindow.setOutsideTouchable(false);
        // 设置此参数获得焦点，否则无法点击
        popupWindow.setFocusable(false);

        TextView tv_item = view.findViewById(R.id.tv_item);
        TextView tv_privacy = view.findViewById(R.id.tv_privacy);
        TextView tv_cancel = view.findViewById(R.id.tv_cancel);
        TextView tv_confirm = view.findViewById(R.id.tv_confirm);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                CommonUtils.setBackgroundAlpha(DeviceSplashActivity.this, 1.0f);
            }
        });

        tv_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DeviceSplashActivity.this, DeviceItemActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
        tv_privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DeviceSplashActivity.this, DevicePrivacyActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                finish();
            }
        });
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateConnectState();
                popupWindow.dismiss();
            }
        });

    }

    private void UpdateConnectState() {
        RetrofitService.qdoApi.updateFirstConnectState(resultVo.getDeviceId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MessageVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MessageVo messageVo) {
                        if (messageVo.getCode() == 200) {
                            isfirstConnect = 0;
                        } else {
                            finish();
                            ToastUtil.showToast("授权失败");
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
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }

    /**
     * 设置首页的轮播图
     */
    private void setHomeBanner() {
        //载入ViewPager图片资源
        images.add(R.mipmap.qs_two_guide_one);
        images.add(R.mipmap.qs_two_guide_two);
        images.add(R.mipmap.qs_two_guide_three);
        images.add(R.mipmap.qs_two_guide_four);
        images.add(R.mipmap.device_start_use);
        //设置banner样式
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        //设置图片加载器
        banner.setImageLoader(new PicassoLoader());
        //设置图片集合
        banner.setImages(images);
        //设置banner动画效果
        banner.setBannerAnimation(Transformer.Default);
        //设置自动轮播，默认为true
        banner.isAutoPlay(false);
        //设置指示器位置
        banner.setIndicatorGravity(BannerConfig.CENTER);
        //banner设置方法全部调用完毕时最后调用
        banner.start();
        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                if (position == images.size() - 1) {
                    ScanResultVo resultVo1 = new ScanResultVo(resultVo.getDeviceNo(), resultVo.getDeviceName(), resultVo.getDeviceMac(), resultVo.getConnectType(), isfirstConnect, resultVo.getDeviceId());
                    startActivity(new Intent(DeviceSplashActivity.this, QsTwoControlActivity.class)
                            .putExtra("scanResult", resultVo1));
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    finish();
                }
            }
        });
    }

    public class PicassoLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            Picasso.with(context).load((Integer) path).into(imageView);
        }
    }

}
