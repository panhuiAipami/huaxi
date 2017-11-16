package com.spriteapp.booklibrary.model.store;

/**
 * Created by Administrator on 2017/11/16.
 */

public class AppUpDateModel {

    /**
     * version : 版本
     * url : 更新地址
     * content : 更新内容
     */

    private String version;
    private String url;
    private String content;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "AppUpDateModel{" +
                "version='" + version + '\'' +
                ", url='" + url + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
