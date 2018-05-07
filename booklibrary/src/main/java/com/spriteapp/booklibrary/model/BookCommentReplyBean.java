package com.spriteapp.booklibrary.model;

import java.util.List;

/**
 * Created by userfirst on 2018/5/4.
 */

public class BookCommentReplyBean {

    /**
     * status : success
     * comment : {"CmtID":"72","CreatDatetime":"2017-09-30 08:45:17.293","NovelID":"264","CmtTitle":null,"CmtContent":"他们啥时候和好呀，唉","UserID":"267638","UserName":"沐宝儿吖","ReplyCount":"0","Source":"4","IsTop":"0","IsViewOnlyUser":"0","ReplyDatetime":"2017-09-30 08:45:17.293","PID":"0","ChapterID":"0","VolumeID":"0","UserIP":"59.110.12.195"}
     * code : 10000
     * lists : [{"comment_id":72,"comment_content":"期待啊??","comment_datetime":1506760260,"comment_replydatetime":1506760260,"comment_parent_id":0,"user_avatar":"https://img.huaxi.net/userhead/488847.jpg","user_id":488847,"user_nickname":"云间月0717145734","book_id":264,"volume_id":0,"chapter_id":0,"ip_address":"112.38.40.59","children":[]},{"comment_id":72,"comment_content":"是啊","comment_datetime":1506761555,"comment_replydatetime":1506761555,"comment_parent_id":0,"user_avatar":"https://img.huaxi.net/userhead/267638.jpg","user_id":267638,"user_nickname":"沐宝儿吖","book_id":264,"volume_id":0,"chapter_id":0,"ip_address":"101.226.125.114","children":[]},{"comment_id":72,"comment_content":"我觉得应该快，矛盾是正常的，哪对肚夫妻不会产生矛盾呢。不过看着确实揪心，希望他们快点和好。","comment_datetime":1506764985,"comment_replydatetime":1506764985,"comment_parent_id":0,"user_avatar":"https://img.huaxi.net/userhead/488847.jpg","user_id":488847,"user_nickname":"云间月0717145734","book_id":264,"volume_id":0,"chapter_id":0,"ip_address":"112.38.40.59","children":[]},{"comment_id":72,"comment_content":"吕","comment_datetime":1516095049,"comment_replydatetime":1516095049,"comment_parent_id":0,"user_avatar":"https://img.huaxi.net/userhead/1352446.jpg","user_id":1352446,"user_nickname":"罗海123293742","book_id":264,"volume_id":0,"chapter_id":0,"ip_address":"36.110.2.124","children":[]},{"comment_id":72,"comment_content":"期待","comment_datetime":1525242295,"comment_replydatetime":1525242295,"comment_parent_id":0,"user_avatar":"https://img.huaxi.net/userhead/1393130.jpg","user_id":1393130,"user_nickname":"宋武洋_858286","book_id":264,"volume_id":0,"chapter_id":0,"ip_address":"36.110.2.124","children":[]}]
     * total : 5
     * lists_total : 5
     */

    private String status;
    private CommentBean comment;
    private int code;
    private String total;
    private int lists_total;
    private List<ListsBean> lists;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public CommentBean getComment() {
        return comment;
    }

    public void setComment(CommentBean comment) {
        this.comment = comment;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public int getLists_total() {
        return lists_total;
    }

    public void setLists_total(int lists_total) {
        this.lists_total = lists_total;
    }

    public List<ListsBean> getLists() {
        return lists;
    }

    public void setLists(List<ListsBean> lists) {
        this.lists = lists;
    }

    public static class CommentBean {
        /**
         * CmtID : 72
         * CreatDatetime : 2017-09-30 08:45:17.293
         * NovelID : 264
         * CmtTitle : null
         * CmtContent : 他们啥时候和好呀，唉
         * UserID : 267638
         * UserName : 沐宝儿吖
         * ReplyCount : 0
         * Source : 4
         * IsTop : 0
         * IsViewOnlyUser : 0
         * ReplyDatetime : 2017-09-30 08:45:17.293
         * PID : 0
         * ChapterID : 0
         * VolumeID : 0
         * UserIP : 59.110.12.195
         */

        private String CmtID;
        private String CreatDatetime;
        private String NovelID;
        private Object CmtTitle;
        private String CmtContent;
        private String UserID;
        private String UserName;
        private String ReplyCount;
        private String Source;
        private String IsTop;
        private String IsViewOnlyUser;
        private String ReplyDatetime;
        private String PID;
        private String ChapterID;
        private String VolumeID;
        private String UserIP;

        public String getCmtID() {
            return CmtID;
        }

        public void setCmtID(String CmtID) {
            this.CmtID = CmtID;
        }

        public String getCreatDatetime() {
            return CreatDatetime;
        }

        public void setCreatDatetime(String CreatDatetime) {
            this.CreatDatetime = CreatDatetime;
        }

        public String getNovelID() {
            return NovelID;
        }

        public void setNovelID(String NovelID) {
            this.NovelID = NovelID;
        }

        public Object getCmtTitle() {
            return CmtTitle;
        }

        public void setCmtTitle(Object CmtTitle) {
            this.CmtTitle = CmtTitle;
        }

        public String getCmtContent() {
            return CmtContent;
        }

        public void setCmtContent(String CmtContent) {
            this.CmtContent = CmtContent;
        }

        public String getUserID() {
            return UserID;
        }

        public void setUserID(String UserID) {
            this.UserID = UserID;
        }

        public String getUserName() {
            return UserName;
        }

        public void setUserName(String UserName) {
            this.UserName = UserName;
        }

        public String getReplyCount() {
            return ReplyCount;
        }

        public void setReplyCount(String ReplyCount) {
            this.ReplyCount = ReplyCount;
        }

        public String getSource() {
            return Source;
        }

        public void setSource(String Source) {
            this.Source = Source;
        }

        public String getIsTop() {
            return IsTop;
        }

        public void setIsTop(String IsTop) {
            this.IsTop = IsTop;
        }

        public String getIsViewOnlyUser() {
            return IsViewOnlyUser;
        }

        public void setIsViewOnlyUser(String IsViewOnlyUser) {
            this.IsViewOnlyUser = IsViewOnlyUser;
        }

        public String getReplyDatetime() {
            return ReplyDatetime;
        }

        public void setReplyDatetime(String ReplyDatetime) {
            this.ReplyDatetime = ReplyDatetime;
        }

        public String getPID() {
            return PID;
        }

        public void setPID(String PID) {
            this.PID = PID;
        }

        public String getChapterID() {
            return ChapterID;
        }

        public void setChapterID(String ChapterID) {
            this.ChapterID = ChapterID;
        }

        public String getVolumeID() {
            return VolumeID;
        }

        public void setVolumeID(String VolumeID) {
            this.VolumeID = VolumeID;
        }

        public String getUserIP() {
            return UserIP;
        }

        public void setUserIP(String UserIP) {
            this.UserIP = UserIP;
        }
    }

    public static class ListsBean {
        /**
         * comment_id : 72
         * comment_content : 期待啊??
         * comment_datetime : 1506760260
         * comment_replydatetime : 1506760260
         * comment_parent_id : 0
         * user_avatar : https://img.huaxi.net/userhead/488847.jpg
         * user_id : 488847
         * user_nickname : 云间月0717145734
         * book_id : 264
         * volume_id : 0
         * chapter_id : 0
         * ip_address : 112.38.40.59
         * children : []
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
        private List<?> children;

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

        public List<?> getChildren() {
            return children;
        }

        public void setChildren(List<?> children) {
            this.children = children;
        }
    }
}
