package com.spriteapp.booklibrary.recyclerView.viewholder;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.manager.NightModeManager;
import com.spriteapp.booklibrary.manager.StoreColorManager;
import com.spriteapp.booklibrary.model.BookList;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.recyclerView.adapter.BookStoreAdapter;
import com.spriteapp.booklibrary.recyclerView.decorate.Visitable;
import com.spriteapp.booklibrary.ui.activity.HomeActivity;
import com.spriteapp.booklibrary.util.ActivityUtil;
import com.spriteapp.booklibrary.util.CollectionUtil;
import com.spriteapp.booklibrary.util.ScreenUtil;
import com.spriteapp.booklibrary.widget.recyclerview.StoreItemDecoration;
import com.spriteapp.booklibrary.widget.recyclerview.StoreShelfItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的书架
 * Created by kuangxiaoguo on 2017/7/28.
 */

public class ShelfViewHolder extends BaseViewHolder<Visitable> {

    private static final int SHELF_SHOW_COUNT = 3;
    private static final int SPACE_WIDTH = 27;
    private static final int SPACE_COUNT = 2;
    private static final int SPAN_COUNT = 3;
    private final LinearLayout mAllBookLayout;
    private RecyclerView mRecyclerView;
    private TextView mAllBookTextView;
    private BookStoreAdapter mStoreAdapter;
    private Context mContext;
    private List<BookDetailResponse> bookListData;
    private final View markView;
    private final View lineView;
    private final TextView tagTextView;
    private ImageView mAllBookImageView;

    public ShelfViewHolder(View itemView, Context context) {
        super(itemView);
        mRecyclerView = (RecyclerView) itemView.findViewById(R.id.book_reader_inner_recycler_view);
        mAllBookTextView = (TextView) itemView.findViewById(R.id.book_reader_see_all_book_text_view);
        markView = itemView.findViewById(R.id.book_reader_mark_view);
        lineView = itemView.findViewById(R.id.book_reader_line_view);
        tagTextView = (TextView) itemView.findViewById(R.id.book_reader_tag_text_view);
        mAllBookImageView = (ImageView) itemView.findViewById(R.id.book_reader_all_book_image_view);
        mAllBookLayout = (LinearLayout) itemView.findViewById(R.id.book_reader_see_all_book_layout);
        mContext = context;
        initRecyclerView(mContext);
    }

    @Override
    public void bindViewData(Visitable data) {
        if (!(data instanceof BookList)) {
            return;
        }
        BookList bookList = (BookList) data;
        List<BookDetailResponse> detailList = bookList.getDetailResponseList();
        if (CollectionUtil.isEmpty(detailList)) {
            return;
        }
        int bookCount = detailList.size();
        if (detailList.size() > SHELF_SHOW_COUNT) {
            detailList = detailList.subList(0, SHELF_SHOW_COUNT);
        }
        if (bookListData == null) {
            bookListData = new ArrayList<>();
        }
        bookListData.clear();
        bookListData.addAll(detailList);
        if (mStoreAdapter == null) {
            mStoreAdapter = new BookStoreAdapter(mContext, bookListData, true, SPACE_COUNT, SPACE_WIDTH);
            mRecyclerView.setAdapter(mStoreAdapter);
        } else {
            mStoreAdapter.notifyDataSetChanged();
        }
        String showText = bookCount == 0 ? mContext.getResources()
                .getString(R.string.book_reader_see_all_text) :
                String.format(mContext.getResources().getString(R.string.book_reader_see_all_book_text), bookCount);
        mAllBookTextView.setText(showText);
        mAllBookLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtil.toCommonActivity(mContext, HomeActivity.class);
            }
        });

        StoreColorManager manager = NightModeManager.getManager();
        if (manager == null) {
            return;
        }
        mAllBookTextView.setTextColor(manager.getAllBookColor());
        markView.setBackgroundColor(manager.getVerticalMarkColor());
        tagTextView.setTextColor(manager.getBookTitleColor());
        lineView.setBackgroundColor(manager.getDivideLineColor());
        mAllBookImageView.setImageResource(manager.getAllBookImageResource());
    }

    private void initRecyclerView(Context context) {
        GridLayoutManager manager = new GridLayoutManager(context, SPAN_COUNT);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.addItemDecoration(new StoreShelfItemDecoration(ScreenUtil.dpToPxInt(SPACE_WIDTH)));
    }
}
