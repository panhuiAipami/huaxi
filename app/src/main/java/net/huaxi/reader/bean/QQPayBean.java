package net.huaxi.reader.bean;

import java.io.Serializable;

/**
 * @Description: [qq支付的bean数据]
 * @Author: [Saud]
 * @CreateDate: [16/9/1 14:28]
 * @UpDate: [16/9/1 14:28]
 * @Version: [v1.0]
 */
public class QQPayBean implements Serializable {


    private static final long serialVersionUID = -7836181198547908915L;

    /**
     * errorid : 0
     * errordesc :
     * vdata : {"u_mid":"1011891457081145779110009118","ordersn":"147271128010710900","timestamp":1472711280,"nonce":"1db83053868e865a57cca44e5dd84961","sig_type":"HMAC-SHA1","sign":"CxpmLcJZg+jHfzB06IDS0WtpyqI="}
     * version : 1.0
     */

    private int errorid;
    private String errordesc;
    /**
     * u_mid : 1011891457081145779110009118
     * ordersn : 147271128010710900
     * timestamp : 1472711280
     * nonce : 1db83053868e865a57cca44e5dd84961
     * sig_type : HMAC-SHA1
     * sign : CxpmLcJZg+jHfzB06IDS0WtpyqI=
     */

    private VdataBean vdata;
    private String version;

    public int getErrorid() {
        return errorid;
    }

    public void setErrorid(int errorid) {
        this.errorid = errorid;
    }

    public String getErrordesc() {
        return errordesc;
    }

    public void setErrordesc(String errordesc) {
        this.errordesc = errordesc;
    }

    public VdataBean getVdata() {
        return vdata;
    }

    public void setVdata(VdataBean vdata) {
        this.vdata = vdata;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public static class VdataBean implements Serializable {
        private static final long serialVersionUID = -7696053922766851430L;
        private String u_mid;
        private String ordersn;
        private int timestamp;
        private String nonce;
        private String sig_type;
        private String sign;

        private String token_id;

        public String getU_mid() {
            return u_mid;
        }

        public void setU_mid(String u_mid) {
            this.u_mid = u_mid;
        }

        public String getOrdersn() {
            return ordersn;
        }

        public void setOrdersn(String ordersn) {
            this.ordersn = ordersn;
        }

        public int getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(int timestamp) {
            this.timestamp = timestamp;
        }

        public String getNonce() {
            return nonce;
        }

        public void setNonce(String nonce) {
            this.nonce = nonce;
        }

        public String getSig_type() {
            return sig_type;
        }

        public void setSig_type(String sig_type) {
            this.sig_type = sig_type;
        }

        public String getSign() {
            return sign;
        }
        public String getToken_id() {
            return token_id;
        }

        public void setToken_id(String token_id) {
            this.token_id = token_id;
        }
        public void setSign(String sign) {
            this.sign = sign;
        }

        @Override
        public String toString() {
            return "VdataBean{" +
                    "u_mid='" + u_mid + '\'' +
                    ", ordersn='" + ordersn + '\'' +
                    ", timestamp=" + timestamp +
                    ", nonce='" + nonce + '\'' +
                    ", sig_type='" + sig_type + '\'' +
                    ", sign='" + sign + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "QQPayBean{" +
                "errorid=" + errorid +
                ", errordesc='" + errordesc + '\'' +
                ", vdata=" + vdata +
                ", version='" + version + '\'' +
                '}';
    }
}
