package net.huaxi.reader.bean;

import java.io.Serializable;

/**
 * Created by ZMW on 2016/4/6.
 */
public class SplashBean implements Serializable {
    private static final long serialVersionUID = -3859574226780029115L;
    /**照片显示开始的时间段*/
    private Long starttime;
    /**照片显示结束的时间段*/
    private Long endtime;
    /**照片显示的方式，例如横向显示，竖直显示，旋转，或者所在位置*/
    private int effect;
    /**照片的下载路径*/
    private String url;
    //**这个闪屏页的类型，可能是根据时间显示，或者根据性别显示*/
    private int type;

    public Long getStarttime() {
        return starttime;
    }

    public void setStarttime(Long starttime) {
        this.starttime = starttime;
    }

    public Long getEndtime() {
        return endtime;
    }

    public void setEndtime(Long endtime) {
        this.endtime = endtime;
    }

    public int getEffect() {
        return effect;
    }

    public void setEffect(int effect) {
        this.effect = effect;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "SplashBean{" +
                "starttime=" + starttime +
                ", endtime=" + endtime +
                ", effect=" + effect +
                ", url='" + url + '\'' +
                ", type=" + type +
                '}';
    }
}
