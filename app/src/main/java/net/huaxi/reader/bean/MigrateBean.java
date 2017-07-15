package net.huaxi.reader.bean;

import java.io.Serializable;

/**
 * @Description: [ 一句话描述该类的功能 ]
 * @Author: [ Saud ]
 * @CreateDate: [ 16/6/14 17:09 ]
 * @UpDate: [ 16/6/14 17:09 ]
 * @Version: [ v1.0 ]
 */
public class MigrateBean implements Serializable {
    private static final long serialVersionUID = 4036440881590799650L;

    /**
     * errorid : 0
     * errordesc :
     * vdata : {"id":"3646","status":"5","nirvana_code":"1011011466404482547006004253","nirvana_code_cdate":"1466404482","userid":"60549155","remain_coin":"0","hire_expires":"0","hire_type":"0","vip_level":0,"user_name":"你是小小","first_favor_date":"1466131340","first_favor_articleid":"349007","first_favor_imgurl":"http://img.readnovel.com/incoming/book/0/9007/349007_di.jpg","first_reward_price":"0","first_favor_title":"大唐小相公","favor_counts":"4","reg_time":"1466061272","keep_live_time":"343210","queue_status":"1","sub_articleid":"0","sub_time":"0","sub_chapter_count":"0","comment_articleid":"0","queue_ctime":"2016-06-20 06:34:42","comment_message":null,"comment_date":"0","first_reward_time":"0","first_reward_title":"","reward_counts":"0","exec_error_id":"0","start_time":"2016-06-16 08:20:00","info_complete_time":"2016-06-20 06:34:42","last_exec_time":"2016-06-20 06:34:42","last_succ_time":"0000-00-00 00:00:00","disp_num":1,"old_remain_coin":"99956825","old_fav_total":"4","old_sub_total":"3309","umid":"1011961458294076274724007822","xsb_coin":"16"}
     * version : 1.0
     */

    private int errorid;
    private String errordesc;
    /**
     * id : 3646
     * status : 5
     * nirvana_code : 1011011466404482547006004253
     * nirvana_code_cdate : 1466404482
     * userid : 60549155
     * remain_coin : 0
     * hire_expires : 0
     * hire_type : 0
     * vip_level : 0
     * user_name : 你是小小
     * first_favor_date : 1466131340
     * first_favor_articleid : 349007
     * first_favor_imgurl : http://img.readnovel.com/incoming/book/0/9007/349007_di.jpg
     * first_reward_price : 0
     * first_favor_title : 大唐小相公
     * favor_counts : 4
     * reg_time : 1466061272
     * keep_live_time : 343210
     * queue_status : 1
     * sub_articleid : 0
     * sub_time : 0
     * sub_chapter_count : 0
     * comment_articleid : 0
     * queue_ctime : 2016-06-20 06:34:42
     * comment_message : null
     * comment_date : 0
     * first_reward_time : 0
     * first_reward_title :
     * reward_counts : 0
     * exec_error_id : 0
     * start_time : 2016-06-16 08:20:00
     * info_complete_time : 2016-06-20 06:34:42
     * last_exec_time : 2016-06-20 06:34:42
     * last_succ_time : 0000-00-00 00:00:00
     * disp_num : 1
     * old_remain_coin : 99956825
     * old_fav_total : 4
     * old_sub_total : 3309
     * umid : 1011961458294076274724007822
     * xsb_coin : 16
     */

    private VdataBean vdata;
    private String version;

    public int getErrorid() {
        return errorid;
    }

    public void setErrorid(int errorid) {
        this.errorid = errorid;
    }

    public String getErrordesc() {
        return errordesc;
    }

    public void setErrordesc(String errordesc) {
        this.errordesc = errordesc;
    }

    public VdataBean getVdata() {
        return vdata;
    }

    public void setVdata(VdataBean vdata) {
        this.vdata = vdata;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public static class VdataBean implements Serializable{
        private static final long serialVersionUID = 5514646370201400913L;
        private String id;
        private String status;
        private String nirvana_code;
        private String nirvana_code_cdate;
        private String userid;
        private String remain_coin;
        private String hire_expires;
        private String hire_type;
        private int vip_level;
        private String user_name;
        private String first_favor_date;
        private String first_favor_articleid;
        private String first_favor_imgurl;
        private String first_reward_price;
        private String first_favor_title;
        private String favor_counts;
        private String reg_time;
        private String keep_live_time;
        private String queue_status;
        private String sub_articleid;
        private String sub_time;
        private String sub_chapter_count;
        private String comment_articleid;
        private String queue_ctime;
        private Object comment_message;
        private String comment_date;
        private String first_reward_time;
        private String first_reward_title;
        private String reward_counts;
        private String exec_error_id;
        private String start_time;
        private String info_complete_time;
        private String last_exec_time;
        private String last_succ_time;
        private int disp_num;
        private String old_remain_coin;
        private String old_fav_total;
        private String old_sub_total;
        private String umid;
        private String xsb_coin;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getNirvanaCode() {
            return nirvana_code;
        }

        public void setNirvana_code(String nirvana_code) {
            this.nirvana_code = nirvana_code;
        }

        public String getNirvana_code_cdate() {
            return nirvana_code_cdate;
        }

        public void setNirvana_code_cdate(String nirvana_code_cdate) {
            this.nirvana_code_cdate = nirvana_code_cdate;
        }

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public String getRemainCoin() {
            return remain_coin;
        }

        public void setRemain_coin(String remain_coin) {
            this.remain_coin = remain_coin;
        }

        public String getHire_expires() {
            return hire_expires;
        }

        public void setHire_expires(String hire_expires) {
            this.hire_expires = hire_expires;
        }

        public String getHire_type() {
            return hire_type;
        }

        public void setHire_type(String hire_type) {
            this.hire_type = hire_type;
        }

        public int getVip_level() {
            return vip_level;
        }

        public void setVip_level(int vip_level) {
            this.vip_level = vip_level;
        }

        public String getUserName() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        public String getFirst_favor_date() {
            return first_favor_date;
        }

        public void setFirst_favor_date(String first_favor_date) {
            this.first_favor_date = first_favor_date;
        }

        public String getFirst_favor_articleid() {
            return first_favor_articleid;
        }

        public void setFirst_favor_articleid(String first_favor_articleid) {
            this.first_favor_articleid = first_favor_articleid;
        }

        public String getFirst_favor_imgurl() {
            return first_favor_imgurl;
        }

        public void setFirst_favor_imgurl(String first_favor_imgurl) {
            this.first_favor_imgurl = first_favor_imgurl;
        }

        public String getFirst_reward_price() {
            return first_reward_price;
        }

        public void setFirst_reward_price(String first_reward_price) {
            this.first_reward_price = first_reward_price;
        }

        public String getFirst_favor_title() {
            return first_favor_title;
        }

        public void setFirst_favor_title(String first_favor_title) {
            this.first_favor_title = first_favor_title;
        }

        public String getFavorCounts() {
            return favor_counts;
        }

        public void setFavor_counts(String favor_counts) {
            this.favor_counts = favor_counts;
        }

        public String getReg_time() {
            return reg_time;
        }

        public void setReg_time(String reg_time) {
            this.reg_time = reg_time;
        }

        public String getKeep_live_time() {
            return keep_live_time;
        }

        public void setKeep_live_time(String keep_live_time) {
            this.keep_live_time = keep_live_time;
        }

        public String getQueue_status() {
            return queue_status;
        }

        public void setQueue_status(String queue_status) {
            this.queue_status = queue_status;
        }

        public String getSub_articleid() {
            return sub_articleid;
        }

        public void setSub_articleid(String sub_articleid) {
            this.sub_articleid = sub_articleid;
        }

        public String getSub_time() {
            return sub_time;
        }

        public void setSub_time(String sub_time) {
            this.sub_time = sub_time;
        }

        public String getSubChapterCount() {
            return sub_chapter_count;
        }

        public void setSub_chapter_count(String sub_chapter_count) {
            this.sub_chapter_count = sub_chapter_count;
        }

        public String getComment_articleid() {
            return comment_articleid;
        }

        public void setComment_articleid(String comment_articleid) {
            this.comment_articleid = comment_articleid;
        }

        public String getQueue_ctime() {
            return queue_ctime;
        }

        public void setQueue_ctime(String queue_ctime) {
            this.queue_ctime = queue_ctime;
        }

        public Object getComment_message() {
            return comment_message;
        }

        public void setComment_message(Object comment_message) {
            this.comment_message = comment_message;
        }

        public String getComment_date() {
            return comment_date;
        }

        public void setComment_date(String comment_date) {
            this.comment_date = comment_date;
        }

        public String getFirst_reward_time() {
            return first_reward_time;
        }

        public void setFirst_reward_time(String first_reward_time) {
            this.first_reward_time = first_reward_time;
        }

        public String getFirst_reward_title() {
            return first_reward_title;
        }

        public void setFirst_reward_title(String first_reward_title) {
            this.first_reward_title = first_reward_title;
        }

        public String getRewardCounts() {
            return reward_counts;
        }

        public void setReward_counts(String reward_counts) {
            this.reward_counts = reward_counts;
        }

        public String getExec_error_id() {
            return exec_error_id;
        }

        public void setExec_error_id(String exec_error_id) {
            this.exec_error_id = exec_error_id;
        }

        public String getStart_time() {
            return start_time;
        }

        public void setStart_time(String start_time) {
            this.start_time = start_time;
        }

        public String getInfo_complete_time() {
            return info_complete_time;
        }

        public void setInfo_complete_time(String info_complete_time) {
            this.info_complete_time = info_complete_time;
        }

        public String getLast_exec_time() {
            return last_exec_time;
        }

        public void setLast_exec_time(String last_exec_time) {
            this.last_exec_time = last_exec_time;
        }

        public String getLast_succ_time() {
            return last_succ_time;
        }

        public void setLast_succ_time(String last_succ_time) {
            this.last_succ_time = last_succ_time;
        }

        public int getDisp_num() {
            return disp_num;
        }

        public void setDisp_num(int disp_num) {
            this.disp_num = disp_num;
        }

        public String getOld_remain_coin() {
            return old_remain_coin;
        }

        public void setOld_remain_coin(String old_remain_coin) {
            this.old_remain_coin = old_remain_coin;
        }

        public String getOld_fav_total() {
            return old_fav_total;
        }

        public void setOld_fav_total(String old_fav_total) {
            this.old_fav_total = old_fav_total;
        }

        public String getOld_sub_total() {
            return old_sub_total;
        }

        public void setOld_sub_total(String old_sub_total) {
            this.old_sub_total = old_sub_total;
        }

        public String getUmid() {
            return umid;
        }

        public void setUmid(String umid) {
            this.umid = umid;
        }

        public String getXsbCoin() {
            return xsb_coin;
        }

        public void setXsb_coin(String xsb_coin) {
            this.xsb_coin = xsb_coin;
        }
    }
}
