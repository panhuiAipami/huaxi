package com.spriteapp.booklibrary.ui.presenter;

import com.spriteapp.booklibrary.api.BookApi;
import com.spriteapp.booklibrary.base.Base;
import com.spriteapp.booklibrary.base.BasePresenter;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.ui.view.RankView;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 排行
 * Created by panhui on 2018/1/26.
 */

public class RankContentPresenter implements BasePresenter<RankView> {
    RankView rankView;

    public RankContentPresenter(RankView view) {
        attachView(view);
    }

    public void requestGetData(int type ,int interval,int sex) {
        BookApi.getInstance()
                .service
                .book_ranklist("json",type,interval,50,sex)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base<List<BookDetailResponse>>>() {

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Base<List<BookDetailResponse>> listBase) {
                        if(rankView != null)
                            rankView.setData(listBase);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        if(rankView != null)
                            rankView.onError(e);
                    }

                    @Override
                    public void onComplete() {
                        if(rankView != null)
                            rankView.disMissProgress();
                    }
                });
    }

    @Override
    public void attachView(RankView view) {
        rankView = view;
    }


    @Override
    public void detachView() {
        rankView = null;
    }
}
