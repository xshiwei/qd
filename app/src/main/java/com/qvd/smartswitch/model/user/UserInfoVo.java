package com.qvd.smartswitch.model.user;

public class UserInfoVo {

    /**
     * code : 200
     * message : ok
     * data : {"create_time":"2018-08-29 20:02:30.000000","devices_count":2,"user_account":"9405553283","user_avatar":"https://www.qq745.com/uploads/allimg/141009/1-14100ZT451-51.jpg","user_id":"567237325caa579cc8e686e4611d3d5d","user_name":"15986635874","user_phone":null}
     */

    private int code;
    private String message;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * create_time : 2018-08-29 20:02:30.000000
         * devices_count : 2
         * user_account : 9405553283
         * user_avatar : https://www.qq745.com/uploads/allimg/141009/1-14100ZT451-51.jpg
         * user_id : 567237325caa579cc8e686e4611d3d5d
         * user_name : 15986635874
         * user_phone : null
         */

        private String create_time;
        private int devices_count;
        private String user_account;
        private String user_avatar;
        private String user_id;
        private String user_name;
        private Object user_phone;

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public int getDevices_count() {
            return devices_count;
        }

        public void setDevices_count(int devices_count) {
            this.devices_count = devices_count;
        }

        public String getUser_account() {
            return user_account;
        }

        public void setUser_account(String user_account) {
            this.user_account = user_account;
        }

        public String getUser_avatar() {
            return user_avatar;
        }

        public void setUser_avatar(String user_avatar) {
            this.user_avatar = user_avatar;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        public Object getUser_phone() {
            return user_phone;
        }

        public void setUser_phone(Object user_phone) {
            this.user_phone = user_phone;
        }
    }
}
