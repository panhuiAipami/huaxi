package net.huaxi.reader.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.huaxi.reader.db.model.BookTable;
import net.huaxi.reader.view.RoundImageView;

import java.util.List;

import net.huaxi.reader.R;

import net.huaxi.reader.util.ImageUtil;

/**
 * Created by ZMW on 2016/1/14.
 * 同类作品
 */
public class AdapterSameBook extends BaseRecyclerAdapter<BookTable> {

    private Context context;

    public AdapterSameBook(Context context, List<BookTable> list) {
        mDatas = list;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_same_book,
                parent, false);
        return new MyHolder(layout);
    }

    @Override
    public void onBind(RecyclerView.ViewHolder viewHolder, int RealPosition, BookTable data) {
        if (viewHolder instanceof MyHolder) {
            MyHolder holder = (MyHolder) viewHolder;
            ImageUtil.loadImage(context, data.getCoverImageId(), holder.ivBookCover, R.mipmap.detail_book_default);
            holder.tvAuthorName.setText(data.getAuthorName());
            holder.tvBookName.setText(data.getName());
            if (data.getIsMonthly() == 1) {
                holder.ivSomeVip.setVisibility(View.VISIBLE);
            } else {
                holder.ivSomeVip.setVisibility(View.GONE);
            }
        }
    }

    class MyHolder extends Holder {
        private RoundImageView ivBookCover;
        private TextView tvBookName, tvAuthorName;
        private View ivSomeVip;

        public MyHolder(View itemView) {
            super(itemView);
            ivBookCover = (RoundImageView) itemView.findViewById(R.id.detail_same_item_imageview);
            tvBookName = (TextView) itemView.findViewById(R.id.detail_same_bookname_textview);
            tvAuthorName = (TextView) itemView.findViewById(R.id.detail_same_authorname_textview);
            ivSomeVip = itemView.findViewById(R.id.iv_detail_some_vip);
        }
    }
}
