package com.spriteapp.booklibrary.model;

/**
 * Created by Administrator on 2018/1/8.
 * 社区帖子阅读记录
 */

public class ReadHistoryBean {
    /**
     * userid : 3
     * user_avatar : https://img.huaxi.net/userhead/3.jpg
     */

    private String userid;
    private String user_avatar;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUser_avatar() {
        return user_avatar;
    }

    public void setUser_avatar(String user_avatar) {
        this.user_avatar = user_avatar;
    }
}
