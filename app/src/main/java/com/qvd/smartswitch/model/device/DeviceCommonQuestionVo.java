package com.qvd.smartswitch.model.device;

import java.io.Serializable;
import java.util.List;

public class DeviceCommonQuestionVo implements Serializable {
    /**
     * code : 200
     * message : ok
     * data : [{"add_time":"2018-09-04 14:17:42.000000","device_no":"qs02","question_content":"尊敬客户：你需要在蓝牙智能开关接通电源之后，开关的正面的蓝色指示灯闪停了之后，蓝牙智能处于可控状态才能控制智能开关。","question_pic":"https://www.qq745.com/uploads/allimg/141009/1-14100ZT451-51.jpg","question_title":"为什么我接通电源之后立即手控开关，开关无响应？","update_time":"2018-09-04 14:19:32.000000"}]
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
         * add_time : 2018-09-04 14:17:42.000000
         * device_no : qs02
         * question_content : 尊敬客户：你需要在蓝牙智能开关接通电源之后，开关的正面的蓝色指示灯闪停了之后，蓝牙智能处于可控状态才能控制智能开关。
         * question_pic : https://www.qq745.com/uploads/allimg/141009/1-14100ZT451-51.jpg
         * question_title : 为什么我接通电源之后立即手控开关，开关无响应？
         * update_time : 2018-09-04 14:19:32.000000
         */

        private String add_time;
        private String device_no;
        private String question_content;
        private String question_pic;
        private String question_title;
        private String update_time;

        public String getAdd_time() {
            return add_time;
        }

        public void setAdd_time(String add_time) {
            this.add_time = add_time;
        }

        public String getDevice_no() {
            return device_no;
        }

        public void setDevice_no(String device_no) {
            this.device_no = device_no;
        }

        public String getQuestion_content() {
            return question_content;
        }

        public void setQuestion_content(String question_content) {
            this.question_content = question_content;
        }

        public String getQuestion_pic() {
            return question_pic;
        }

        public void setQuestion_pic(String question_pic) {
            this.question_pic = question_pic;
        }

        public String getQuestion_title() {
            return question_title;
        }

        public void setQuestion_title(String question_title) {
            this.question_title = question_title;
        }

        public String getUpdate_time() {
            return update_time;
        }

        public void setUpdate_time(String update_time) {
            this.update_time = update_time;
        }
    }
}
