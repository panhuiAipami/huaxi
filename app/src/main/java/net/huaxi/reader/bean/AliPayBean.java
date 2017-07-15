package net.huaxi.reader.bean;

import java.io.Serializable;

/**
 * Created by ZMW on 2015/12/21.
 */
public class AliPayBean implements Serializable {
    private static final long serialVersionUID = 2941506163025077185L;
    private String success;
    private String content;
    private String sign;
    private String sign_type;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getSign_type() {
        return sign_type;
    }

    public void setSign_type(String sign_type) {
        this.sign_type = sign_type;
    }


}
