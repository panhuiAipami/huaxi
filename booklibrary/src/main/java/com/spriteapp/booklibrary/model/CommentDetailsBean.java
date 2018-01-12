package com.spriteapp.booklibrary.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2018/1/12.
 */

public class CommentDetailsBean {
    @SerializedName(value = "commentList", alternate = {"default", "new", "hot"})//分别适应不同名称的集合
    private List<CommentReply> commentList;

    public List<CommentReply> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<CommentReply> commentList) {
        this.commentList = commentList;
    }
}
