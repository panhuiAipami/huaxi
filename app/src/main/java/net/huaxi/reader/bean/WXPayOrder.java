package net.huaxi.reader.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ZMW on 2015/12/21.
 */
public class WXPayOrder implements Serializable{


    private static final long serialVersionUID = -3890538114318613325L;
    
    private String appid;
    private String appkey;
    private String partnerId;
    @SerializedName("prepay_id")
    private String prepayId;
    private String packageValue;
    @SerializedName("nonce_str")
    private String nonce;
    @SerializedName("timestamp")
    private String timeStamp;
    @SerializedName("sign")
    private String sign;
    @SerializedName("ordersn")
    private String ordersn;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getAppkey() {
        return appkey;
    }

    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getPrepayId() {
        return prepayId;
    }

    public void setPrepayId(String prepayId) {
        this.prepayId = prepayId;
    }

    public String getPackageValue() {
        return packageValue;
    }

    public void setPackageValue(String packageValue) {
        this.packageValue = packageValue;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }


}
