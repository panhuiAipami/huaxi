package com.spriteapp.booklibrary.thread;

import android.util.Base64;
import android.util.Log;

import com.huawei.hms.api.HuaweiApiClient;
import com.huawei.hms.support.api.client.PendingResult;
import com.huawei.hms.support.api.client.ResultCallback;
import com.huawei.hms.support.api.entity.pay.HwPayConstant;
import com.huawei.hms.support.api.entity.pay.PayReq;
import com.huawei.hms.support.api.pay.HuaweiPay;
import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.config.HuaXiConfig;
import com.spriteapp.booklibrary.util.AppUtil;


import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by panhui on 2017/8/24.
 */

public class HuaWeiPayTask {
    public static final String TAG = "HuaWeiPay-------->";
    //华为移动服务Client
    private HuaweiApiClient client;
    //支付相关信息map
    private Map<String, Object> params;
    //支付id   华为移动服务配置
    public final static String cpId = "890086000102076485";
    //appid   华为移动服务配置
    public final static String appId = "100073201";

    //开发者联盟提供的支付公钥 华为移动服务配置 替换成实际的公钥
//    public  static String publicKey ="";
//    //开发者联盟网站申请的支付私钥，请妥善保管，最好存储在服务器端 华为移动服务配置 替换成实际的私钥
    private  static String privateKey ="";
    //使用加密算法规则
    public  static String SIGN_ALGORITHMS = "SHA256WithRSA";

    public  static String charset = "UTF-8";
    //启动参数，区分startactivityforresult的处理结果,调用支付接口
    public static final int REQ_CODE_PAY = 4001;
    public static final int REQUEST_HMS_RESOLVE_ERROR = 1000;

    ResultCallback<com.huawei.hms.support.api.pay.PayResult> result;

    public HuaWeiPayTask(HuaweiApiClient client, ResultCallback<com.huawei.hms.support.api.pay.PayResult> result){
        this.result = result;
        this.client = client;
    }


    /**
     * 支付接口，CP可以直接参照该方法写法
     */
    public void pay(String privateKey,String productName,String productDesc,double productPrice,String orderId) {
        if(!client.isConnected()) {
            Log.i(TAG, "支付失败，原因：HuaweiApiClient未连接");
            client.connect();
            return;
        }
        this.privateKey = privateKey;
        PendingResult<com.huawei.hms.support.api.pay.PayResult> payResult = HuaweiPay.HuaweiPayApi.pay(client, createPayReq(productName,productDesc,productPrice,orderId));
        payResult.setResultCallback(result);
    }


    /**
     * 生成PayReq对象，用来在进行支付请求的时候携带支付相关信息
     * payReq订单参数需要商户使用在华为开发者联盟申请的RSA私钥进行签名，强烈建议将签名操作在商户服务端处理，避免私钥泄露
     */
    private PayReq createPayReq(String productName,String productDesc,double productPrice,String orderId) {
        getPaySignInfo(productName,productDesc,productPrice,orderId);
        PayReq payReq = new PayReq();

        //商品名称
        payReq.productName = (String) params.get(HwPayConstant.KEY_PRODUCTNAME);
        //商品描述
        payReq.productDesc = (String) params.get(HwPayConstant.KEY_PRODUCTDESC);
        // 商户ID
        payReq.merchantId = (String)params.get(HwPayConstant.KEY_MERCHANTID);
        // 应用ID
        payReq.applicationID = (String) params.get(HwPayConstant.KEY_APPLICATIONID);
        // 支付金额
        payReq.amount = String.valueOf(params.get(HwPayConstant.KEY_AMOUNT));
        // 支付订单号
        payReq.requestId = (String) params.get(HwPayConstant.KEY_REQUESTID);
        // 国家码
        payReq.country = (String)params.get(HwPayConstant.KEY_COUNTRY);
        //币种
        payReq.currency = (String)params.get(HwPayConstant.KEY_CURRENCY);
        // 渠道号
        payReq.sdkChannel = (Integer) params.get(HwPayConstant.KEY_SDKCHANNEL);
        // 回调接口版本号
        payReq.urlVer = (String) params.get(HwPayConstant.KEY_URLVER);

        //以上信息按照一定规则进行签名,建议CP在服务器端储存签名私钥，并在服务器端进行签名操作。
        payReq.sign = this.getSign(params);

        // 商户名称，必填，不参与签名。会显示在支付结果页面
        payReq.merchantName = productName;
        //分类，必填，不参与签名。该字段会影响风控策略
        // X4：主题,X5：应用商店,	X6：游戏,X7：天际通,X8：云空间,X9：电子书,X10：华为学习,X11：音乐,X12 视频,
        // X31 话费充值,X32 机票/酒店,X33 电影票,X34 团购,X35 手机预购,X36 公共缴费,X39 流量充值
        payReq.serviceCatalog = "X10";
        //商户保留信息，选填不参与签名，支付成功后会华为支付平台会原样 回调CP服务端
        payReq.extReserved = productName+"_"+productDesc+"_"+productPrice;

        return payReq;
    }

    /**
     * 生成支付信息map 包含一下信息，该信息需要参与签名
     * HwPayConstant.KEY_MERCHANTID  必选参数 商户id，开发者联盟网站生成的商户ID/支付ID
     * HwPayConstant.KEY_APPLICATIONID 必选参数 应用的appid，开发者联盟网站生成
     * HwPayConstant.KEY_PRODUCTNAME 必选参数 商品名称 此名称将会在支付时显示给用户确认 注意：该字段中不能包含特殊字符，包括# " & / ? $ ^ *:) \ < > ,
     * HwPayConstant.KEY_PRODUCTDESC 必选参数 商品描述 注意：该字段中不能包含特殊字符，包括# " & / ? $ ^ *:) \ < > , |
     * HwPayConstant.KEY_REQUESTID  必选参数 请求订单号。其值由商户定义生成，用于标识一次支付请求，每次请求需唯一，不可重复。
     * 					支付平台在服务器回调接口中会原样返回requestId的值。注意：该字段中不能包含特殊字符，包括# " & / ? $ ^ *:) \ < > , .以及中文字符
     * HwPayConstant.KEY_AMOUNT 必选参数 支付金额 string类型，精确到小数点后2位 比如 20.00
     * HwPayConstant.KEY_CURRENCY必选参数 币种 币种，用于支付的币种，如USD、CNY、MYR。符合ISO 4217，默认CNY，
     * 参见： http://www.iso.org/iso/home/standards/currency_codes.htm
     * HwPayConstant.KEY_COUNTRY 必选参数 国家码.用于区分国家信息，如US、CN、MY，符合ISO 3166标准，参见：http://www.iso.org/iso/home/standards/country_codes.htm
     * HwPayConstant.KEY_URL 可选参数 支付结果回调URL. 华为服务器收到后检查该应用有无在开发者联盟配置回调URL，如果配置了则使用应用配置的URL，否则使用此url
     *   					作为该次支付的回调URL,建议直接 以配置在 华为开发者联盟的回调URL为准
     * HwPayConstant.KEY_URLVER 可选参数  回调接口版本号。如果传值则必须传2， 额外回调信息，具体参考接口文档
     * HwPayConstant.KEY_SDKCHANNEL 必选参数 渠道信息。 取值如下：0 代表自有应用，无渠道 1 代表应用市场渠道 2 代表预装渠道 3 代表游戏中心渠道
     */
    private void getPaySignInfo(String productName,String productDesc,double productPrice,String orderId) {
        if(params != null) {
            params.clear();
        } else {
            params = new HashMap<>();
        }
        params.put(HwPayConstant.KEY_MERCHANTID, cpId);
        params.put(HwPayConstant.KEY_APPLICATIONID, appId);

        //以下数据以实际商品信息为准
        params.put(HwPayConstant.KEY_PRODUCTNAME, productName);
        params.put(HwPayConstant.KEY_PRODUCTDESC, productDesc);

        params.put(HwPayConstant.KEY_REQUESTID, orderId);
        DecimalFormat    df   = new DecimalFormat("######0.00");
        params.put(HwPayConstant.KEY_AMOUNT, df.format(productPrice/100)+"");

        //币种 用于支付的获知
        params.put(HwPayConstant.KEY_CURRENCY, "CNY");
        params.put(HwPayConstant.KEY_COUNTRY, "CN");

        params.put(HwPayConstant.KEY_URLVER, "2");
        params.put(HwPayConstant.KEY_SDKCHANNEL, 1);
    }


    /**
     * 将商户id，应用id, 商品名称，商品说明，支付金额，订单号，渠道号，回调地址版本号等信息按照key值升序排列后
     * 以key=value并以&的方式连接起来生成待签名的字符串，将生成的代签名字符串使用开发者联盟网站提供的应用支付私钥
     * 进行签名
     * @return
     */
    public static String getSign(Map<String, Object> params) {
        //对参数按照key做升序排序，对map的所有value进行处理，转化成string类型
        //拼接成key=value&key=value&....格式的字符串
        StringBuffer content = new StringBuffer();
        // 按照key做排序
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        String value = null;
        Object object = null;
        for (int i = 0; i < keys.size(); i++) {
            String key = (String) keys.get(i);
            object = params.get(key);
            if (object instanceof String) {
                value = (String) object;
            } else {
                value = String.valueOf(object);
            }

            if (value != null) {
                content.append((i == 0 ? "" : "&") + key + "=" + value);
            } else {
                continue;
            }
        }

        //带签名的字符串
        String signOri = content.toString();
        return rsaSign(signOri);
    }

    /**
     * 使用开发者联盟网站分配的支付私钥对支付信息进行签名
     * 强烈建议在 商户服务端做签名处理，且私钥存储在服务端，防止信息泄露
     * CP通过服务器获取服务器端的签名之后，再进行支付请求
     * @param content
     * @return
     */
    public static String rsaSign(String content) {

        if (null == content) {
            return null;
        }

        try {
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decode(privateKey, Base64.DEFAULT));
            KeyFactory keyf = KeyFactory.getInstance("RSA");
            PrivateKey priKey = keyf.generatePrivate(priPKCS8);
            java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);
            signature.initSign(priKey);
            signature.update(content.getBytes(charset));
            byte[] signed = signature.sign();
            return Base64.encodeToString(signed, Base64.DEFAULT);
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "sign NoSuchAlgorithmException");
        } catch (InvalidKeySpecException e) {
            Log.e(TAG, "sign InvalidKeySpecException");
        } catch (InvalidKeyException e) {
            Log.e(TAG, "sign InvalidKeyException");
        } catch (SignatureException e) {
            Log.e(TAG, "sign SignatureException");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "sign UnsupportedEncodingException");
        }
        return null;
    }




    /**
     * 将商户id，应用id, 商品名称，商品说明，支付金额，订单号，渠道号，回调地址版本号等信息按照key值升序排列后
     * 以key=value并以&的方式连接起来生成待签名的字符串
     * @return
     */
    public static String getNoSign(Map<String, Object> params) {
        //对参数按照key做升序排序，对map的所有value进行处理，转化成string类型
        //拼接成key=value&key=value&....格式的字符串
        StringBuffer content = new StringBuffer();
        // 按照key做排序
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        String value = null;
        Object object = null;
        for (int i = 0; i < keys.size(); i++) {
            String key = (String) keys.get(i);
            object = params.get(key);
            if (object instanceof String) {
                value = (String) object;
            } else {
                value = String.valueOf(object);
            }

            if (value != null) {
                content.append((i == 0 ? "" : "&") + key + "=" + value);
            } else {
                continue;
            }
        }

        //带签名的字符串
        String signOri = content.toString();
        return signOri;
    }


    /**
     * 使用开发者联盟提供的支付公钥对支付成功结果中的签名信息进行验证
     * 如果签名验证成功，则表明支付流程正确
     * 如果签名验证不成功，那么支付已经成功，但是签名有误，CP需要到服务器上查询支付情况
     * @param content
     * @param sign
     * @return
     */
    public static boolean doCheck(String content, String sign) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] encodedKey = Base64.decode(AppUtil.getAppContext().getString(R.string.app_public_key), Base64.DEFAULT);
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));

            java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);

            signature.initVerify(pubKey);
            signature.update(content.getBytes(charset));

            boolean bverify = signature.verify(Base64.decode(sign, Base64.DEFAULT));
            return bverify;

        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "doCheck NoSuchAlgorithmException" + e);
        } catch (InvalidKeySpecException e) {
            Log.e(TAG, "doCheck InvalidKeySpecException" + e);
        } catch (InvalidKeyException e) {
            Log.e(TAG, "doCheck InvalidKeyException" + e);
        } catch (SignatureException e) {
            Log.e(TAG, "doCheck SignatureException" + e);
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "doCheck UnsupportedEncodingException" + e);
        }
        return false;
    }
}
