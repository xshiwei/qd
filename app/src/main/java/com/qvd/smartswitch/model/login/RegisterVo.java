package com.qvd.smartswitch.model.login;

public class RegisterVo {

    /**
     * code : 200
     * message : ok
     * map_string : {"identifier":"xiongtony007@gmail.com","password":"202cb962ac59075b964b07152d234b70","user_id":"32947a727127cebbec5295bc445a8eab"}
     */

    private int code;
    private String message;
    private MapStringBean map_string;

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

    public MapStringBean getMap_string() {
        return map_string;
    }

    public void setMap_string(MapStringBean map_string) {
        this.map_string = map_string;
    }

    public static class MapStringBean {
        /**
         * identifier : xiongtony007@gmail.com
         * password : 202cb962ac59075b964b07152d234b70
         * user_id : 32947a727127cebbec5295bc445a8eab
         */

        private String identifier;
        private String password;
        private String user_id;

        public String getIdentifier() {
            return identifier;
        }

        public void setIdentifier(String identifier) {
            this.identifier = identifier;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }
    }
}
