package net.huaxi.reader.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.huaxi.reader.db.model.BookTable;
import net.huaxi.reader.util.ImageUtil;
import net.huaxi.reader.view.RoundImageView;

import java.util.List;

import net.huaxi.reader.R;

/**
 * 猜你喜欢Adapter
 * ryantao
 * 16/5/12.
 */
public class AdapterLikeBook extends BaseRecyclerAdapter<BookTable> {

    Context mContext;

    public AdapterLikeBook(Context context, List<BookTable> bookTables) {
        mContext = context;
        mDatas = bookTables;
    }


    @Override
    public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_like_book,
                parent, false);
        return new MyHolder(layout);
    }


    @Override
    public void onBind(RecyclerView.ViewHolder viewHolder, int RealPosition, BookTable data) {
        if (viewHolder instanceof MyHolder && data != null) {
            MyHolder holder = (MyHolder) viewHolder;
            ImageUtil.loadImage(mContext, data.getCoverImageId(), holder.ivBookCover, R.mipmap.detail_book_default);
            holder.tvAuthorName.setText(data.getAuthorName());
            holder.tvBookName.setText(data.getName());
        }
    }

    class MyHolder extends RecyclerView.ViewHolder {

        public RoundImageView ivBookCover;
        public TextView tvBookName, tvAuthorName;

        public MyHolder(View view) {
            super(view);
            ivBookCover = (RoundImageView) itemView.findViewById(R.id.shelf_item_imageview);
            tvBookName = (TextView) itemView.findViewById(R.id.detail_same_bookname_textview);
            tvAuthorName = (TextView) itemView.findViewById(R.id.detail_same_authorname_textview);
        }

    }


}
