package com.spriteapp.booklibrary.util;

import android.text.TextUtils;
import android.util.Log;

import com.spriteapp.booklibrary.api.BookApi;
import com.spriteapp.booklibrary.base.Base;
import com.spriteapp.booklibrary.constant.Constant;
import com.spriteapp.booklibrary.enumeration.ApiCodeEnum;
import com.spriteapp.booklibrary.model.UserBean;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by userfirst on 2018/3/8.
 */

public class HistoryTime {
    public static void getHistory(String book_id, final String chapter_id) {
        try {
            if (!AppUtil.isLogin()) return;
            if (TextUtils.isEmpty(book_id) || TextUtils.isEmpty(chapter_id)) return;
            if (SharedPreferencesUtil.getInstance().getString(chapter_id) != null && SharedPreferencesUtil.getInstance().getString(chapter_id).equals(chapter_id)) {
                Log.d("getHistory", "已经上传过的章节不需要再次上传---chapter_id===" + chapter_id);
                return;
            }
            Log.d("getHistory", "上传阅读时长");
            if (!AppUtil.isLogin())
                return;
            BookApi.getInstance().
                    service
                    .book_readhistroy(book_id, chapter_id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Base>() {
                        @Override
                        public void onComplete() {
                        }

                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(Base userModelBase) {
                            if (userModelBase.getCode() == ApiCodeEnum.SUCCESS.getValue()) {
                                Log.d("getHistory", "上传阅读时长成功--chapter_id===" + chapter_id);
                                SharedPreferencesUtil.getInstance().putString(chapter_id, chapter_id);
                            } else {
                                Log.d("getHistory", "上传阅读时长失败");
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
