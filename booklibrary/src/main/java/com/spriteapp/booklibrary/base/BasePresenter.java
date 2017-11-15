package com.spriteapp.booklibrary.base;

public interface BasePresenter<T> {

    void attachView(T view);

    void detachView();
}
