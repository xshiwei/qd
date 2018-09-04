package com.qvd.smartswitch.model.home;

import java.util.List;

public class HomeListVo {


    /**
     * code : 200
     * message : ok
     * data : [{"create_time":"2018-08-08 17:22:58.000000","device_count":2,"family_background":"https://www.qq745.com/uploads/allimg/141009/1-14100ZT451-51.jpg","family_id":"F9575990794","family_location":"Beijing","family_name":"1105943292@qq.com","is_opened":1,"room_count":7},{"create_time":"2018-08-13 15:45:05.000000","device_count":0,"family_background":"https://www.qq745.com/uploads/allimg/141009/1-14100ZT451-51.jpg","family_id":"F7199558263","family_location":" 浙江省 杭州市 滨江区","family_name":"喜欢","is_opened":0,"room_count":1}]
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
         * create_time : 2018-08-08 17:22:58.000000
         * device_count : 2
         * family_background : https://www.qq745.com/uploads/allimg/141009/1-14100ZT451-51.jpg
         * family_id : F9575990794
         * family_location : Beijing
         * family_name : 1105943292@qq.com
         * is_opened : 1
         * room_count : 7
         */

        private String create_time;
        private int device_count;
        private String family_background;
        private String family_id;
        private String family_location;
        private String family_name;
        private int is_opened;
        private int room_count;

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

        public String getFamily_background() {
            return family_background;
        }

        public void setFamily_background(String family_background) {
            this.family_background = family_background;
        }

        public String getFamily_id() {
            return family_id;
        }

        public void setFamily_id(String family_id) {
            this.family_id = family_id;
        }

        public String getFamily_location() {
            return family_location;
        }

        public void setFamily_location(String family_location) {
            this.family_location = family_location;
        }

        public String getFamily_name() {
            return family_name;
        }

        public void setFamily_name(String family_name) {
            this.family_name = family_name;
        }

        public int getIs_opened() {
            return is_opened;
        }

        public void setIs_opened(int is_opened) {
            this.is_opened = is_opened;
        }

        public int getRoom_count() {
            return room_count;
        }

        public void setRoom_count(int room_count) {
            this.room_count = room_count;
        }
    }
}
