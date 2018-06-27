package com.qvd.smartswitch.db;

import android.content.Context;

import com.qvd.smartswitch.dao.DeviceNickNameVoDao;
import com.qvd.smartswitch.model.DeviceNickNameVo;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * Created by Administrator on 2018/4/12 0012.
 */

public class DeviceNickNameDaoOpe {
    /**
     * 添加数据至数据库
     *
     * @param context
     * @param dev
     */
    public static void insertData(Context context, DeviceNickNameVo dev) {
        DbManager.getDaoSession(context).getDeviceNickNameVoDao().insert(dev);
    }


    /**
     * 将数据实体通过事务添加至数据库
     *
     * @param context
     * @param list
     */
    public static void insertData(Context context, List<DeviceNickNameVo> list) {
        if (null == list || list.size() <= 0) {
            return;
        }
        DbManager.getDaoSession(context).getDeviceNickNameVoDao().insertInTx(list);
    }

    /**
     * 添加数据至数据库，如果存在，将原来的数据覆盖
     * 内部代码判断了如果存在就update(entity);不存在就insert(entity)；
     *
     * @param context
     * @param deviceVo
     */
    public static void saveData(Context context, DeviceNickNameVo deviceVo) {
        DbManager.getDaoSession(context).getDeviceNickNameVoDao().save(deviceVo);
    }

    /**
     * 删除数据至数据库
     *
     * @param context
     * @param deviceVo 删除具体内容
     */
    public static void deleteData(Context context, DeviceNickNameVo deviceVo) {
        DbManager.getDaoSession(context).getDeviceNickNameVoDao().delete(deviceVo);
    }

    /**
     * 根据id删除数据至数据库
     *
     * @param context
     * @param id      删除具体内容
     */
    public static void deleteByKeyData(Context context, long id) {
        DbManager.getDaoSession(context).getDeviceNickNameVoDao().deleteByKey(id);
    }

    /**
     * 删除全部数据
     *
     * @param context
     */
    public static void deleteAllData(Context context) {
        DbManager.getDaoSession(context).getDeviceNickNameVoDao().deleteAll();
    }

    /**
     * 更新数据库
     *
     * @param context
     * @param deviceNickNameVo
     */
    public static void updateData(Context context, DeviceNickNameVo deviceNickNameVo) {
        DbManager.getDaoSession(context).getDeviceNickNameVoDao().update(deviceNickNameVo);
    }


    /**
     * 查询所有数据
     *
     * @param context
     * @return
     */
    public static List<DeviceNickNameVo> queryAll(Context context) {
        QueryBuilder<DeviceNickNameVo> builder = DbManager.getDaoSession(context).getDeviceNickNameVoDao().queryBuilder();
        return builder.build().list();
    }


    public static DeviceNickNameVo queryOne(Context context, String mac) {
        List<DeviceNickNameVo> list = DbManager.getDaoSession(context).getDeviceNickNameVoDao().queryBuilder().where(DeviceNickNameVoDao.Properties.DeviceId.eq(mac)).build().list();
        if (list == null || list.size() <= 0) {
            return null;
        } else {
            return list.get(0);
        }
    }

    public static boolean queryIs(Context context, String mac) {
        List<DeviceNickNameVo> list = DbManager.getDaoSession(context).getDeviceNickNameVoDao().queryBuilder().where(DeviceNickNameVoDao.Properties.DeviceId.eq(mac)).build().list();
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
    public static List<DeviceNickNameVo> queryPaging(int pageSize, int pageNum, Context context) {
        DeviceNickNameVoDao deviceNickNameVo = DbManager.getDaoSession(context).getDeviceNickNameVoDao();
        List<DeviceNickNameVo> listMsg = deviceNickNameVo.queryBuilder()
                .offset(pageSize * pageNum).limit(pageNum).list();
        return listMsg;
    }
}
