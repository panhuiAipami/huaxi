package net.huaxi.reader.bean;

import com.google.gson.annotations.SerializedName;
import net.huaxi.reader.common.Constants;

import java.io.Serializable;

/**
 * Created by ZMW on 2015/12/22.
 * 用户
 */
public class User implements Serializable{

    private static final long serialVersionUID = -3227060667561638724L;

    //用户类型type可能的值
    public static final int USER_TYPE_UNKNOWN= -1;	//	未知用户类型
    public static final int USER_TYPE_UNM = 0;                       //老用户类型
    public static final int  USER_TYPE_EMAIL = 1;                     //邮箱用户
    public static final int  USER_TYPE_MOBILE = 2;                    //手机用户
    public static final int  USER_TYPE_QQ = 3;                        //QQ用户
    public static final int  USER_TYPE_AUTH_QQ = 4;                   //第三方登录QQ
    public static final int  USER_TYPE_AUTH_WECHAT = 5;               //第三方登录微信
    public static final int  USER_TYPE_AUTH_WEIBO = 6;                //第三方登录微博
    public static final int  USER_TYPE_AUTH_ALI = 7;                  //第三方登录阿里
    public static final int  USER_TYPE_ANONYMOU = 100;                  //手机客户端匿名登录
    //source可能的值。。
    public static final int SOURCE_ANDROID = 1;    //      Android
    public static final int SOURCE_IOS = 2;    //      IOS

    //status用户状态
    public static final int STATUS_UNVERIFIED = 0;    //	unverified
    public static final int STATUS_OKAY = 1;    //	okay
    public static final int STATUS_DELETED = 2;    //	deleted

    @SerializedName("u_mid")
    private String umid = Constants.DEFAULT_USERID;
    @SerializedName("u_gender")
    private String gender;
    @SerializedName("u_status")
    private int status;
    @SerializedName("u_birth")
    private String birth;
    @SerializedName("u_mobile")
    private String mobile;
    @SerializedName("u_type")
    private int type;
    @SerializedName("u_imgid")
    private String imgid;
    @SerializedName("u_nickname")
    private String nickname;
    @SerializedName("u_action")
    private String action;
    @SerializedName("u_source")
    private String source;
    @SerializedName("u_qq")
    private String qq;
    @SerializedName("u_email")
    private String email;
    @SerializedName("u_wechat")
    private String wechat;
    @SerializedName("u_weibo")
    private String weibo;

    public String getUmid() {
        return umid;
    }

    public void setUmid(String umid) {
        this.umid = umid;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }


    public String getImgid() {
        return imgid;
    }

    public void setImgid(String imgid) {
        this.imgid = imgid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }

    public String getWeibo() {
        return weibo;
    }

    public void setWeibo(String weibo) {
        this.weibo = weibo;
    }
}
