package net.huaxi.reader.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ZMW on 2015/12/26.
 * 消费记录
 */
public class ConsumeRecord implements Serializable {


    private static final long serialVersionUID = 3861837608032716034L;
    @SerializedName("u_mid")
    private String umid;
    @SerializedName("u_mid_seller")
    private String sellerMid;
    @SerializedName("cm_status")
    private String cmStatus;
    @SerializedName("cm_type")
    private String cmType;//消费类型：1 阅读币消费，2 包月/租赁消费
    @SerializedName("cm_upr_code")
    private String cmCode;
    @SerializedName("cm_upr_desc")
    private String cmDesc;
    @SerializedName("cm_coins")
    private String cmCoins;
    @SerializedName("cm_ip")
    private String cmIp;
    @SerializedName("cm_source")
    private String cmSource;
    @SerializedName("cm_cdate")
    private long cdate;

    public String getUmid() {
        return umid;
    }

    public void setUmid(String umid) {
        this.umid = umid;
    }

    public String getSellerMid() {
        return sellerMid;
    }

    public void setSellerMid(String sellerMid) {
        this.sellerMid = sellerMid;
    }

    public String getCmStatus() {
        return cmStatus;
    }

    public void setCmStatus(String cmStatus) {
        this.cmStatus = cmStatus;
    }

    public String getCmType() {
        return cmType;
    }

    public void setCmType(String cmType) {
        this.cmType = cmType;
    }

    public String getCmCode() {
        return cmCode;
    }

    public void setCmCode(String cmCode) {
        this.cmCode = cmCode;
    }

    public String getCmDesc() {
        return cmDesc;
    }

    public void setCmDesc(String cmDesc) {
        this.cmDesc = cmDesc;
    }

    public String getCmCoins() {
        return cmCoins;
    }

    public void setCmCoins(String cmCoins) {
        this.cmCoins = cmCoins;
    }

    public String getCmIp() {
        return cmIp;
    }

    public void setCmIp(String cmIp) {
        this.cmIp = cmIp;
    }

    public String getCmSource() {
        return cmSource;
    }

    public void setCmSource(String cmSource) {
        this.cmSource = cmSource;
    }

    public void setCdate(long cdate) {
        this.cdate = cdate;
    }

    public long getCdate() {
        return cdate;
    }

    @Override
    public String toString() {
        return "ConsumeRecord{" +
                "umid='" + umid + '\'' +
                ", sellerMid='" + sellerMid + '\'' +
                ", cmStatus='" + cmStatus + '\'' +
                ", cmType='" + cmType + '\'' +
                ", cmCode='" + cmCode + '\'' +
                ", cmDesc='" + cmDesc + '\'' +
                ", cmCoins='" + cmCoins + '\'' +
                ", cmIp='" + cmIp + '\'' +
                ", cmSource='" + cmSource + '\'' +
                ", cdate=" + cdate +
                '}';
    }
}
