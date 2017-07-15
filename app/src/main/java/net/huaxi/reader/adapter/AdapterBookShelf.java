package net.huaxi.reader.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.huaxi.reader.R;
import net.huaxi.reader.book.BookContentBottomView;
import net.huaxi.reader.db.dao.BookDao;
import net.huaxi.reader.db.dao.ChapterDao;
import net.huaxi.reader.db.model.BookTable;
import net.huaxi.reader.fragment.FmBookShelf;
import net.huaxi.reader.thread.SycnDeleteFailedBookTask;
import net.huaxi.reader.util.ImageUtil;
import net.huaxi.reader.util.UMEventAnalyze;
import net.huaxi.reader.view.RoundImageView;

import java.util.List;

/**
 * Function:书架Adapter
 * Author:zhumingwei
 * Create:
 */

public class AdapterBookShelf extends BaseRecyclerAdapter<BookTable> {
    private Context context;
    private FmBookShelf fmBookShelf;
    private boolean isDeleteState;
    private BookContentBottomView mbookContentBottomView;

    public AdapterBookShelf(FmBookShelf fmBookShelf, List<BookTable> list) {
        this.fmBookShelf = fmBookShelf;
        mDatas = list;
        this.context = fmBookShelf.getActivity();
        mbookContentBottomView=new BookContentBottomView(context);
    }

    public AdapterBookShelf(Context context, List<BookTable> list) {
        mDatas = list;
        this.context = context;
    }

    public boolean getDeleteState() {
        return isDeleteState;
    }

    public void setDeleteState(boolean state) {
        isDeleteState = state;
    }

    @Override
    public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bookshelf,
                parent, false);
        return new MyHolder(layout);
    }

    @Override
    public void onBind(RecyclerView.ViewHolder viewHolder, final int RealPosition, final
    BookTable book) {
        if (viewHolder instanceof MyHolder) {
//            String proess=mbookContentBottomView.getPercentStr1();
            final MyHolder holder = ((MyHolder) viewHolder);
            holder.tvReadProgress.setText("已读" + (int) (book.getReadPercentage() * 100) + "%");
//            holder.tvReadProgress.setText(proess);
            holder.tvBookName.setText(book.getName());

            if (book.getIsMonthly() == 1) { //是否VIP(包月)
                holder.isVip.setVisibility(View.VISIBLE);
            } else {
                holder.isVip.setVisibility(View.GONE);
            }
            int localCount = ChapterDao.getInstance().chapterCount(book.getBookId());
            int diff = book.getChapterCount() - localCount;
            if (diff <= 0) {
                diff = 0;
            }
            //章节更新数量
            if (diff > 0) {
                holder.tvNewChapters.setText(String.valueOf(diff));
                holder.tvNewChapters.setVisibility(View.VISIBLE);
            } else {
                holder.tvNewChapters.setVisibility(View.GONE);
            }
            if (book.getCoverImageId().startsWith("http")) {
                ImageUtil.loadImage(fmBookShelf.getActivity(), book.getCoverImageId(), holder.ivBookCover, R.mipmap.detail_book_default);
            }

            if (isDeleteState) {
                preventDrag = true;
                holder.ivDelete.setVisibility(View.VISIBLE);
                holder.ivDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UMEventAnalyze.countEvent(context, UMEventAnalyze.BOOK_SHELF_DELETE);
                        if (RealPosition >= mDatas.size()) {
                            return;
                        }
                        //1,数据库中删除
                        BookTable b = BookDao.getInstance().findBook(book.getBookId());
                        b.setAddToShelfTime(0);
                        b.setIsOnShelf(0);
                        BookDao.getInstance().addBook(b);
                        //2,移除数据
                        mDatas.remove(RealPosition);
                        notifyItemRemoved(holder.getLayoutPosition());
                        notifyItemRangeChanged(holder.getLayoutPosition(), mDatas.size() - holder.getLayoutPosition());
                        //3,同步网络
                        asynDeleteBook(b.getBookId());
                        if (mDatas.size() == 0) {
                            if (fmBookShelf != null) {
                                fmBookShelf.setHeadBackground(true);
                                fmBookShelf.refreshLocalData();
                            }
                        }
                    }
                });

            } else {
                preventDrag = false;
                holder.ivDelete.setVisibility(View.GONE);
            }

        }
    }

    private void asynDeleteBook(final String bid) {
        SycnDeleteFailedBookTask sycnDeleteFailedBookTask = new SycnDeleteFailedBookTask((Activity) context);
        sycnDeleteFailedBookTask.execute(bid);
    }


    class MyHolder extends Holder {
        private RoundImageView ivBookCover;
        private ImageView ivDelete, isVip;
        private TextView tvBookName, tvReadProgress, tvNewChapters;

        public MyHolder(View itemView) {
            super(itemView);
            ivBookCover = (RoundImageView) itemView.findViewById(R.id.shelf_item_imageview);
            tvBookName = (TextView) itemView.findViewById(R.id.shelf_item_bookname_textview);
            tvReadProgress = (TextView) itemView.findViewById(R.id
                    .shelf_item_read_process_textview);
            ivDelete = (ImageView) itemView.findViewById(R.id.shelf_item_delete_imageview);
            isVip = (ImageView) itemView.findViewById(R.id.shelf_item_vip);
            tvNewChapters = (TextView) itemView.findViewById(R.id.bookshelf_item_new_chapters);
        }

        @Override
        public void onItemSelected() {
            itemView.setScaleX(1.1f);
            itemView.setScaleY(1.1f);
            fmBookShelf.swipeRefreshLayout.setEnabled(false);
        }

        @Override
        public void onItemClear() {
            itemView.setScaleX(1f);
            itemView.setScaleY(1f);
            fmBookShelf.swipeRefreshLayout.setEnabled(true);
        }


    }
}
