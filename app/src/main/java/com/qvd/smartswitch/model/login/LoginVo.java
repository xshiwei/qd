package com.qvd.smartswitch.model.login;

public class LoginVo {
    /**
     * code : 200
     * message : ok
     * data : {"create_time":"2018-07-11 10:04:16.000000","password":"0b30e02434ce51ac34087101374ea6e9","update_time":"2018-07-11 10:04:16.000000","user_account":"9574968766","user_avatar":"https://www.qq745.com/uploads/allimg/141009/1-14100ZT451-51.jpg","user_id":"c0f3e214be19d22a4c10c052983f8a24","user_name":"1105943292@qq.com","user_phone":""}
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
         * create_time : 2018-07-11 10:04:16.000000
         * password : 0b30e02434ce51ac34087101374ea6e9
         * update_time : 2018-07-11 10:04:16.000000
         * user_account : 9574968766
         * user_avatar : https://www.qq745.com/uploads/allimg/141009/1-14100ZT451-51.jpg
         * user_id : c0f3e214be19d22a4c10c052983f8a24
         * user_name : 1105943292@qq.com
         * user_phone :
         */

        private String create_time;
        private String password;
        private String update_time;
        private String user_account;
        private String user_avatar;
        private String user_id;
        private String user_name;
        private String user_phone;

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getUpdate_time() {
            return update_time;
        }

        public void setUpdate_time(String update_time) {
            this.update_time = update_time;
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

        public String getUser_phone() {
            return user_phone;
        }

        public void setUser_phone(String user_phone) {
            this.user_phone = user_phone;
        }
    }
}
