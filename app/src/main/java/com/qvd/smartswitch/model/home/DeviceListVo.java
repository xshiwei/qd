package com.qvd.smartswitch.model.home;

import java.util.List;

public class DeviceListVo {
    /**
     * code : 200
     * message : ok
     * data : [{"add_time":"2018-07-26 21:34:07.000000","capacity_id":null,"car_device_id":null,"connect_type":0,"delete_time":null,"device_id":"D2223981588","device_mac":"c0f3e214be19d22a4c10c052983f8a24","device_name":"QS01 智能开关","device_no":"QS01","device_pic":"https://www.qq745.com/uploads/allimg/141009/1-14100ZT451-51.jpg","device_version":"nil","family_id":"F866612178","id":14,"is_common":1,"is_delete":null,"is_first_connect":0,"is_share":0,"person_device_id":null,"room_id":"R5206417693","share_object":"nil","type":0,"type_name":"智能开关","update_time":null,"user_id":"32947a727127cebbec5295bc445a8eab"},{"add_time":"2018-07-27 11:38:40.000000","capacity_id":null,"car_device_id":null,"connect_type":0,"delete_time":null,"device_id":"D6560121857","device_mac":"c0f3e214be19d22a4c10c052983f8a24","device_name":"QP01汽车空气净化器","device_no":"QP01","device_pic":"https://www.qq745.com/uploads/allimg/141009/1-14100ZT451-51.jpg","device_version":"nil","family_id":"F866612178","id":21,"is_common":1,"is_delete":null,"is_first_connect":0,"is_share":0,"person_device_id":null,"room_id":"R3024730165","share_object":"nil","type":1,"type_name":"汽车空气净化器","update_time":null,"user_id":"32947a727127cebbec5295bc445a8eab"},{"add_time":"2018-07-27 11:39:19.000000","capacity_id":null,"car_device_id":null,"connect_type":0,"delete_time":null,"device_id":"D9725917305","device_mac":"c0f3e214be19d22a4c10c052983f8a24","device_name":"摩托车蓝牙对讲机","device_no":"QTA35","device_pic":"https://www.qq745.com/uploads/allimg/141009/1-14100ZT451-51.jpg","device_version":"nil","family_id":"F866612178","id":22,"is_common":1,"is_delete":null,"is_first_connect":0,"is_share":0,"person_device_id":null,"room_id":"R3024730165","share_object":"nil","type":1,"type_name":"摩托车蓝牙对讲机","update_time":null,"user_id":"32947a727127cebbec5295bc445a8eab"},{"add_time":"2018-07-26 11:40:03.000000","capacity_id":null,"car_device_id":null,"connect_type":2,"delete_time":null,"device_id":"D8344958162","device_mac":"fdgdfhrtythgjgfjfgj","device_name":"WIFI 智能开关 双模开关","device_no":"QS02","device_pic":"https://www.qq745.com/uploads/allimg/141009/1-14100ZT451-51.jpg","device_version":"243546546546","family_id":"F866612178","id":23,"is_common":1,"is_delete":null,"is_first_connect":1,"is_share":0,"person_device_id":null,"room_id":"R5206417693","share_object":"yy","type":1,"type_name":"WIFI 智能开关 双模开关","update_time":"2018-07-27 11:45:25.000000","user_id":"32947a727127cebbec5295bc445a8eab"}]
     */

    private int code;
    private String message;
    private List<MyDataBean> dataBeans;

    public List<MyDataBean> getDataBeans() {
        return dataBeans;
    }

    public void setDataBeans(List<MyDataBean> dataBeans) {
        this.dataBeans = dataBeans;
    }

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

    public static class MyDataBean {
        public MyDataBean(DataBean bean, boolean isSelete) {
            this.bean = bean;
            this.isSelete = isSelete;
        }

        private DataBean bean;
        private boolean isSelete;

        public DataBean getBean() {
            return bean;
        }

        public void setBean(DataBean bean) {
            this.bean = bean;
        }

        public boolean isSelete() {
            return isSelete;
        }

        public void setSelete(boolean selete) {
            isSelete = selete;
        }
    }

    public static class DataBean {
        /**
         * add_time : 2018-07-26 21:34:07.000000
         * capacity_id : null
         * car_device_id : null
         * connect_type : 0
         * delete_time : null
         * device_id : D2223981588
         * device_mac : c0f3e214be19d22a4c10c052983f8a24
         * device_name : QS01 智能开关
         * device_no : QS01
         * device_pic : https://www.qq745.com/uploads/allimg/141009/1-14100ZT451-51.jpg
         * device_version : nil
         * family_id : F866612178
         * id : 14
         * is_common : 1
         * is_delete : null
         * is_first_connect : 0
         * is_share : 0
         * person_device_id : null
         * room_id : R5206417693
         * share_object : nil
         * type : 0
         * type_name : 智能开关
         * update_time : null
         * user_id : 32947a727127cebbec5295bc445a8eab
         */

        private String add_time;
        private Object capacity_id;
        private Object car_device_id;
        private int connect_type;
        private Object delete_time;
        private String device_id;
        private String device_mac;
        private String device_name;
        private String device_no;
        private String device_pic;
        private String device_version;
        private String family_id;
        private int id;
        private int is_common;
        private Object is_delete;
        private int is_first_connect;
        private int is_share;
        private Object person_device_id;
        private String room_id;
        private String share_object;
        private int type;
        private String type_name;
        private Object update_time;
        private String user_id;

        public String getAdd_time() {
            return add_time;
        }

        public void setAdd_time(String add_time) {
            this.add_time = add_time;
        }

        public Object getCapacity_id() {
            return capacity_id;
        }

        public void setCapacity_id(Object capacity_id) {
            this.capacity_id = capacity_id;
        }

        public Object getCar_device_id() {
            return car_device_id;
        }

        public void setCar_device_id(Object car_device_id) {
            this.car_device_id = car_device_id;
        }

        public int getConnect_type() {
            return connect_type;
        }

        public void setConnect_type(int connect_type) {
            this.connect_type = connect_type;
        }

        public Object getDelete_time() {
            return delete_time;
        }

        public void setDelete_time(Object delete_time) {
            this.delete_time = delete_time;
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

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getIs_common() {
            return is_common;
        }

        public void setIs_common(int is_common) {
            this.is_common = is_common;
        }

        public Object getIs_delete() {
            return is_delete;
        }

        public void setIs_delete(Object is_delete) {
            this.is_delete = is_delete;
        }

        public int getIs_first_connect() {
            return is_first_connect;
        }

        public void setIs_first_connect(int is_first_connect) {
            this.is_first_connect = is_first_connect;
        }

        public int getIs_share() {
            return is_share;
        }

        public void setIs_share(int is_share) {
            this.is_share = is_share;
        }

        public Object getPerson_device_id() {
            return person_device_id;
        }

        public void setPerson_device_id(Object person_device_id) {
            this.person_device_id = person_device_id;
        }

        public String getRoom_id() {
            return room_id;
        }

        public void setRoom_id(String room_id) {
            this.room_id = room_id;
        }

        public String getShare_object() {
            return share_object;
        }

        public void setShare_object(String share_object) {
            this.share_object = share_object;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getType_name() {
            return type_name;
        }

        public void setType_name(String type_name) {
            this.type_name = type_name;
        }

        public Object getUpdate_time() {
            return update_time;
        }

        public void setUpdate_time(Object update_time) {
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
