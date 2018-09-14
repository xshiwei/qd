package com.qvd.smartswitch.model.device;

import java.util.List;

public class DeviceAddTimingVo {
    /**
     * device_id : 5ca483d1145414dd10ccff5c6d41be0c
     * start_time : 18:00
     * end_time : 22:00
     * timing_content : 客厅灯控开关定时
     * timing_date : ["1","2","4"]
     */

    private String device_id;
    private String start_time;
    private String end_time;
    private String timing_content;
    private List<String> timing_date;

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getTiming_content() {
        return timing_content;
    }

    public void setTiming_content(String timing_content) {
        this.timing_content = timing_content;
    }

    public List<String> getTiming_date() {
        return timing_date;
    }

    public void setTiming_date(List<String> timing_date) {
        this.timing_date = timing_date;
    }
}
