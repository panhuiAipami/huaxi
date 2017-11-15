package com.spriteapp.booklibrary.ui.activity;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.database.RecentBookDb;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.ui.adapter.BookShelfAdapter;
import com.spriteapp.booklibrary.ui.fragment.BookshelfFragment;
import com.spriteapp.booklibrary.util.CollectionUtil;
import com.spriteapp.booklibrary.util.ScreenUtil;
import com.spriteapp.booklibrary.widget.recyclerview.SpaceItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kuangxiaoguo on 2017/9/18.
 */

public class RecentReadActivity extends TitleActivity {

    private static final int SHELF_SPAN_COUNT = 3;
    private static final int HORIZONTAL_SPACE = 2;
    private static final int VERTICAL_SPACE = 5;
    private RecyclerView mRecyclerView;
    private List<BookDetailResponse> mReadList;
    private BookShelfAdapter mAdapter;
    private RecentBookDb mBookDb;
    private ImageView mEmptyImageView;

    @Override
    public void initData() {
        setTitle(getString(R.string.book_reader_recent_read_text));
        mBookDb = new RecentBookDb(this);
        initRecyclerView();
        setAdapter();
        initFragment();
    }

    /**
     * 解决从最近阅读页面到阅读页面加入书架不生效问题
     * 原因：BookshelfFragment未初始化，EventBus还未订阅，无法处理该逻辑
     */
    private void initFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.book_shelf_layout,
                        new BookshelfFragment()).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        queryRecentBook();
    }

    private void queryRecentBook() {
        List<BookDetailResponse> bookList = mBookDb.queryBookData();
        if (CollectionUtil.isEmpty(bookList)) {
            mEmptyImageView.setVisibility(View.VISIBLE);
            return;
        }
        mEmptyImageView.setVisibility(View.GONE);
        if (mReadList == null) {
            mReadList = new ArrayList<>();
        }
        mReadList.clear();
        mReadList.addAll(bookList);
        setAdapter();
    }

    @Override
    public void addContentView() {
        View view = getLayoutInflater().inflate(R.layout.book_reader_activity_recent_read, null);
        mContainerLayout.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void findViewId() {
        super.findViewId();
        mRecyclerView = (RecyclerView) findViewById(R.id.book_reader_recycler_view);
        mEmptyImageView = (ImageView) findViewById(R.id.empty_image_view);
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, SHELF_SPAN_COUNT,
                LinearLayoutManager.VERTICAL, false));
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(ScreenUtil.dpToPxInt(HORIZONTAL_SPACE),
                ScreenUtil.dpToPxInt(VERTICAL_SPACE)));
    }

    private void setAdapter() {
        if (mReadList == null) {
            mReadList = new ArrayList<>();
        }
        if (mAdapter == null) {
            mAdapter = new BookShelfAdapter(this, mReadList,
                    SHELF_SPAN_COUNT, HORIZONTAL_SPACE, true);
            mRecyclerView.setAdapter(mAdapter);
            return;
        }
        mAdapter.notifyDataSetChanged();
    }
}
