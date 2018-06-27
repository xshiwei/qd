package com.qvd.smartswitch.model.home;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/6/7 0007.
 */

public class HomeContentUtils {

    public static List<Object> getDataAfterHandle(List<Test1Vo> list) {
        List<Object> dataList = new ArrayList<>();
        //遍历服务器回传的数据
        for (Test1Vo test1Vo : list) {
            //构造生成room类型
            Header header = new Header();
            header.setText(test1Vo.getRoom());
            //获取服务器里的设备列表
            List<Test1Vo.ArgumentsBean> data = test1Vo.getArguments();
            //构造自己的设备列表
            List<Footer> list1 = new ArrayList<>();
            for (Test1Vo.ArgumentsBean bean : data) {
                Footer footer = new Footer();
                footer.setDevice(bean.getText());
                list1.add(footer);
            }
            //添加头部
            dataList.add(header);
            //添加列表
            dataList.addAll(list1);

        }
        return dataList;
    }
}
