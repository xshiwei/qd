package com.qvd.smartswitch.activity.device;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.clj.fastble.data.BleDevice;
import com.qvd.smartswitch.R;
import com.qvd.smartswitch.activity.base.BaseActivity;
import com.qvd.smartswitch.db.DeviceNickNameDaoOpe;
import com.qvd.smartswitch.model.DeviceNickNameVo;
import com.qvd.smartswitch.utils.CommonUtils;
import com.qvd.smartswitch.utils.ToastUtil;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2018/4/16 0016.
 */

public class DeviceUpdatePicActivity extends BaseActivity {


    @BindView(R.id.iv_common_actionbar_goback)
    ImageView ivCommonActionbarGoback;
    @BindView(R.id.tv_common_actionbar_title)
    TextView tvCommonActionbarTitle;
    @BindView(R.id.tv_device_update_pic_save)
    TextView tvDeviceUpdatePicSave;
    @BindView(R.id.iv_device_update_pic)
    CircleImageView ivDeviceUpdatePic;
    @BindView(R.id.tv_device_update_pic_image)
    TextView tvDeviceUpdatePicImage;
    @BindView(R.id.tv_device_update_pic_camera)
    TextView tvDeviceUpdatePicCamera;
    @BindView(R.id.ll_device_pic_one)
    LinearLayout llDevicePicOne;

    //调用系统相册-选择图片
    private static final int IMAGE = 1;
    //调用相机拍照
    private static final int CAMERA = 2;


    private BleDevice bledevice;

    private String path;
    private DeviceNickNameVo deviceNickNameVo1;


    @Override
    protected int setLayoutId() {
        return R.layout.activity_device_update_pic;
    }

    @OnClick({R.id.iv_common_actionbar_goback, R.id.tv_device_update_pic_save, R.id.tv_device_update_pic_image, R.id.tv_device_update_pic_camera,R.id.ll_device_pic_one})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_common_actionbar_goback:
                finish();
                break;
            case R.id.tv_device_update_pic_save:
                saveDrawable();
                break;
            case R.id.tv_device_update_pic_image:
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMAGE);
                break;
            case R.id.tv_device_update_pic_camera:
                Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera, CAMERA);
                break;
            case R.id.ll_device_pic_one:
                path = "file:///android_asset/device_type_one.png";
                Picasso.with(this).load(path).into(ivDeviceUpdatePic);
                break;
        }
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.fitsSystemWindows(true).statusBarColor(R.color.app_color).init();
    }

    @Override
    protected void initData() {
        super.initData();
        tvCommonActionbarTitle.setText("更换图标");
        bledevice = getIntent().getParcelableExtra("bledevice");
        if (bledevice!=null){
            deviceNickNameVo1 = DeviceNickNameDaoOpe.queryOne(this, CommonUtils.getMac(bledevice.getMac()));
            String pic = deviceNickNameVo1.getPic();
            if (CommonUtils.isEmptyString(pic)) {
                path = "file:///android_asset/qvedo_app_icon.png";
            } else {
                path = pic;
            }
            Picasso.with(this).load(path).into(ivDeviceUpdatePic);
        }
    }

    /**
     * 保存图片
     */
    private void saveDrawable() {
        DeviceNickNameVo deviceNickNameVo = new DeviceNickNameVo(deviceNickNameVo1.getId(), deviceNickNameVo1.getDeviceId()
                , deviceNickNameVo1.getDeviceName(), CommonUtils.getDate(), deviceNickNameVo1.getDeviceNickname(), path, deviceNickNameVo1.getType());
        DeviceNickNameDaoOpe.updateData(this, deviceNickNameVo);
        ToastUtil.showToast("保存成功");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //获取图片路径
        if (requestCode == IMAGE && resultCode == this.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumns = {MediaStore.Images.Media.DATA};
            Cursor c = this.getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            String imagePath = c.getString(columnIndex);
            path = "file://" + imagePath;
            Bitmap bm = BitmapFactory.decodeFile(imagePath);
            ivDeviceUpdatePic.setImageBitmap(bm);
            c.close();
        }

        //调用相机
        if (requestCode == CAMERA && resultCode == this.RESULT_OK && data != null) {
            String sdState = Environment.getExternalStorageState();
            if (!sdState.equals(Environment.MEDIA_MOUNTED)) {
                return;
            }
            String name = CommonUtils.getPicDate() + ".jpg";
            Bundle bundle = data.getExtras();
            //获取相机返回的数据，并转换为图片格式
            Bitmap bitmap = (Bitmap) bundle.get("data");
            FileOutputStream fout = null;
            File file = new File("/sdcard/qvd/image");
            file.mkdirs();
            String filename = file.getPath() + "/" + name;
            path = "file://" + filename;
            try {
                fout = new FileOutputStream(filename);
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
            ivDeviceUpdatePic.setImageBitmap(bitmap);
        }
    }



}
