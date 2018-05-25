package com.spriteapp.booklibrary.ui.activity;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.api.BookApi;
import com.spriteapp.booklibrary.base.Base;
import com.spriteapp.booklibrary.constant.Constant;
import com.spriteapp.booklibrary.enumeration.ApiCodeEnum;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.ui.adapter.StoreDetailsAdapter;
import com.spriteapp.booklibrary.util.NetworkUtil;
import com.spriteapp.booklibrary.util.ScreenUtil;
import com.spriteapp.booklibrary.widget.recyclerview.URecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.spriteapp.booklibrary.util.ActivityUtil.FRAGMENT_SEX;
import static com.spriteapp.booklibrary.util.ActivityUtil.STORE_DETAILS_TITLE;
import static com.spriteapp.booklibrary.util.ActivityUtil.STORE_DETAILS_TYPE;

/**
 * 书城详情列表activity
 */
public class StoreDetailsActivity extends TitleActivity implements SwipeRefreshLayout.OnRefreshListener, URecyclerView.LoadingListener {
    private int sex = 0;
    private int title;
    private SwipeRefreshLayout swipe_refresh;
    private URecyclerView recyclerView;
    private LinearLayout null_layout;
    private RelativeLayout big_layout;
    private TextView miaoshu;
    private int mColumnCount;
    private int page = 1;
    private int last_page = 1;
    private List<BookDetailResponse> list = new ArrayList<>();
    private StoreDetailsAdapter adapter;


    @Override
    public void initData() throws Exception {
        Intent intent = getIntent();
        mColumnCount = intent.getIntExtra(STORE_DETAILS_TYPE, 0);
        sex = intent.getIntExtra(FRAGMENT_SEX, 0);
        title = intent.getIntExtra(STORE_DETAILS_TITLE, 0);
        setTitle(title);
        getData();
    }

    @Override
    public void addContentView() {
        View view = getLayoutInflater().inflate(R.layout.choice_fragment_item_list, null);
        mContainerLayout.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void findViewId() throws Exception {
        super.findViewId();
        swipe_refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        recyclerView = (URecyclerView) findViewById(R.id.list);
        null_layout = (LinearLayout) findViewById(R.id.null_layout);
        big_layout = (RelativeLayout) findViewById(R.id.big_layout);
        miaoshu = (TextView) findViewById(R.id.miaoshu);
        swipe_refresh.setOnRefreshListener(this);
        recyclerView.setLoadingListener(this);
        miaoshu.setText("竟然没有加载出来");
        big_layout.setPadding(-ScreenUtil.dpToPxInt(10), ScreenUtil.dpToPxInt(47), 0, 0);
        big_layout.setBackgroundColor(getResources().getColor(R.color.app_background));
        swipe_refresh.setColorSchemeResources(R.color.square_comment_selector);
        adapter = new StoreDetailsAdapter(this, list, 1, 1,1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void getData() {
        switch (mColumnCount) {
            case 1://重磅推荐
                book_recommand();
                break;
            case 2://经典完本
                book_finish();
                break;
            case 3://免费新书
                book_freenew();
                break;
            case 4://最近更新
                book_news();
                break;
            case 5://男生玄幻仙侠
                xuanhuan(12);
                break;
            case 6://女生现言总裁
                xuanhuan(10);
                break;
            case 7://男生玄幻仙侠
                xuanhuan(13);
                break;
            case 8://女生现言总裁
                xuanhuan(11);
                break;

        }
    }

    public void goneOrShow() {
        if (null_layout != null) {
            if (list.size() == 0) {
                null_layout.setVisibility(View.VISIBLE);
            } else {
                null_layout.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 重磅推荐
     */
    private void book_recommand() {
        if (!NetworkUtil.isAvailable(this)) return;
        BookApi.getInstance()
                .service
                .book_recommand(Constant.JSON_TYPE, page, sex)
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
                                    if (page == 1) list.clear();
                                    page++;
                                    setLastPage();
                                    list.addAll(bookStoreResponse.getData());
                                    adapter.notifyDataSetChanged();
                                    goneOrShow();
                                }
                            }
                        }

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        swipe_refresh.setRefreshing(false);
                    }
                });

    }

    /**
     * 经典完本
     */
    private void book_finish() {
        if (!NetworkUtil.isAvailable(this)) return;

        BookApi.getInstance()
                .service
                .book_finish(Constant.JSON_TYPE, page, sex)
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
                                    if (page == 1) list.clear();
                                    page++;
                                    setLastPage();
                                    list.addAll(bookStoreResponse.getData());
                                    adapter.notifyDataSetChanged();
                                    goneOrShow();
                                }

                            }
                        }

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        swipe_refresh.setRefreshing(false);
                    }
                });

    }

    /**
     * 免费新书
     */
    private void book_freenew() {
        if (!NetworkUtil.isAvailable(this)) return;
        BookApi.getInstance()
                .service
                .book_freenew(Constant.JSON_TYPE, page, sex)
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
                                    if (page == 1) list.clear();
                                    page++;
                                    setLastPage();
                                    list.addAll(bookStoreResponse.getData());
                                    adapter.notifyDataSetChanged();
                                    goneOrShow();
                                }

                            }
                        }

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        swipe_refresh.setRefreshing(false);
                    }
                });

    }

    /**
     * 最近更新
     */
    private void book_news() {
        if (!NetworkUtil.isAvailable(this)) return;
        BookApi.getInstance()
                .service
                .book_news(Constant.JSON_TYPE, page, sex)
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
                                    if (page == 1) list.clear();
                                    page++;
                                    setLastPage();
                                    list.addAll(bookStoreResponse.getData());
                                    adapter.notifyDataSetChanged();
                                    goneOrShow();
                                }
                            }
                        }

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        swipe_refresh.setRefreshing(false);
                    }
                });

    }


    /**
     * 男生玄幻仙侠
     */
    private void xuanhuan(int type) {
        if (!NetworkUtil.isAvailable(this)) return;
        BookApi.getInstance()
                .service
                .book_widget(Constant.JSON_TYPE, page, type, sex)
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
                                    if (page == 1) list.clear();
                                    page++;
                                    setLastPage();
                                    list.addAll(bookStoreResponse.getData());
                                    adapter.notifyDataSetChanged();
                                    goneOrShow();
                                }
                            }
                        }

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        swipe_refresh.setRefreshing(false);
                    }
                });

    }


    @Override
    public void onRefresh() {
        page = 1;
        last_page = 1;
        getData();

    }

    @Override
    public void onLoadMore() {
        if (page > last_page)
            getData();
    }

    private void setLastPage() {
        if (mColumnCount == 5 || mColumnCount == 6 || mColumnCount == 7 || mColumnCount == 8)
            last_page++;
    }
}
