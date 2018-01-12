package com.spriteapp.booklibrary.model;

/**
 * Created by Administrator on 2018/1/8.
 * 社区帖子评论实体类
 */

public class CommentBean {
    /**
     * id : 4
     * squareid : 21
     * content : 111
     * addtime : 1515142165
     * updatetime : 1515149040
     * userid : 173
     * username : 九尾狐
     * supportnum : 0
     * replyto : 0
     * replyusername : null
     * user_avatar : https://img.huaxi.net/userhead/173.jpg
     */

    private String id;
    private String squareid;
    private String content;
    private String addtime;
    private String updatetime;
    private String userid;
    private String username;
    private String supportnum;
    private String replyto;
    private String replyusername;
    private String user_avatar;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSquareid() {
        return squareid;
    }

    public void setSquareid(String squareid) {
        this.squareid = squareid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSupportnum() {
        return supportnum;
    }

    public void setSupportnum(String supportnum) {
        this.supportnum = supportnum;
    }

    public String getReplyto() {
        return replyto;
    }

    public void setReplyto(String replyto) {
        this.replyto = replyto;
    }

    public String getReplyusername() {
        return replyusername;
    }

    public void setReplyusername(String replyusername) {
        this.replyusername = replyusername;
    }

    public String getUser_avatar() {
        return user_avatar;
    }

    public void setUser_avatar(String user_avatar) {
        this.user_avatar = user_avatar;
    }
}
