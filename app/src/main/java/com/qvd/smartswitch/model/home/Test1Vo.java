package com.qvd.smartswitch.model.home;

import java.util.List;

/**
 * Created by Administrator on 2018/6/7 0007.
 */

public class Test1Vo {
    private String room;

    public Test1Vo(String room) {
        this.room = room;
    }

    public String getText() {
        return room;
    }

    public void setText(String text) {
        this.room = text;
    }
}
