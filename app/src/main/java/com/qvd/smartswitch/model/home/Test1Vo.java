package com.qvd.smartswitch.model.home;

import java.util.List;

/**
 * Created by Administrator on 2018/6/7 0007.
 */

public class Test1Vo {
    private String room;
    private List<ArgumentsBean> arguments;

    public Test1Vo(String room, List<ArgumentsBean> arguments) {
        this.room = room;
        this.arguments = arguments;
    }

    public List<ArgumentsBean> getArguments() {
        return arguments;
    }

    public void setArguments(List<ArgumentsBean> arguments) {
        this.arguments = arguments;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }


    public static class ArgumentsBean {
        private String text;

        public ArgumentsBean(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
