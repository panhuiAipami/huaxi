package com.spriteapp.booklibrary.ui.adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.spriteapp.booklibrary.model.UserBean;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.ui.activity.HomeActivity;
import com.spriteapp.booklibrary.ui.activity.ReadActivity;
import com.spriteapp.booklibrary.util.ActivityUtil;
import com.spriteapp.booklibrary.util.AppUtil;
import com.spriteapp.booklibrary.util.CollectionUtil;
import com.spriteapp.booklibrary.util.PreferenceHelper;
import com.spriteapp.booklibrary.util.RecyclerViewUtil;
import com.spriteapp.booklibrary.util.ScreenUtil;
import com.spriteapp.booklibrary.util.SharedPreferencesUtil;
import com.spriteapp.booklibrary.util.StringUtil;

import java.util.List;

import de.greenrobot.event.EventBus;

import static com.spriteapp.booklibrary.ui.activity.HomeActivity.BOOKSHELF_TO_BOOKSTORE;
import static com.spriteapp.booklibrary.ui.fragment.BookshelfFragment.IS_BAG;


/**
 * Created by kuangxiaoguo on 2017/7/13.
 */

public class BookShelfAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "BookShelfAdapter";
    private static final int ITEM_ONE = 1;
    private static final int ITEM_OTHER = 2;
    private static final int ITEM_SHU = 3;
    private static final int ITEM_LAST = 4;
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
    public static boolean ISSHU = false;

    private static boolean IS_HAVE_ONE = false;

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
        } else if (viewType == ITEM_SHU) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.bookshelfshu, parent, false);
            return new ShelfViewHolder(convertView);
        } else if (viewType == ITEM_LAST) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.goto_store, parent, false);
            return new GoToStore(convertView);
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
        if (position > mDetailList.size()) return;
        final BookDetailResponse detail = mDetailList.get(position == mDetailList.size() ? mDetailList.size() - 1 : position);
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
            String lastRead = PreferenceHelper.getString(ReadActivity.LAST_CHAPTER + UserBean.getInstance().getUser_id() + detail.getBook_id(), "");
            if (!TextUtils.isEmpty(lastRead))
                oneViewHolder.chapter_title.setText("上次阅读到:" + lastRead);
            else
                oneViewHolder.chapter_title.setText("未阅读");
//            if (!StringUtil.isEmpty(detail.getLast_update_chapter_title())) {
//                oneViewHolder.chapter_title.setText("更新至:" + detail.getLast_update_chapter_title());
//            }
            if (detail.getBook_chapter_total() != 0 && !isRecentReadBook) {
                oneViewHolder.progressTextView.setVisibility(View.VISIBLE);
                oneViewHolder.progressTextView.setText
                        (String.format(mContext.getResources().getString(R.string.book_reader_read_progress_text),
                                detail.getLast_chapter_index() * 100 / detail.getBook_chapter_total()));
            } else {
                oneViewHolder.progressTextView.setVisibility(View.GONE);
            }
            oneViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
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
            oneViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isDeleteBook) {
                        setOneState(oneViewHolder, position, detail);
                        return;//删除时不进入阅读页
                    }
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
            if (num == 0) {
                oneViewHolder.deleteImageView.setSelected(false);
                detail.setSelector(false);
            }
            if (isDeleteBook) {
                if (detail.isSelector()) {
                    oneViewHolder.deleteImageView.setSelected(true);
                } else {
                    oneViewHolder.deleteImageView.setSelected(false);
                }
            } else {
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
                    setOneState(oneViewHolder, position, detail);
                }
            });
            judgeShowDeleteView(oneViewHolder.deleteImageView);


        } else if (holder instanceof ShelfViewHolder) {
            final int newposition;
            final ShelfViewHolder shelfViewHolder = (BookShelfAdapter.ShelfViewHolder) holder;
            if (!ISSHU) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) shelfViewHolder.mShadowLayout.getLayoutParams();
                params.height = mImageHeight - ScreenUtil.dpToPxInt(12);
                shelfViewHolder.mShadowLayout.setLayoutParams(params);
            } else {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) shelfViewHolder.mShadowLayout.getLayoutParams();
                params.height = mImageHeight - ScreenUtil.dpToPxInt(12);
                params.width = (BaseActivity.deviceWidth - ScreenUtil.dpToPxInt(40)) / 3;
                shelfViewHolder.mShadowLayout.setLayoutParams(params);
            }
            BookDetailResponse detailResponse = detail;
            final BookDetailResponse realBookDetailResponse;
            Log.d("realBookDetailResponse", "IS_HAVE_ONE===" + IS_HAVE_ONE);
//            if (AppUtil.isLogin() && UserBean.getInstance().getUser_package() == 1) {
            if (AppUtil.isLogin() && IS_BAG == 1) {
                if (position > 0) {
                    realBookDetailResponse = mDetailList.get(position - 1);
                    newposition = position - 1;
                } else {
                    realBookDetailResponse = detailResponse;
                    newposition = position;

                }

                if (IS_HAVE_ONE && position == 1 || !IS_HAVE_ONE && position == 0) {
                    IS_HAVE_ONE = false;
                    Glide.with(mContext)
                            .load(R.mipmap.huaxi_icon)
                            .into(shelfViewHolder.logoImageView);
                    shelfViewHolder.titleTextView.setText("书包");
                    shelfViewHolder.progressTextView.setVisibility(View.INVISIBLE);
                    shelfViewHolder.deleteImageView.setVisibility(View.INVISIBLE);
                    shelfViewHolder.purchaseImageView.setVisibility(View.VISIBLE);
                    shelfViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            ToastUtil.showToast("书包");
                            ActivityUtil.toBagActivity(mContext);
                        }
                    });
                    return;

                } else {
                    shelfViewHolder.purchaseImageView.setVisibility(View.GONE);
                }

            } else{
                newposition = position;
                realBookDetailResponse = detailResponse;
                shelfViewHolder.purchaseImageView.setVisibility(View.GONE);
            }
            if (realBookDetailResponse == null) return;


            if (ISSHU) {
                if (realBookDetailResponse.getBook_finish_flag())
                    shelfViewHolder.book_reader_chapter_text_view.setText("已完本");
                else
                    shelfViewHolder.book_reader_chapter_text_view.setText("更新至" + realBookDetailResponse.getBook_chapter_total() + "章");
                if (!StringUtil.isEmpty(realBookDetailResponse.getAuthor_name())) {
                    shelfViewHolder.book_reader_author_text_view.setText(realBookDetailResponse.getAuthor_name());
                }
            }
            Glide.with(mContext)
                    .load(realBookDetailResponse.getBook_image())
                    .into(shelfViewHolder.logoImageView);
            if (!StringUtil.isEmpty(realBookDetailResponse.getBook_name())) {
                shelfViewHolder.titleTextView.setText(realBookDetailResponse.getBook_name());
            }
            if (realBookDetailResponse.getBook_chapter_total() != 0 && !isRecentReadBook) {
                shelfViewHolder.progressTextView.setVisibility(View.VISIBLE);
                shelfViewHolder.progressTextView.setText
                        (String.format(mContext.getResources().getString(R.string.book_reader_read_progress_text),
                                realBookDetailResponse.getLast_chapter_index() * 100 / realBookDetailResponse.getBook_chapter_total()));
            } else {
                shelfViewHolder.progressTextView.setVisibility(View.GONE);
            }
            shelfViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
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
            if (newposition == 0) {//没有头部时调用
                Log.d("getDelBookShelf", "没有头部时调用");
                if (num == 0 && isDeleteBook) {//取消全选
                    Log.d("getDelBookShelf", "取消全选");
                    if (ListenerManager.getInstance().getDelBookShelf() != null) {
                        ListenerManager.getInstance().getDelBookShelf().del_book(1, 0, 0, 0);
                    }
                    for (int i = 0; i < mDetailList.size(); i++) {
                        mDetailList.get(i).setSelector(false);
                        Log.d("getDelBookShelf", "取消全选setSelector");
                    }
                }
                if (num == mDetailList.size()) {//全选
                    Log.d("getDelBookShelf", "num==" + mDetailList.size());
                    shelfViewHolder.deleteImageView.setSelected(true);
                    for (int i = 0; i < mDetailList.size(); i++) {
                        mDetailList.get(i).setSelector(true);
                        if (ListenerManager.getInstance().getDelBookShelf() != null) {
                            ListenerManager.getInstance().getDelBookShelf().del_book(mDetailList.get(i).getBook_id(), i, i + 1, 1);
                            Log.d("getDelBookShelf", "num===" + (i + 1));
                        }
                    }
                }
            }
            shelfViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isDeleteBook) {
                        setShelfState(shelfViewHolder, newposition, realBookDetailResponse);
                        return;//删除时不进入阅读页
                    }
//                    int layoutPosition = holder.getLayoutPosition();
//                    if (CollectionUtil.isEmpty(mDetailList) || layoutPosition >= mDetailList.size()) {
//                        return;
//                    }
                    if (CollectionUtil.isEmpty(mDetailList) || newposition >= mDetailList.size()) {
                        return;
                    }
                    BookDetailResponse bookDetail = mDetailList.get(newposition);
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
                if (realBookDetailResponse.isSelector()) {
                    shelfViewHolder.deleteImageView.setSelected(true);
                } else {
                    shelfViewHolder.deleteImageView.setSelected(false);
                }
            } else {
                shelfViewHolder.deleteImageView.setSelected(false);
            }
            if (num == mDetailList.size()) shelfViewHolder.deleteImageView.setSelected(true);
            if (!isDeleteBook && del_layout.getVisibility() == View.VISIBLE)
                goneDel();
            shelfViewHolder.deleteImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setShelfState(shelfViewHolder, newposition, realBookDetailResponse);
                }
            });
            judgeShowDeleteView(shelfViewHolder.deleteImageView);
        } else if (holder instanceof GoToStore) {
            GoToStore goToStore = (GoToStore) holder;
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) goToStore.mShadowLayout.getLayoutParams();
            params.height = mImageHeight - ScreenUtil.dpToPxInt(12);
            goToStore.mShadowLayout.setLayoutParams(params);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mContext instanceof HomeActivity) {
                        HomeActivity homeActivity = (HomeActivity) mContext;
                        homeActivity.setSelectView(BOOKSHELF_TO_BOOKSTORE);
                    }
                }
            });
        }

    }

    public void setOneState(OneViewHolder oneViewHolder, int position, BookDetailResponse detail) {
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

    public void setShelfState(ShelfViewHolder shelfViewHolder, int position, BookDetailResponse detail) {
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
            Log.d("setNum", "mDetailList.size===" + mDetailList.size());
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
//        return CollectionUtil.isEmpty(mDetailList) ? 0 : (AppUtil.isLogin() && UserBean.getInstance().getUser_package() == 1) ? mDetailList.size() + 1 : mDetailList.size();
//        return CollectionUtil.isEmpty(mDetailList) ? 0 : (AppUtil.isLogin() && IS_BAG == 1) ? mDetailList.size() + 1 : mDetailList.size();
        return CollectionUtil.isEmpty(mDetailList) ? 0 : mDetailList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mDetailList.size()) return ITEM_LAST;
        if (position == 0 && SharedPreferencesUtil.getInstance().getBoolean(ReadActivity.LAST_CHAPTER, false)) {
            IS_HAVE_ONE = true;
            return ITEM_ONE;
        }
        if (ISSHU) {
            return ITEM_SHU;
        }
        return ITEM_OTHER;
    }

    static class ShelfViewHolder extends RecyclerView.ViewHolder {

        TextView progressTextView, book_reader_chapter_text_view, book_reader_author_text_view;
        TextView titleTextView;
        ImageView logoImageView;
        RelativeLayout mShadowLayout;
        ImageView deleteImageView, purchaseImageView;

        ShelfViewHolder(View itemView) {
            super(itemView);
            book_reader_chapter_text_view = (TextView) itemView.findViewById(R.id.book_reader_chapter_text_view);
            book_reader_author_text_view = (TextView) itemView.findViewById(R.id.book_reader_author_text_view);
            progressTextView = (TextView) itemView.findViewById(R.id.book_reader_progress_text_view);
            titleTextView = (TextView) itemView.findViewById(R.id.book_reader_title_text_view);
            logoImageView = (ImageView) itemView.findViewById(R.id.book_reader_logo_image_view);
            deleteImageView = (ImageView) itemView.findViewById(R.id.book_reader_delete_image_view);
            purchaseImageView = (ImageView) itemView.findViewById(R.id.book_reader_purchase_image_view);
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

    static class GoToStore extends RecyclerView.ViewHolder {
        private RelativeLayout mShadowLayout;

        GoToStore(View itemView) {
            super(itemView);
            mShadowLayout = (RelativeLayout) itemView.findViewById(R.id.book_reader_image_layout);
        }
    }

    public void setDeleteListener(DeleteBookListener mDeleteListener) {
        this.mDeleteListener = mDeleteListener;
    }

    public void setIsRecommendData(boolean recommendData) {
        isRecommendData = recommendData;
    }

    public boolean getNum() {
        if (num == mDetailList.size())
            return true;
        return false;
    }


}
