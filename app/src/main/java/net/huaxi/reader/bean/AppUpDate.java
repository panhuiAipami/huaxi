package net.huaxi.reader.bean;

/**
 * Created by Administrator on 2017/7/21.
 */

public class AppUpDate {
    private String ver;
    private String downurl;

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public String getDownurl() {
        return downurl;
    }

    public void setDownurl(String downurl) {
        this.downurl = downurl;
    }

    @Override
    public String toString() {
        return "AppUpDate{" +
                "ver='" + ver + '\'' +
                ", downurl='" + downurl + '\'' +
                '}';
    }
}
