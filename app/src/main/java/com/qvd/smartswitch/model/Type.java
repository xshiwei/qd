package com.qvd.smartswitch.model;

/**
 * Created by Administrator on 2018/5/17 0017.
 */

public class Type {
    private int imageUrl;
    private String name;

    public Type(int imageUrl, String name) {
        this.imageUrl = imageUrl;
        this.name = name;
    }

    public int getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(int imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
