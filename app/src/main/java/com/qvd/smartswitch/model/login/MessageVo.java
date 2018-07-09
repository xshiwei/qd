package com.qvd.smartswitch.model.login;

/**
 * Created by Administrator on 2018-7-9.
 */

public class MessageVo {

    /**
     * code : 400
     * message : username and email or phone is not same
     */

    private int code;
    private String message;

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
}
