package com.qvd.smartswitch.model.home;

/**
 * Created by Administrator on 2018/6/13 0013.
 */

class RoomDeviceVo {
    private String text;
    private boolean selete;

    public RoomDeviceVo(String text, boolean selete) {
        this.text = text;
        this.selete = selete;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isSelete() {
        return selete;
    }

    public void setSelete(boolean selete) {
        this.selete = selete;
    }
}
