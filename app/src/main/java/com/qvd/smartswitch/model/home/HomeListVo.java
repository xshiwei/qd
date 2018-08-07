package com.qvd.smartswitch.model.home;

import java.util.List;

public class HomeListVo {

    /**
     * code : 200
     * message : ok
     * data : [{"create_time":"2018-07-24 19:30:20.000000","delete_time":null,"family_background":"https://www.qq745.com/uploads/allimg/141009/1-14100ZT451-51.jpg","family_id":"F00000005","family_location":" 浙江省 杭州市 滨江区","family_name":"人生","id":13,"is_delete":0,"is_opened":0,"update_time":null,"user_id":"c0f3e214be19d22a4c10c052983f8a24"},{"create_time":"2018-07-24 19:16:18.000000","delete_time":null,"family_background":"https://www.qq745.com/uploads/allimg/141009/1-14100ZT451-51.jpg","family_id":"F00000003","family_location":" 浙江省 杭州市 滨江区","family_name":"你好","id":11,"is_delete":0,"is_opened":0,"update_time":null,"user_id":"c0f3e214be19d22a4c10c052983f8a24"},{"create_time":"2018-07-24 19:27:43.000000","delete_time":null,"family_background":"https://www.qq745.com/uploads/allimg/141009/1-14100ZT451-51.jpg","family_id":"F00000004","family_location":" 浙江省 杭州市 滨江区","family_name":"你好","id":12,"is_delete":0,"is_opened":0,"update_time":null,"user_id":"c0f3e214be19d22a4c10c052983f8a24"},{"create_time":"2018-07-24 19:43:19.000000","delete_time":null,"family_background":"https://www.qq745.com/uploads/allimg/141009/1-14100ZT451-51.jpg","family_id":"F00000007","family_location":" 浙江省 杭州市 滨江区","family_name":"好","id":20,"is_delete":0,"is_opened":0,"update_time":null,"user_id":"c0f3e214be19d22a4c10c052983f8a24"}]
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
         * create_time : 2018-07-24 19:30:20.000000
         * delete_time : null
         * family_background : https://www.qq745.com/uploads/allimg/141009/1-14100ZT451-51.jpg
         * family_id : F00000005
         * family_location :  浙江省 杭州市 滨江区
         * family_name : 人生
         * id : 13
         * is_delete : 0
         * is_opened : 0
         * update_time : null
         * user_id : c0f3e214be19d22a4c10c052983f8a24
         */

        private String create_time;
        private Object delete_time;
        private String family_background;
        private String family_id;
        private String family_location;
        private String family_name;
        private int id;
        private int is_delete;
        private int is_opened;
        private Object update_time;
        private String user_id;

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public Object getDelete_time() {
            return delete_time;
        }

        public void setDelete_time(Object delete_time) {
            this.delete_time = delete_time;
        }

        public String getFamily_background() {
            return family_background;
        }

        public void setFamily_background(String family_background) {
            this.family_background = family_background;
        }

        public String getFamily_id() {
            return family_id;
        }

        public void setFamily_id(String family_id) {
            this.family_id = family_id;
        }

        public String getFamily_location() {
            return family_location;
        }

        public void setFamily_location(String family_location) {
            this.family_location = family_location;
        }

        public String getFamily_name() {
            return family_name;
        }

        public void setFamily_name(String family_name) {
            this.family_name = family_name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getIs_delete() {
            return is_delete;
        }

        public void setIs_delete(int is_delete) {
            this.is_delete = is_delete;
        }

        public int getIs_opened() {
            return is_opened;
        }

        public void setIs_opened(int is_opened) {
            this.is_opened = is_opened;
        }

        public Object getUpdate_time() {
            return update_time;
        }

        public void setUpdate_time(Object update_time) {
            this.update_time = update_time;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }
    }
}
