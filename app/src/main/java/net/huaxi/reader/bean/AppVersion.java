package net.huaxi.reader.bean;

import net.huaxi.reader.common.Constants;

import java.io.Serializable;
import java.util.List;

/**
 * 版本信息
 * Created by ryantao on 16/2/19.
 */
public class AppVersion implements Serializable {

    private static final long serialVersionUID = 3638013439797982445L;

    private String versionName;
    private int build;
    private String uptime;
    private List<String> features;
    private List<String> urls;
    private boolean isOptional = false;//是否强制更新
    public String getFilePath(){
        return Constants.XSREADER_TEMP + "jiuyuu-"+versionName + "-" + build + ".apk";
    }

    public boolean isOptional() {
        return isOptional;
    }

    public void setIsOptional(boolean isOptional) {
        this.isOptional = isOptional;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getBuild() {
        return build;
    }

    public void setBuild(int build) {
        this.build = build;
    }

    public String getUptime() {
        return uptime;
    }

    public void setUptime(String uptime) {
        this.uptime = uptime;
    }

    public List<String> getFeatures() {
        return features;
    }

    public void setFeatures(List<String> features) {
        this.features = features;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }
}
