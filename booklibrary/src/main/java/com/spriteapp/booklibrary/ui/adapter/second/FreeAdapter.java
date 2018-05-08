package com.spriteapp.booklibrary.ui.adapter.second;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.base.BaseActivity;
import com.spriteapp.booklibrary.constant.WebConstant;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.ui.adapter.ChoiceAdapter;
import com.spriteapp.booklibrary.util.ActivityUtil;
import com.spriteapp.booklibrary.util.GlideUtils;
import com.spriteapp.booklibrary.util.RecyclerViewUtil;
import com.spriteapp.booklibrary.util.ScreenUtil;

import java.util.List;

/**
 * Created by userfirst on 2018/3/7.
 */

public class FreeAdapter extends RecyclerView.Adapter<FreeAdapter.MyViewHolder> {
    private Context context;
    private List<BookDetailResponse> list;
    private String jumpUrl = "";

//    private static final int IMAGE_WIDTH = 95;
//    private static final int IMAGE_HEIGHT = 138;
//    private static final int ANIMATION_TIME = 300;
//    private final int mImageWidth;
//    private final int mImageHeight;

    public FreeAdapter(Context context, List<BookDetailResponse> list) {
        this.context = context;
        this.list = list;
//        mImageWidth = RecyclerViewUtil.getImageWidth(3 - 1, 2);
//        mImageHeight = mImageWidth * ScreenUtil.dpToPxInt(IMAGE_HEIGHT)
//                / ScreenUtil.dpToPxInt(IMAGE_WIDTH);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.free_item_layout, parent, false);
        return new FreeAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (position >= list.size()) return;
        final BookDetailResponse bookDetailResponse = list.get(position);
        holder.book_name.setText(bookDetailResponse.getBook_name());
        holder.author_name.setText(bookDetailResponse.getAuthor_name());

//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.image_layout.getLayoutParams();
//        params.height = mImageHeight - ScreenUtil.dpToPxInt(12);
//        holder.image_layout.setLayoutParams(params);


        GlideUtils.loadImage(holder.book_cover, bookDetailResponse.getBook_image(), context);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bookDetailResponse.getBook_url() != null && !bookDetailResponse.getBook_url().isEmpty()) {
                    Uri uri = Uri.parse(bookDetailResponse.getBook_url());
                    jumpUrl = uri.getQueryParameter(WebConstant.URL_QUERY);
                    ActivityUtil.toWebViewActivity(context, jumpUrl);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView book_name,author_name;
        private ImageView book_cover;
        private LinearLayout free_item;

        public MyViewHolder(View itemView) {
            super(itemView);
            book_name = (TextView) itemView.findViewById(R.id.book_name);
            author_name = (TextView) itemView.findViewById(R.id.author_name);
            book_cover = (ImageView) itemView.findViewById(R.id.book_cover);
            free_item = (LinearLayout) itemView.findViewById(R.id.free_item);
            ViewGroup.LayoutParams layoutParams = free_item.getLayoutParams();
            layoutParams.width = BaseActivity.deviceWidth / 4;
            int padding = 30;
            if (BaseActivity.deviceWidth <= 480) {
                padding = 20;
            } else if (BaseActivity.deviceWidth > 480 && BaseActivity.deviceWidth <= 720) {
                padding = 30;
            } else if (BaseActivity.deviceWidth > 720 && BaseActivity.deviceWidth <= 1080) {
                padding = 45;
            } else if (BaseActivity.deviceWidth >= 1080) {
                padding = 60;
            }
            int imageWidth = layoutParams.width;

            Log.d("layoutParams", "item_width===" + layoutParams.width);
            free_item.setLayoutParams(layoutParams);

            if (imageWidth > padding) {
                ViewGroup.LayoutParams layoutParams1 = book_cover.getLayoutParams();
                layoutParams1.width = imageWidth - padding;
                layoutParams1.height = (int) ((imageWidth - padding) * 1.32f);
                Log.d("layoutParams", "width===" + layoutParams1.width + "height===" + layoutParams1.height + "padding===" + padding);
                book_cover.setLayoutParams(layoutParams1);
                ViewGroup.LayoutParams layoutParams2 = book_name.getLayoutParams();
                layoutParams2.width = imageWidth - padding;
                book_name.setLayoutParams(layoutParams2);
                author_name.setLayoutParams(layoutParams2);
            }

        }
    }
}
