package com.qvd.smartswitch.activity.user;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;
import com.qiniu.android.common.AutoZone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.activity.base.BaseHandler;
import com.qvd.smartswitch.activity.base.BaseRunnable;
import com.qvd.smartswitch.activity.login.LoginActivity;
import com.qvd.smartswitch.api.CacheSetting;
import com.qvd.smartswitch.api.RetrofitService;
import com.qvd.smartswitch.model.login.MessageVo;
import com.qvd.smartswitch.model.user.QiNiuPicTokenVo;
import com.qvd.smartswitch.model.user.UserInfoVo;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.ConfigUtils;
import com.qvd.smartswitch.utils.PermissionUtils;
import com.qvd.smartswitch.utils.RegexpUtils;
import com.qvd.smartswitch.utils.SharedPreferencesUtil;
import com.qvd.smartswitch.utils.ToastUtil;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.EvictDynamicKey;

/**
 * Created by Administrator on 2018/6/15 0015.
 */

public class UserInformationActivity extends BaseActivity {
    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.tv_logout)
    TextView tvLogout;
    @BindView(R.id.iv_one)
    ImageView ivOne;
    @BindView(R.id.tv_account)
    TextView tvAccount;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.rl_name)
    RelativeLayout rlName;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.rl_phone)
    RelativeLayout rlPhone;
    @BindView(R.id.cir_head_portrait)
    CircleImageView circleImageView;
    @BindView(R.id.iv_three)
    ImageView ivThree;
    private MaterialDialog dialog;


    private boolean isUpdate = false;
    private String userId;

    //调用系统相册-选择图片
    private static final int IMAGE = 1;

    private static final int CAMERA = 2;

    //图片的路径
    private String imagePath;

    private boolean isUpdatePic = false;

    private UploadManager uploadManager;

    private MaterialDialog materDialog;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_user_information;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.white).init();
    }

    private final MyHandler myHandler = new MyHandler(this);

    private static class MyHandler extends BaseHandler<UserInformationActivity> {

        protected MyHandler(UserInformationActivity reference) {
            super(reference);
        }

        @Override
        protected void handleMessage(UserInformationActivity reference, Message msg) {

        }
    }

    @Override
    protected void initData() {
        super.initData();
        userId = SharedPreferencesUtil.getString(this, SharedPreferencesUtil.USER_ID);
        tvCommonActionbarTitle.setText(R.string.user_information_title);
        Configuration config = new Configuration.Builder()
                .chunkSize(512 * 1024)        // 分片上传时，每片的大小。 默认256K
                .putThreshhold(1024 * 1024)   // 启用分片上传阀值。默认512K
                .connectTimeout(10)           // 链接超时。默认10秒
                .useHttps(false)               // 是否使用https上传域名
                .responseTimeout(60)          // 服务器响应超时。默认60秒
                .zone(AutoZone.autoZone)
                .build();
        uploadManager = new UploadManager(config);
        getUserInfo();
    }

    private void getUserInfo() {
        CacheSetting.getCache().getUserInfo(RetrofitService.qdoApi.getUserInfo(userId),
                new DynamicKey(userId), new EvictDynamicKey(false))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UserInfoVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(UserInfoVo userInfoVo) {
                        if (userInfoVo != null) {
                            if (userInfoVo.getCode() == 200) {
                                if (userInfoVo.getData() != null) {
                                    tvName.setText(userInfoVo.getData().getUser_name());
                                    tvAccount.setText(userInfoVo.getData().getUser_account());
                                    if (!CommonUtils.isEmptyString(userInfoVo.getData().getUser_phone().toString())) {
                                        tvPhone.setText(userInfoVo.getData().getUser_phone() + "");
                                        rlPhone.setEnabled(false);
                                        ivThree.setVisibility(View.INVISIBLE);
                                    } else {
                                        tvPhone.setHint(R.string.user_information_binding_phone);
                                    }
                                    Glide.with(UserInformationActivity.this).load(userInfoVo.getData().getUser_avatar()).into(circleImageView);
                                    imagePath = userInfoVo.getData().getUser_avatar();
                                } else {
                                    tvName.setHint("_ _");
                                    tvAccount.setHint("_ _");
                                    tvPhone.setHint("_ _");
                                }
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

    /**
     * 保存个人信息
     */
    private void saveUserInfo(String pic) {
        RetrofitService.qdoApi.updateuserInfo(userId, tvName.getText().toString(), tvPhone.getText().toString(), pic)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MessageVo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MessageVo messageVo) {
                        if (messageVo.getCode() == 200) {
                            ToastUtil.showToast(getString(R.string.common_update_two_success));
                            materDialog.dismiss();
                            finish();
                        } else {
                            ToastUtil.showToast(getString(R.string.common_update_two_fail));
                            materDialog.dismiss();
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

    @OnClick({R.id.iv_common_actionbar_goback, R.id.tv_logout, R.id.rl_name, R.id.rl_phone, R.id.rl_update_password, R.id.rl_portrait})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                if (userId != null) {
                    if (isUpdate) {
                        showConfirmSaveDialog();
                    } else {
                        finish();
                    }
                } else {
                    finish();
                }
                break;
            case R.id.rl_portrait:
                setUserHeadPortrait();
                break;
            case R.id.tv_logout:
                dialog = new MaterialDialog.Builder(this)
                        .content(R.string.common_exiting)
                        .progress(true, 0)
                        .autoDismiss(false)
                        .show();
                SharedPreferencesUtil.putString(this, SharedPreferencesUtil.IDENTIFIER, null);
                SharedPreferencesUtil.putString(this, SharedPreferencesUtil.PASSWORD, null);
                SharedPreferencesUtil.putString(this, SharedPreferencesUtil.USER_ID, null);
                myHandler.postDelayed(new BaseRunnable(() -> {
                    dialog.dismiss();
                    startActivity(new Intent(UserInformationActivity.this, LoginActivity.class));
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    ConfigUtils.user_id = "";
                    finish();
                }), 3000);
                break;
            case R.id.rl_name:
                showUpdateNameDialog();
                break;
            case R.id.rl_phone:
                showUpdatePhoneDialog();
                break;
            case R.id.rl_update_password:
                //修改密码
                startActivity(new Intent(this, UpdatePasswordActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
        }
    }

    private void setUserHeadPortrait() {
        new MaterialDialog.Builder(this)
                .content("设置用户头像")
                .items(R.array.head_portrait)
                .itemsColor(getResources().getColor(R.color.app_red_color))
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                        switch (position) {
                            case 0:
                                Intent intent = new Intent(Intent.ACTION_PICK,
                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(intent, IMAGE);
                                break;
                            case 1:
                                PermissionUtils.requestPermission(UserInformationActivity.this, Permission.CAMERA);
                                if (AndPermission.hasPermissions(UserInformationActivity.this, Permission.CAMERA)) {
                                    Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    startActivityForResult(camera, CAMERA);
                                }
                                break;
                        }
                    }
                })
                .negativeText("取消")
                .show();
    }

    private void showUpdateNameDialog() {
        new MaterialDialog.Builder(this)
                .content(R.string.user_information_update_name)
                .negativeText(R.string.common_cancel)
                .positiveText(R.string.common_confirm)
                .inputRange(1, 20, getResources().getColor(R.color.red))
                .input(tvName.getText().toString(), null, false, (dialog, input) -> {

                })
                .onNegative((dialog, which) -> CommonUtils.closeSoftKeyboard(UserInformationActivity.this))
                .onPositive((dialog, which) -> {
                    tvName.setText(Objects.requireNonNull(dialog.getInputEditText()).getText().toString());
                    CommonUtils.closeSoftKeyboard(UserInformationActivity.this);
                    isUpdate = true;
                })
                .show();
    }

    private void showUpdatePhoneDialog() {
        new MaterialDialog.Builder(this)
                .title(R.string.user_information_binding_phone)
                .content("手机号码一经绑定，不允许解绑或更换其他的手机号")
                .negativeText(R.string.common_cancel)
                .positiveText(R.string.common_confirm)
                .inputType(InputType.TYPE_CLASS_PHONE)
                .inputRange(11, 20, getResources().getColor(R.color.red))
                .input(tvPhone.getText().toString(), null, false, (dialog, input) -> {

                })
                .onNegative((dialog, which) -> CommonUtils.closeSoftKeyboard(UserInformationActivity.this))
                .onPositive((dialog, which) -> {
                    if (RegexpUtils.isMobileNO(Objects.requireNonNull(dialog.getInputEditText()).getText().toString())) {
                        tvPhone.setText(Objects.requireNonNull(dialog.getInputEditText()).getText().toString());
                        CommonUtils.closeSoftKeyboard(UserInformationActivity.this);
                        isUpdate = true;
                    } else {
                        ToastUtil.showToast(getString(R.string.common_phone_type_error));
                    }

                })
                .show();
    }

    @Override
    public void onBackPressed() {
        if (userId != null) {
            if (isUpdate) {
                showConfirmSaveDialog();
            } else {
                finish();
            }
        } else {
            finish();
        }
    }

    /**
     * 展示保存的dialog
     */
    private void showConfirmSaveDialog() {
        new MaterialDialog.Builder(this)
                .content("你确定要保存这些修改吗")
                .negativeText("取消")
                .positiveText("确定")
                .onPositive((dialog, which) -> {
                    materDialog = new MaterialDialog.Builder(UserInformationActivity.this)
                            .content("正在保存个人信息")
                            .progress(true, 0)
                            .autoDismiss(false)
                            .show();
                    if (isUpdatePic) {
                        RetrofitService.qdoApi.getQiNiuToken()
                                .subscribeOn(Schedulers.io())
                                .filter(qiNiuPicTokenVo -> qiNiuPicTokenVo.getCode() == 200)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<QiNiuPicTokenVo>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onNext(QiNiuPicTokenVo qiNiuPicTokenVo) {
                                        uploadManager.put(imagePath, imagePath + "avatar", qiNiuPicTokenVo.getUploadToken(), new UpCompletionHandler() {
                                            @Override
                                            public void complete(String key, ResponseInfo info, JSONObject response) {
                                                if (info.isOK()) {
                                                    saveUserInfo("http://pckgfzc5s.bkt.clouddn.com/" + imagePath + "avatar");
                                                } else {
                                                    materDialog.dismiss();
                                                    ToastUtil.showToast("保存失败");
                                                }
                                                Logger.e("key==" + key + "info==" + info + "response== " + response);
                                            }
                                        }, null);
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        materDialog.dismiss();
                                        ToastUtil.showToast("保存失败");
                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                });
                    } else {
                        saveUserInfo(imagePath);
                    }
                })
                .onNegative((dialog, which) -> finish())
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //获取图片路径
        if (requestCode == IMAGE && resultCode == this.RESULT_OK && data != null) {
            isUpdatePic = true;
            Uri selectedImage = data.getData();
            String[] filePathColumns = {MediaStore.Images.Media.DATA};
            Cursor c = this.getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            imagePath = c.getString(columnIndex);
            circleImageView.setImageBitmap(BitmapFactory.decodeFile(imagePath));
            c.close();
        }
        //调用相机
        else if (requestCode == CAMERA && resultCode == this.RESULT_OK && data != null) {
            isUpdatePic = true;
            String sdState = Environment.getExternalStorageState();
            if (!sdState.equals(Environment.MEDIA_MOUNTED)) {
                return;
            }
            new DateFormat();
            String name = DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";
            Bundle bundle = data.getExtras();
            //获取相机返回的数据，并转换为图片格式
            Bitmap bitmap = (Bitmap) bundle.get("data");
            FileOutputStream fout = null;
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "MyApp" + File.separator);
            file.mkdirs();
            imagePath = file.getPath() + name;
            try {
                fout = new FileOutputStream(imagePath);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fout);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    fout.flush();
                    fout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            circleImageView.setImageBitmap(bitmap);
        }
    }
}
