package com.spriteapp.booklibrary.model.response;

/**
 * Created by kuangxiaoguo on 2017/7/18.
 */

public class PayResponse {
    /*
    "transaction_id": "801020170711163332557",
        "pay_str": "app_id=2016112103058726&biz_content=%7B%22timeout_express%22%3A%2230m%22%2C%22body%22%3A%22%E5%85%85%E5%80%BC%22%2C%22subject%22%3Anull%2C%22out_trade_no%22%3A%22801020170711163332557%22%2C%22total_amount%22%3A12%2C%22product_code%22%3A%22QUICK_MSECURITY_PAY%22%7D&charset=utf-8&method=alipay.trade.app.pay&notify_url=http%3A%2F%2Fapi.huaxi.net%2Fpay_alipaynotify&timestamp=2017-07-11%2016%3A33%3A46&version=1.0&sign=YScDoGZOsPVJd84htZT1yPVn1BlMYD%2FKEdSVOBd5RLGwEDudukY12%2ByXTc4Avva5hZlGLEyeHl4pFDnJygICf6URubJXsoltt%2BlLAzQkv2%2FVq%2F0VbFH721jYMqMEjBm9eP%2BDfxvRxWn7rqRTwbg91QL%2BXn00ypQJegbVSGkOwos%3D",
        "amount": 12,
        "params": {
            "app_id": "2016112103058726",
            "method": "alipay.trade.app.pay",
            "charset": "utf-8",
            "sign_type": "RSA",
            "timestamp": "2017-07-11 16:33:46",
            "version": "1.0",
            "notify_url": "http://api.huaxi.net/pay_alipaynotify",
            "biz_content": "{\"timeout_express\":\"30m\",\"body\":\"充值\",\"subject\":null,\"out_trade_no\":\"801020170711163332557\",\"total_amount\":12,\"product_code\":\"QUICK_MSECURITY_PAY\"}",
            "sign": "YScDoGZOsPVJd84htZT1yPVn1BlMYD%2FKEdSVOBd5RLGwEDudukY12%2ByXTc4Avva5hZlGLEyeHl4pFDnJygICf6URubJXsoltt%2BlLAzQkv2%2FVq%2F0VbFH721jYMqMEjBm9eP%2BDfxvRxWn7rqRTwbg91QL%2BXn00ypQJegbVSGkOwos%3D"
        },
        "return_code": "SUCCESS",
        "order_no": "801020170711163332557"
     */
    private String transaction_id;
    private String pay_str;
    private float amount;
    private String return_code;
    private String order_no;
    private String token_id;
    private PayParams params;

    public static class PayParams {
        private String app_id;
        private String method;
        private String charset;
        private String sign_type;
        private String timestamp;
        private String version;
        private String notify_url;
        private String sign;

        public String getApp_id() {
            return app_id;
        }

        public void setApp_id(String app_id) {
            this.app_id = app_id;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public String getCharset() {
            return charset;
        }

        public void setCharset(String charset) {
            this.charset = charset;
        }

        public String getSign_type() {
            return sign_type;
        }

        public void setSign_type(String sign_type) {
            this.sign_type = sign_type;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getNotify_url() {
            return notify_url;
        }

        public void setNotify_url(String notify_url) {
            this.notify_url = notify_url;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        @Override
        public String toString() {
            return "PayParams{" +
                    "app_id='" + app_id + '\'' +
                    ", method='" + method + '\'' +
                    ", charset='" + charset + '\'' +
                    ", sign_type='" + sign_type + '\'' +
                    ", timestamp='" + timestamp + '\'' +
                    ", version='" + version + '\'' +
                    ", notify_url='" + notify_url + '\'' +
                    ", sign='" + sign + '\'' +
                    '}';
        }
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getPay_str() {
        return pay_str;
    }

    public void setPay_str(String pay_str) {
        this.pay_str = pay_str;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getReturn_code() {
        return return_code;
    }

    public void setReturn_code(String return_code) {
        this.return_code = return_code;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public String getToken_id() {
        return token_id;
    }

    public void setToken_id(String token_id) {
        this.token_id = token_id;
    }

    public PayParams getParams() {
        return params;
    }

    public void setParams(PayParams params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return "PayResponse{" +
                "transaction_id='" + transaction_id + '\'' +
                ", pay_str='" + pay_str + '\'' +
                ", amount=" + amount +
                ", return_code='" + return_code + '\'' +
                ", order_no='" + order_no + '\'' +
                ", params=" + params +
                '}';
    }
}
