package com.spriteapp.booklibrary.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.util.ActivityUtil;
import com.spriteapp.booklibrary.util.RecyclerViewUtil;
import com.spriteapp.booklibrary.util.ScreenUtil;
import com.spriteapp.booklibrary.util.StringUtil;

import java.util.List;

/**
 * Created by userfirst on 2018/4/9.
 */

public class BagAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<BookDetailResponse> list;
    private static final int IMAGE_WIDTH = 100;
    private static final int IMAGE_HEIGHT = 144;
    private static final int ANIMATION_TIME = 300;
    private final int mImageWidth;
    private final int mImageHeight;


    public BagAdapter(Context mContext, List<BookDetailResponse> list) {
        this.mContext = mContext;
        this.list = list;
        mImageWidth = RecyclerViewUtil.getImageWidth(3, 2);
        mImageHeight = mImageWidth * ScreenUtil.dpToPxInt(IMAGE_HEIGHT)
                / ScreenUtil.dpToPxInt(IMAGE_WIDTH);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = null;
        convertView = LayoutInflater.from(mContext).inflate(R.layout.book_reader_item_book_shelf, parent, false);
        return new BookShelfAdapter.ShelfViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final BookDetailResponse details = list.get(position);
        if (details == null) return;
        if (holder instanceof ShelfViewHolder) {
            final ShelfViewHolder shelfViewHolder = (ShelfViewHolder) holder;
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) shelfViewHolder.mShadowLayout.getLayoutParams();
            params.height = mImageHeight - ScreenUtil.dpToPxInt(12);
            shelfViewHolder.mShadowLayout.setLayoutParams(params);
            shelfViewHolder.purchaseImageView.setVisibility(View.VISIBLE);//显示已购icon
            Glide.with(mContext)
                    .load(details.getBook_image())
                    .into(shelfViewHolder.logoImageView);
            if (!StringUtil.isEmpty(details.getBook_name())) {
                shelfViewHolder.titleTextView.setText(details.getBook_name());
            }
            if (details.getBook_chapter_total() != 0) {
                shelfViewHolder.progressTextView.setVisibility(View.VISIBLE);
                shelfViewHolder.progressTextView.setText
                        (String.format(mContext.getResources().getString(R.string.book_reader_read_progress_text),
                                details.getLast_chapter_index() * 100 / details.getBook_chapter_total()));
            } else {
                shelfViewHolder.progressTextView.setVisibility(View.GONE);
            }
            shelfViewHolder.logoImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//进入阅读页
                    ActivityUtil.toReadActivity(mContext, details);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    private class ShelfViewHolder extends RecyclerView.ViewHolder {

        TextView progressTextView;
        TextView titleTextView;
        ImageView logoImageView;
        RelativeLayout mShadowLayout;
        ImageView deleteImageView, purchaseImageView;


        ShelfViewHolder(View itemView) {
            super(itemView);
            progressTextView = (TextView) itemView.findViewById(R.id.book_reader_progress_text_view);
            titleTextView = (TextView) itemView.findViewById(R.id.book_reader_title_text_view);
            logoImageView = (ImageView) itemView.findViewById(R.id.book_reader_logo_image_view);
            deleteImageView = (ImageView) itemView.findViewById(R.id.book_reader_delete_image_view);
            purchaseImageView = (ImageView) itemView.findViewById(R.id.book_reader_purchase_image_view);
            mShadowLayout = (RelativeLayout) itemView.findViewById(R.id.book_reader_image_layout);
        }
    }
}
