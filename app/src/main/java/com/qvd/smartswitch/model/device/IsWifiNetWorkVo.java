package com.qvd.smartswitch.model.device;

public class IsWifiNetWorkVo {
    /**
     * code : 200
     * message : ok
     * data : {"device_id":"03085447cdb05562c5585b5d50d36494","is_networking":1}
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
         * device_id : 03085447cdb05562c5585b5d50d36494
         * is_networking : 1
         */

        private String device_id;
        private int is_networking;

        public String getDevice_id() {
            return device_id;
        }

        public void setDevice_id(String device_id) {
            this.device_id = device_id;
        }

        public int getIs_networking() {
            return is_networking;
        }

        public void setIs_networking(int is_networking) {
            this.is_networking = is_networking;
        }
    }
}
