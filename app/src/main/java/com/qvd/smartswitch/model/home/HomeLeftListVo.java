package com.qvd.smartswitch.model.home;

import java.io.Serializable;
import java.util.List;

public class HomeLeftListVo implements Serializable {


    /**
     * code : 200
     * message : ok
     * data : [{"room_name":"常用设备","room_pic":"http://pckgfzc5s.bkt.clouddn.com/a.png","rooom_id":"","type":1},{"room_id":"R1165081998","room_name":"卧室","room_pic":"http://pckgfzc5s.bkt.clouddn.com/b.png","type":2},{"room_id":"R613045395","room_name":"你好","room_pic":"http://pckgfzc5s.bkt.clouddn.com/j.png","type":2},{"room_id":"R1775192314","room_name":"珊珊","room_pic":"http://pckgfzc5s.bkt.clouddn.com/j.png","type":2},{"room_id":"R4745233804","room_name":"垃圾","room_pic":"http://pckgfzc5s.bkt.clouddn.com/j.png","type":2},{"room_id":"R3800558144","room_name":"最初","room_pic":"http://pckgfzc5s.bkt.clouddn.com/j.png","type":2},{"room_id":"R552109384","room_name":"贪生","room_pic":"http://pckgfzc5s.bkt.clouddn.com/h.png","type":2},{"room_id":"R1633697338","room_name":"bedroom","room_pic":"http://pckgfzc5s.bkt.clouddn.com/b.png","type":2},{"room_id":"R7699338800","room_name":"kitchenRoom","room_pic":"http://pckgfzc5s.bkt.clouddn.com/b.png","type":2}]
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
         * room_name : 常用设备
         * room_pic : http://pckgfzc5s.bkt.clouddn.com/a.png
         * rooom_id :
         * type : 1
         * room_id : R1165081998
         */

        private String room_name;
        private String room_pic;
        private String rooom_id;
        private int type;
        private String room_id;

        public String getRoom_name() {
            return room_name;
        }

        public void setRoom_name(String room_name) {
            this.room_name = room_name;
        }

        public String getRoom_pic() {
            return room_pic;
        }

        public void setRoom_pic(String room_pic) {
            this.room_pic = room_pic;
        }

        public String getRooom_id() {
            return rooom_id;
        }

        public void setRooom_id(String rooom_id) {
            this.rooom_id = rooom_id;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getRoom_id() {
            return room_id;
        }

        public void setRoom_id(String room_id) {
            this.room_id = room_id;
        }
    }
}
