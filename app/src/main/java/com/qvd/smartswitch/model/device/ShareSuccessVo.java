package com.qvd.smartswitch.model.device;

public class ShareSuccessVo {
    /**
     * code : 205
     * message : share devices is not same
     * is_share : 1
     */

    private int code;
    private String message;
    private int is_share;

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

    public int getIs_share() {
        return is_share;
    }

    public void setIs_share(int is_share) {
        this.is_share = is_share;
    }
}
