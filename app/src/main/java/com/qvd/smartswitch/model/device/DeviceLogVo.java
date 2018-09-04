package com.qvd.smartswitch.model.device;

import java.util.List;

public class DeviceLogVo {
    /**
     * code : 200
     * message : ok
     * data : [{"create_time":"2018-08-22 11:50:46.000000","device_id":"13474a9f61516b586818321e0ae68395","log_content":"897897878"},{"create_time":"2018-08-22 11:50:33.000000","device_id":"13474a9f61516b586818321e0ae68395","log_content":"786786786"},{"create_time":"2018-08-22 11:50:17.000000","device_id":"13474a9f61516b586818321e0ae68395","log_content":"999999"},{"create_time":"2018-08-22 11:50:02.000000","device_id":"13474a9f61516b586818321e0ae68395","log_content":"777777"}]
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
         * create_time : 2018-08-22 11:50:46.000000
         * device_id : 13474a9f61516b586818321e0ae68395
         * log_content : 897897878
         */

        private String create_time;
        private String device_id;
        private String log_content;

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public String getDevice_id() {
            return device_id;
        }

        public void setDevice_id(String device_id) {
            this.device_id = device_id;
        }

        public String getLog_content() {
            return log_content;
        }

        public void setLog_content(String log_content) {
            this.log_content = log_content;
        }
    }
}
