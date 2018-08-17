package com.qvd.smartswitch.model.device;

import java.io.Serializable;
import java.util.List;

public class AddDeviceListVo implements Serializable {

    /**
     * code : 200
     * message : ok
     * data : [{"device_detail_list":[{"add_time":"2018-08-13 12:20:43.000000","connect_type":0,"device_name":"车载净化器","device_no":"qp01","device_pic":"https://www.qq745.com/uploads/allimg/141009/1-14100ZT451-51.jpg","device_version":"v1.0","firmware_address":"https://www.qq745.com/uploads/allimg/141009/1-14100ZT451-51.jpg","firmware_version":"1.0","update_time":null}],"device_type":"Car device"},{"device_detail_list":[{"add_time":"2018-08-13 12:21:58.000000","connect_type":1,"device_name":"蓝牙智能耳机","device_no":"qta35","device_pic":"https://www.qq745.com/uploads/allimg/141009/1-14100ZT451-51.jpg","device_version":"v1.0","firmware_address":"https://www.qq745.com/uploads/allimg/141009/1-14100ZT451-51.jpg","firmware_version":"1.0","update_time":null},{"add_time":"2018-08-13 12:22:17.000000","connect_type":1,"device_name":"蓝牙智能对讲耳机","device_no":"qtae6","device_pic":"https://www.qq745.com/uploads/allimg/141009/1-14100ZT451-51.jpg","device_version":"v1.0","firmware_address":"https://www.qq745.com/uploads/allimg/141009/1-14100ZT451-51.jpg","firmware_version":"1.0","update_time":null}],"device_type":"Smart headset"},{"device_detail_list":[{"add_time":"2018-08-13 12:16:26.000000","connect_type":1,"device_name":"蓝牙智能开关","device_no":"qs02","device_pic":"https://www.qq745.com/uploads/allimg/141009/1-14100ZT451-51.jpg","device_version":"v1.0","firmware_address":"https://www.qq745.com/uploads/allimg/141009/1-14100ZT451-51.jpg","firmware_version":"1.0","update_time":null},{"add_time":"2018-08-13 12:16:47.000000","connect_type":2,"device_name":"Wifi 智能开关","device_no":"qs03","device_pic":"https://www.qq745.com/uploads/allimg/141009/1-14100ZT451-51.jpg","device_version":"v1.0","firmware_address":"https://www.qq745.com/uploads/allimg/141009/1-14100ZT451-51.jpg","firmware_version":"1.0","update_time":null}],"device_type":"Smart switch"}]
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

    public static class DataBean implements Serializable {
        /**
         * device_detail_list : [{"add_time":"2018-08-13 12:20:43.000000","connect_type":0,"device_name":"车载净化器","device_no":"qp01","device_pic":"https://www.qq745.com/uploads/allimg/141009/1-14100ZT451-51.jpg","device_version":"v1.0","firmware_address":"https://www.qq745.com/uploads/allimg/141009/1-14100ZT451-51.jpg","firmware_version":"1.0","update_time":null}]
         * device_type : Car device
         */

        private String device_type;
        private List<DeviceDetailListBean> device_detail_list;

        public String getDevice_type() {
            return device_type;
        }

        public void setDevice_type(String device_type) {
            this.device_type = device_type;
        }

        public List<DeviceDetailListBean> getDevice_detail_list() {
            return device_detail_list;
        }

        public void setDevice_detail_list(List<DeviceDetailListBean> device_detail_list) {
            this.device_detail_list = device_detail_list;
        }

        public static class DeviceDetailListBean implements Serializable {
            /**
             * add_time : 2018-08-13 12:20:43.000000
             * connect_type : 0
             * device_name : 车载净化器
             * device_no : qp01
             * device_pic : https://www.qq745.com/uploads/allimg/141009/1-14100ZT451-51.jpg
             * device_version : v1.0
             * firmware_address : https://www.qq745.com/uploads/allimg/141009/1-14100ZT451-51.jpg
             * firmware_version : 1.0
             * update_time : null
             */

            private String add_time;
            private int connect_type;
            private String device_name;
            private String device_no;
            private String device_pic;
            private String device_version;
            private String firmware_address;
            private String firmware_version;
            private Object update_time;

            public String getAdd_time() {
                return add_time;
            }

            public void setAdd_time(String add_time) {
                this.add_time = add_time;
            }

            public int getConnect_type() {
                return connect_type;
            }

            public void setConnect_type(int connect_type) {
                this.connect_type = connect_type;
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

            public String getFirmware_address() {
                return firmware_address;
            }

            public void setFirmware_address(String firmware_address) {
                this.firmware_address = firmware_address;
            }

            public String getFirmware_version() {
                return firmware_version;
            }

            public void setFirmware_version(String firmware_version) {
                this.firmware_version = firmware_version;
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
