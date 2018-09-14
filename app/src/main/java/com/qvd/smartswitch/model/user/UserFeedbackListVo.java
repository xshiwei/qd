package com.qvd.smartswitch.model.user;

import java.io.Serializable;
import java.util.List;

public class UserFeedbackListVo implements Serializable {
    /**
     * code : 200
     * message : ok
     * data : [{"category_type":"qs02","contact_way":"15218703952","feedback_content":"添加了蓝牙智能开关之后，在 App 主页无法控制该蓝牙设备，估计是产品的状态维护的缘故。","feedback_id":"fb8837116883","feedback_time":"2018-09-12 14:48:05.000000","log_file":""},{"category_type":"qs02","contact_way":"15218703952","feedback_content":"添加了蓝牙智能开关之后，在 App 主页无法控制该蓝牙设备，估计是产品的状态维护的缘故。","feedback_id":"fb2989574520","feedback_time":"2018-09-12 14:50:32.000000","log_file":""},{"category_type":"qs02","contact_way":"15218703952","feedback_content":"添加了蓝牙智能开关之后，在 App 主页无法控制该蓝牙设备，估计是产品的状态维护的缘故。","feedback_id":"fb1915887655","feedback_time":"2018-09-12 14:50:34.000000","log_file":""}]
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
         * category_type : qs02
         * contact_way : 15218703952
         * feedback_content : 添加了蓝牙智能开关之后，在 App 主页无法控制该蓝牙设备，估计是产品的状态维护的缘故。
         * feedback_id : fb8837116883
         * feedback_time : 2018-09-12 14:48:05.000000
         * log_file :
         */

        private String category_type;
        private String contact_way;
        private String feedback_content;
        private String feedback_id;
        private String feedback_time;
        private String log_file;

        public String getCategory_type() {
            return category_type;
        }

        public void setCategory_type(String category_type) {
            this.category_type = category_type;
        }

        public String getContact_way() {
            return contact_way;
        }

        public void setContact_way(String contact_way) {
            this.contact_way = contact_way;
        }

        public String getFeedback_content() {
            return feedback_content;
        }

        public void setFeedback_content(String feedback_content) {
            this.feedback_content = feedback_content;
        }

        public String getFeedback_id() {
            return feedback_id;
        }

        public void setFeedback_id(String feedback_id) {
            this.feedback_id = feedback_id;
        }

        public String getFeedback_time() {
            return feedback_time;
        }

        public void setFeedback_time(String feedback_time) {
            this.feedback_time = feedback_time;
        }

        public String getLog_file() {
            return log_file;
        }

        public void setLog_file(String log_file) {
            this.log_file = log_file;
        }
    }
}
