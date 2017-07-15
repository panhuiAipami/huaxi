package net.huaxi.reader.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ZMW on 2016/1/9.
 * 消费记录bean
 */

public class ChargeRecord implements Serializable {


    private static final long serialVersionUID = -6545978227233564109L;
    //用户id
    @SerializedName("u_mid")
    private String umid;
    //产品id
    @SerializedName("pr_id")
    private String prid;
    //渠道id
    @SerializedName("ch_id")
    private String chid;
    //订单mid
    @SerializedName("od_mid")
    private String ormid;
    //订单号
    @SerializedName("od_sn")
    private String orsn;
    //订单状态
    @SerializedName("od_status")
    private String orState;
    //订单数量
    @SerializedName("od_quantity")
    private String orQuantity;
    //订单显示金额
    @SerializedName("od_amount_disp")
    private String orAmountDisp;
    //订单实际支付金额
    @SerializedName("od_amount_paied")
    private String orAmountPaid;
    //订单名称
    @SerializedName("od_name")
    private String orName;
    //最后支付金额
    @SerializedName("od_findate")
    private long orFinalDate;
    //添加阅读币的数量
    @SerializedName("od_supply_coins")
    private String orCoins;
    //添加包月时间
    @SerializedName("od_supply_days")
    private String orDays;
    //创建订单的IP
    @SerializedName("od_ip")
    private String orIp;
    @SerializedName("od_source")//0 - unknown; 1 - Android; 2 - IOS; 3 - WAP; 4 - PC
    private String orSource;//创建订单来源
    @SerializedName("od_cdate")
    private long orCdate;//创建订单时间

    public String getUmid() {
        return umid;
    }

    public void setUmid(String umid) {
        this.umid = umid;
    }

    public String getPrid() {
        return prid;
    }

    public void setPrid(String prid) {
        this.prid = prid;
    }

    /**
     *渠道id 充值渠道ID：1 支付宝，2 微信，3 苹果
     * @return
     */
    public String getChid() {
        return chid;
    }

    public void setChid(String chid) {
        this.chid = chid;
    }

    public String getOrmid() {
        return ormid;
    }

    public void setOrmid(String ormid) {
        this.ormid = ormid;
    }

    public String getOrsn() {
        return orsn;
    }

    public void setOrsn(String orsn) {
        this.orsn = orsn;
    }

    public String getOrState() {
        return orState;
    }

    public void setOrState(String orState) {
        this.orState = orState;
    }

    public String getOrQuantity() {
        return orQuantity;
    }

    public void setOrQuantity(String orQuantity) {
        this.orQuantity = orQuantity;
    }

    public String getOrAmountDisp() {
        return orAmountDisp;
    }

    public void setOrAmountDisp(String orAmountDisp) {
        this.orAmountDisp = orAmountDisp;
    }

    public String getOrAmountPaid() {
        return orAmountPaid;
    }

    public void setOrAmountPaid(String orAmountPaid) {
        this.orAmountPaid = orAmountPaid;
    }

    public String getOrName() {
        return orName;
    }

    public void setOrName(String orName) {
        this.orName = orName;
    }

    public long getOrFinalDate() {
        return orFinalDate;
    }

    public void setOrFinalDate(long orFinalDate) {
        this.orFinalDate = orFinalDate;
    }

    public String getOrCoins() {
        return orCoins;
    }

    public void setOrCoins(String orCoins) {
        this.orCoins = orCoins;
    }

    public String getOrDays() {
        return orDays;
    }

    public void setOrDays(String orDays) {
        this.orDays = orDays;
    }

    public String getOrIp() {
        return orIp;
    }

    public void setOrIp(String orIp) {
        this.orIp = orIp;
    }

    public String getOrSource() {
        return orSource;
    }

    public void setOrSource(String orSource) {
        this.orSource = orSource;
    }

    public long getOrCdate() {
        return orCdate;
    }

    public void setOrCdate(long orCdate) {
        this.orCdate = orCdate;
    }
}
