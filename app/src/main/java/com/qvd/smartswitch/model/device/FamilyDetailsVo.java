package com.qvd.smartswitch.model.device;

import java.util.List;

public class FamilyDetailsVo {
    /**
     * code : 200
     * message : ok
     * data : {"share_devices_count":1,"share_devices_data":[{"add_time":"2018-09-21 15:45:42","device_id":"55da8f2d341bcf90e5e5c5c2ba28d328","device_name":"蓝牙智能开关","device_pic":"http://pckgfzc5s.bkt.clouddn.com/p.png","device_share_id":"ds240489597"}],"user_avatar":"https://www.qq745.com/uploads/allimg/141009/1-14100ZT451-51.jpg","user_name":"xiong0209ican@163.com"}
     */

    private int code;
    private String message;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * share_devices_count : 1
         * share_devices_data : [{"add_time":"2018-09-21 15:45:42","device_id":"55da8f2d341bcf90e5e5c5c2ba28d328","device_name":"蓝牙智能开关","device_pic":"http://pckgfzc5s.bkt.clouddn.com/p.png","device_share_id":"ds240489597"}]
         * user_avatar : https://www.qq745.com/uploads/allimg/141009/1-14100ZT451-51.jpg
         * user_name : xiong0209ican@163.com
         */

        private int share_devices_count;
        private String user_avatar;
        private String user_name;
        private List<ShareDevicesDataBean> share_devices_data;

        public int getShare_devices_count() {
            return share_devices_count;
        }

        public void setShare_devices_count(int share_devices_count) {
            this.share_devices_count = share_devices_count;
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

        public List<ShareDevicesDataBean> getShare_devices_data() {
            return share_devices_data;
        }

        public void setShare_devices_data(List<ShareDevicesDataBean> share_devices_data) {
            this.share_devices_data = share_devices_data;
        }

        public static class ShareDevicesDataBean {
            /**
             * add_time : 2018-09-21 15:45:42
             * device_id : 55da8f2d341bcf90e5e5c5c2ba28d328
             * device_name : 蓝牙智能开关
             * device_pic : http://pckgfzc5s.bkt.clouddn.com/p.png
             * device_share_id : ds240489597
             */

            private String add_time;
            private String device_id;
            private String device_name;
            private String device_pic;
            private String device_share_id;

            public String getAdd_time() {
                return add_time;
            }

            public void setAdd_time(String add_time) {
                this.add_time = add_time;
            }

            public String getDevice_id() {
                return device_id;
            }

            public void setDevice_id(String device_id) {
                this.device_id = device_id;
            }

            public String getDevice_name() {
                return device_name;
            }

            public void setDevice_name(String device_name) {
                this.device_name = device_name;
            }

            public String getDevice_pic() {
                return device_pic;
            }

            public void setDevice_pic(String device_pic) {
                this.device_pic = device_pic;
            }

            public String getDevice_share_id() {
                return device_share_id;
            }

            public void setDevice_share_id(String device_share_id) {
                this.device_share_id = device_share_id;
            }
        }
    }
}
