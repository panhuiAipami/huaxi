package com.spriteapp.booklibrary.ui.adapter;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.util.ActivityUtil;
import com.spriteapp.booklibrary.util.GlideUtils;

import java.util.List;

public class RankAdapter extends RecyclerView.Adapter<RankAdapter.ViewHolder> {

    private final List<BookDetailResponse> mValues;
    private Activity c;

    public RankAdapter(List<BookDetailResponse> items, Activity c) {
        mValues = items;
        this.c = c;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rank_fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final BookDetailResponse bean = mValues.get(position);
        GlideUtils.loadImage(holder.author_cover, bean.getAuthor_avatar(), c);
        GlideUtils.loadImage(holder.ranking_book_cover, bean.getBook_image(), c);

        int color;
        if (position < 3) {
            color = ContextCompat.getColor(c, R.color.square_support_selector);
        } else {
            color = ContextCompat.getColor(c, R.color.three_font_color);
        }
        holder.top_num.setTextColor(color);
        holder.top_num.setText(position + 1 + "");
        holder.ranking_book_name.setText(bean.getBook_name());
        holder.ranking_book_intro.setText(bean.getBook_intro());
        holder.ranking_book_author.setText(bean.getAuthor_name());
        holder.ranking_book_zan.setText(bean.getSupport_num());
        holder.ranking_book_comment.setText(bean.getComment_num());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtil.toReadActivity(c, bean.getBook_id(), 0);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ranking_book_cover, author_cover;
        public TextView top_num, ranking_book_name, ranking_book_intro, ranking_book_author, ranking_book_zan, ranking_book_comment;

        public ViewHolder(View view) {
            super(view);
            ranking_book_cover = (ImageView) view.findViewById(R.id.ranking_book_cover);
            author_cover = (ImageView) view.findViewById(R.id.author_cover);

            top_num = (TextView) view.findViewById(R.id.top_num);
            ranking_book_name = (TextView) view.findViewById(R.id.ranking_book_name);
            ranking_book_intro = (TextView) view.findViewById(R.id.ranking_book_intro);
            ranking_book_author = (TextView) view.findViewById(R.id.ranking_book_author);
            ranking_book_zan = (TextView) view.findViewById(R.id.ranking_book_zan);
            ranking_book_comment = (TextView) view.findViewById(R.id.ranking_book_comment);
        }
    }
}
