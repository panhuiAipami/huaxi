package com.spriteapp.booklibrary.ui.dialog;

import android.app.Activity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.api.BookApi;
import com.spriteapp.booklibrary.base.Base;
import com.spriteapp.booklibrary.base.BaseTwo;
import com.spriteapp.booklibrary.constant.Constant;
import com.spriteapp.booklibrary.enumeration.ApiCodeEnum;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.ui.adapter.GuessAdapter;
import com.spriteapp.booklibrary.util.AppUtil;
import com.spriteapp.booklibrary.util.NetworkUtil;
import com.spriteapp.booklibrary.util.ScreenUtil;

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
    //新添
    private LinearLayout store_title_bar;
    private TextView cate_title, cate_title_small;

    public GuessYouLikeDialog(Activity context, BookDetailResponse response, int type) {
        this.context = context;
        this.response = response;
        this.type = type;
        if (!NetworkUtil.isAvailable(context) || !AppUtil.isLogin()) {
            if (type == 2) {
                context.finish();
            }
        } else {
            initDialog(context, null, type == 2 ? R.layout.guess_layout : R.layout.book_store_layout, BaseDialog.TYPE_BOTTOM, true);
            WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
            mDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            lp.flags = lp.flags | (WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            mDialog.getWindow().setAttributes(lp);
            mDialog.getWindow().setWindowAnimations(R.style.bottomPopupDialog);
            findViewById();
            getLike();
        }

    }

    private void findViewById() {
        recyclerView = (RecyclerView) mDialog.findViewById(R.id.recyclerView);
        share_layout = (LinearLayout) mDialog.findViewById(R.id.share_layout);
        if (type == 1) {

            refresh = (SwipeRefreshLayout) mDialog.findViewById(R.id.refresh);
            refresh.setEnabled(false);
//            share_layout.setPadding(0, ScreenUtil.dpToPxInt(25), 0, 0);
        } else if (type == 2) {
            store_title_bar = (LinearLayout) mDialog.findViewById(R.id.store_title_bar);
            cate_title = (TextView) mDialog.findViewById(R.id.cate_title);
            cate_title_small = (TextView) mDialog.findViewById(R.id.cate_title_small);
            store_title_bar.setVisibility(View.VISIBLE);
            cate_title_small.setVisibility(View.GONE);
            cate_title.setText("猜你喜欢");
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ScreenUtil.dpToPxInt(438));
            recyclerView.setLayoutParams(layoutParams);
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
                                } else {
                                    if (type == 2) {
                                        dismiss();
                                        context.finish();
                                    } else {
                                        dismiss();
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
