package com.qvd.smartswitch.model.device;

import java.util.List;

public class DeviceTimingVo {
    /**
     * code : 200
     * message : ok
     * data : [{"date_time":["1","2","3","4","5","6","7"],"device_id":"5ca483d1145414dd10ccff5c6d41be0c","end_time":"","start_time":"18:00","timing_contetnt":"客厅灯控开关定时开启","timing_id":"tim4814526964","timing_state":"1"},{"date_time":["1","2","4"],"device_id":"5ca483d1145414dd10ccff5c6d41be0c","end_time":"22:00","start_time":"18:00","timing_contetnt":"客厅灯控开关定时","timing_id":"tim94411805","timing_state":"1"}]
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
         * date_time : ["1","2","3","4","5","6","7"]
         * device_id : 5ca483d1145414dd10ccff5c6d41be0c
         * end_time :
         * start_time : 18:00
         * timing_contetnt : 客厅灯控开关定时开启
         * timing_id : tim4814526964
         * timing_state : 1
         */

        private String device_id;
        private String end_time;
        private String start_time;
        private String timing_contetnt;
        private String timing_id;
        private String timing_state;
        private List<String> date_time;

        public String getDevice_id() {
            return device_id;
        }

        public void setDevice_id(String device_id) {
            this.device_id = device_id;
        }

        public String getEnd_time() {
            return end_time;
        }

        public void setEnd_time(String end_time) {
            this.end_time = end_time;
        }

        public String getStart_time() {
            return start_time;
        }

        public void setStart_time(String start_time) {
            this.start_time = start_time;
        }

        public String getTiming_contetnt() {
            return timing_contetnt;
        }

        public void setTiming_contetnt(String timing_contetnt) {
            this.timing_contetnt = timing_contetnt;
        }

        public String getTiming_id() {
            return timing_id;
        }

        public void setTiming_id(String timing_id) {
            this.timing_id = timing_id;
        }

        public String getTiming_state() {
            return timing_state;
        }

        public void setTiming_state(String timing_state) {
            this.timing_state = timing_state;
        }

        public List<String> getDate_time() {
            return date_time;
        }

        public void setDate_time(List<String> date_time) {
            this.date_time = date_time;
        }
    }
}
