package com.spriteapp.booklibrary.base;

import android.content.Context;

/**
 * Created by kuangxiaoguo on 2017/7/8.
 */

public interface BaseView<T> {

    void onError(Throwable t);

    void setData(Base<T> result);

    void showNetWorkProgress();

    void disMissProgress();

    Context getMyContext();
}
