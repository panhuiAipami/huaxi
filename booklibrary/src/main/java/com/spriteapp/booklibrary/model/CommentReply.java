package com.spriteapp.booklibrary.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2018/1/8.
 */

public class CommentReply implements Serializable {

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
     * replay : {"data":[{"id":"16","squareid":"21","pid":"4","content":"4444","addtime":"1515149040","userid":"153","username":"小肥羊","supportnum":"0","replyto":"163","replyusername":"冷暖自知","user_avatar":"https://img.huaxi.net/userhead/153.jpg"},{"id":"15","squareid":"21","pid":"4","content":"4444","addtime":"1515149015","userid":"1","username":"1","supportnum":"0","replyto":"163","replyusername":"冷暖自知","user_avatar":"https://img.huaxi.net/userhead/1.jpg"}],"total":11}
     */

    private int id;
    private int squareid;
    private String content;
    private long addtime;
    private long updatetime;
    private int userid;
    private String username;
    private int supportnum;
    private int replyto;
    private String replyusername;
    private String user_avatar;
    private ReplayBean replay;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSquareid() {
        return squareid;
    }

    public void setSquareid(int squareid) {
        this.squareid = squareid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getAddtime() {
        return addtime;
    }

    public void setAddtime(long addtime) {
        this.addtime = addtime;
    }

    public long getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(long updatetime) {
        this.updatetime = updatetime;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
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

    public int getSupportnum() {
        return supportnum;
    }

    public void setSupportnum(int supportnum) {
        this.supportnum = supportnum;
    }

    public int getReplyto() {
        return replyto;
    }

    public void setReplyto(int replyto) {
        this.replyto = replyto;
    }

    public String getReplyusername() {
        if (replyusername == null)
            return "";
        else
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

    public ReplayBean getReplay() {
        return replay;
    }

    public void setReplay(ReplayBean replay) {
        this.replay = replay;
    }


    public static class ReplayBean {
        /**
         * data : [{"id":"16","squareid":"21","pid":"4","content":"4444","addtime":"1515149040","userid":"153","username":"小肥羊","supportnum":"0","replyto":"163","replyusername":"冷暖自知","user_avatar":"https://img.huaxi.net/userhead/153.jpg"},{"id":"15","squareid":"21","pid":"4","content":"4444","addtime":"1515149015","userid":"1","username":"1","supportnum":"0","replyto":"163","replyusername":"冷暖自知","user_avatar":"https://img.huaxi.net/userhead/1.jpg"}]
         * total : 11
         */

        private int total;
        private List<DataBean> data;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public List<DataBean> getData() {
            return data;
        }

        public void setData(List<DataBean> data) {
            this.data = data;
        }

        public static class DataBean {
            /**
             * id : 16
             * squareid : 21
             * pid : 4
             * content : 4444
             * addtime : 1515149040
             * userid : 153
             * username : 小肥羊
             * supportnum : 0
             * replyto : 163
             * replyusername : 冷暖自知
             * user_avatar : https://img.huaxi.net/userhead/153.jpg
             */

            private int id;
            private String squareid;
            private int pid;
            private String content;
            private long addtime;
            private int userid;
            private String username;
            private String supportnum;
            private int replyto;
            private Object replyusername;
            private String user_avatar;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getSquareid() {
                return squareid;
            }

            public void setSquareid(String squareid) {
                this.squareid = squareid;
            }

            public int getPid() {
                return pid;
            }

            public void setPid(int pid) {
                this.pid = pid;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public long getAddtime() {
                return addtime;
            }

            public void setAddtime(long addtime) {
                this.addtime = addtime;
            }

            public int getUserid() {
                return userid;
            }

            public void setUserid(int userid) {
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

            public int getReplyto() {
                return replyto;
            }

            public void setReplyto(int replyto) {
                this.replyto = replyto;
            }

            public Object getReplyusername() {
                return replyusername;
            }

            public void setReplyusername(Object replyusername) {
                this.replyusername = replyusername;
            }

            public String getUser_avatar() {
                return user_avatar;
            }

            public void setUser_avatar(String user_avatar) {
                this.user_avatar = user_avatar;
            }
        }
    }


}
