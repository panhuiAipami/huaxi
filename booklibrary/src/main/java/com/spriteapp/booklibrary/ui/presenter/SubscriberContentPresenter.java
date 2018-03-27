package com.spriteapp.booklibrary.ui.presenter;

import android.util.Log;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.api.BookApi;
import com.spriteapp.booklibrary.base.Base;
import com.spriteapp.booklibrary.base.BasePresenter;
import com.spriteapp.booklibrary.constant.Constant;
import com.spriteapp.booklibrary.database.ContentDb;
import com.spriteapp.booklibrary.enumeration.ApiCodeEnum;
import com.spriteapp.booklibrary.enumeration.ChapterEnum;
import com.spriteapp.booklibrary.model.response.BookChapterResponse;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.model.response.SubscriberContent;
import com.spriteapp.booklibrary.ui.view.SubscriberContentView;
import com.spriteapp.booklibrary.util.AppUtil;
import com.spriteapp.booklibrary.util.NetworkUtil;
import com.spriteapp.booklibrary.util.SharedPreferencesUtil;
import com.spriteapp.booklibrary.util.ToastUtil;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by kuangxiaoguo on 2017/7/8.
 */

public class SubscriberContentPresenter implements BasePresenter<SubscriberContentView> {

    private SubscriberContentView mView;
    private Disposable mDisposable;

    @Override
    public void attachView(SubscriberContentView view) {
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

    public void getContent(int bookId, int chapterId) {
        getContent(bookId, chapterId,
                SharedPreferencesUtil.getInstance().getInt(Constant.IS_BOOK_AUTO_SUB));
    }

    public void getContent(int bookId, int chapterId, int isAutoSub) {
        getContent(bookId, chapterId, isAutoSub, true);
    }

    public void getContent(int bookId, int chapterId, int isAutoSub, boolean isShowDialog) {
        if (mView != null && !NetworkUtil.isAvailable(mView.getMyContext())) {
            ContentDb contentDb = new ContentDb(mView.getMyContext());
            SubscriberContent subscriberContent = contentDb.queryContent(bookId, chapterId);
            Base<SubscriberContent> result = new Base<>();
            if (subscriberContent != null) {
                result.setData(subscriberContent);
                mView.setData(result);
            } else {
                ToastUtil.showSingleToast(R.string.please_check_network_info);
            }
            return;
        }
        if (isShowDialog && mView != null) {
            mView.showNetWorkProgress();
        }
        BookApi.getInstance().
                service
                .getSubscriberContent(bookId, chapterId, isAutoSub)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base<SubscriberContent>>() {
                    @Override
                    public void onComplete() {
                        if (mView != null) {
                            mView.disMissProgress();
                        }
                        Log.d("book_content", "请求完成");
                    }

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.disMissProgress();
                        }
                        Log.d("book_content", "请求失败");
                    }

                    @Override
                    public void onNext(Base<SubscriberContent> subscriberContentBase) {
                        Log.d("book_content", "请求成功");
                        if (mView == null) {
                            return;
                        }
                        int resultCode = subscriberContentBase.getCode();
                        if (resultCode == ApiCodeEnum.SUCCESS.getValue()
                                || resultCode == ChapterEnum.BALANCE_SHORT.getCode()
                                || resultCode == ChapterEnum.UN_SUBSCRIBER.getCode()) {
                            mView.setData(subscriberContentBase);
                        } else if (resultCode == ChapterEnum.UN_LOGIN.getCode()
                                || resultCode == ChapterEnum.USER_UN_LOGIN.getCode()) {
                            mView.toChannelLogin();
                        } else {
                            ToastUtil.showSingleToast(subscriberContentBase.getMessage());
                        }
                    }
                });
    }

    public void getChapter(int bookId) {
        getChapter(bookId, true);
    }

    public void getChapter(int bookId, boolean isShowDialog) {
        if (!AppUtil.isNetAvailable(mView.getMyContext())) {
            return;
        }
        if (isShowDialog) {
            mView.showNetWorkProgress();
        }
        BookApi.getInstance().
                service
                .getCatalog(bookId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base<List<BookChapterResponse>>>() {
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
                            mView.disMissProgress();
                        }
                    }

                    @Override
                    public void onNext(Base<List<BookChapterResponse>> listBase) {
                        if (listBase.getCode() == ApiCodeEnum.SUCCESS.getValue() && mView != null) {
                            mView.setChapter(listBase.getData());
                        }
                    }
                });
    }

    public void getBookDetail(int bookId) {
        getBookDetail(bookId, true);
    }

    public void getBookDetail(int bookId, boolean isShowDialog) {
        if (!AppUtil.isNetAvailable(mView.getMyContext())) {
            return;
        }
        if (isShowDialog) {
            mView.showNetWorkProgress();
        }
        Log.d("getInstance_bookId", "bookId===" + bookId);
        BookApi.getInstance().
                service.getBookDetail(bookId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base<BookDetailResponse>>() {
                    @Override
                    public void onComplete() {
                        Log.d("book_details", "date完成");
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
                        Log.d("book_details", "date失败");
                        if (mView != null) {
                            mView.disMissProgress();
                        }
                    }

                    @Override
                    public void onNext(Base<BookDetailResponse> bookDetailResponseBase) {
                        if (bookDetailResponseBase.getCode() ==
                                ApiCodeEnum.SUCCESS.getValue() && mView != null && bookDetailResponseBase.getData() != null) {
                            mView.setBookDetail(bookDetailResponseBase.getData());

                        }
                    }
                });
    }
}
