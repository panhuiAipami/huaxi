package net.huaxi.reader.aidl;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 迁移记录实体类
 *
 * @author ryantao
 */
public class XSMigrateStore implements Parcelable {

    //收藏作品
    private int collectWorks;
    //阅读币
    private int readCoin;
    //赠币
    private int rewardsCoin;
    //订阅记录
    private int orderRecords;
    //打赏记录
    private int rewardsRecord;
    //失效时间
    private long expireTime;
    //用户ID
    private String userId;
    //token
    private String token;
    //用户昵称
    private String userName;
    //迁移码
    private String migrateCode;
    //验证码（保证内容的完整性）
    private String verifyCode;

    public int getCollectWorks() {
        return collectWorks;
    }

    public void setCollectWorks(int collectWorks) {
        this.collectWorks = collectWorks;
    }

    public int getReadCoin() {
        return readCoin;
    }

    public void setReadCoin(int readCoin) {
        this.readCoin = readCoin;
    }

    public int getRewardsCoin() {
        return rewardsCoin;
    }

    public void setRewardsCoin(int rewardsCoin) {
        this.rewardsCoin = rewardsCoin;
    }

    public int getOrderRecords() {
        return orderRecords;
    }

    public void setOrderRecords(int orderRecords) {
        this.orderRecords = orderRecords;
    }

    public int getRewardsRecord() {
        return rewardsRecord;
    }

    public void setRewardsRecord(int rewardsRecord) {
        this.rewardsRecord = rewardsRecord;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMigrateCode() {
        return migrateCode;
    }

    public void setMigrateCode(String migrateCode) {
        this.migrateCode = migrateCode;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    public String toString() {
        return "migrateString####  expireTime=" + expireTime + " userId=" + userId + " token=" + token + " userName=" + userName + " migrateCode="
                + migrateCode + " verifyCode=" + verifyCode;
    }

    public XSMigrateStore() {
    }

    private XSMigrateStore(Parcel in) {
        this.collectWorks = in.readInt();
        this.readCoin = in.readInt();
        this.rewardsCoin = in.readInt();
        this.orderRecords = in.readInt();
        this.rewardsRecord = in.readInt();
        this.expireTime = in.readLong();
        this.userId = in.readString();
        this.token = in.readString();
        this.userName = in.readString();
        this.migrateCode = in.readString();
        this.verifyCode = in.readString();
    }

    public static final Parcelable.Creator<XSMigrateStore> CREATOR = new Parcelable.Creator<XSMigrateStore>() {

        public XSMigrateStore createFromParcel(Parcel in) {
            return new XSMigrateStore(in);
        }

        public XSMigrateStore[] newArray(int size) {
            return new XSMigrateStore[size];
        }

    };

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.collectWorks);
        dest.writeInt(this.readCoin);
        dest.writeInt(this.rewardsCoin);
        dest.writeInt(this.orderRecords);
        dest.writeInt(this.rewardsRecord);
        dest.writeLong(this.expireTime);
        dest.writeString(this.userId);
        dest.writeString(this.token);
        dest.writeString(this.userName);
        dest.writeString(this.migrateCode);
        dest.writeString(this.verifyCode);
    }

}
