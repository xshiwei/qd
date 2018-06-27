package com.qvd.smartswitch.model.home;

/**
 * Created by Administrator on 2018/6/6 0006.
 */

public class TestVo {
    private String test;
    private int type;

    public String getTest() {
        return test;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public TestVo(String test, int type) {
        this.test = test;
        this.type = type;
    }

    public void setTest(String test) {
        this.test = test;
    }
}
