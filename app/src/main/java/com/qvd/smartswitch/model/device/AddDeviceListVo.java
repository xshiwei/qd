package com.qvd.smartswitch.model.device;

import java.util.List;

public class AddDeviceListVo {
    /**
     * code : 200
     * message : ok
     * data : [{"device_detail_list":[{"add_time":"2018-08-07 09:25:48.000000","device_name":"车载空气净化器","device_no":"QP01","device_pic":"https://www.qq745.com/uploads/allimg/141009/1-14100ZT451-51.jpg","device_version":"v1.0","update_time":null}],"type":3,"type_name":"air cleaner"},{"device_detail_list":[{"add_time":"2018-08-07 09:24:47.000000","device_name":"蓝牙智能对讲机","device_no":"QTAE5","device_pic":"https://www.qq745.com/uploads/allimg/141009/1-14100ZT451-51.jpg","device_version":"v1.0","update_time":"2018-08-07 09:33:40.000000"},{"add_time":"2018-08-07 09:27:10.000000","device_name":"蓝牙智能耳机","device_no":"QTAE6","device_pic":"https://www.qq745.com/uploads/allimg/141009/1-14100ZT451-51.jpg","device_version":"v1.0","update_time":null}],"type":2,"type_name":"Headse"},{"device_detail_list":[{"add_time":"2018-08-07 09:21:39.000000","device_name":"蓝牙智能开关","device_no":"QS02","device_pic":"https://www.qq745.com/uploads/allimg/141009/1-14100ZT451-51.jpg","device_version":"v1.0","update_time":null},{"add_time":"2018-08-07 09:23:16.000000","device_name":"Wi-fi 智能开关","device_no":"QS03","device_pic":"https://www.qq745.com/uploads/allimg/141009/1-14100ZT451-51.jpg","device_version":"v1.0","update_time":null}],"type":1,"type_name":"Smart switch"}]
     */

    private int code;
    private String message;
    private List<DataBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * device_detail_list : [{"add_time":"2018-08-07 09:25:48.000000","device_name":"车载空气净化器","device_no":"QP01","device_pic":"https://www.qq745.com/uploads/allimg/141009/1-14100ZT451-51.jpg","device_version":"v1.0","update_time":null}]
         * type : 3
         * type_name : air cleaner
         */

        private int type;
        private String type_name;
        private List<DeviceDetailListBean> device_detail_list;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getType_name() {
            return type_name;
        }

        public void setType_name(String type_name) {
            this.type_name = type_name;
        }

        public List<DeviceDetailListBean> getDevice_detail_list() {
            return device_detail_list;
        }

        public void setDevice_detail_list(List<DeviceDetailListBean> device_detail_list) {
            this.device_detail_list = device_detail_list;
        }

        public static class DeviceDetailListBean {
            /**
             * add_time : 2018-08-07 09:25:48.000000
             * device_name : 车载空气净化器
             * device_no : QP01
             * device_pic : https://www.qq745.com/uploads/allimg/141009/1-14100ZT451-51.jpg
             * device_version : v1.0
             * update_time : null
             */

            private String add_time;
            private String device_name;
            private String device_no;
            private String device_pic;
            private String device_version;
            private Object update_time;

            public String getAdd_time() {
                return add_time;
            }

            public void setAdd_time(String add_time) {
                this.add_time = add_time;
            }

            public String getDevice_name() {
                return device_name;
            }

            public void setDevice_name(String device_name) {
                this.device_name = device_name;
            }

            public String getDevice_no() {
                return device_no;
            }

            public void setDevice_no(String device_no) {
                this.device_no = device_no;
            }

            public String getDevice_pic() {
                return device_pic;
            }

            public void setDevice_pic(String device_pic) {
                this.device_pic = device_pic;
            }

            public String getDevice_version() {
                return device_version;
            }

            public void setDevice_version(String device_version) {
                this.device_version = device_version;
            }

            public Object getUpdate_time() {
                return update_time;
            }

            public void setUpdate_time(Object update_time) {
                this.update_time = update_time;
            }
        }
    }
}
