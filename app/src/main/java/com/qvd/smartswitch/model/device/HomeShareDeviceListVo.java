package com.qvd.smartswitch.model.device;


import java.util.List;

public class HomeShareDeviceListVo {


    /**
     * code : 200
     * message : ok
     * data : [{"connect_type":1,"device_id":"5ca483d1145414dd10ccff5c6d41be0c","device_mac":"44:EA:D8:2D:9C:BF","device_name":"3楼","device_no":"qs02","device_pic":"http://pckgfzc5s.bkt.clouddn.com/p.png","device_share_id":"ds6568516947","is_control":1,"share_user_name":"13632612491"},{"connect_type":1,"device_id":"55da8f2d341bcf90e5e5c5c2ba28d328","device_mac":"44:EA:D8:2D:9C:B1","device_name":"蓝牙智能开关","device_no":"qs02","device_pic":"http://pckgfzc5s.bkt.clouddn.com/p.png","device_share_id":"ds5849628097","is_control":1,"share_user_name":"13632612491"}]
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
         * connect_type : 1
         * device_id : 5ca483d1145414dd10ccff5c6d41be0c
         * device_mac : 44:EA:D8:2D:9C:BF
         * device_name : 3楼
         * device_no : qs02
         * device_pic : http://pckgfzc5s.bkt.clouddn.com/p.png
         * device_share_id : ds6568516947
         * is_control : 1
         * share_user_name : 13632612491
         */

        private int connect_type;
        private String device_id;
        private String device_mac;
        private String device_name;
        private String device_no;
        private String device_pic;
        private String device_share_id;
        private int is_control;
        private String share_user_name;

        public int getConnect_type() {
            return connect_type;
        }

        public void setConnect_type(int connect_type) {
            this.connect_type = connect_type;
        }

        public String getDevice_id() {
            return device_id;
        }

        public void setDevice_id(String device_id) {
            this.device_id = device_id;
        }

        public String getDevice_mac() {
            return device_mac;
        }

        public void setDevice_mac(String device_mac) {
            this.device_mac = device_mac;
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

        public String getDevice_share_id() {
            return device_share_id;
        }

        public void setDevice_share_id(String device_share_id) {
            this.device_share_id = device_share_id;
        }

        public int getIs_control() {
            return is_control;
        }

        public void setIs_control(int is_control) {
            this.is_control = is_control;
        }

        public String getShare_user_name() {
            return share_user_name;
        }

        public void setShare_user_name(String share_user_name) {
            this.share_user_name = share_user_name;
        }
    }
}
