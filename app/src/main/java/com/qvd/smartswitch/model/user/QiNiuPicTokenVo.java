package com.qvd.smartswitch.model.user;

public class QiNiuPicTokenVo {
    /**
     * code : 200
     * message : ok
     * uploadToken : :QCBqIfFHg0qga8s6c7Bpxblb8XI=:eyJzY29wZSI6IiIsImRlYWRsaW5lIjoxNTQwOTU5MDM3fQ==
     */

    private int code;
    private String message;
    private String uploadToken;

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

    public String getUploadToken() {
        return uploadToken;
    }

    public void setUploadToken(String uploadToken) {
        this.uploadToken = uploadToken;
    }
}
