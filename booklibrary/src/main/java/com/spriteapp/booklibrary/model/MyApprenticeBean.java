package com.spriteapp.booklibrary.model;

import java.util.List;

/**
 * Created by userfirst on 2018/3/19.
 */

public class MyApprenticeBean {
    /**
     * total_data : {"total_gold_coins":6431,"total_rmb":50}
     * pupil_data : [{"pupil_userid":6,"rmb":25,"gold_coins":1731,"pupil_user_name":"","pupil_user_avatar":"https://img.hxdrive.net/userhead/6/6.jpg"},{"pupil_userid":7,"rmb":25,"gold_coins":4700,"pupil_user_name":"","pupil_user_avatar":"https://img.hxdrive.net/userhead/7/7.jpg"},{"pupil_userid":0,"rmb":0,"gold_coins":0,"pupil_user_name":"","pupil_user_avatar":"https://img.hxdrive.net/userhead/0/.jpg"}]
     */

    private TotalDataBean total_data;
    private List<PupilDataBean> pupil_data;

    public TotalDataBean getTotal_data() {
        return total_data;
    }

    public void setTotal_data(TotalDataBean total_data) {
        this.total_data = total_data;
    }

    public List<PupilDataBean> getPupil_data() {
        return pupil_data;
    }

    public void setPupil_data(List<PupilDataBean> pupil_data) {
        this.pupil_data = pupil_data;
    }

    public static class TotalDataBean {
        /**
         * total_gold_coins : 6431
         * total_rmb : 50
         */

        private int total_gold_coins;
        private int total_rmb;

        public int getTotal_gold_coins() {
            return total_gold_coins;
        }

        public void setTotal_gold_coins(int total_gold_coins) {
            this.total_gold_coins = total_gold_coins;
        }

        public int getTotal_rmb() {
            return total_rmb;
        }

        public void setTotal_rmb(int total_rmb) {
            this.total_rmb = total_rmb;
        }
    }

    public static class PupilDataBean {
        /**
         * pupil_userid : 6
         * rmb : 25
         * gold_coins : 1731
         * pupil_user_name :
         * pupil_user_avatar : https://img.hxdrive.net/userhead/6/6.jpg
         */

        private int pupil_userid;
        private int rmb;
        private int gold_coins;
        private String pupil_user_name;
        private String pupil_user_avatar;
        private String mobile;
        private int gender;

        public int getPupil_userid() {
            return pupil_userid;
        }

        public void setPupil_userid(int pupil_userid) {
            this.pupil_userid = pupil_userid;
        }

        public int getRmb() {
            return rmb;
        }

        public void setRmb(int rmb) {
            this.rmb = rmb;
        }

        public int getGold_coins() {
            return gold_coins;
        }

        public void setGold_coins(int gold_coins) {
            this.gold_coins = gold_coins;
        }

        public String getPupil_user_name() {
            return pupil_user_name;
        }

        public void setPupil_user_name(String pupil_user_name) {
            this.pupil_user_name = pupil_user_name;
        }

        public String getPupil_user_avatar() {
            return pupil_user_avatar;
        }

        public void setPupil_user_avatar(String pupil_user_avatar) {
            this.pupil_user_avatar = pupil_user_avatar;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public int getGender() {
            return gender;
        }

        public void setGender(int gender) {
            this.gender = gender;
        }
    }
}
