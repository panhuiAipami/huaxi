package com.spriteapp.booklibrary.ui.presenter;

import com.spriteapp.booklibrary.api.BookApi;
import com.spriteapp.booklibrary.base.Base;
import com.spriteapp.booklibrary.base.BasePresenter;
import com.spriteapp.booklibrary.model.ChoiceBean;
import com.spriteapp.booklibrary.ui.view.ChoiceView;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 精选
 * Created by panhui on 2018/1/26.
 */

public class ChoiceContentPresenter implements BasePresenter<ChoiceView> {
    ChoiceView choiceView;

    public ChoiceContentPresenter(ChoiceView view) {
        attachView(view);
    }

    public void requestGetData(final int page,int sex) {
        BookApi.getInstance()
                .service
                .book_weekly("json",page,sex)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base<List<ChoiceBean>>>() {

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Base<List<ChoiceBean>> listBase) {
                        if(choiceView != null)
                            choiceView.setData(listBase);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        if(choiceView != null)
                            choiceView.onError(e);
                    }

                    @Override
                    public void onComplete() {
                        if(choiceView != null)
                            choiceView.disMissProgress();
                    }
                });
    }

    @Override
    public void attachView(ChoiceView view) {
        choiceView = view;
    }

    @Override
    public void detachView() {
        choiceView = null;
    }
}
