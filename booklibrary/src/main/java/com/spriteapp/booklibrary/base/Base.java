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
    @SerializedName(value = "data", alternate = {"lists", "squarelist", "detail"})//分别适应不同名称的集合
    private T data;
    private int command;

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

    public int getCommand() {
        return command;
    }

    public void setCommand(int command) {
        this.command = command;
    }

    @Override
    public String toString() {
        return "Base{" +
                "status='" + status + '\'' +
                ", code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", command=" + command +
                '}';
    }
}
