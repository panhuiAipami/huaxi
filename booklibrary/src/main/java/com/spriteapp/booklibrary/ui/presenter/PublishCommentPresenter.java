package com.spriteapp.booklibrary.ui.presenter;

import com.spriteapp.booklibrary.api.BookApi;
import com.spriteapp.booklibrary.base.Base;
import com.spriteapp.booklibrary.base.BasePresenter;
import com.spriteapp.booklibrary.enumeration.ApiCodeEnum;
import com.spriteapp.booklibrary.ui.view.PublishCommentView;
import com.spriteapp.booklibrary.util.AppUtil;
import com.spriteapp.booklibrary.util.ToastUtil;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by kuangxiaoguo on 2017/7/8.
 */

public class PublishCommentPresenter implements BasePresenter<PublishCommentView> {

    private PublishCommentView mView;
    private Disposable mDisposable;

    @Override
    public void attachView(PublishCommentView view) {
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

    public void sendComment(int bookId, String title,String content,float score) {
        if (!AppUtil.isNetAvailable(mView.getMyContext())) {
            return;
        }
        mView.showNetWorkProgress();
        BookApi.getInstance().
                service
                .addComment(bookId,title, content,score)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base<Void>>() {
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
                    public void onNext(Base<Void> voidBase) {
                        if (voidBase.getCode() == ApiCodeEnum.SUCCESS.getValue()) {
                            ToastUtil.showSingleToast("发表成功");
                            mView.setData(voidBase);
                        } else {
                            ToastUtil.showSingleToast(voidBase.getMessage());
                        }
                    }
                });
    }

}
