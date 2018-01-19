package com.spriteapp.booklibrary.ui.adapter;

import com.spriteapp.booklibrary.model.CommentReply;

import java.util.List;

/**
 * Created by Administrator on 2018/1/19.
 */

public class CommentReplyBean {


    private List<CommentReply> data;
    private CommentNew replydata;

    public List<CommentReply> getData() {
        return data;
    }

    public void setData(List<CommentReply> data) {
        this.data = data;
    }

    public CommentNew getReplydata() {
        return replydata;
    }

    public void setReplydata(CommentNew replydata) {
        this.replydata = replydata;
    }

    public static class CommentNew {
        private List<CommentReply> data;

        public List<CommentReply> getData() {
            return data;
        }

        public void setData(List<CommentReply> data) {
            this.data = data;
        }
    }
}
