package com.spriteapp.booklibrary.model;

import java.util.List;

/**
 * Created by Administrator on 2018/1/6.
 * 社区详情实体类
 */

public class SquareBean {

    /**
     * id : 22
     * userid : 1
     * subject : 111
     * pic_url : 222
     * addtime : 1515123066
     * isauthor : 0
     * readnum : 0
     * supportnum : 0
     * commentnum : 0
     * location : 未知星球
     * username : 东方树叶
     * user_avatar : https://img.huaxi.net/userhead/1.jpg
     * readhistory : []
     * comments : []
     */

    private int id;
    private String userid;
    private String subject;
    private List<String> pic_url;
    private long addtime;
    private String isauthor;
    private int readnum;
    private int supportnum;
    private int commentnum;
    private String location;
    private String username;
    private String user_avatar;
    private int follow_status;
    private List<ReadHistoryBean> readhistory;
    private List<CommentBean> comments;
    private List<CommentReply> commentList;
    private int height;

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public List<String> getPic_url() {
        return pic_url;
    }

    public void setPic_url(List<String> pic_url) {
        this.pic_url = pic_url;
    }

    public long getAddtime() {
        return addtime;
    }

    public void setAddtime(long addtime) {
        this.addtime = addtime;
    }

    public String getIsauthor() {
        return isauthor;
    }

    public void setIsauthor(String isauthor) {
        this.isauthor = isauthor;
    }

    public int getReadnum() {
        return readnum;
    }

    public void setReadnum(int readnum) {
        this.readnum = readnum;
    }

    public int getSupportnum() {
        return supportnum;
    }

    public void setSupportnum(int supportnum) {
        this.supportnum = supportnum;
    }

    public int getCommentnum() {
        return commentnum;
    }

    public void setCommentnum(int commentnum) {
        this.commentnum = commentnum;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getFollow_status() {
        return follow_status;
    }

    public void setFollow_status(int follow_status) {
        this.follow_status = follow_status;
    }

    public String getUsername() {
        if (username == null)
            return "";
        else
            return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUser_avatar() {
        return user_avatar;
    }

    public void setUser_avatar(String user_avatar) {
        this.user_avatar = user_avatar;
    }

    public List<ReadHistoryBean> getReadhistory() {
        return readhistory;
    }

    public void setReadhistory(List<ReadHistoryBean> readhistory) {
        this.readhistory = readhistory;
    }

    public List<CommentBean> getComments() {
        return comments;
    }

    public void setComments(List<CommentBean> comments) {
        this.comments = comments;
    }

    public List<CommentReply> getCommentReply() {
        return commentList;
    }

    public void setCommentReply(List<CommentReply> commentList) {
        this.commentList = commentList;
    }
}
