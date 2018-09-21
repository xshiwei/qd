package com.qvd.smartswitch.model.user;

import java.util.List;

public class FamilyListVo {

    /**
     * code : 200
     * message : ok
     * data : {"master_family_members":[{"add_time":"2018-09-18 17:36:59","family_members_id":"567237325caa579cc8e686e4611d3d5d","family_members_relation":"室友","family_members_userid":"567237325caa579cc8e686e4611d3d5d","is_agree":1,"user_avatar":"https://www.qq745.com/uploads/allimg/141009/1-14100ZT451-51.jpg","user_name":"15986635874"},{"add_time":"2018-09-18 17:58:11","family_members_id":"78b3c74b49a85b973eddbd97a2ce91fd","family_members_relation":"战友","family_members_userid":"78b3c74b49a85b973eddbd97a2ce91fd","is_agree":1,"user_avatar":"https://www.qq745.com/uploads/allimg/141009/1-14100ZT451-51.jpg","user_name":"18898356076"}],"slave_family_members":[{"add_time":"2018-09-18 18:00:15","family_members_id":"2a72d8cbdb8846a3423e02fbb1951f28","family_members_relation":"基友","is_agree":0,"user_avatar":"https://www.qq745.com/uploads/allimg/141009/1-14100ZT451-51.jpg","user_id":"4de218dada69be49d44a7d34922fa95a","user_name":"13631787352"},{"add_time":"2018-09-18 18:01:13","family_members_id":"2a72d8cbdb8846a3423e02fbb1951f28","family_members_relation":"猪队友","is_agree":1,"user_avatar":"https://www.qq745.com/uploads/allimg/141009/1-14100ZT451-51.jpg","user_id":"c0f3e214be19d22a4c10c052983f8a24","user_name":"1105943292@qq.com"}]}
     */

    private int code;
    private String message;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private List<MasterFamilyMembersBean> master_family_members;
        private List<SlaveFamilyMembersBean> slave_family_members;

        public List<MasterFamilyMembersBean> getMaster_family_members() {
            return master_family_members;
        }

        public void setMaster_family_members(List<MasterFamilyMembersBean> master_family_members) {
            this.master_family_members = master_family_members;
        }

        public List<SlaveFamilyMembersBean> getSlave_family_members() {
            return slave_family_members;
        }

        public void setSlave_family_members(List<SlaveFamilyMembersBean> slave_family_members) {
            this.slave_family_members = slave_family_members;
        }

        public static class MasterFamilyMembersBean {
            /**
             * add_time : 2018-09-18 17:36:59
             * family_members_id : 567237325caa579cc8e686e4611d3d5d
             * family_members_relation : 室友
             * family_members_userid : 567237325caa579cc8e686e4611d3d5d
             * is_agree : 1
             * user_avatar : https://www.qq745.com/uploads/allimg/141009/1-14100ZT451-51.jpg
             * user_name : 15986635874
             */

            private String add_time;
            private String family_members_id;
            private String family_members_relation;
            private String family_members_userid;
            private int is_agree;
            private String user_avatar;
            private String user_name;

            public String getAdd_time() {
                return add_time;
            }

            public void setAdd_time(String add_time) {
                this.add_time = add_time;
            }

            public String getFamily_members_id() {
                return family_members_id;
            }

            public void setFamily_members_id(String family_members_id) {
                this.family_members_id = family_members_id;
            }

            public String getFamily_members_relation() {
                return family_members_relation;
            }

            public void setFamily_members_relation(String family_members_relation) {
                this.family_members_relation = family_members_relation;
            }

            public String getFamily_members_userid() {
                return family_members_userid;
            }

            public void setFamily_members_userid(String family_members_userid) {
                this.family_members_userid = family_members_userid;
            }

            public int getIs_agree() {
                return is_agree;
            }

            public void setIs_agree(int is_agree) {
                this.is_agree = is_agree;
            }

            public String getUser_avatar() {
                return user_avatar;
            }

            public void setUser_avatar(String user_avatar) {
                this.user_avatar = user_avatar;
            }

            public String getUser_name() {
                return user_name;
            }

            public void setUser_name(String user_name) {
                this.user_name = user_name;
            }
        }

        public static class SlaveFamilyMembersBean {
            /**
             * add_time : 2018-09-18 18:00:15
             * family_members_id : 2a72d8cbdb8846a3423e02fbb1951f28
             * family_members_relation : 基友
             * is_agree : 0
             * user_avatar : https://www.qq745.com/uploads/allimg/141009/1-14100ZT451-51.jpg
             * user_id : 4de218dada69be49d44a7d34922fa95a
             * user_name : 13631787352
             */

            private String add_time;
            private String family_members_id;
            private String family_members_relation;
            private int is_agree;
            private String user_avatar;
            private String user_id;
            private String user_name;

            public String getAdd_time() {
                return add_time;
            }

            public void setAdd_time(String add_time) {
                this.add_time = add_time;
            }

            public String getFamily_members_id() {
                return family_members_id;
            }

            public void setFamily_members_id(String family_members_id) {
                this.family_members_id = family_members_id;
            }

            public String getFamily_members_relation() {
                return family_members_relation;
            }

            public void setFamily_members_relation(String family_members_relation) {
                this.family_members_relation = family_members_relation;
            }

            public int getIs_agree() {
                return is_agree;
            }

            public void setIs_agree(int is_agree) {
                this.is_agree = is_agree;
            }

            public String getUser_avatar() {
                return user_avatar;
            }

            public void setUser_avatar(String user_avatar) {
                this.user_avatar = user_avatar;
            }

            public String getUser_id() {
                return user_id;
            }

            public void setUser_id(String user_id) {
                this.user_id = user_id;
            }

            public String getUser_name() {
                return user_name;
            }

            public void setUser_name(String user_name) {
                this.user_name = user_name;
            }
        }
    }
}
