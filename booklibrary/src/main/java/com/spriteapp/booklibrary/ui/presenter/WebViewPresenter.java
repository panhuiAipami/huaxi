package com.spriteapp.booklibrary.ui.presenter;

import com.spriteapp.booklibrary.api.BookApi;
import com.spriteapp.booklibrary.base.Base;
import com.spriteapp.booklibrary.base.BasePresenter;
import com.spriteapp.booklibrary.enumeration.ApiCodeEnum;
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

    public void requestAliPay(String productId) {
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
                            mView.setAliPayResult(data);
                        }
                    }
                });
    }

}
