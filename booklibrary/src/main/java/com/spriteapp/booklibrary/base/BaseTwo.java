package com.spriteapp.booklibrary.base;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by kuangxiaoguo on 2017/7/7.
 */

public class BaseTwo<T> implements Serializable {

    private String status;
    private int code;
    @SerializedName(value = "message", alternate = {"hello_messages"})//分别适应不同名称的集合
    private String message;
    @SerializedName(value = "hot", alternate = {"hotkeywords","data"})//分别适应不同名称的集合
    private T hot;

    public T getHot() {
        return hot;
    }

    public void setHot(T hot) {
        this.hot = hot;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }



    @Override
    public String toString() {
        return "Base{" +
                "status='" + status + '\'' +
                ", code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
