package com.spriteapp.booklibrary.base;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by kuangxiaoguo on 2017/7/7.
 */

public class Base<T> implements Serializable {

    private String status;
    private int code;
    private String message;
    @SerializedName(value = "data", alternate = {"lists"})
    private T data;

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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Base{" +
                "status='" + status + '\'' +
                ", code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
