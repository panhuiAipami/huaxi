package com.spriteapp.booklibrary.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/12/25.
 */

public class WeChatBean implements Serializable{
    private String token_id;
    private String pay_info;

    public String getToken_id() {
        return token_id;
    }

    public void setToken_id(String token_id) {
        this.token_id = token_id;
    }

    public String getPay_info() {
        return pay_info;
    }

    public void setPay_info(String pay_info) {
        this.pay_info = pay_info;
    }

    @Override
    public String toString() {
        return "WeChatBean{" +
                "token_id='" + token_id + '\'' +
                "pay_info='" + pay_info + '\'' +
                '}';
    }
}
