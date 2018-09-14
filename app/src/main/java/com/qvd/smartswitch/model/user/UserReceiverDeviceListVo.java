package com.qvd.smartswitch.model.user;

import java.util.List;

public class UserReceiverDeviceListVo {
    /**
     * code : 200
     * message : ok
     * data : [{"add_time":"2018-09-14 09:25:52","device_id":"5ca483d1145414dd10ccff5c6d41be0c","device_name":"3楼","device_pic":"http://pckgfzc5s.bkt.clouddn.com/p.png","device_share_id":"ds8339043528","is_share":0,"share_object_userid":"7e13271403828073a4b5b7b28be99995","user_name":"13632612491"}]
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
         * add_time : 2018-09-14 09:25:52
         * device_id : 5ca483d1145414dd10ccff5c6d41be0c
         * device_name : 3楼
         * device_pic : http://pckgfzc5s.bkt.clouddn.com/p.png
         * device_share_id : ds8339043528
         * is_share : 0
         * share_object_userid : 7e13271403828073a4b5b7b28be99995
         * user_name : 13632612491
         */

        private String add_time;
        private String device_id;
        private String device_name;
        private String device_pic;
        private String device_share_id;
        private int is_share;
        private String share_object_userid;
        private String user_name;

        public String getAdd_time() {
            return add_time;
        }

        public void setAdd_time(String add_time) {
            this.add_time = add_time;
        }

        public String getDevice_id() {
            return device_id;
        }

        public void setDevice_id(String device_id) {
            this.device_id = device_id;
        }

        public String getDevice_name() {
            return device_name;
        }

        public void setDevice_name(String device_name) {
            this.device_name = device_name;
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

        public int getIs_share() {
            return is_share;
        }

        public void setIs_share(int is_share) {
            this.is_share = is_share;
        }

        public String getShare_object_userid() {
            return share_object_userid;
        }

        public void setShare_object_userid(String share_object_userid) {
            this.share_object_userid = share_object_userid;
        }

        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }
    }
}
