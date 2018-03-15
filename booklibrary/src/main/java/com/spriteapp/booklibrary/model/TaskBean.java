package com.spriteapp.booklibrary.model;

import java.util.List;

/**
 * Created by userfirst on 2018/3/13.
 */

public class TaskBean {
    /**
     * status : success
     * code : 10000
     * tasklist : {"beginnertask":[{"id":"1","title":"绑定手机号","memo":"绑定手机号成功后，奖励100金币","rewardnum":"100","classify":"1"},{"id":"6","title":"输入邀请码","memo":"输入他人邀请码，自己可获得1000金币","rewardnum":"1000","classify":"1"},{"id":"5","title":"首次充值","memo":"第一次充值成功，可获得1000金币奖励","rewardnum":"1000","classify":"1"},{"id":"4","title":"新手阅读奖励","memo":"首次登录日30天内，完成以下任务可获得相应金币奖励\n\n奖励金币累积5200\n\n当天\t300金币\n\n3天\t400金币\n\n6天\t500金币\n\n9天\t600金币\n\n12天\t700金币\n\n15天\t800金币\n\n18天\t900金币\n\n21天\t1000金币","rewardnum":"5200","classify":"1"},{"id":"3","title":"首次收徒","memo":"首次收徒可获得奖励2元现金+5000金币\n\n现金分5次发放\n\n第一次\t1元\n\n第二次\t0.25元\n\n第三次\t0.25元\n\n第四次\t0.25元\n\n第五次\t0.25元","rewardnum":"5000","classify":"1"},{"id":"2","title":"提现1元","memo":"提现1元到支付宝或微信后，奖励1000金币","rewardnum":"1000","classify":"1"}],"dailytask":[{"id":"10","title":"优质评论","memo":"单条评论点赞超过50，为优质评论\n\n次日奖励200金币","rewardnum":"200","classify":"2"},{"id":"9","title":"分享到微信收徒","memo":"通过微信好友收徒成功，额外奖励50 金币","rewardnum":"58","classify":"2"},{"id":"8","title":"分享到朋友圈收徒","memo":"通过朋友圈收徒成功，额外奖励50金币","rewardnum":"50","classify":"2"},{"id":"7","title":"分享到QQ好友收徒","memo":"通过QQ链接收徒成功，额外奖励50金币","rewardnum":"50","classify":"2"},{"id":"11","title":"分享小说","memo":"每天分享小说3篇，次日可获得50金币","rewardnum":"50","classify":"2"}]}
     * apprentice_num : 0
     * apprentice_provide_money : 0
     * user_invite_code : 485973c1
     * top_banner : https://www.baidu.com/img/bd_logo1.png
     * center_banner :
     */

    private String status;
    private int code;
    private TasklistBean tasklist;
    private int apprentice_num;
    private int apprentice_provide_money;
    private String user_invite_code;
    private String top_banner;
    private String center_banner;

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

    public TasklistBean getTasklist() {
        return tasklist;
    }

    public void setTasklist(TasklistBean tasklist) {
        this.tasklist = tasklist;
    }

    public int getApprentice_num() {
        return apprentice_num;
    }

    public void setApprentice_num(int apprentice_num) {
        this.apprentice_num = apprentice_num;
    }

    public int getApprentice_provide_money() {
        return apprentice_provide_money;
    }

    public void setApprentice_provide_money(int apprentice_provide_money) {
        this.apprentice_provide_money = apprentice_provide_money;
    }

    public String getUser_invite_code() {
        return user_invite_code;
    }

    public void setUser_invite_code(String user_invite_code) {
        this.user_invite_code = user_invite_code;
    }

    public String getTop_banner() {
        return top_banner;
    }

    public void setTop_banner(String top_banner) {
        this.top_banner = top_banner;
    }

    public String getCenter_banner() {
        return center_banner;
    }

    public void setCenter_banner(String center_banner) {
        this.center_banner = center_banner;
    }

    public static class TasklistBean {
        private List<BeginnertaskBean> beginnertask;
        private List<DailytaskBean> dailytask;

        public List<BeginnertaskBean> getBeginnertask() {
            return beginnertask;
        }

        public void setBeginnertask(List<BeginnertaskBean> beginnertask) {
            this.beginnertask = beginnertask;
        }

        public List<DailytaskBean> getDailytask() {
            return dailytask;
        }

        public void setDailytask(List<DailytaskBean> dailytask) {
            this.dailytask = dailytask;
        }

        public static class BeginnertaskBean {
            /**
             * id : 1
             * title : 绑定手机号
             * memo : 绑定手机号成功后，奖励100金币
             * rewardnum : 100
             * classify : 1
             */

            private String id;
            private String title;
            private String memo;
            private String rewardnum;
            private String classify;
            private int tasktype;

            public int getTasktype() {
                return tasktype;
            }

            public void setTasktype(int tasktype) {
                this.tasktype = tasktype;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getMemo() {
                return memo;
            }

            public void setMemo(String memo) {
                this.memo = memo;
            }

            public String getRewardnum() {
                return rewardnum;
            }

            public void setRewardnum(String rewardnum) {
                this.rewardnum = rewardnum;
            }

            public String getClassify() {
                return classify;
            }

            public void setClassify(String classify) {
                this.classify = classify;
            }
        }

        public static class DailytaskBean {
            /**
             * id : 10
             * title : 优质评论
             * memo : 单条评论点赞超过50，为优质评论
             * <p>
             * 次日奖励200金币
             * rewardnum : 200
             * classify : 2
             */

            private String id;
            private String title;
            private String memo;
            private String rewardnum;
            private String classify;
            private int tasktype;

            public int getTasktype() {
                return tasktype;
            }

            public void setTasktype(int tasktype) {
                this.tasktype = tasktype;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getMemo() {
                return memo;
            }

            public void setMemo(String memo) {
                this.memo = memo;
            }

            public String getRewardnum() {
                return rewardnum;
            }

            public void setRewardnum(String rewardnum) {
                this.rewardnum = rewardnum;
            }

            public String getClassify() {
                return classify;
            }

            public void setClassify(String classify) {
                this.classify = classify;
            }
        }
    }
}
