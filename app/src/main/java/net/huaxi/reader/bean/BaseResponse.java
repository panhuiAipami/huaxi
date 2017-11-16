package net.huaxi.reader.bean;

import java.io.Serializable;

/**
 * Created by panhui on 16/7/25.
 */
public class BaseResponse implements Serializable {
    public static final int SUCCESS_CODE = 200;
    public static final int SUCCESS_CODE_LOGIN = 10000;
    public static final int TOKEN_ERROR = 312;

    private int code;
    private String message;

    public boolean isSuccess() {
        if(code == SUCCESS_CODE  || code == SUCCESS_CODE_LOGIN){
            return true;
        }
        return false;
    }

    public boolean reLogin() {
        if(code == TOKEN_ERROR){
            return true;
        }
        return false;
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
}
