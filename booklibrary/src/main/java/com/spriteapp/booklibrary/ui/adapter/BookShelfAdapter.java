package com.spriteapp.booklibrary.ui.adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.base.BaseActivity;
import com.spriteapp.booklibrary.database.BookDb;
import com.spriteapp.booklibrary.database.RecentBookDb;
import com.spriteapp.booklibrary.enumeration.UpdaterShelfEnum;
import com.spriteapp.booklibrary.listener.DeleteBookListener;
import com.spriteapp.booklibrary.listener.ListenerManager;
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

public class BookShelfAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "BookShelfAdapter";
    private static final int ITEM_ONE = 1;
    private static final int ITEM_OTHER = 2;
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
    private LinearLayout del_layout;
    private TextView is_del;
    private int num = 0;
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
                            int spanCount, int spaceWidth, boolean isRecentReadBook, LinearLayout del_layout, TextView is_del) {
        this.mContext = mContext;
        this.mDetailList = mDetailList;
        this.del_layout = del_layout;
        this.is_del = is_del;
        this.mLayoutInflater = LayoutInflater.from(mContext);
        this.isRecentReadBook = isRecentReadBook;
        mBookDb = new BookDb(mContext);
        mRecentBookDb = new RecentBookDb(mContext);
        mImageWidth = RecyclerViewUtil.getImageWidth(spanCount - 1, spaceWidth);
        mImageHeight = mImageWidth * ScreenUtil.dpToPxInt(IMAGE_HEIGHT)
                / ScreenUtil.dpToPxInt(IMAGE_WIDTH);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = null;
        if (viewType == ITEM_ONE) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.book_shelf_one_item, parent, false);
            return new OneViewHolder(convertView);
        } else {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.book_reader_item_book_shelf, parent, false);
            return new ShelfViewHolder(convertView);
        }
    }

    public boolean isDeleteBook() {
        return isDeleteBook;
    }

    public void setDeleteBook(boolean deleteBook) {
        isDeleteBook = deleteBook;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final BookDetailResponse detail = mDetailList.get(position);
        if (detail == null) {
            return;

        }
        if (holder instanceof OneViewHolder) {
            final OneViewHolder oneViewHolder = (OneViewHolder) holder;

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) oneViewHolder.mShadowLayout.getLayoutParams();
            params.height = mImageHeight - ScreenUtil.dpToPxInt(12);
            params.width = (BaseActivity.deviceWidth - ScreenUtil.dpToPxInt(40)) / 3;
            oneViewHolder.mShadowLayout.setLayoutParams(params);
            Glide.with(mContext)
                    .load(detail.getBook_image())
                    .into(oneViewHolder.logoImageView);
            if (!StringUtil.isEmpty(detail.getBook_name())) {
                oneViewHolder.titleTextView.setText(detail.getBook_name());
            }
            if (!StringUtil.isEmpty(detail.getLast_update_chapter_title())) {
                oneViewHolder.chapter_title.setText(detail.getLast_update_chapter_title());
            }
            if (detail.getBook_chapter_total() != 0 && !isRecentReadBook) {
                oneViewHolder.progressTextView.setVisibility(View.VISIBLE);
                oneViewHolder.progressTextView.setText
                        (String.format(mContext.getResources().getString(R.string.book_reader_read_progress_text),
                                detail.getLast_chapter_index() * 100 / detail.getBook_chapter_total()));
            } else {
                oneViewHolder.progressTextView.setVisibility(View.GONE);
            }
            oneViewHolder.logoImageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Log.d("IsLong", "执行上面");
                    if (isRecommendData || isRecentReadBook) {
                        return false;
                    }
                    Log.d("IsLong", "执行下面");
                    if (isDeleteBook) {
                        return true;
                    }
                    isDeleteBook = true;
                    notifyDataSetChanged();
                    if (del_layout.getVisibility() == View.GONE)
                        del_layout.setVisibility(View.VISIBLE);
                    return true;
                }
            });
            oneViewHolder.to_read.setOnClickListener(new View.OnClickListener() {
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
            oneViewHolder.logoImageView.setOnClickListener(new View.OnClickListener() {
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
            if (num == 0) oneViewHolder.deleteImageView.setSelected(false);
            if (isDeleteBook) {
                if (detail.isSelector()) {
                    oneViewHolder.deleteImageView.setSelected(true);
                } else {
                    oneViewHolder.deleteImageView.setSelected(false);
                }
            }else {
                oneViewHolder.deleteImageView.setSelected(false);
            }
            if (num == 0 && isDeleteBook) {//取消全选
                if (ListenerManager.getInstance().getDelBookShelf() != null) {
                    ListenerManager.getInstance().getDelBookShelf().del_book(1, 0, 0, 0);
                }
                for (int i = 0; i < mDetailList.size(); i++) {
                    mDetailList.get(i).setSelector(false);
                }
            }
            if (num == mDetailList.size()) {//全选
                oneViewHolder.deleteImageView.setSelected(true);
                for (int i = 0; i < mDetailList.size(); i++) {
                    mDetailList.get(i).setSelector(true);
                    if (ListenerManager.getInstance().getDelBookShelf() != null) {
                        ListenerManager.getInstance().getDelBookShelf().del_book(mDetailList.get(i).getBook_id(), i, i + 1, 1);
                    }
                }
            }
            if (!isDeleteBook && del_layout.getVisibility() == View.VISIBLE)
                goneDel();
            oneViewHolder.deleteImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mDeleteListener != null) {
                        if (!oneViewHolder.deleteImageView.isSelected()) {
                            mDetailList.get(position).setSelector(true);
                            oneViewHolder.deleteImageView.setSelected(true);

                            num++;
                            closeSelector();
                            if (ListenerManager.getInstance().getDelBookShelf() != null)
                                ListenerManager.getInstance().getDelBookShelf().del_book(detail.getBook_id(), position, num, 1);
                        } else {
                            mDetailList.get(position).setSelector(false);
                            oneViewHolder.deleteImageView.setSelected(false);

                            if (num > 0)
                                num--;
                            closeSelector();
                            if (ListenerManager.getInstance().getDelBookShelf() != null)
                                ListenerManager.getInstance().getDelBookShelf().del_book(detail.getBook_id(), position, num, 2);
                        }

//                        mDeleteListener.showDeleteDialog(detail.getBook_id(), holder.getLayoutPosition());
                    }
                }
            });
            judgeShowDeleteView(oneViewHolder.deleteImageView);


        } else if (holder instanceof ShelfViewHolder) {
            final ShelfViewHolder shelfViewHolder = (BookShelfAdapter.ShelfViewHolder) holder;
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) shelfViewHolder.mShadowLayout.getLayoutParams();
            params.height = mImageHeight - ScreenUtil.dpToPxInt(12);
            shelfViewHolder.mShadowLayout.setLayoutParams(params);
            Glide.with(mContext)
                    .load(detail.getBook_image())
                    .into(shelfViewHolder.logoImageView);
            if (!StringUtil.isEmpty(detail.getBook_name())) {
                shelfViewHolder.titleTextView.setText(detail.getBook_name());
            }
            if (detail.getBook_chapter_total() != 0 && !isRecentReadBook) {
                shelfViewHolder.progressTextView.setVisibility(View.VISIBLE);
                shelfViewHolder.progressTextView.setText
                        (String.format(mContext.getResources().getString(R.string.book_reader_read_progress_text),
                                detail.getLast_chapter_index() * 100 / detail.getBook_chapter_total()));
            } else {
                shelfViewHolder.progressTextView.setVisibility(View.GONE);
            }
            shelfViewHolder.logoImageView.setOnLongClickListener(new View.OnLongClickListener() {
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
                    if (del_layout.getVisibility() == View.GONE)
                        del_layout.setVisibility(View.VISIBLE);
                    return true;
                }
            });
            shelfViewHolder.logoImageView.setOnClickListener(new View.OnClickListener() {
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
            if (num == 0) shelfViewHolder.deleteImageView.setSelected(false);
            if (isDeleteBook) {
                if (detail.isSelector()) {
                    shelfViewHolder.deleteImageView.setSelected(true);
                } else {
                    shelfViewHolder.deleteImageView.setSelected(false);
                }
            }else {
                shelfViewHolder.deleteImageView.setSelected(false);
            }
            if (num == mDetailList.size()) shelfViewHolder.deleteImageView.setSelected(true);
            if (!isDeleteBook && del_layout.getVisibility() == View.VISIBLE)
                goneDel();
            shelfViewHolder.deleteImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mDeleteListener != null) {
                        if (!shelfViewHolder.deleteImageView.isSelected()) {
                            mDetailList.get(position).setSelector(true);
                            shelfViewHolder.deleteImageView.setSelected(true);
                            num++;
                            closeSelector();
                            if (ListenerManager.getInstance().getDelBookShelf() != null)
                                ListenerManager.getInstance().getDelBookShelf().del_book(detail.getBook_id(), position, num, 1);
                        } else {
                            mDetailList.get(position).setSelector(false);
                            shelfViewHolder.deleteImageView.setSelected(false);
                            if (num > 0)
                                num--;
                            closeSelector();
                            if (ListenerManager.getInstance().getDelBookShelf() != null)
                                ListenerManager.getInstance().getDelBookShelf().del_book(detail.getBook_id(), position, num, 2);
                        }
//                        mDeleteListener.showDeleteDialog(detail.getBook_id(), holder.getLayoutPosition());
                    }
                }
            });
            judgeShowDeleteView(shelfViewHolder.deleteImageView);
        }

    }

    public void goneDel() {
        del_layout.setVisibility(View.GONE);
        num = 0;
        if (ListenerManager.getInstance().getDelBookShelf() != null) {
            ListenerManager.getInstance().getDelBookShelf().del_book(0, 0, 0, 0);
            is_del.setEnabled(false);
        }
    }

    public void closeSelector() {
        if (num < mDetailList.size()) {//取消全选
            if (ListenerManager.getInstance().getDelBookShelf() != null)
                ListenerManager.getInstance().getDelBookShelf().del_book(1, 0, 0, 4);
        } else if (num == mDetailList.size()) {//全选
            if (ListenerManager.getInstance().getDelBookShelf() != null)
                ListenerManager.getInstance().getDelBookShelf().del_book(1, 0, 0, 3);
        }
    }

    public void setNum(int type) {
        if (type == 1) {
            num = 0;
            isDeleteBook = false;
        } else if (type == 2 && mDetailList.size() != 0) {
            num = mDetailList.size();
            isDeleteBook = true;
        } else if (type == 3 && mDetailList.size() != 0) {
            num = 0;
            isDeleteBook = true;
            for (int i = 0; i < mDetailList.size(); i++) {
                mDetailList.get(i).setSelector(false);
            }
        }
        notifyDataSetChanged();
    }


    private void judgeShowDeleteView(ImageView img) {
        if (!isDeleteBook) {
            img.setVisibility(View.GONE);
            return;
        }
        AnimatorSet set = new AnimatorSet();
        img.setVisibility(View.VISIBLE);
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(img, View.SCALE_X, 0, 1.0f);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(img, View.SCALE_Y, 0, 1.0f);
        animatorX.setDuration(ANIMATION_TIME);
        animatorY.setDuration(ANIMATION_TIME);
        set.play(animatorX).with(animatorY);
        set.start();
    }

    @Override
    public int getItemCount() {
        return CollectionUtil.isEmpty(mDetailList) ? 0 : mDetailList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return ITEM_ONE;
        return ITEM_OTHER;
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

    static class OneViewHolder extends RecyclerView.ViewHolder {

        TextView progressTextView, to_read;
        TextView titleTextView, chapter_title;
        ImageView logoImageView;
        RelativeLayout mShadowLayout;
        ImageView deleteImageView;

        OneViewHolder(View itemView) {
            super(itemView);
            progressTextView = (TextView) itemView.findViewById(R.id.progress);
            to_read = (TextView) itemView.findViewById(R.id.to_read);
            titleTextView = (TextView) itemView.findViewById(R.id.book_name);
            chapter_title = (TextView) itemView.findViewById(R.id.chapter_name);
            logoImageView = (ImageView) itemView.findViewById(R.id.book_cover);
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
