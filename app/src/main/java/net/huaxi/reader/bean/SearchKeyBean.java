package net.huaxi.reader.bean;

import java.io.Serializable;

/**
 * Created by Saud on 16/4/15.
 */
public class SearchKeyBean implements Serializable {

    private static final long serialVersionUID = -6913702022211992356L;
    private int tag;

    private String key;


    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


}
