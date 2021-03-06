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
    private long user_qq;
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
    private int has_role;//0不是，1是
    private int read_timespan;//上传时长的秒数
    private int gold_coins;//用户金币
    private double rmb;//人民币
    private int user_package;

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

    public int getUser_package() {
        return user_package;
    }

    public void setUser_package(int user_package) {
        this.user_package = user_package;
    }

    public double getRmb() {
        return rmb;
    }

    public void setRmb(double rmb) {
        this.rmb = rmb;
    }

    public int getGold_coins() {
        return gold_coins;
    }

    public void setGold_coins(int gold_coins) {
        this.gold_coins = gold_coins;
    }

    public int getRead_timespan() {
        return read_timespan;
    }

    public void setRead_timespan(int read_timespan) {
        this.read_timespan = read_timespan;
    }

    public int getHas_role() {
        return has_role;
    }

    public void setHas_role(int has_role) {
        this.has_role = has_role;
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

    public long getUser_qq() {
        return user_qq;
    }

    public void setUser_qq(long user_qq) {
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

    @Override
    public String toString() {
        return "UserBean{" +
                "id=" + id +
                ", user_nickname='" + user_nickname + '\'' +
                ", user_birthday=" + user_birthday +
                ", user_wechat='" + user_wechat + '\'' +
                ", user_qq=" + user_qq +
                ", user_oldnickname=" + user_oldnickname +
                ", user_gender=" + user_gender +
                ", user_id=" + user_id +
                ", user_mobile='" + user_mobile + '\'' +
                ", user_avatar='" + user_avatar + '\'' +
                ", user_real_point=" + user_real_point +
                ", user_false_point=" + user_false_point +
                ", user_vip_class=" + user_vip_class +
                ", user_favorites=" + user_favorites +
                ", user_message=" + user_message +
                ", has_role=" + has_role +
                '}';
    }
}
