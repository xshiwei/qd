package com.qvd.smartswitch.model.user;

import java.util.List;

public class UserShareDeviceListVo {
    /**
     * code : 200
     * message : ok
     * data : [{"device_id":"5ca483d1145414dd10ccff5c6d41be0c","device_name":"3楼","device_pic":"http://pckgfzc5s.bkt.clouddn.com/p.png","device_share_id":"","is_share":0,"room_name":"u","share_object_userid":null,"share_object_username":null,"table_type":"qs02"},{"device_id":"92a1a65ec95a9670e70a3e950274f012","device_name":"蓝牙智能开关","device_pic":"http://pckgfzc5s.bkt.clouddn.com/p.png","device_share_id":"","is_share":0,"room_name":"u","share_object_userid":null,"share_object_username":null,"table_type":"qs02"}]
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
         * device_id : 5ca483d1145414dd10ccff5c6d41be0c
         * device_name : 3楼
         * device_pic : http://pckgfzc5s.bkt.clouddn.com/p.png
         * device_share_id :
         * is_share : 0
         * room_name : u
         * share_object_userid : null
         * share_object_username : null
         * table_type : qs02
         */

        private String device_id;
        private String device_name;
        private String device_pic;
        private String device_share_id;
        private int is_share;
        private String room_name;
        private Object share_object_userid;
        private Object share_object_username;
        private String table_type;

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

        public String getRoom_name() {
            return room_name;
        }

        public void setRoom_name(String room_name) {
            this.room_name = room_name;
        }

        public Object getShare_object_userid() {
            return share_object_userid;
        }

        public void setShare_object_userid(Object share_object_userid) {
            this.share_object_userid = share_object_userid;
        }

        public Object getShare_object_username() {
            return share_object_username;
        }

        public void setShare_object_username(Object share_object_username) {
            this.share_object_username = share_object_username;
        }

        public String getTable_type() {
            return table_type;
        }

        public void setTable_type(String table_type) {
            this.table_type = table_type;
        }
    }
}
