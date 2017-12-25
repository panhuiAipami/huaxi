package com.spriteapp.booklibrary.ui.presenter;

import android.util.Log;

import com.spriteapp.booklibrary.api.BookApi;
import com.spriteapp.booklibrary.base.Base;
import com.spriteapp.booklibrary.base.BasePresenter;
import com.spriteapp.booklibrary.config.HuaXiSDK;
import com.spriteapp.booklibrary.enumeration.ApiCodeEnum;
import com.spriteapp.booklibrary.model.WeChatBean;
import com.spriteapp.booklibrary.model.response.PayResponse;
import com.spriteapp.booklibrary.ui.view.WebViewView;
import com.spriteapp.booklibrary.util.AppUtil;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by kuangxiaoguo on 2017/7/8.
 */

public class WebViewPresenter implements BasePresenter<WebViewView> {

    private WebViewView mView;
    private Disposable mDisposable;

    @Override
    public void attachView(WebViewView view) {
        mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
        if (mDisposable != null) {
            mDisposable.dispose();
            mDisposable = null;
        }
    }

    public void requestAliPay(String productId) {//生成支付宝订单信息
        if (!AppUtil.isNetAvailable(mView.getMyContext())) {
            return;
        }
        mView.showNetWorkProgress();
        BookApi.getInstance().
                service
                .getAliPayRequest(productId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base<PayResponse>>() {
                    @Override
                    public void onComplete() {
                        if (mView != null) {
                            mView.disMissProgress();
                        }
                    }

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.onError(e);
                        }
                    }

                    @Override
                    public void onNext(Base<PayResponse> payResponseBase) {
                        if (payResponseBase.getCode() == ApiCodeEnum.SUCCESS.getValue() && mView != null) {
                            PayResponse data = payResponseBase.getData();
                            Log.d("alipay-->", data.toString());
                            mView.setAliPayResult(data);
                        }
                    }
                });
    }

    public void requestWeChatPay(String productId) {//生成微信订单信息
        if (!AppUtil.isNetAvailable(mView.getMyContext())) {
            return;
        }
        mView.showNetWorkProgress();
        BookApi.getInstance().
                service
                .getWeChatRequest(productId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base<WeChatBean>>() {
                    @Override
                    public void onComplete() {
                        if (mView != null) {
                            mView.disMissProgress();
                        }
                    }

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            Log.d("PayResponse", "微信订单请求失败");
//                            PayResponse response = new PayResponse();
//                            HuaXiSDK.getInstance().toWXPay(response);
                            mView.onError(e);
                        }
                    }

                    @Override
                    public void onNext(Base<WeChatBean> payResponseBase) {//微信wechat
                        Log.d("PayResponse", payResponseBase.toString());
                        if (payResponseBase.getCode() == ApiCodeEnum.SUCCESS.getValue() && mView != null) {
                            WeChatBean data = payResponseBase.getData();
//                            mView.setWechatPayResult(data);
                            if (AppUtil.isLogin()) {
                                HuaXiSDK.getInstance().toWXPay(data);
                            }
                        }
                    }
                });
    }

}
