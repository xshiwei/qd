package com.qvd.smartswitch.activity.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gyf.barlibrary.ImmersionBar;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.login.LoginVo;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.ConfigUtils;
import com.qvd.smartswitch.utils.SharedPreferencesUtil;
import com.qvd.smartswitch.widget.MyEmptyLayout;
import com.qvd.smartswitch.widget.MyErrorLayout;
import com.qvd.smartswitch.widget.MyLoadingLayout;
import com.trello.rxlifecycle2.components.support.RxFragment;


import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * 所有fragment的基类
 */
public abstract class BaseFragment extends RxFragment {

    protected Activity mActivity;
    private View mRootView;

    protected ImmersionBar mImmersionBar;
    private Unbinder unbinder;

    protected MyEmptyLayout myEmptyLayout;
    protected MyErrorLayout myErrorLayout;
    protected MyLoadingLayout myLoadingLayout;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(setLayoutId(), container, false);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
            initImmersionBar();
        myEmptyLayout = new MyEmptyLayout(getActivity());
        myErrorLayout = new MyErrorLayout(getActivity());
        myLoadingLayout = new MyLoadingLayout(getActivity());
        initData();
        initView();
    }

    /**
     * 自动登录
     */
    public void AutoLogin() {
        String password = SharedPreferencesUtil.getString(getActivity(), SharedPreferencesUtil.PASSWORD);
        String identifier = SharedPreferencesUtil.getString(getActivity(), SharedPreferencesUtil.IDENTIFIER);
        if (!CommonUtils.isEmptyString(password) && !CommonUtils.isEmptyString(identifier)) {
            RetrofitService.qdoApi.login(identifier, password)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<LoginVo>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(LoginVo loginVo) {
                            if (loginVo != null) {
                                if (loginVo.getData() != null && loginVo.getCode() == 200) {
                                    SharedPreferencesUtil.putString(getActivity(), SharedPreferencesUtil.USER_ID, loginVo.getData().getUser_id());
                                    SharedPreferencesUtil.putString(getActivity(), SharedPreferencesUtil.IDENTIFIER, loginVo.getData().getIdentifier());
                                    SharedPreferencesUtil.putString(getActivity(), SharedPreferencesUtil.PASSWORD, loginVo.getData().getPassword());
                                    ConfigUtils.user_id = loginVo.getData().getUser_id();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        if (mImmersionBar != null)
            mImmersionBar.destroy();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden && mImmersionBar != null)
            mImmersionBar.init();
    }

    /**
     * Sets layout id.
     *
     * @return the layout id
     */
    protected abstract int setLayoutId();

    /**
     * 初始化沉浸式
     */
    protected void initImmersionBar() {
        mImmersionBar = ImmersionBar.with(mActivity);
        mImmersionBar.keyboardEnable(true).navigationBarWithKitkatEnable(false).statusBarDarkFont(true, 1).init();
    }


    /**
     * 初始化数据
     */
    protected void initData() {

    }

    /**
     * view与数据绑定
     */
    private void initView() {

    }


    /**
     * 找到activity的控件
     *
     * @param <T> the type parameter
     * @param id  the id
     * @return the t
     */
    @SuppressWarnings("unchecked")
    protected <T extends View> T findActivityViewById(@IdRes int id) {
        return (T) mActivity.findViewById(id);
    }
}
