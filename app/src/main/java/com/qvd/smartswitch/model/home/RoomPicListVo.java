package com.qvd.smartswitch.model.home;

import java.util.List;

public class RoomPicListVo {
    /**
     * code : 200
     * message : ok
     * data : [{"id":10,"room_max_pic":"http://pckgfzc5s.bkt.clouddn.com/j@3x.png","room_min_pic":"http://pckgfzc5s.bkt.clouddn.com/j.png","room_name":"activitiesroom"},{"id":16,"room_max_pic":"http://pckgfzc5s.bkt.clouddn.com/p@3x.png","room_min_pic":"http://pckgfzc5s.bkt.clouddn.com/p.png","room_name":"backyard"},{"id":5,"room_max_pic":"http://pckgfzc5s.bkt.clouddn.com/e@3x.png","room_min_pic":"http://pckgfzc5s.bkt.clouddn.com/e.png","room_name":"bathroom"},{"id":19,"room_max_pic":"http://pckgfzc5s.bkt.clouddn.com/b@3x.png","room_min_pic":"http://pckgfzc5s.bkt.clouddn.com/b.png","room_name":"bedroom"},{"id":7,"room_max_pic":"http://pckgfzc5s.bkt.clouddn.com/g@3x.png","room_min_pic":"http://pckgfzc5s.bkt.clouddn.com/g.png","room_name":"childrenroom"},{"id":15,"room_max_pic":"http://pckgfzc5s.bkt.clouddn.com/o@3x.png","room_min_pic":"http://pckgfzc5s.bkt.clouddn.com/o.png","room_name":"cloakroom"},{"id":6,"room_max_pic":"http://pckgfzc5s.bkt.clouddn.com/f@3x.png","room_min_pic":"http://pckgfzc5s.bkt.clouddn.com/f.png","room_name":"kitchen"},{"id":13,"room_max_pic":"http://pckgfzc5s.bkt.clouddn.com/m@3x.png","room_min_pic":"http://pckgfzc5s.bkt.clouddn.com/m.png","room_name":"lounge"},{"id":11,"room_max_pic":"http://pckgfzc5s.bkt.clouddn.com/k@3x.png","room_min_pic":"http://pckgfzc5s.bkt.clouddn.com/k.png","room_name":"mediaroom"},{"id":8,"room_max_pic":"http://pckgfzc5s.bkt.clouddn.com/h@3x.png","room_min_pic":"http://pckgfzc5s.bkt.clouddn.com/h.png","room_name":"nursery"},{"id":12,"room_max_pic":"http://pckgfzc5s.bkt.clouddn.com/l@3x.png","room_min_pic":"http://pckgfzc5s.bkt.clouddn.com/l.png","room_name":"office"},{"id":1,"room_max_pic":"http://pckgfzc5s.bkt.clouddn.com/A@3x.png","room_min_pic":"http://pckgfzc5s.bkt.clouddn.com/a.png","room_name":"parlour"},{"id":3,"room_max_pic":"http://pckgfzc5s.bkt.clouddn.com/c@3x.png","room_min_pic":"http://pckgfzc5s.bkt.clouddn.com/c.png","room_name":"restaurant"},{"id":14,"room_max_pic":"http://pckgfzc5s.bkt.clouddn.com/n@3x.png","room_min_pic":"http://pckgfzc5s.bkt.clouddn.com/n.png","room_name":"stationery"},{"id":4,"room_max_pic":"http://pckgfzc5s.bkt.clouddn.com/d@3x.png","room_min_pic":"http://pckgfzc5s.bkt.clouddn.com/d.png","room_name":"toilet"},{"id":9,"room_max_pic":"http://pckgfzc5s.bkt.clouddn.com/i@3x.png","room_min_pic":"http://pckgfzc5s.bkt.clouddn.com/i.png","room_name":"workroom"}]
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
         * id : 10
         * room_max_pic : http://pckgfzc5s.bkt.clouddn.com/j@3x.png
         * room_min_pic : http://pckgfzc5s.bkt.clouddn.com/j.png
         * room_name : activitiesroom
         */

        private int id;
        private String room_max_pic;
        private String room_min_pic;
        private String room_name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getRoom_max_pic() {
            return room_max_pic;
        }

        public void setRoom_max_pic(String room_max_pic) {
            this.room_max_pic = room_max_pic;
        }

        public String getRoom_min_pic() {
            return room_min_pic;
        }

        public void setRoom_min_pic(String room_min_pic) {
            this.room_min_pic = room_min_pic;
        }

        public String getRoom_name() {
            return room_name;
        }

        public void setRoom_name(String room_name) {
            this.room_name = room_name;
        }
    }
}
