package com.spriteapp.booklibrary.model;

import java.util.List;

/**
 * Created by panhui on 2018/5/2.
 */

public class BookCommentBean {

    /**
     * comment_id : 1065
     * comment_content : 作者公告：本文为花溪独家发表，更新时间为每天早上九点，日更五章每日更新万字以上，如果有更新不准时的情况，一般是因为后台前台有短暂时间差的问题，请大家稍微刷新一下就可以看到了。谢谢大家一直以来的支持，祝大家阅读愉快O(∩_∩)O~
     * comment_datetime : 1496237050
     * comment_replydatetime : 1496237050
     * comment_parent_id : 0
     * user_avatar : https://img.huaxi.net/userhead/121.jpg
     * user_id : 121
     * user_nickname : 前尘远歌
     * book_id : 7
     * volume_id : 0
     * chapter_id : 0
     * ip_address : 59.110.12.195
     */

    private int comment_id;
    private String comment_content;
    private long comment_datetime;
    private long comment_replydatetime;
    private int comment_parent_id;
    private String user_avatar;
    private int user_id;
    private String user_nickname;
    private int book_id;
    private int volume_id;
    private int chapter_id;
    private String ip_address;
    private int reply_count;
    private int source;
    private List<BookRepyBean> children;

    public int getReply_count() {
        return reply_count;
    }

    public void setReply_count(int reply_count) {
        this.reply_count = reply_count;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public List<BookRepyBean> getChildren() {
        return children;
    }

    public void setChildren(List<BookRepyBean> children) {
        this.children = children;
    }

    public int getComment_id() {
        return comment_id;
    }

    public void setComment_id(int comment_id) {
        this.comment_id = comment_id;
    }

    public String getComment_content() {
        return comment_content;
    }

    public void setComment_content(String comment_content) {
        this.comment_content = comment_content;
    }

    public long getComment_datetime() {
        return comment_datetime;
    }

    public void setComment_datetime(long comment_datetime) {
        this.comment_datetime = comment_datetime;
    }

    public long getComment_replydatetime() {
        return comment_replydatetime;
    }

    public void setComment_replydatetime(long comment_replydatetime) {
        this.comment_replydatetime = comment_replydatetime;
    }

    public int getComment_parent_id() {
        return comment_parent_id;
    }

    public void setComment_parent_id(int comment_parent_id) {
        this.comment_parent_id = comment_parent_id;
    }

    public String getUser_avatar() {
        return user_avatar;
    }

    public void setUser_avatar(String user_avatar) {
        this.user_avatar = user_avatar;
    }

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

    public int getBook_id() {
        return book_id;
    }

    public void setBook_id(int book_id) {
        this.book_id = book_id;
    }

    public int getVolume_id() {
        return volume_id;
    }

    public void setVolume_id(int volume_id) {
        this.volume_id = volume_id;
    }

    public int getChapter_id() {
        return chapter_id;
    }

    public void setChapter_id(int chapter_id) {
        this.chapter_id = chapter_id;
    }

    public String getIp_address() {
        return ip_address;
    }

    public void setIp_address(String ip_address) {
        this.ip_address = ip_address;
    }

    @Override
    public String toString() {
        return "BookCommentBean{" +
                "comment_id=" + comment_id +
                ", comment_content='" + comment_content + '\'' +
                ", comment_datetime=" + comment_datetime +
                ", comment_replydatetime=" + comment_replydatetime +
                ", comment_parent_id=" + comment_parent_id +
                ", user_avatar='" + user_avatar + '\'' +
                ", user_id=" + user_id +
                ", user_nickname='" + user_nickname + '\'' +
                ", book_id=" + book_id +
                ", volume_id=" + volume_id +
                ", chapter_id=" + chapter_id +
                ", ip_address='" + ip_address + '\'' +
                ", children=" + children +
                '}';
    }
}
