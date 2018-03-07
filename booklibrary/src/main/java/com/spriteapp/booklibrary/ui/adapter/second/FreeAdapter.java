package com.spriteapp.booklibrary.ui.adapter.second;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
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

    private static final int IMAGE_WIDTH = 100;
    private static final int IMAGE_HEIGHT = 144;
    private static final int ANIMATION_TIME = 300;
    private final int mImageWidth;
    private final int mImageHeight;

    public FreeAdapter(Context context, List<BookDetailResponse> list) {
        this.context = context;
        this.list = list;
        mImageWidth = RecyclerViewUtil.getImageWidth(3 - 1, 2);
        mImageHeight = mImageWidth * ScreenUtil.dpToPxInt(IMAGE_HEIGHT)
                / ScreenUtil.dpToPxInt(IMAGE_WIDTH);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.free_item_layout, parent, false);
        return new FreeAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final BookDetailResponse bookDetailResponse = list.get(position);
        holder.book_name.setText(bookDetailResponse.getBook_name());
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.image_layout.getLayoutParams();
        params.height = mImageHeight - ScreenUtil.dpToPxInt(12);
        holder.image_layout.setLayoutParams(params);
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
        private TextView book_name;
        private ImageView book_cover;
        private RelativeLayout image_layout;

        public MyViewHolder(View itemView) {
            super(itemView);
            book_name = (TextView) itemView.findViewById(R.id.book_name);
            book_cover = (ImageView) itemView.findViewById(R.id.book_cover);
            image_layout = (RelativeLayout) itemView.findViewById(R.id.book_reader_image_layout);
        }
    }
}
