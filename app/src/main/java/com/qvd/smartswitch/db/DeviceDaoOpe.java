package com.qvd.smartswitch.db;

import android.content.Context;

import com.qvd.smartswitch.dao.DeviceLogVoDao;
import com.qvd.smartswitch.model.DeviceLogVo;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * Created by Administrator on 2018/4/12 0012.
 */

public class DeviceDaoOpe {
    /**
     * 添加数据至数据库
     *
     * @param context
     * @param dev
     */
    public static void insertData(Context context, DeviceLogVo dev) {
        DbManager.getDaoSession(context).getDeviceLogVoDao().insert(dev);
    }


    /**
     * 将数据实体通过事务添加至数据库
     *
     * @param context
     * @param list
     */
    public static void insertData(Context context, List<DeviceLogVo> list) {
        if (null == list || list.size() <= 0) {
            return;
        }
        DbManager.getDaoSession(context).getDeviceLogVoDao().insertInTx(list);
    }

    /**
     * 添加数据至数据库，如果存在，将原来的数据覆盖
     * 内部代码判断了如果存在就update(entity);不存在就insert(entity)；
     *
     * @param context
     * @param deviceLogVo
     */
    public static void saveData(Context context, DeviceLogVo deviceLogVo) {
        DbManager.getDaoSession(context).getDeviceLogVoDao().save(deviceLogVo);
    }

    /**
     * 删除数据至数据库
     *
     * @param context
     * @param deviceLogVo 删除具体内容
     */
    public static void deleteData(Context context, DeviceLogVo deviceLogVo) {
        DbManager.getDaoSession(context).getDeviceLogVoDao().delete(deviceLogVo);
    }

    /**
     * 根据id删除数据至数据库
     *
     * @param context
     * @param id      删除具体内容
     */
    public static void deleteByKeyData(Context context, long id) {
        DbManager.getDaoSession(context).getDeviceLogVoDao().deleteByKey(id);
    }

    /**
     * 删除全部数据
     *
     * @param context
     */
    public static void deleteAllData(Context context) {
        DbManager.getDaoSession(context).getDeviceLogVoDao().deleteAll();
    }

    /**
     * 更新数据库
     *
     * @param context
     * @param deviceLogVo
     */
    public static void updateData(Context context, DeviceLogVo deviceLogVo) {
        DbManager.getDaoSession(context).getDeviceLogVoDao().update(deviceLogVo);
    }


    /**
     * 查询所有数据
     *
     * @param context
     * @return
     */
    public static List<DeviceLogVo> queryAll(Context context) {
        QueryBuilder<DeviceLogVo> builder = DbManager.getDaoSession(context).getDeviceLogVoDao().queryBuilder();

        return builder.build().list();
    }

    public static List<DeviceLogVo> queryList(Context context, String mac) {
        List<DeviceLogVo> list = DbManager.getDaoSession(context).getDeviceLogVoDao().queryBuilder().where(DeviceLogVoDao.Properties.DeviceId.eq(mac)).build().list();
        if (list == null || list.size() <= 0) {
            return null;
        } else {
            return list;
        }
    }

    public static boolean queryIs(Context context, String mac) {
        List<DeviceLogVo> list = DbManager.getDaoSession(context).getDeviceLogVoDao().queryBuilder().where(DeviceLogVoDao.Properties.DeviceId.eq(mac)).build().list();
        if (list == null || list.size() <= 0) {
            return false;
        } else {
            return true;
        }
    }


    /**
     * 分页加载
     *
     * @param context
     * @param pageSize 当前第几页(程序中动态修改pageSize的值即可)
     * @param pageNum  每页显示多少个
     * @return
     */
    public static List<DeviceLogVo> queryPaging(int pageSize, int pageNum, Context context, String mac) {
        DeviceLogVoDao deviceVoDao = DbManager.getDaoSession(context).getDeviceLogVoDao();
        List<DeviceLogVo> listMsg = deviceVoDao.queryBuilder()
                .where(DeviceLogVoDao.Properties.DeviceId.eq(mac))
                .offset(pageSize * pageNum)
                .limit(pageNum)
                .orderDesc(DeviceLogVoDao.Properties.Id)
                .list();
        return listMsg;
    }
}
