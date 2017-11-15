package com.spriteapp.booklibrary.ui.adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.database.BookDb;
import com.spriteapp.booklibrary.database.RecentBookDb;
import com.spriteapp.booklibrary.enumeration.UpdaterShelfEnum;
import com.spriteapp.booklibrary.listener.DeleteBookListener;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.ui.activity.ReadActivity;
import com.spriteapp.booklibrary.util.CollectionUtil;
import com.spriteapp.booklibrary.util.RecyclerViewUtil;
import com.spriteapp.booklibrary.util.ScreenUtil;
import com.spriteapp.booklibrary.util.StringUtil;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by kuangxiaoguo on 2017/7/13.
 */

public class BookShelfAdapter extends RecyclerView.Adapter<BookShelfAdapter.ShelfViewHolder> {

    private static final String TAG = "BookShelfAdapter";
    private static final int IMAGE_WIDTH = 100;
    private static final int IMAGE_HEIGHT = 144;
    private static final int ANIMATION_TIME = 300;
    private final int mImageWidth;
    private final int mImageHeight;
    private Context mContext;
    private List<BookDetailResponse> mDetailList;
    private LayoutInflater mLayoutInflater;
    private BookDb mBookDb;
    private RecentBookDb mRecentBookDb;
    private static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            EventBus.getDefault().post(UpdaterShelfEnum.UPDATE_SHELF);
        }
    };

    private DeleteBookListener mDeleteListener;
    private boolean isRecommendData;
    private boolean isDeleteBook;
    private boolean isRecentReadBook;

    public BookShelfAdapter(Context mContext, List<BookDetailResponse> mDetailList,
                            int spanCount, int spaceWidth, boolean isRecentReadBook) {
        this.mContext = mContext;
        this.mDetailList = mDetailList;
        this.mLayoutInflater = LayoutInflater.from(mContext);
        this.isRecentReadBook = isRecentReadBook;
        mBookDb = new BookDb(mContext);
        mRecentBookDb = new RecentBookDb(mContext);
        mImageWidth = RecyclerViewUtil.getImageWidth(spanCount - 1, spaceWidth);
        mImageHeight = mImageWidth * ScreenUtil.dpToPxInt(IMAGE_HEIGHT)
                / ScreenUtil.dpToPxInt(IMAGE_WIDTH);
    }

    @Override
    public ShelfViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.book_reader_item_book_shelf, parent, false);
        return new ShelfViewHolder(itemView);
    }

    public boolean isDeleteBook() {
        return isDeleteBook;
    }

    public void setDeleteBook(boolean deleteBook) {
        isDeleteBook = deleteBook;
    }

    @Override
    public void onBindViewHolder(final ShelfViewHolder holder, int position) {
        final BookDetailResponse detail = mDetailList.get(position);
        if (detail == null) {
            return;
        }

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.mShadowLayout.getLayoutParams();
        params.height = mImageHeight - ScreenUtil.dpToPxInt(12);
        holder.mShadowLayout.setLayoutParams(params);
        Glide.with(mContext)
                .load(detail.getBook_image())
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .centerCrop()
                .into(holder.logoImageView);
        if (!StringUtil.isEmpty(detail.getBook_name())) {
            holder.titleTextView.setText(detail.getBook_name());
        }
        if (detail.getBook_chapter_total() != 0 && !isRecentReadBook) {
            holder.progressTextView.setVisibility(View.VISIBLE);
            holder.progressTextView.setText
                    (String.format(mContext.getResources().getString(R.string.book_reader_read_progress_text),
                            detail.getLast_chapter_index() * 100 / detail.getBook_chapter_total()));
        } else {
            holder.progressTextView.setVisibility(View.GONE);
        }
        holder.logoImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (isRecommendData || isRecentReadBook) {
                    return false;
                }
                if (isDeleteBook) {
                    return true;
                }
                isDeleteBook = true;
                notifyDataSetChanged();
                return true;
            }
        });
        holder.logoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int layoutPosition = holder.getLayoutPosition();
                if (CollectionUtil.isEmpty(mDetailList) || layoutPosition >= mDetailList.size()) {
                    return;
                }
                BookDetailResponse bookDetail = mDetailList.get(layoutPosition);
                mBookDb.updateReadTime(bookDetail.getBook_id());
                mRecentBookDb.updateReadTime(bookDetail.getBook_id());
                Intent intent = new Intent(mContext, ReadActivity.class);
                intent.putExtra(ReadActivity.BOOK_DETAIL_TAG, bookDetail);
                mContext.startActivity(intent);
                mHandler.sendEmptyMessageDelayed(0, 1000);
            }
        });
        holder.deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDeleteListener != null) {
                    mDeleteListener.showDeleteDialog(detail.getBook_id(), holder.getLayoutPosition());
                }
            }
        });
        judgeShowDeleteView(holder);
    }

    private void judgeShowDeleteView(ShelfViewHolder holder) {
        if (!isDeleteBook) {
            holder.deleteImageView.setVisibility(View.GONE);
            return;
        }
        AnimatorSet set = new AnimatorSet();
        holder.deleteImageView.setVisibility(View.VISIBLE);
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(holder.deleteImageView, View.SCALE_X, 0, 1.0f);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(holder.deleteImageView, View.SCALE_Y, 0, 1.0f);
        animatorX.setDuration(ANIMATION_TIME);
        animatorY.setDuration(ANIMATION_TIME);
        set.play(animatorX).with(animatorY);
        set.start();
    }

    @Override
    public int getItemCount() {
        return CollectionUtil.isEmpty(mDetailList) ? 0 : mDetailList.size();
    }

    static class ShelfViewHolder extends RecyclerView.ViewHolder {

        TextView progressTextView;
        TextView titleTextView;
        ImageView logoImageView;
        RelativeLayout mShadowLayout;
        ImageView deleteImageView;

        ShelfViewHolder(View itemView) {
            super(itemView);
            progressTextView = (TextView) itemView.findViewById(R.id.book_reader_progress_text_view);
            titleTextView = (TextView) itemView.findViewById(R.id.book_reader_title_text_view);
            logoImageView = (ImageView) itemView.findViewById(R.id.book_reader_logo_image_view);
            deleteImageView = (ImageView) itemView.findViewById(R.id.book_reader_delete_image_view);
            mShadowLayout = (RelativeLayout) itemView.findViewById(R.id.book_reader_image_layout);
        }
    }

    public void setDeleteListener(DeleteBookListener mDeleteListener) {
        this.mDeleteListener = mDeleteListener;
    }

    public void setIsRecommendData(boolean recommendData) {
        isRecommendData = recommendData;
    }
}
