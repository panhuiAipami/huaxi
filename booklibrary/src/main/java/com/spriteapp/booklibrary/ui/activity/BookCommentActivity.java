package com.spriteapp.booklibrary.ui.activity;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.api.BookApi;
import com.spriteapp.booklibrary.base.Base;
import com.spriteapp.booklibrary.enumeration.ApiCodeEnum;
import com.spriteapp.booklibrary.model.BookCommentBean;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.ui.adapter.NewBookCommentAdapter;
import com.spriteapp.booklibrary.util.ActivityUtil;
import com.spriteapp.booklibrary.util.AppUtil;
import com.spriteapp.booklibrary.util.GlideUtils;
import com.spriteapp.booklibrary.util.ScreenUtil;
import com.spriteapp.booklibrary.widget.recyclerview.URecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.spriteapp.booklibrary.ui.activity.ReadActivity.BOOK_DETAIL_TAG;

public class BookCommentActivity extends TitleActivity implements SwipeRefreshLayout.OnRefreshListener, URecyclerView.LoadingListener {
    private URecyclerView recycler_view_comment;
    private SwipeRefreshLayout refresh;
    private ImageView book_cover, book_author_cover;
    private TextView book_name, author_name, book_comment_num, book_collection, goto_comment;
    private BookDetailResponse detailResponse;
    private List<BookCommentBean> list = new ArrayList<>();
    private NewBookCommentAdapter adapter;
    private long firstTime = 0;
    private long lastTime = 0;
    private int book_id;


    @Override
    public void initData() throws Exception {
        setTitle("作品评论");
        Intent intent = getIntent();
        detailResponse = (BookDetailResponse) intent.getSerializableExtra(BOOK_DETAIL_TAG);
        listener();
        setAdapter();
        setBookDetails();
    }

    @Override
    public void findViewId() throws Exception {
        super.findViewId();
        recycler_view_comment = (URecyclerView) findViewById(R.id.recycler_view_comment);
        refresh = (SwipeRefreshLayout) findViewById(R.id.refresh);
        refresh.setColorSchemeResources(R.color.square_comment_selector);
        book_cover = (ImageView) findViewById(R.id.book_cover);
        book_author_cover = (ImageView) findViewById(R.id.book_author_cover);
        book_name = (TextView) findViewById(R.id.book_name);
        author_name = (TextView) findViewById(R.id.author_name);
        book_comment_num = (TextView) findViewById(R.id.book_comment_num);
        book_collection = (TextView) findViewById(R.id.book_collection);
        goto_comment = (TextView) findViewById(R.id.goto_comment);

    }

    private void setAdapter() {
        adapter = new NewBookCommentAdapter(this, list);
        recycler_view_comment.setLayoutManager(new LinearLayoutManager(this));
        recycler_view_comment.setAdapter(adapter);
    }

    private void listener() {
        goto_comment.setOnClickListener(this);
        refresh.setOnRefreshListener(this);
        recycler_view_comment.setLoadingListener(this);
    }

    @Override
    public void configViews() throws Exception {
        super.configViews();
        getComment();

    }

    private void getComment() {
        BookApi.getInstance()
                .service
                .getBookComment(book_id, 0, firstTime, 20)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Base<List<BookCommentBean>>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Base<List<BookCommentBean>> listBase) {
                if (listBase != null) {
                    int resultCode = listBase.getCode();
                    if (resultCode == ApiCodeEnum.SUCCESS.getValue()) {//成功

                        if (listBase.getData() != null && listBase.getData().size() != 0) {
                            if (firstTime == 0) list.clear();
                            firstTime = listBase.getData().get(listBase.getData().size() - 1).getComment_replydatetime();
                            list.addAll(listBase.getData());
                            book_comment_num.setText(listBase.getCount() + "条评论");
                            adapter.notifyDataSetChanged();


                        }
                    }
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                refresh.setRefreshing(false);
            }
        });
    }

    @Override
    public void addContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_book_comment, null);
        mContainerLayout.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mContainerLayout.setPadding(0, ScreenUtil.dpToPxInt(47), 0, 0);
    }

    private void setBookDetails() {
        if (detailResponse == null) return;
        book_id = detailResponse.getBook_id();
        GlideUtils.loadImage(book_cover, detailResponse.getBook_image(), this);
        Log.d("book_author_cover", "作者名称：" + detailResponse.getAuthor_avatar());
        GlideUtils.loadImage(book_author_cover, detailResponse.getAuthor_avatar(), this);
        book_name.setText(detailResponse.getBook_name());
        author_name.setText(detailResponse.getAuthor_name());
        if (detailResponse.getBook_add_shelf() == 1){
            book_collection.setSelected(true);
            book_collection.setText("已收藏");
        }

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == goto_comment) {//去评论
            if (!AppUtil.isLogin(this)) return;
            if (detailResponse == null) return;
            ActivityUtil.gotoPublishCommentActivity(this, detailResponse.getBook_id());
        }
    }

    @Override
    public void onRefresh() {
        firstTime = 0;
        lastTime = 0;
        getComment();

    }

    @Override
    public void onLoadMore() {
        if (firstTime != lastTime) {
            lastTime = firstTime;
            getComment();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if(requestCode==0){
                firstTime = 0;
                lastTime = 0;
                getComment();
            }
        }
    }
}
