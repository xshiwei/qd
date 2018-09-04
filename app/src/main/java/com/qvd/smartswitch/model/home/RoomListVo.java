package com.qvd.smartswitch.model.home;

import java.io.Serializable;
import java.util.List;

public class RoomListVo implements Serializable {

    /**
     * code : 200
     * message : ok
     * data : [{"create_time":"2018-08-08 17:22:58.000000","device_count":0,"family_id":"F9575990794","is_default":1,"room_id":"R9102879504","room_name":"default room","room_pic":"http://pckgfzc5s.bkt.clouddn.com/a.png"},{"create_time":"2018-08-08 18:26:14.000000","device_count":0,"family_id":"F9575990794","is_default":0,"room_id":"R613045395","room_name":"你好","room_pic":"http://pckgfzc5s.bkt.clouddn.com/j.png"},{"create_time":"2018-08-08 18:08:49.000000","device_count":2,"family_id":"F9575990794","is_default":0,"room_id":"R1165081998","room_name":"卧室","room_pic":"http://pckgfzc5s.bkt.clouddn.com/b.png"},{"create_time":"2018-08-10 11:38:20.000000","device_count":0,"family_id":"F9575990794","is_default":0,"room_id":"R4745233804","room_name":"垃圾","room_pic":"ssss"},{"create_time":"2018-08-10 13:08:27.000000","device_count":0,"family_id":"F9575990794","is_default":0,"room_id":"R3800558144","room_name":"最初","room_pic":"http://pckgfzc5s.bkt.clouddn.com/j.png"},{"create_time":"2018-08-10 11:16:01.000000","device_count":0,"family_id":"F9575990794","is_default":0,"room_id":"R1775192314","room_name":"珊珊","room_pic":"ssss"},{"create_time":"2018-08-10 13:08:39.000000","device_count":0,"family_id":"F9575990794","is_default":0,"room_id":"R552109384","room_name":"贪生","room_pic":"http://pckgfzc5s.bkt.clouddn.com/h.png"}]
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
         * create_time : 2018-08-08 17:22:58.000000
         * device_count : 0
         * family_id : F9575990794
         * is_default : 1
         * room_id : R9102879504
         * room_name : default room
         * room_pic : http://pckgfzc5s.bkt.clouddn.com/a.png
         */

        private String create_time;
        private int device_count;
        private String family_id;
        private int is_default;
        private String room_id;
        private String room_name;
        private String room_pic;

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public int getDevice_count() {
            return device_count;
        }

        public void setDevice_count(int device_count) {
            this.device_count = device_count;
        }

        public String getFamily_id() {
            return family_id;
        }

        public void setFamily_id(String family_id) {
            this.family_id = family_id;
        }

        public int getIs_default() {
            return is_default;
        }

        public void setIs_default(int is_default) {
            this.is_default = is_default;
        }

        public String getRoom_id() {
            return room_id;
        }

        public void setRoom_id(String room_id) {
            this.room_id = room_id;
        }

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
    }
}
