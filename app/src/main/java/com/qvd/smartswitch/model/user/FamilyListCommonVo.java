package com.qvd.smartswitch.model.user;

import java.io.Serializable;

public class FamilyListCommonVo implements Serializable {
    private String family_id;
    private String family_relation;
    private String family_userid;
    private String family_avatar;
    private String family_name;

    public FamilyListCommonVo(String family_id, String family_relation, String family_userid, String family_avatar, String family_name) {
        this.family_id = family_id;
        this.family_relation = family_relation;
        this.family_userid = family_userid;
        this.family_avatar = family_avatar;
        this.family_name = family_name;
    }

    public String getFamily_id() {
        return family_id;
    }

    public void setFamily_id(String family_id) {
        this.family_id = family_id;
    }

    public String getFamily_relation() {
        return family_relation;
    }

    public void setFamily_relation(String family_relation) {
        this.family_relation = family_relation;
    }

    public String getFamily_userid() {
        return family_userid;
    }

    public void setFamily_userid(String family_userid) {
        this.family_userid = family_userid;
    }

    public String getFamily_avatar() {
        return family_avatar;
    }

    public void setFamily_avatar(String family_avatar) {
        this.family_avatar = family_avatar;
    }

    public String getFamily_name() {
        return family_name;
    }

    public void setFamily_name(String family_name) {
        this.family_name = family_name;
    }
}
