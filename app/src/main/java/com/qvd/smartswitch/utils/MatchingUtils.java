package com.qvd.smartswitch.utils;

/**
 * Created by Administrator on 2018/4/13 0013.
 */

public class MatchingUtils {
    /**
     * 匹配日志中的设备状态
     *
     * @param state
     * @return
     */
    public static String getLogDeviceState(int state) {
        String str = "";
        switch (state) {
            case 0:
                str = "关";
                break;
            case 1:
                str = "开";
                break;
        }
        return str;
    }

    /**
     * 匹配日志中的设备类型
     *
     * @param type
     * @return
     */
    public static String getLogDeviceType(int type) {
        String str = "";
        switch (type) {
            case 1:
                str = "蓝牙";
                break;
            case 2:
                str = "wifi";
                break;
        }
        return str;
    }

}
