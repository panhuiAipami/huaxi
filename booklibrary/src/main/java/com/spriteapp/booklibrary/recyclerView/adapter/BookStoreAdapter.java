package com.spriteapp.booklibrary.recyclerView.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.database.BookDb;
import com.spriteapp.booklibrary.database.RecentBookDb;
import com.spriteapp.booklibrary.manager.NightModeManager;
import com.spriteapp.booklibrary.manager.StoreColorManager;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.util.ActivityUtil;
import com.spriteapp.booklibrary.util.CollectionUtil;
import com.spriteapp.booklibrary.util.RecyclerViewUtil;
import com.spriteapp.booklibrary.util.ScreenUtil;
import com.spriteapp.booklibrary.util.StringUtil;

import java.util.List;

/**
 * 书城嵌套recyclerView适配器
 * Created by kuangxiaoguo on 2017/7/28.
 */

public class BookStoreAdapter extends RecyclerView.Adapter<BookStoreAdapter.BookStoreViewHolder> {

    private static final String TAG = "BookStoreAdapter";
    private static final int BOOK_IMAGE_WIDTH = 69;
    private static final int BOOK_IMAGE_HEIGHT = 100;
    private static final int SHELF_BOOK_WIDTH = 91;
    private static final int SHELF_BOOK_HEIGHT = 136;
    private Context mContext;
    private List<BookDetailResponse> mDetailList;
    private LayoutInflater mInflater;
    private BookDb mBookDb;
    private boolean isShelfBook;
    private int mImageWidth;
    private int mImageHeight;
    private RecentBookDb mRecentBookDb;

    public BookStoreAdapter(Context context, List<BookDetailResponse> mDetailList, boolean isShelfBook,
                            int spaceCount, int spaceWidth) {
        this.mContext = context;
        this.mDetailList = mDetailList;
        this.isShelfBook = isShelfBook;
        mInflater = LayoutInflater.from(mContext);
        mBookDb = new BookDb(mContext);
        mRecentBookDb = new RecentBookDb(mContext);
        mImageWidth = RecyclerViewUtil.getImageWidth(spaceCount, spaceWidth);
        if (isShelfBook) {
            mImageHeight = mImageWidth * ScreenUtil.dpToPxInt(SHELF_BOOK_HEIGHT)
                    / ScreenUtil.dpToPxInt(SHELF_BOOK_WIDTH);
        } else {
            mImageHeight = mImageWidth * ScreenUtil.dpToPxInt(BOOK_IMAGE_HEIGHT)
                    / ScreenUtil.dpToPxInt(BOOK_IMAGE_WIDTH);
        }
    }

    @Override
    public BookStoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.book_reader_store_inner_item, parent, false);
        return new BookStoreViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final BookStoreViewHolder holder, int position) {
        StoreColorManager manager = NightModeManager.getManager();
        setLayoutAttributes(holder, position);
        final BookDetailResponse detail = mDetailList.get(position);
        Glide.with(mContext)
                .load(detail.getBook_image())
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .centerCrop()
                .into(holder.bookLogoImageView);
        String bookName = detail.getBook_name();
        if (!StringUtil.isEmpty(bookName)) {
            holder.bookNameTextView.setText(bookName);
        }

        String authorName = detail.getAuthor_name();
        if (!StringUtil.isEmpty(authorName)) {
            holder.bookAuthorTextView.setText(authorName);
        }
        if (detail.getBook_chapter_total() != 0) {
            holder.progressTextView.setText
                    (String.format(mContext.getResources().getString(R.string.book_reader_read_progress_text),
                            detail.getLast_chapter_index() * 100 / detail.getBook_chapter_total()));
        } else {
            holder.progressTextView.setVisibility(View.GONE);
        }
        holder.bookLogoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int layoutPosition = holder.getLayoutPosition();
                BookDetailResponse detailResponse = mDetailList.get(layoutPosition);
                if (detailResponse == null) {
                    return;
                }
                mBookDb.updateReadTime(detailResponse.getBook_id());
                mRecentBookDb.updateReadTime(detailResponse.getBook_id());
                ActivityUtil.toReadActivity(mContext, detailResponse);
            }
        });

        if (manager == null) {
            return;
        }
        holder.bookNameTextView.setTextColor(manager.getBookTitleColor());
        holder.bookAuthorTextView.setTextColor(manager.getBookAuthorColor());
        holder.bookLayout.setBackgroundResource(manager.getBookLineBackground());
    }

    private void setLayoutAttributes(BookStoreViewHolder holder, int position) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.bookLayout.getLayoutParams();
        if (!isShelfBook) {
            params.width = mImageWidth;
            params.height = mImageHeight;
            holder.bookLayout.setLayoutParams(params);
            return;
        }
        ViewGroup.LayoutParams parentParams = holder.parentLayout.getLayoutParams();
        parentParams.height = mImageHeight + ScreenUtil.dpToPxInt(36);
        holder.parentLayout.setLayoutParams(parentParams);
        holder.parentLayout.setGravity(Gravity.BOTTOM);

        params.width = position == 0 ? mImageWidth + ScreenUtil.dpToPxInt(9) : mImageWidth;
        params.height = position == 0 ? (mImageHeight + ScreenUtil.dpToPxInt(8)) : mImageHeight;
        holder.bookLayout.setLayoutParams(params);

        holder.bookAuthorTextView.setVisibility(View.GONE);
        holder.bookNameTextView.setMaxLines(1);
        holder.bookNameTextView.setEllipsize(TextUtils.TruncateAt.END);
        holder.progressTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return CollectionUtil.isEmpty(mDetailList) ? 0 : mDetailList.size();
    }

    class BookStoreViewHolder extends RecyclerView.ViewHolder {

        ImageView bookLogoImageView;
        TextView bookNameTextView;
        TextView bookAuthorTextView;
        RelativeLayout bookLayout;
        LinearLayout parentLayout;
        TextView progressTextView;

        public BookStoreViewHolder(View itemView) {
            super(itemView);
            bookLogoImageView = (ImageView) itemView.findViewById(R.id.book_reader_book_image_view);
            bookNameTextView = (TextView) itemView.findViewById(R.id.book_reader_book_name_text_view);
            bookAuthorTextView = (TextView) itemView.findViewById(R.id.book_reader_author_text_view);
            bookLayout = (RelativeLayout) itemView.findViewById(R.id.book_reader_book_layout);
            parentLayout = (LinearLayout) itemView.findViewById(R.id.book_reader_parent_layout);
            progressTextView = (TextView) itemView.findViewById(R.id.book_reader_progress_text_view);
        }
    }
}
