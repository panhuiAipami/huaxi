package com.spriteapp.booklibrary.model.response;

import java.io.Serializable;

/**
 * Created by kuangxiaoguo on 2017/7/8.
 */

public class LoginResponse implements Serializable {

    private int user_id;
    private String user_nickname;
    private int user_gender;
    private String user_mobile;
    private String user_avatar;
    private int user_real_point;
    private int user_false_point;
    private int user_vip_class;

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUser_nickname() {
        return user_nickname;
    }

    public void setUser_nickname(String user_nickname) {
        this.user_nickname = user_nickname;
    }

    public int getUser_gender() {
        return user_gender;
    }

    public void setUser_gender(int user_gender) {
        this.user_gender = user_gender;
    }

    public String getUser_mobile() {
        return user_mobile;
    }

    public void setUser_mobile(String user_mobile) {
        this.user_mobile = user_mobile;
    }

    public String getUser_avatar() {
        return user_avatar;
    }

    public void setUser_avatar(String user_avatar) {
        this.user_avatar = user_avatar;
    }

    public int getUser_real_point() {
        return user_real_point;
    }

    public void setUser_real_point(int user_real_point) {
        this.user_real_point = user_real_point;
    }

    public int getUser_false_point() {
        return user_false_point;
    }

    public void setUser_false_point(int user_false_point) {
        this.user_false_point = user_false_point;
    }

    public int getUser_vip_class() {
        return user_vip_class;
    }

    public void setUser_vip_class(int user_vip_class) {
        this.user_vip_class = user_vip_class;
    }
}
