package com.spriteapp.booklibrary.model;

/**
 * Created by userfirst on 2018/3/22.
 */

public class TaskRewardBean {
    /**
     * pupil_userid : 6
     * pupil_user_name :
     * reward_type : 34
     * rmb : 0
     * gold_coins : 26
     * memo :
     * addtime : 2018-03-12 00:03
     * reward_name : 徒弟进贡
     * pupil_user_avatar :
     */

    private int pupil_userid;
    private String pupil_user_name;
    private int reward_type;
    private double rmb;
    private int gold_coins;
    private String memo;
    private String addtime;
    private String reward_name;
    private String pupil_user_avatar;

    public int getPupil_userid() {
        return pupil_userid;
    }

    public void setPupil_userid(int pupil_userid) {
        this.pupil_userid = pupil_userid;
    }

    public String getPupil_user_name() {
        return pupil_user_name;
    }

    public void setPupil_user_name(String pupil_user_name) {
        this.pupil_user_name = pupil_user_name;
    }

    public int getReward_type() {
        return reward_type;
    }

    public void setReward_type(int reward_type) {
        this.reward_type = reward_type;
    }

    public double getRmb() {
        return rmb;
    }

    public void setRmb(double rmb) {
        this.rmb = rmb;
    }

    public int getGold_coins() {
        return gold_coins;
    }

    public void setGold_coins(int gold_coins) {
        this.gold_coins = gold_coins;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public String getReward_name() {
        return reward_name;
    }

    public void setReward_name(String reward_name) {
        this.reward_name = reward_name;
    }

    public String getPupil_user_avatar() {
        return pupil_user_avatar;
    }

    public void setPupil_user_avatar(String pupil_user_avatar) {
        this.pupil_user_avatar = pupil_user_avatar;
    }
}
