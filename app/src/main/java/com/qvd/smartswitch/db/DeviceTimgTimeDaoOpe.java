package com.qvd.smartswitch.db;

import android.content.Context;

import com.qvd.smartswitch.dao.DeviceLogVoDao;
import com.qvd.smartswitch.dao.DeviceTimgTimeVoDao;
import com.qvd.smartswitch.model.DeviceLogVo;
import com.qvd.smartswitch.model.DeviceTimgTimeVo;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * Created by Administrator on 2018/4/12 0012.
 */

public class DeviceTimgTimeDaoOpe {
    /**
     * 添加数据至数据库
     *
     * @param context
     * @param dev
     */
    public static void insertData(Context context, DeviceTimgTimeVo dev) {
        DbManager.getDaoSession(context).getDeviceTimgTimeVoDao().insert(dev);
    }


    /**
     * 将数据实体通过事务添加至数据库
     *
     * @param context
     * @param list
     */
    public static void insertData(Context context, List<DeviceTimgTimeVo> list) {
        if (null == list || list.size() <= 0) {
            return;
        }
        DbManager.getDaoSession(context).getDeviceTimgTimeVoDao().insertInTx(list);
    }

    /**
     * 添加数据至数据库，如果存在，将原来的数据覆盖
     * 内部代码判断了如果存在就update(entity);不存在就insert(entity)；
     *
     * @param context
     * @param deviceLogVo
     */
    public static void saveData(Context context, DeviceTimgTimeVo deviceLogVo) {
        DbManager.getDaoSession(context).getDeviceTimgTimeVoDao().save(deviceLogVo);
    }

    /**
     * 删除数据至数据库
     *
     * @param context
     * @param deviceLogVo 删除具体内容
     */
    public static void deleteData(Context context, DeviceTimgTimeVo deviceLogVo) {
        DbManager.getDaoSession(context).getDeviceTimgTimeVoDao().delete(deviceLogVo);
    }

    /**
     * 根据id删除数据至数据库
     *
     * @param context
     * @param id      删除具体内容
     */
    public static void deleteByKeyData(Context context, long id) {
        DbManager.getDaoSession(context).getDeviceTimgTimeVoDao().deleteByKey(id);
    }

    /**
     * 删除全部数据
     *
     * @param context
     */
    public static void deleteAllData(Context context) {
        DbManager.getDaoSession(context).getDeviceTimgTimeVoDao().deleteAll();
    }

    /**
     * 更新数据库
     *
     * @param context
     * @param deviceLogVo
     */
    public static void updateData(Context context, DeviceTimgTimeVo deviceLogVo) {
        DbManager.getDaoSession(context).getDeviceTimgTimeVoDao().update(deviceLogVo);
    }


    /**
     * 查询所有数据
     *
     * @param context
     * @return
     */
    public static List<DeviceTimgTimeVo> queryAll(Context context) {
        QueryBuilder<DeviceTimgTimeVo> builder = DbManager.getDaoSession(context).getDeviceTimgTimeVoDao().queryBuilder();

        return builder.build().list();
    }

    public static List<DeviceTimgTimeVo> queryList(Context context, String mac) {
        List<DeviceTimgTimeVo> list = DbManager.getDaoSession(context).getDeviceTimgTimeVoDao().queryBuilder().where(DeviceTimgTimeVoDao.Properties.DeviceId.eq(mac)).build().list();
        if (list == null || list.size() <= 0) {
            return null;
        } else {
            return list;
        }
    }

    public static boolean queryIs(Context context, String mac) {
        List<DeviceTimgTimeVo> list = DbManager.getDaoSession(context).getDeviceTimgTimeVoDao().queryBuilder().where(DeviceTimgTimeVoDao.Properties.DeviceId.eq(mac)).build().list();
        if (list == null || list.size() <= 0) {
            return false;
        } else {
            return true;
        }
    }

}
