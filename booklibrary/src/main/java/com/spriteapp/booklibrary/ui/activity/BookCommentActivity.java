package com.spriteapp.booklibrary.ui.activity;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.api.BookApi;
import com.spriteapp.booklibrary.base.Base;
import com.spriteapp.booklibrary.model.BookCommentBean;
import com.spriteapp.booklibrary.util.ScreenUtil;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class BookCommentActivity extends TitleActivity {
    RecyclerView recycler_view_comment;


    @Override
    public void initData() throws Exception {
        setTitle("作品评论");
    }

    @Override
    public void findViewId() throws Exception {
        super.findViewId();
        recycler_view_comment = (RecyclerView) findViewById(R.id.recycler_view_comment);

    }

    @Override
    public void configViews() throws Exception {
        super.configViews();
        BookApi.getInstance().service.getBookComment(1627,0,0,0,20). subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Base<List<BookCommentBean>>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Base<List<BookCommentBean>> listBase) {
                Log.e("onNext","-----onNext----->"+listBase.toString());
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

    }

    @Override
    public void addContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_book_comment, null);
        mContainerLayout.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mContainerLayout.setPadding(0, ScreenUtil.dpToPxInt(47),0,0);
    }


}
