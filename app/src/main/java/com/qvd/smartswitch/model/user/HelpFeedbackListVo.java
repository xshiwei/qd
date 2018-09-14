package com.qvd.smartswitch.model.user;

import java.util.List;

public class HelpFeedbackListVo {
    /**
     * code : 200
     * message : ok
     * data : [{"data":[{"device_feedback_pic":"","device_name":"蓝牙智能开关","device_no":"qs02"}],"feedback_type":"device","feedback_type_name":"我的设备"},{"data":[{"device_feedback_pic":"","device_name":"应用体验","device_no":"ae01"},{"device_feedback_pic":"","device_name":"其他","device_no":"other01"}],"feedback_type":"more","feedback_type_name":"更多"}]
     */

    private int code;
    private String message;
    private List<DataBeanX> data;

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

    public List<DataBeanX> getData() {
        return data;
    }

    public void setData(List<DataBeanX> data) {
        this.data = data;
    }

    public static class DataBeanX {
        /**
         * data : [{"device_feedback_pic":"","device_name":"蓝牙智能开关","device_no":"qs02"}]
         * feedback_type : device
         * feedback_type_name : 我的设备
         */

        private String feedback_type;
        private String feedback_type_name;
        private List<DataBean> data;

        public String getFeedback_type() {
            return feedback_type;
        }

        public void setFeedback_type(String feedback_type) {
            this.feedback_type = feedback_type;
        }

        public String getFeedback_type_name() {
            return feedback_type_name;
        }

        public void setFeedback_type_name(String feedback_type_name) {
            this.feedback_type_name = feedback_type_name;
        }

        public List<DataBean> getData() {
            return data;
        }

        public void setData(List<DataBean> data) {
            this.data = data;
        }

        public static class DataBean {
            /**
             * device_feedback_pic :
             * device_name : 蓝牙智能开关
             * device_no : qs02
             */

            private String device_feedback_pic;
            private String device_name;
            private String device_no;

            public String getDevice_feedback_pic() {
                return device_feedback_pic;
            }

            public void setDevice_feedback_pic(String device_feedback_pic) {
                this.device_feedback_pic = device_feedback_pic;
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
        }
    }
}
