package com.spriteapp.booklibrary.ui.presenter;

import android.util.Log;

import com.google.gson.Gson;
import com.spriteapp.booklibrary.api.BookApi;
import com.spriteapp.booklibrary.base.Base;
import com.spriteapp.booklibrary.base.BasePresenter;
import com.spriteapp.booklibrary.config.HuaXiSDK;
import com.spriteapp.booklibrary.constant.Constant;
import com.spriteapp.booklibrary.constant.SignConstant;
import com.spriteapp.booklibrary.enumeration.ApiCodeEnum;
import com.spriteapp.booklibrary.enumeration.LoginStateEnum;
import com.spriteapp.booklibrary.enumeration.UpdateTextStateEnum;
import com.spriteapp.booklibrary.manager.CacheManager;
import com.spriteapp.booklibrary.model.RegisterModel;
import com.spriteapp.booklibrary.model.UserModel;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.model.response.BookStoreResponse;
import com.spriteapp.booklibrary.model.response.LoginResponse;
import com.spriteapp.booklibrary.ui.view.BookShelfView;
import com.spriteapp.booklibrary.util.AppUtil;
import com.spriteapp.booklibrary.util.NetworkUtil;
import com.spriteapp.booklibrary.util.SharedPreferencesUtil;

import java.util.List;

import de.greenrobot.event.EventBus;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by kuangxiaoguo on 2017/7/11.
 */

public class BookShelfPresenter implements BasePresenter<BookShelfView> {

    private static final String TAG = "BookShelfPresenter";
    private BookShelfView mView;
    private Disposable mDisposable;
    private Call<Base<LoginResponse>> mLoginCall;
    private Call<BookStoreResponse> mStoreCall;

    @Override
    public void attachView(BookShelfView view) {
        if (mView == null) {
            mView = view;
        }
    }

    @Override
    public void detachView() {
        mView = null;
        if (mDisposable != null) {
            mDisposable.dispose();
            mDisposable = null;
        }
        if (mLoginCall != null) {
            mLoginCall.cancel();
            mLoginCall = null;
        }
        if (mStoreCall != null) {
            mStoreCall.cancel();
            mStoreCall = null;
        }
    }

    public void getBookShelf() {
        if (mView == null) {
            return;
        }
        if (!AppUtil.isNetAvailable(mView.getMyContext())) {
            return;
        }
        BookApi.getInstance()
                .service
                .getBookShelf()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base<List<BookDetailResponse>>>() {

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(@NonNull Base<List<BookDetailResponse>> listBase) {
                        if (mView == null) {
                            return;
                        }
                        mView.setData(listBase);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    public void getLoginInfo(RegisterModel model) {
        getLoginInfo(model, true);
    }

    public void getLoginInfo(RegisterModel model, final boolean isShowDialog) {
        if (mView == null) {
            return;
        }
//        if (!AppUtil.isNetAvailable(mView.getMyContext())) {
//            return;
//        }
//        if (isShowDialog) {
//            mView.showNetWorkProgress();
//        }
//        mLoginCall = BookApi.getInstance().service.getLoginInfo("format");
//        HuaXiSDK.mLoginState = LoginStateEnum.LOADING;
//        EventBus.getDefault().post(UpdateTextStateEnum.UPDATE_TEXT_STATE);
        if (model != null) {
//            if (isShowDialog) {
//                mView.disMissProgress();
//            }
            HuaXiSDK.mLoginState = LoginStateEnum.SUCCESS;
            EventBus.getDefault().post(UpdateTextStateEnum.UPDATE_TEXT_STATE);
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setUser_vip_class(model.getUser_vip_class());
            loginResponse.setUser_avatar(model.getUser_avatar());
            loginResponse.setUser_gender(model.getUser_gender());
            loginResponse.setUser_false_point(model.getUser_false_point());
            loginResponse.setUser_real_point(model.getUser_real_point());
            loginResponse.setUser_id(model.getUser_id());
            loginResponse.setUser_mobile(model.getUser_mobile());
            loginResponse.setUser_nickname(model.getUserName());
            SharedPreferencesUtil.getInstance().putString(SignConstant.HUA_XI_TOKEN_KEY, model.getToken());
            Log.i("getLoginInfo", "getLoginInfo: " + model.getToken());
            mView.setLoginInfo(loginResponse);
        }
//        用户信息
//
//        BookApi.getInstance()
//                .service
//                .getUserInfo("format")
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<Base<UserModel>>() {
//                    @Override
//                    public void onSubscribe(@NonNull Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(@NonNull Base<UserModel> userModelBase) {
//                        if (userModelBase.getCode() == ApiCodeEnum.SUCCESS.getValue()) {
//                            String name = userModelBase.getData().toString();
//                            Log.d("update11", name + "哈g哈");
//                            if(userModelBase!=null&&userModelBase.getData()!=null){
//                                mView.setLoginInfo(userModelBase.getData());
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onError(@NonNull Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                })
//        mLoginCall.enqueue(new Callback<Base<LoginResponse>>() {
//            @Override
//            public void onResponse(Call<Base<LoginResponse>> call, Response<Base<LoginResponse>> response) {
//                HuaXiSDK.mLoginState = LoginStateEnum.SUCCESS;
//                EventBus.getDefault().post(UpdateTextStateEnum.UPDATE_TEXT_STATE);
//                if (mView == null) {
//                    return;
//                }
//                if (isShowDialog) {
//                    mView.disMissProgress();
//                }
//                Base<LoginResponse> body = response.body();
//                if (body == null) {
//                    return;
//                }
//                LoginResponse data = body.getData();
//                if (data != null) {
//                    mView.setLoginInfo(data);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Base<LoginResponse>> call, Throwable t) {
//                if (mView != null && isShowDialog) {
//                    mView.disMissProgress();
//                }
//                if (mView != null) {
//                    mView.onError(t);
//                }
//                HuaXiSDK.mLoginState = LoginStateEnum.FAILED;
//                EventBus.getDefault().post(UpdateTextStateEnum.UPDATE_TEXT_STATE);
//            }
//        });
    }

    public void deleteBook(int bookId) {
        if (mView == null) {
            return;
        }
        if (!AppUtil.isNetAvailable(mView.getMyContext())) {
            return;
        }
        BookApi.getInstance().
                service
                .deleteBook(bookId, "del")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base<Void>>() {
                    @Override
                    public void onComplete() {
                    }

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Base<Void> voidBase) {
                        if (mView == null) {
                            return;
                        }
                        if (voidBase.getCode() == ApiCodeEnum.SUCCESS.getValue()) {
                            mView.setDeleteBookResponse();
                        }
                    }
                });
    }

    public void addToShelf(int bookId, String action, int chapterId) {
        addToShelf(bookId, action, chapterId, true);
    }

    public void addToShelf(int bookId, String action, int chapterId, final boolean isAdd) {
        if (mView == null) {
            return;
        }
        if (!NetworkUtil.isAvailable(mView.getMyContext())) {
            return;
        }
        BookApi.getInstance()
                .service
                .addBookToShelf(bookId, action, chapterId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base<Void>>() {
                    @Override
                    public void onComplete() {
                    }

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Base<Void> result) {
                        if (mView == null) {
                            return;
                        }
                        if (result.getCode() == ApiCodeEnum.SUCCESS.getValue() && isAdd) {
                            mView.setAddShelfResponse();
                        }
                    }
                });
    }

    public void getBookDetail(int bookId) {
        if (mView == null) {
            return;
        }
        if (!AppUtil.isNetAvailable(mView.getMyContext())) {
            return;
        }
        BookApi.getInstance().
                service.getBookDetail(bookId).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base<BookDetailResponse>>() {
                    @Override
                    public void onComplete() {
                    }

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Base<BookDetailResponse> bookDetailResponseBase) {
                        if (bookDetailResponseBase.getCode()
                                == ApiCodeEnum.SUCCESS.getValue() && mView != null) {
                            mView.setBookDetail(bookDetailResponseBase.getData());
                        }
                    }
                });
    }

    public void addOneMoreBookToShelf(String bookJson) {
        if (mView == null) {
            return;
        }
        if (!NetworkUtil.isAvailable(mView.getMyContext())) {
            return;
        }
        BookApi.getInstance().
                service
                .addBook("multi", bookJson)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base<Void>>() {
                    @Override
                    public void onComplete() {
                    }

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Base<Void> result) {
                    }
                });
    }

    public void getBookStoreData() {
        if (mView == null) {
            return;
        }
        if (!NetworkUtil.isAvailable(mView.getMyContext())) {
            mView.onError(null);
            return;
        }
        mStoreCall = BookApi.getInstance().service.getBookStore(Constant.JSON_TYPE);
        mStoreCall.enqueue(new Callback<BookStoreResponse>() {
            @Override
            public void onResponse(Call<BookStoreResponse> call, Response<BookStoreResponse> response) {
                if (mView == null) {
                    return;
                }
                BookStoreResponse body = response.body();
                if (body == null) {
                    return;
                }
                if (body.getCode() == ApiCodeEnum.SUCCESS.getValue()) {
                    Gson gson = new Gson();
                    String json = gson.toJson(body);
                    CacheManager.saveNativeBookStore(json);
                    mView.setBookStoreData(body);
                }
            }

            @Override
            public void onFailure(Call<BookStoreResponse> call, Throwable t) {
                if (mView != null) {
                    mView.onError(t);
                }
            }
        });
    }

    public void getUserInfo() {
        Log.d("userInfo", "用户信息");
        if (mView == null) {
            return;
        }
        if (!NetworkUtil.isAvailable(mView.getMyContext())) {
            return;
        }
        BookApi.getInstance().
                service
                .getUserInfo(Constant.JSON_TYPE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base<UserModel>>() {
                    @Override
                    public void onComplete() {
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
                    public void onNext(Base<UserModel> userModelBase) {
                        Log.d("userInfo", userModelBase.getData().toString());
                        if (mView == null) {
                            return;
                        }
                        if (userModelBase.getCode() == ApiCodeEnum.SUCCESS.getValue()) {
                            Log.d("userInfo", userModelBase.getData().toString());
                            mView.setUserInfo(userModelBase.getData());
                        } else {
                            mView.setUserInfo(null);
                        }
                    }
                });
    }

}


