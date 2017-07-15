package net.huaxi.reader.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ZMW on 2016/1/27.
 */
public class SearchBean implements Serializable {


    private static final long serialVersionUID = 3471826940570152184L;

    @SerializedName("bk_mid")
    String bid;

    @SerializedName("bk_title")
    String bTitle;

    @SerializedName("bk_description")
    String description;

    @SerializedName("bp_au_pname")
    String authorname;

    @SerializedName("bk_cover_imgid")
    String imageid;

    @SerializedName("bp_hire_flag")
    int isMonthly;

    public int getIsMonthly() {
        return isMonthly;
    }

    public void setIsMonthly(int isMonthly) {
        this.isMonthly = isMonthly;
    }

    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }

    public String getbTitle() {
        return bTitle;
    }

    public void setbTitle(String bTitle) {
        this.bTitle = bTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageid() {
        return imageid;
    }

    public void setImageid(String imageid) {
        this.imageid = imageid;
    }

    public String getAuthorname() {
        return authorname;
    }

    public void setAuthorname(String authorname) {
        this.authorname = authorname;
    }

    @Override
    public String toString() {
        return "SearchBean{" +
                "bid='" + bid + '\'' +
                ", bTitle='" + bTitle + '\'' +
                ", description='" + description + '\'' +
                ", authorname='" + authorname + '\'' +
                ", imageid='" + imageid + '\'' +
                ", isMonthly=" + isMonthly +
                '}';
    }
}
