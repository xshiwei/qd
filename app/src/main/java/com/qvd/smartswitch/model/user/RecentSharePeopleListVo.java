package com.qvd.smartswitch.model.user;

import java.io.Serializable;
import java.util.List;

public class RecentSharePeopleListVo implements Serializable {
    /**
     * code : 200
     * message : ok
     * data : [{"share_object_userid":"7e13271403828073a4b5b7b28be99995","user_account":"4353949764","user_avatar":"https://www.qq745.com/uploads/allimg/141009/1-14100ZT451-51.jpg","user_name":"15218703952"}]
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
         * share_object_userid : 7e13271403828073a4b5b7b28be99995
         * user_account : 4353949764
         * user_avatar : https://www.qq745.com/uploads/allimg/141009/1-14100ZT451-51.jpg
         * user_name : 15218703952
         */

        private String share_object_userid;
        private String user_account;
        private String user_avatar;
        private String user_name;
        private String people_type;

        public String getPeople_type() {
            return people_type;
        }

        public void setPeople_type(String people_type) {
            this.people_type = people_type;
        }

        public String getShare_object_userid() {
            return share_object_userid;
        }

        public void setShare_object_userid(String share_object_userid) {
            this.share_object_userid = share_object_userid;
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

        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }
    }
}
