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
import com.spriteapp.booklibrary.model.store.BookTypeResponse;
import com.spriteapp.booklibrary.recyclerView.adapter.BookStoreAdapter;
import com.spriteapp.booklibrary.recyclerView.decorate.Visitable;
import com.spriteapp.booklibrary.util.CollectionUtil;
import com.spriteapp.booklibrary.util.ScreenUtil;
import com.spriteapp.booklibrary.util.StringUtil;
import com.spriteapp.booklibrary.util.WebViewUtil;
import com.spriteapp.booklibrary.widget.recyclerview.StoreItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * 书籍展示
 * Created by kuangxiaoguo on 2017/7/28.
 */

public class BookViewHolder extends BaseViewHolder<Visitable> {

    private static final String TAG = "BookViewHolder";
    private static final int SPACE_WIDTH = 15;
    private static final int SPACE_COUNT = 3;
    private static final int SPAN_COUNT = 4;
    private final LinearLayout mAllBookLayout;
    private ImageView mAllBookImageView;
    private RecyclerView mRecyclerView;
    private TextView mBookTagTextView;
    private TextView mAllBookTextView;
    private BookStoreAdapter mStoreAdapter;
    private List<BookDetailResponse> mDataList;
    private Context mContext;
    private final View markView;
    private final View lineView;

    public BookViewHolder(View itemView, Context context) {
        super(itemView);
        mRecyclerView = (RecyclerView) itemView.findViewById(R.id.book_reader_inner_recycler_view);
        mBookTagTextView = (TextView) itemView.findViewById(R.id.book_reader_tag_text_view);
        mAllBookTextView = (TextView) itemView.findViewById(R.id.book_reader_see_all_book_text_view);
        markView = itemView.findViewById(R.id.book_reader_mark_view);
        lineView = itemView.findViewById(R.id.book_reader_line_view);
        mAllBookImageView = (ImageView) itemView.findViewById(R.id.book_reader_all_book_image_view);
        mAllBookLayout = (LinearLayout) itemView.findViewById(R.id.book_reader_see_all_book_layout);
        mDataList = new ArrayList<>();
        initRecyclerView(context);
        mContext = context;
    }

    private void initRecyclerView(Context context) {
        GridLayoutManager manager = new GridLayoutManager(context, SPAN_COUNT);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.addItemDecoration(new StoreItemDecoration(ScreenUtil.dpToPxInt(SPACE_WIDTH),
                ScreenUtil.dpToPxInt(5)));
    }

    @Override
    public void bindViewData(Visitable data) {
        if (!(data instanceof BookList)) {
            return;
        }
        BookList bookList = (BookList) data;
        final BookTypeResponse typeResponse = bookList.getTypeResponse();
        if (typeResponse == null) {
            return;
        }
        List<BookDetailResponse> detailList = typeResponse.getLists();
        if (CollectionUtil.isEmpty(detailList)) {
            return;
        }
        mDataList.clear();
        mDataList.addAll(detailList);
        if (CollectionUtil.isEmpty(mDataList)) {
            return;
        }
        if (mStoreAdapter == null) {
            mStoreAdapter = new BookStoreAdapter(mContext, mDataList, false, SPACE_COUNT, SPACE_WIDTH);
            mRecyclerView.setAdapter(mStoreAdapter);
        } else {
            mStoreAdapter.notifyDataSetChanged();
        }
        mBookTagTextView.setText(typeResponse.getName());
        int bookCount = typeResponse.getCount();
        String showText = bookCount == 0 ? mContext.getResources()
                .getString(R.string.book_reader_see_all_text) :
                String.format(mContext.getResources().getString(R.string.book_reader_see_all_book_text), bookCount);
        mAllBookTextView.setText(showText);
        mAllBookLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = typeResponse.getUrl();
                if (StringUtil.isEmpty(url)) {
                    return;
                }
                WebViewUtil.getInstance().getJumpUrl(mContext, url);
            }
        });
        StoreColorManager manager = NightModeManager.getManager();
        if (manager == null) {
            return;
        }
        mAllBookTextView.setTextColor(manager.getAllBookColor());
        markView.setBackgroundColor(manager.getVerticalMarkColor());
        lineView.setBackgroundColor(manager.getDivideLineColor());
        mBookTagTextView.setTextColor(manager.getBookTitleColor());
        mAllBookImageView.setImageResource(manager.getAllBookImageResource());
    }

}
