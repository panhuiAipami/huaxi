package com.spriteapp.booklibrary.ui.activity;

import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.api.BookApi;
import com.spriteapp.booklibrary.base.Base;
import com.spriteapp.booklibrary.base.BaseActivity;
import com.spriteapp.booklibrary.config.HuaXiSDK;
import com.spriteapp.booklibrary.constant.Constant;
import com.spriteapp.booklibrary.enumeration.ApiCodeEnum;
import com.spriteapp.booklibrary.enumeration.PayResultEnum;
import com.spriteapp.booklibrary.enumeration.UpdaterPayEnum;
import com.spriteapp.booklibrary.model.PayResult;
import com.spriteapp.booklibrary.model.UserBean;
import com.spriteapp.booklibrary.model.WeChatBean;
import com.spriteapp.booklibrary.model.response.PayResponse;
import com.spriteapp.booklibrary.ui.presenter.WebViewPresenter;
import com.spriteapp.booklibrary.ui.view.WebViewView;
import com.spriteapp.booklibrary.util.ActivityUtil;
import com.spriteapp.booklibrary.util.AppUtil;
import com.spriteapp.booklibrary.util.GlideUtils;
import com.spriteapp.booklibrary.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * 充值
 */
public class RechargeActivity extends TitleActivity {
    private TextView hua_ban, hua_bei, user_name;
    private TextView old_price1, old_price2, old_price3;
    private LinearLayout price1_layout, price2_layout, price3_layout;
    private View line1, line2, line3;
    private ImageView user_head, huaxi_pay;
    private RadioGroup pay_method_group;
    private RadioButton hw_pay;
    private TextView goto_pay;
    private float price = 49.9f;//充值金额
    private int type = 0;//充值类型
    private List<LinearLayout> linearLayouts = new ArrayList<>();
    private static final int SDK_PAY_FLAG = 1;
    private List<View> views = new ArrayList<>();
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    //对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        Toast.makeText(RechargeActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                        EventBus.getDefault().post(UpdaterPayEnum.UPDATE_PAY_RESULT);
                        EventBus.getDefault().post(PayResultEnum.SUCCESS);
                        finish();
                        return;
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(RechargeActivity.this, "支付失败,请重试", Toast.LENGTH_SHORT).show();
                        EventBus.getDefault().post(PayResultEnum.FAILED);
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };


    @Override
    public void initData() throws Exception {
        setTitle("在线充值");
//        EventBus.getDefault().register(this);
        setText();
        listener();
        setUserData();
    }

    @Override
    public void configViews() throws Exception {
    }

    @Override
    public void addContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_recharge, null);
        mContainerLayout.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void findViewId() throws Exception {
        super.findViewId();
        user_head = (ImageView) findViewById(R.id.user_head);
        huaxi_pay = (ImageView) findViewById(R.id.huaxi_pay);
        user_name = (TextView) findViewById(R.id.user_name);
        hua_ban = (TextView) findViewById(R.id.hua_ban);
        hua_bei = (TextView) findViewById(R.id.hua_bei);
        old_price1 = (TextView) findViewById(R.id.old_price1);
        old_price2 = (TextView) findViewById(R.id.old_price2);
        old_price3 = (TextView) findViewById(R.id.old_price3);
        price1_layout = (LinearLayout) findViewById(R.id.price1_layout);
        price2_layout = (LinearLayout) findViewById(R.id.price2_layout);
        price3_layout = (LinearLayout) findViewById(R.id.price3_layout);
        pay_method_group = (RadioGroup) findViewById(R.id.pay_method_group);
        hw_pay = (RadioButton) findViewById(R.id.hw_pay);
        line1 = findViewById(R.id.line1);
        line2 = findViewById(R.id.line2);
        line3 = findViewById(R.id.line3);
        goto_pay = (TextView) findViewById(R.id.goto_pay);

    }

    public void listener() {
        price1_layout.setOnClickListener(this);
        price2_layout.setOnClickListener(this);
        price3_layout.setOnClickListener(this);
        goto_pay.setOnClickListener(this);
        mLeftLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
        });
        pay_method_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                Log.d("onCheckedChanged", "i===" + i);
                if (i == R.id.wx_pay)
                    setLine(0);//微信支付
                else if (i == R.id.ali_pay)
                    setLine(1);//支付宝支付
                else if (i == R.id.hw_pay)
                    setLine(2);//华为支付
            }
        });
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == price1_layout) {
            setPriceState(0);
        } else if (v == price2_layout) {
            setPriceState(1);
        } else if (v == price3_layout) {
            setPriceState(2);
        } else if (v == goto_pay) {//去支付
            if (type == 0) {//微信支付
//                net.huaxi.12yuan
                requestWxWebPay("com.huaxiapp." + price + "yuan");
            } else if (type == 1) {//支付宝支付
                requestAliPay("com.huaxiapp." + price + "yuan");
            } else if (type == 2) {//华为支付,暂无

            }
        }
    }

    public void setLine(int pos) {
        for (int i = 0; i < views.size(); i++) {
            if (pos == i)
                views.get(i).setVisibility(View.VISIBLE);
            else
                views.get(i).setVisibility(View.INVISIBLE);
        }
        type = pos;
        Log.d("setLine", "pos===" + pos + "type===" + type);
    }

    public void setPriceState(int pos) {
        for (int i = 0; i < linearLayouts.size(); i++) {
            if (pos == i)
                linearLayouts.get(i).setEnabled(false);
            else
                linearLayouts.get(i).setEnabled(true);
        }
        if (pos == 0) {
            price = 29.9f;
        } else if (pos == 1) {
            price = 49.9f;
        } else if (pos == 2) {
            price = 99.9f;
        }
        Log.d("setPriceState", "pos===" + pos + "price===" + price);
    }

    public void setText() {
        old_price1.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        old_price2.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        old_price3.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        GlideUtils.loadImage(huaxi_pay, "http://img.hxdrive.net/themes/baisibudejie_v2/images/20180118145145.png", this);//引用图片
        linearLayouts.add(price1_layout);
        linearLayouts.add(price2_layout);
        linearLayouts.add(price3_layout);
        views.add(line1);
        views.add(line2);
        if (HomeActivity.CHANNEL_IS_HUAWEI) {
            hw_pay.setVisibility(View.VISIBLE);
            views.add(line3);
        }

        setLine(0);
        setPriceState(1);
    }

    public void setUserData() {
        if (AppUtil.isLogin(this)) {
            GlideUtils.loadImage(user_head, UserBean.getInstance().getUser_avatar(), this);
            hua_ban.setText(Util.getString(UserBean.getInstance().getUser_false_point() + ""));
            hua_bei.setText(Util.getString(UserBean.getInstance().getUser_real_point() + ""));
            user_name.setText(UserBean.getInstance().getUser_nickname());
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void requestAliPay(String productId) {//生成支付宝订单信息
        if (!AppUtil.isNetAvailable(this)) {
            return;
        }
        Log.d("productId", "ali===" + productId);
        showDialog();
        BookApi.getInstance().
                service
                .getAliPayRequest(productId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base<PayResponse>>() {
                    @Override
                    public void onComplete() {
                        dismissDialog();
                    }

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Base<PayResponse> payResponseBase) {
                        if (payResponseBase.getCode() == ApiCodeEnum.SUCCESS.getValue()) {
                            PayResponse data = payResponseBase.getData();
                            Log.d("alipay-->", data.toString());
                            setAliPayResult(data);
                        }
                    }
                });
    }

    public void setAliPayResult(PayResponse result) {
        if (result == null) {
            return;
        }
        final String orderInfo = result.getPay_str();
        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                PayTask alipay = new PayTask(RechargeActivity.this);
                Map<String, String> result = alipay.payV2(orderInfo, true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    public void requestWxPay(String productId) {//生成原生微信订单信息
        if (!AppUtil.isNetAvailable(this)) {
            return;
        }
        Log.d("productId", "wx===" + productId);
        showDialog();
        BookApi.getInstance().
                service
                .getWeChatRequest(productId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base<WeChatBean>>() {
                    @Override
                    public void onComplete() {
                        dismissDialog();
                    }

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onError(Throwable e) {

                        Log.d("PayResponse", "微信订单请求失败");
//                            PayResponse response = new PayResponse();
//                            HuaXiSDK.getInstance().toWXPay(response);

                    }

                    @Override
                    public void onNext(Base<WeChatBean> payResponseBase) {//微信wechat
                        Log.d("PayResponse", payResponseBase.toString());
                        if (payResponseBase.getCode() == ApiCodeEnum.SUCCESS.getValue()) {
                            WeChatBean data = payResponseBase.getData();
//                            mView.setWechatPayResult(data);
                            if (AppUtil.isLogin()) {//微信支付
                                HuaXiSDK.getInstance().toWXPay(data, null);
                            }
                        }
                    }
                });
    }

    public void requestWxWebPay(String productId) {//生成网页微信订单信息
        if (!AppUtil.isNetAvailable(this)) {
            return;
        }
        Log.d("productId", "wx===" + productId);
        showDialog();
        BookApi.getInstance().
                service
                .pay_wapswiftpassg(productId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base<WeChatBean>>() {
                    @Override
                    public void onComplete() {
                        dismissDialog();
                    }

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onError(Throwable e) {

                        Log.d("PayResponse", "微信订单请求失败");
//                            PayResponse response = new PayResponse();
//                            HuaXiSDK.getInstance().toWXPay(response);

                    }

                    @Override
                    public void onNext(Base<WeChatBean> payResponseBase) {//微信wechat
                        Log.d("PayResponse", payResponseBase.toString());
                        if (payResponseBase != null) {
                            if (payResponseBase.getCode() == ApiCodeEnum.SUCCESS.getValue()) {
                                if (!TextUtils.isEmpty(payResponseBase.getData().getPay_info())) {
                                    ActivityUtil.toWebViewActivityBack(RechargeActivity.this, payResponseBase.getData().getPay_info(), true);
                                }
                            }
                        }

                    }
                });
    }

    public void setWechatPayResult(PayResponse result) {

    }
}
