package com.qvd.smartswitch.model.home;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

public class RoomListVo implements Serializable {
    /**
     * code : 200
     * message : ok
     * data : [{"create_time":"2018-07-26 11:09:30.000000","delete_time":null,"device_num":0,"family_id":"F00000006","id":8,"room_delete":null,"room_id":"R3024730165","room_name":"kitchen","room_pic":"https://www.qq745.com/uploads/allimg/141009/1-14100ZT451-51.jpg","update_time":null}]
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
         * create_time : 2018-07-26 11:09:30.000000
         * delete_time : null
         * device_num : 0
         * family_id : F00000006
         * id : 8
         * room_delete : null
         * room_id : R3024730165
         * room_name : kitchen
         * room_pic : https://www.qq745.com/uploads/allimg/141009/1-14100ZT451-51.jpg
         * update_time : null
         */

        private String create_time;
        private Object delete_time;
        private int device_num;
        private String family_id;
        private int id;
        private Object room_delete;
        private String room_id;
        private String room_name;
        private String room_pic;
        private Object update_time;

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public Object getDelete_time() {
            return delete_time;
        }

        public void setDelete_time(Object delete_time) {
            this.delete_time = delete_time;
        }

        public int getDevice_num() {
            return device_num;
        }

        public void setDevice_num(int device_num) {
            this.device_num = device_num;
        }

        public String getFamily_id() {
            return family_id;
        }

        public void setFamily_id(String family_id) {
            this.family_id = family_id;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public Object getRoom_delete() {
            return room_delete;
        }

        public void setRoom_delete(Object room_delete) {
            this.room_delete = room_delete;
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

        public Object getUpdate_time() {
            return update_time;
        }

        public void setUpdate_time(Object update_time) {
            this.update_time = update_time;
        }

    }
}
