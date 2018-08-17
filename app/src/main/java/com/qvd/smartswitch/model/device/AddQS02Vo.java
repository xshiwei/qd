package com.qvd.smartswitch.model.device;

public class AddQS02Vo {
    /**
     * code : 200
     * message : ok
     * device_id : 123234a
     */

    private int code;
    private String message;
    private String device_id;

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

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }
}
