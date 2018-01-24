package com.spriteapp.booklibrary.model;

import com.spriteapp.booklibrary.util.FileHelper;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/1/15.
 */

public class UserBean implements Serializable {
    public static UserBean userBean = null;

    public static UserBean getInstance() {
        if (userBean == null) {
            synchronized (UserBean.class) {
                if (userBean == null) {
                    userBean = new UserBean();
                }
            }
        }
        return userBean;
    }

    public UserBean() {
        userBean = this;
    }


    /**
     * 获取缓存
     */
    public void restData() {
        UserBean userInfoTemp = (UserBean) FileHelper.getEntity();
        if (userInfoTemp != null)
            userBean = userInfoTemp;
    }


    /**
     * 获取缓存
     */
    public void commit() {
        FileHelper.saveEntity(userBean);
    }


    private int id;
    private String user_nickname;
    private int user_birthday;
    private String user_wechat;
    private int user_qq;
    private int user_oldnickname;
    private int user_gender;
    private int user_id;
    private String user_mobile;
    private String user_avatar;
    private int user_real_point;
    private int user_false_point;
    private int user_vip_class;
    private int user_favorites;
    private int user_message;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser_nickname() {
        if (user_nickname == null)
            user_nickname = "";
        return user_nickname;
    }

    public void setUser_nickname(String user_nickname) {
        this.user_nickname = user_nickname;
    }

    public int getUser_birthday() {
        return user_birthday;
    }

    public void setUser_birthday(int user_birthday) {
        this.user_birthday = user_birthday;
    }

    public String getUser_wechat() {
        return user_wechat;
    }

    public void setUser_wechat(String user_wechat) {
        this.user_wechat = user_wechat;
    }

    public int getUser_qq() {
        return user_qq;
    }

    public void setUser_qq(int user_qq) {
        this.user_qq = user_qq;
    }

    public int getUser_oldnickname() {
        return user_oldnickname;
    }

    public void setUser_oldnickname(int user_oldnickname) {
        this.user_oldnickname = user_oldnickname;
    }

    public int getUser_gender() {
        return user_gender;
    }

    public void setUser_gender(int user_gender) {
        this.user_gender = user_gender;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUser_mobile() {
        return user_mobile;
    }

    public void setUser_mobile(String user_mobile) {
        this.user_mobile = user_mobile;
    }

    public String getUser_avatar() {
        if (user_avatar == null)
            user_avatar = "";
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

    public int getUser_favorites() {
        return user_favorites;
    }

    public void setUser_favorites(int user_favorites) {
        this.user_favorites = user_favorites;
    }

    public int getUser_message() {
        return user_message;
    }

    public void setUser_message(int user_message) {
        this.user_message = user_message;
    }
}
