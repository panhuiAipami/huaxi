package com.spriteapp.booklibrary.ui.dialog;

import android.app.Activity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.api.BookApi;
import com.spriteapp.booklibrary.base.Base;
import com.spriteapp.booklibrary.base.BaseTwo;
import com.spriteapp.booklibrary.constant.Constant;
import com.spriteapp.booklibrary.enumeration.ApiCodeEnum;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.ui.adapter.GuessAdapter;
import com.spriteapp.booklibrary.util.NetworkUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by userfirst on 2018/4/26.
 */

public class GuessYouLikeDialog extends BaseDialog {
    private Activity context;
    private BookDetailResponse response;
    private int type;//1为阅读到结尾,全屏,2为中途退出,半屏
    private RecyclerView recyclerView;
    private LinearLayout share_layout;
    private SwipeRefreshLayout refresh;
    private GuessAdapter adapter;
    private List<BookDetailResponse> list = new ArrayList<>();

    public GuessYouLikeDialog(Activity context, BookDetailResponse response, int type) {
        this.context = context;
        this.response = response;
        this.type = type;
        if (!NetworkUtil.isAvailable(context)) {

            if (type == 2) {
                context.finish();
            }
        } else {
            initDialog(context, null, type == 2 ? R.layout.guess_layout : R.layout.book_store_layout, BaseDialog.TYPE_BOTTOM, true);
            mDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
            lp.flags = lp.flags | (WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            mDialog.getWindow().setAttributes(lp);
            mDialog.getWindow().setWindowAnimations(R.style.bottomPopupDialog);
            findViewById();
            getLike();
//            if (type == 1) {
//                getOtherPeopleLike();
//            }
        }

    }

    private void findViewById() {
        recyclerView = (RecyclerView) mDialog.findViewById(R.id.recyclerView);
        share_layout = (LinearLayout) mDialog.findViewById(R.id.share_layout);
        if (type == 1) {
//            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//            recyclerView.setLayoutParams(layoutParams);
            refresh = (SwipeRefreshLayout) mDialog.findViewById(R.id.refresh);
            refresh.setEnabled(false);
        }
        share_layout.setBackgroundColor(context.getResources().getColor(R.color.app_background));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new GuessAdapter(context, list, response, type);
        recyclerView.setAdapter(adapter);


    }

    private void getLike() {
        if (!NetworkUtil.isAvailable(context)) return;
        BookApi.getInstance()
                .service
                .book_newrecommend(response.getBook_id(), Constant.JSON_TYPE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base<List<BookDetailResponse>>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Base<List<BookDetailResponse>> bookStoreResponse) {
                        if (bookStoreResponse != null) {
                            int resultCode = bookStoreResponse.getCode();
                            if (resultCode == ApiCodeEnum.SUCCESS.getValue()) {//成功
                                if (bookStoreResponse.getData() != null && bookStoreResponse.getData().size() != 0) {
                                    list.addAll(bookStoreResponse.getData());
                                    adapter.notifyDataSetChanged();
                                }

                            }
                        }

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 看过本书的人也喜欢
     */
    private void getOtherPeopleLike() {
        if (!NetworkUtil.isAvailable(context)) return;
        BookApi.getInstance()
                .service
                .book_newrecommend(response.getBook_id(), Constant.JSON_TYPE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base<List<BookDetailResponse>>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Base<List<BookDetailResponse>> bookStoreResponse) {
                        if (bookStoreResponse != null) {
                            int resultCode = bookStoreResponse.getCode();
                            if (resultCode == ApiCodeEnum.SUCCESS.getValue()) {//成功
                                if (bookStoreResponse.getData() != null && bookStoreResponse.getData().size() != 0) {
                                    if (bookStoreResponse.getData().size() > 2) {
                                        List<BookDetailResponse> bookDetailResponses = bookStoreResponse.getData().subList(0, 2);
                                        list.addAll(0, bookDetailResponses);
                                        adapter.notifyDataSetChanged();
                                    } else {
                                        list.addAll(0, bookStoreResponse.getData());
                                        adapter.notifyDataSetChanged();
                                    }

                                }

                            }
                        }

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
