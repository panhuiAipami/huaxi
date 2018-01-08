package com.spriteapp.booklibrary.model;

import java.util.List;

/**
 * Created by Administrator on 2018/1/6.
 * 社区详情实体类
 */

public class SquareBean {

    /**
     * status : success
     * squarelist : [{"id":"22","userid":"1","subject":"111","pic_url":"222","addtime":"1515123066","isauthor":"0","readnum":"0","supportnum":"0","commentnum":"0","location":"未知星球","username":"东方树叶","user_avatar":"https://img.huaxi.net/userhead/1.jpg","readhistory":[],"comments":[]},{"id":"21","userid":"1","subject":"111","pic_url":"222","addtime":"1515122943","isauthor":"0","readnum":"4","supportnum":"15","commentnum":"35","location":"未知星球","username":"东方树叶","user_avatar":"https://img.huaxi.net/userhead/1.jpg","readhistory":[{"userid":"3","user_avatar":"https://img.huaxi.net/userhead/3.jpg"},{"userid":"2","user_avatar":"https://img.huaxi.net/userhead/2.jpg"},{"userid":"1","user_avatar":"https://img.huaxi.net/userhead/1.jpg"}],"comments":[{"id":"4","squareid":"21","content":"111","addtime":"1515142165","updatetime":"1515149040","userid":"173","username":"九尾狐","supportnum":"0","replyto":"0","replyusername":null,"user_avatar":"https://img.huaxi.net/userhead/173.jpg"},{"id":"3","squareid":"21","content":"111","addtime":"1515142123","updatetime":"0","userid":"163","username":"冷暖自知","supportnum":"0","replyto":"0","replyusername":null,"user_avatar":"https://img.huaxi.net/userhead/163.jpg"},{"id":"2","squareid":"21","content":"111","addtime":"1515142121","updatetime":"0","userid":"153","username":"小肥羊","supportnum":"0","replyto":"0","replyusername":null,"user_avatar":"https://img.huaxi.net/userhead/153.jpg"}]},{"id":"20","userid":"1","subject":"111","pic_url":"222","addtime":"1515122861","isauthor":"0","readnum":"0","supportnum":"0","commentnum":"0","location":"北京 北京","username":"东方树叶","user_avatar":"https://img.huaxi.net/userhead/1.jpg","readhistory":[],"comments":[]},{"id":"19","userid":"1","subject":"111","pic_url":"222","addtime":"1515122469","isauthor":"0","readnum":"0","supportnum":"0","commentnum":"0","location":"Array","username":"东方树叶","user_avatar":"https://img.huaxi.net/userhead/1.jpg","readhistory":[],"comments":[]},{"id":"18","userid":"1","subject":"111","pic_url":"222","addtime":"1515122417","isauthor":"0","readnum":"0","supportnum":"0","commentnum":"0","location":"Array","username":"东方树叶","user_avatar":"https://img.huaxi.net/userhead/1.jpg","readhistory":[],"comments":[]},{"id":"17","userid":"1","subject":"111","pic_url":"222","addtime":"1515122341","isauthor":"0","readnum":"0","supportnum":"0","commentnum":"0","location":"Array","username":"东方树叶","user_avatar":"https://img.huaxi.net/userhead/1.jpg","readhistory":[],"comments":[]},{"id":"16","userid":"1","subject":"111","pic_url":"222","addtime":"1515122301","isauthor":"0","readnum":"0","supportnum":"0","commentnum":"0","location":"Array","username":"东方树叶","user_avatar":"https://img.huaxi.net/userhead/1.jpg","readhistory":[],"comments":[]},{"id":"15","userid":"1","subject":"111","pic_url":"222","addtime":"1515122227","isauthor":"0","readnum":"0","supportnum":"0","commentnum":"0","location":"Array","username":"东方树叶","user_avatar":"https://img.huaxi.net/userhead/1.jpg","readhistory":[],"comments":[]},{"id":"14","userid":"1","subject":"111","pic_url":"222","addtime":"1515122168","isauthor":"0","readnum":"0","supportnum":"0","commentnum":"0","location":"Array","username":"东方树叶","user_avatar":"https://img.huaxi.net/userhead/1.jpg","readhistory":[],"comments":[]},{"id":"13","userid":"1","subject":"111","pic_url":"222","addtime":"1515122134","isauthor":"0","readnum":"0","supportnum":"0","commentnum":"0","location":"Array","username":"东方树叶","user_avatar":"https://img.huaxi.net/userhead/1.jpg","readhistory":[],"comments":[]},{"id":"12","userid":"1","subject":"111","pic_url":"222","addtime":"1515122110","isauthor":"0","readnum":"0","supportnum":"0","commentnum":"0","location":"Array","username":"东方树叶","user_avatar":"https://img.huaxi.net/userhead/1.jpg","readhistory":[],"comments":[]},{"id":"11","userid":"1","subject":"111","pic_url":"222","addtime":"1515122042","isauthor":"0","readnum":"0","supportnum":"0","commentnum":"0","location":"Array","username":"东方树叶","user_avatar":"https://img.huaxi.net/userhead/1.jpg","readhistory":[],"comments":[]},{"id":"10","userid":"1","subject":"111","pic_url":"222","addtime":"1515121881","isauthor":"0","readnum":"0","supportnum":"0","commentnum":"0","location":"Array","username":"东方树叶","user_avatar":"https://img.huaxi.net/userhead/1.jpg","readhistory":[],"comments":[]},{"id":"9","userid":"1","subject":"111","pic_url":"222","addtime":"1515121720","isauthor":"0","readnum":"0","supportnum":"0","commentnum":"0","location":"Array","username":"东方树叶","user_avatar":"https://img.huaxi.net/userhead/1.jpg","readhistory":[],"comments":[]},{"id":"8","userid":"8","subject":"1112233","pic_url":"222","addtime":"1508306156","isauthor":"0","readnum":"0","supportnum":"0","commentnum":"0","location":"","username":"admin","user_avatar":"https://img.huaxi.net/userhead/8.jpg","readhistory":[],"comments":[]},{"id":"5","userid":"5","subject":"1112233","pic_url":"222","addtime":"1508306155","isauthor":"0","readnum":"0","supportnum":"0","commentnum":"0","location":"","username":"年糕","user_avatar":"https://img.huaxi.net/userhead/5.jpg","readhistory":[],"comments":[]},{"id":"6","userid":"6","subject":"1112233","pic_url":"222","addtime":"1508306155","isauthor":"0","readnum":"0","supportnum":"0","commentnum":"0","location":"","username":"排骨","user_avatar":"https://img.huaxi.net/userhead/6.jpg","readhistory":[],"comments":[]},{"id":"7","userid":"7","subject":"1112233","pic_url":"222","addtime":"1508306155","isauthor":"0","readnum":"0","supportnum":"0","commentnum":"0","location":"","username":"admin","user_avatar":"https://img.huaxi.net/userhead/7.jpg","readhistory":[],"comments":[]},{"id":"4","userid":"4","subject":"1112233","pic_url":"222","addtime":"1508306152","isauthor":"0","readnum":"0","supportnum":"0","commentnum":"0","location":"","username":"niangao","user_avatar":"https://img.huaxi.net/userhead/4.jpg","readhistory":[],"comments":[]},{"id":"3","userid":"3","subject":"喜欢傍晚时的天空，连绵的青山在黄昏下朦胧了轮廓，紫粉色的云朵，烧得赤红的火烧云，片片如醉般的晚霞。还有那不急不缓的风，又吹到谁的耳畔。","pic_url":"222","addtime":"1508306128","isauthor":"0","readnum":"0","supportnum":"0","commentnum":"0","location":"","username":"pss2","user_avatar":"https://img.huaxi.net/userhead/3.jpg","readhistory":[],"comments":[]}]
     * code : 10000
     */

    private String status;
    private int code;
    private List<SquarelistBean> squarelist;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<SquarelistBean> getSquarelist() {
        return squarelist;
    }

    public void setSquarelist(List<SquarelistBean> squarelist) {
        this.squarelist = squarelist;
    }

    public static class SquarelistBean {
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

        private String id;
        private String userid;
        private String subject;
        private List<String> pic_url;
        private String addtime;
        private String isauthor;
        private String readnum;
        private String supportnum;
        private String commentnum;
        private String location;
        private String username;
        private String user_avatar;
        private List<ReadHistoryBean> readhistory;
        private List<CommentBean> comments;

        public String getId() {
            return id;
        }

        public void setId(String id) {
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

        public String getAddtime() {
            return addtime;
        }

        public void setAddtime(String addtime) {
            this.addtime = addtime;
        }

        public String getIsauthor() {
            return isauthor;
        }

        public void setIsauthor(String isauthor) {
            this.isauthor = isauthor;
        }

        public String getReadnum() {
            return readnum;
        }

        public void setReadnum(String readnum) {
            this.readnum = readnum;
        }

        public String getSupportnum() {
            return supportnum;
        }

        public void setSupportnum(String supportnum) {
            this.supportnum = supportnum;
        }

        public String getCommentnum() {
            return commentnum;
        }

        public void setCommentnum(String commentnum) {
            this.commentnum = commentnum;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getUsername() {
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
    }
}
