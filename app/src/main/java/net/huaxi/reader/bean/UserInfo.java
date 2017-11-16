package net.huaxi.reader.bean;

import android.content.Intent;
import android.text.TextUtils;

import net.huaxi.reader.LoginActivity;
import net.huaxi.reader.MyApplication;
import net.huaxi.reader.utils.FileHelper;

/**
 * Created by panhui on 2017/8/5.
 */

public class UserInfo extends BaseResponse {
    private static final long serialVersionUID = -1470045421200602327L;
    public static UserInfo userInfo = null;

    private String id;
    private String user_nickname;
    private String user_birthday;
    private String user_wechat;
    private String user_qq;
    private String user_oldnickname;
    private String user_gender;
    private String user_id;
    private String user_mobile;
    private String user_avatar;
    private int user_real_point;
    private int user_false_point;
    private String user_vip_class;
    private String user_favorites;
    private String user_message;
    private int user_vip;//0不是vip，1是
    private long user_vip_expire_time;
    private String auther_name;//基本都用这个现实用户名
    private String auther_oldnickname;
    private String auther_novel;
    private String auther_qq;
    private String auther_mobile;
    private String user_funs;
    private String user_follow;
    private String user_backg;
    private String token = "";

    public static UserInfo getInstance() {
        if (userInfo == null) {
            synchronized (UserInfo.class) {
                if (userInfo == null) {
                    userInfo = new UserInfo();
                }
            }
        }
        return userInfo;
    }

    public UserInfo() {
        userInfo = this;
    }

    public UserInfo(String user_id, String user_nickname, String user_gender, String user_mobile, String user_avatar, int user_real_point,
                    int user_false_point, String user_vip_class, String user_favorites, String user_message, String user_birthday, String user_qq) {
        super();
        this.user_id = user_id;
        this.user_nickname = user_nickname;
        this.user_gender = user_gender;
        this.user_mobile = user_mobile;
        this.user_avatar = user_avatar;
        this.user_real_point = user_real_point;
        this.user_false_point = user_false_point;
        this.user_vip_class = user_vip_class;
        this.user_favorites = user_favorites;
        this.user_message = user_message;
        this.user_birthday = user_birthday;
        this.user_qq = user_qq;
        userInfo = this;
    }
    /**
     * 获取缓存
     */
    public void restData() {
        UserInfo userInfoTemp = (UserInfo) FileHelper.getEntity();
        if (userInfoTemp != null)
            userInfo = userInfoTemp;
    }


    /**
     * 获取缓存
     */
    public void commit() {
        FileHelper.saveEntity(userInfo);
    }

    public void exit() {
        userInfo = new UserInfo();
    }

    /**
     * 是否登录
     *
     * @return
     */
    public Boolean isLogin() {
        return isLogin(false);
    }

    /**
     * 是否登录
     *
     * @return
     */
    public Boolean isLogin(boolean toLogin) {
        if (user_id != null && token != null && !user_id.equals("")) {
            return true;
        } else {
            if (toLogin) {
                Intent intent = new Intent(MyApplication.getInstance(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                MyApplication.getInstance().startActivity(intent);
            }
            return false;
        }
    }

    public long getUser_vip_expire_time() {
        return user_vip_expire_time;
    }

    public void setUser_vip_expire_time(long user_vip_expire_time) {
        this.user_vip_expire_time = user_vip_expire_time;
    }

    public String getUser_id() {
        return TextUtils.isEmpty(user_id)?"0":user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_nickname() {

        return user_nickname;
    }

    public String getUser_qq() {
        return user_qq;
    }

    public void setUser_qq(String user_qq) {
        this.user_qq = user_qq;
    }

    public void setUser_nickname(String user_nickname) {
        this.user_nickname = user_nickname;
    }

    public String getUser_gender() {
        return user_gender;
    }

    public void setUser_gender(String user_gender) {
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

    public String getUser_real_point() {
        return user_real_point+"";
    }

    public void setUser_real_point(int user_real_point) {
        this.user_real_point = user_real_point;
    }

    public String getUser_false_point() {
        return user_false_point+"";
    }

    public void setUser_false_point(int user_false_point) {
        this.user_false_point = user_false_point;
    }

    public String getUser_vip_class() {
        return user_vip_class;
    }

    public void setUser_vip_class(String user_vip_class) {
        this.user_vip_class = user_vip_class;
    }

    public String getUser_favorites() {
        return user_favorites;
    }

    public void setUser_favorites(String user_favorites) {
        this.user_favorites = user_favorites;
    }

    public String getUser_message() {
        return user_message;
    }

    public void setUser_message(String user_message) {
        this.user_message = user_message;
    }

    public String getUser_birthday() {
        return user_birthday;
    }

    public void setUser_birthday(String user_birthday) {
        this.user_birthday = user_birthday;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_wechat() {
        return user_wechat;
    }

    public void setUser_wechat(String user_wechat) {
        this.user_wechat = user_wechat;
    }

    public String getUser_oldnickname() {
        return user_oldnickname;
    }

    public void setUser_oldnickname(String user_oldnickname) {
        this.user_oldnickname = user_oldnickname;
    }

    public int getUser_vip() {
        return user_vip;
    }

    public void setUser_vip(int user_vip) {
        this.user_vip = user_vip;
    }

    public String getAuther_name() {
        return auther_name;
    }

    public void setAuther_name(String auther_name) {
        this.auther_name = auther_name;
    }

    public String getAuther_oldnickname() {
        return auther_oldnickname;
    }

    public void setAuther_oldnickname(String auther_oldnickname) {
        this.auther_oldnickname = auther_oldnickname;
    }

    public String getAuther_novel() {
        return auther_novel;
    }

    public void setAuther_novel(String auther_novel) {
        this.auther_novel = auther_novel;
    }

    public String getAuther_qq() {
        return auther_qq;
    }

    public void setAuther_qq(String auther_qq) {
        this.auther_qq = auther_qq;
    }

    public String getAuther_mobile() {
        return auther_mobile;
    }

    public void setAuther_mobile(String auther_mobile) {
        this.auther_mobile = auther_mobile;
    }

    public String getUser_funs() {
        return user_funs;
    }

    public void setUser_funs(String user_funs) {
        this.user_funs = user_funs;
    }

    public String getUser_follow() {
        return user_follow;
    }

    public void setUser_follow(String user_follow) {
        this.user_follow = user_follow;
    }

    public String getUser_backg() {
        return user_backg;
    }

    public void setUser_backg(String user_backg) {
        this.user_backg = user_backg;
    }


    @Override
    public String toString() {
        return "UserInfo{" +
                "id='" + id + '\'' +
                ", user_nickname='" + user_nickname + '\'' +
                ", user_birthday='" + user_birthday + '\'' +
                ", user_wechat='" + user_wechat + '\'' +
                ", user_qq='" + user_qq + '\'' +
                ", user_oldnickname='" + user_oldnickname + '\'' +
                ", user_gender='" + user_gender + '\'' +
                ", user_id='" + user_id + '\'' +
                ", user_mobile='" + user_mobile + '\'' +
                ", user_avatar='" + user_avatar + '\'' +
                ", user_real_point='" + user_real_point + '\'' +
                ", user_false_point='" + user_false_point + '\'' +
                ", user_vip_class='" + user_vip_class + '\'' +
                ", user_favorites='" + user_favorites + '\'' +
                ", user_message='" + user_message + '\'' +
                ", user_vip='" + user_vip + '\'' +
                ", auther_name='" + auther_name + '\'' +
                ", auther_oldnickname='" + auther_oldnickname + '\'' +
                ", auther_novel='" + auther_novel + '\'' +
                ", auther_qq='" + auther_qq + '\'' +
                ", auther_mobile='" + auther_mobile + '\'' +
                ", user_funs='" + user_funs + '\'' +
                ", user_follow='" + user_follow + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
