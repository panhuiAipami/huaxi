package com.spriteapp.booklibrary.model;

import com.google.gson.annotations.SerializedName;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by Administrator on 2017/12/20.
 */

public class StoreBean {
    String url;
    String name;
    @SerializedName("default")
    int default_;

    public StoreBean() {
    }

    public StoreBean(String url, String name) {
        this.url = url;
        this.name = name;
    }

    public String getUrl() {
        try {
            url = URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDefault_() {
        return default_;
    }

    public void setDefault_(int default_) {
        this.default_ = default_;
    }

    @Override
    public String toString() {
        return "Store{" +
                "url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", default_=" + default_ +
                '}';
    }
}
