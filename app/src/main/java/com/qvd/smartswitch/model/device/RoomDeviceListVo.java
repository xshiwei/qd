package com.qvd.smartswitch.model.device;

import java.util.List;

public class RoomDeviceListVo {
    /**
     * code : 200
     * message : ok
     * data : [{"add_time":"2018-08-11 16:26:58.000000","connect_type":1,"device_id":"a9cc31c3392a4b059cdc71cd4e223961","device_mac":"88:4A:EA:39:82:B1","device_name":"哈哈","device_no":"qs02","device_pic":"http://pckgfzc5s.bkt.clouddn.com/p.png","device_type":"智能开关","device_version":"v1.0","family_id":"F9575990794","firmware_address":"","firmware_version":"","is_common":1,"is_first_connect":1,"room_id":"R9102879504","table_type":"qs02","update_time":"2018-08-11 16:27:08.000000","user_id":"c0f3e214be19d22a4c10c052983f8a24"}]
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
         * add_time : 2018-08-11 16:26:58.000000
         * connect_type : 1
         * device_id : a9cc31c3392a4b059cdc71cd4e223961
         * device_mac : 88:4A:EA:39:82:B1
         * device_name : 哈哈
         * device_no : qs02
         * device_pic : http://pckgfzc5s.bkt.clouddn.com/p.png
         * device_type : 智能开关
         * device_version : v1.0
         * family_id : F9575990794
         * firmware_address :
         * firmware_version :
         * is_common : 1
         * is_first_connect : 1
         * room_id : R9102879504
         * table_type : qs02
         * update_time : 2018-08-11 16:27:08.000000
         * user_id : c0f3e214be19d22a4c10c052983f8a24
         */

        private String add_time;
        private int connect_type;
        private String device_id;
        private String device_mac;
        private String device_name;
        private String device_no;
        private String device_pic;
        private String device_type;
        private String device_version;
        private String family_id;
        private String firmware_address;
        private String firmware_version;
        private int is_common;
        private int is_first_connect;
        private String room_id;
        private String table_type;
        private String update_time;
        private String user_id;

        public String getAdd_time() {
            return add_time;
        }

        public void setAdd_time(String add_time) {
            this.add_time = add_time;
        }

        public int getConnect_type() {
            return connect_type;
        }

        public void setConnect_type(int connect_type) {
            this.connect_type = connect_type;
        }

        public String getDevice_id() {
            return device_id;
        }

        public void setDevice_id(String device_id) {
            this.device_id = device_id;
        }

        public String getDevice_mac() {
            return device_mac;
        }

        public void setDevice_mac(String device_mac) {
            this.device_mac = device_mac;
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

        public String getDevice_pic() {
            return device_pic;
        }

        public void setDevice_pic(String device_pic) {
            this.device_pic = device_pic;
        }

        public String getDevice_type() {
            return device_type;
        }

        public void setDevice_type(String device_type) {
            this.device_type = device_type;
        }

        public String getDevice_version() {
            return device_version;
        }

        public void setDevice_version(String device_version) {
            this.device_version = device_version;
        }

        public String getFamily_id() {
            return family_id;
        }

        public void setFamily_id(String family_id) {
            this.family_id = family_id;
        }

        public String getFirmware_address() {
            return firmware_address;
        }

        public void setFirmware_address(String firmware_address) {
            this.firmware_address = firmware_address;
        }

        public String getFirmware_version() {
            return firmware_version;
        }

        public void setFirmware_version(String firmware_version) {
            this.firmware_version = firmware_version;
        }

        public int getIs_common() {
            return is_common;
        }

        public void setIs_common(int is_common) {
            this.is_common = is_common;
        }

        public int getIs_first_connect() {
            return is_first_connect;
        }

        public void setIs_first_connect(int is_first_connect) {
            this.is_first_connect = is_first_connect;
        }

        public String getRoom_id() {
            return room_id;
        }

        public void setRoom_id(String room_id) {
            this.room_id = room_id;
        }

        public String getTable_type() {
            return table_type;
        }

        public void setTable_type(String table_type) {
            this.table_type = table_type;
        }

        public String getUpdate_time() {
            return update_time;
        }

        public void setUpdate_time(String update_time) {
            this.update_time = update_time;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }
    }
}
