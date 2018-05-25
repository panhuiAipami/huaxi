package com.spriteapp.booklibrary.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.constant.WebConstant;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.util.ActivityUtil;
import com.spriteapp.booklibrary.util.GlideUtils;
import com.spriteapp.booklibrary.util.RecyclerViewUtil;
import com.spriteapp.booklibrary.util.ScreenUtil;
import com.spriteapp.booklibrary.util.Util;

import java.util.List;

import static com.spriteapp.booklibrary.ui.adapter.BookShelfAdapter.ISSHU;

/**
 * Created by userfirst on 2018/4/23.
 */

public class StoreDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Activity context;
    private List<BookDetailResponse> list;
    private final int mImageWidth;
    private final int mImageHeight;
    private int HENG = 0;
    private int SHU = 1;
    private int type;
    private String jumpUrl = "";
    private static final int IMAGE_WIDTH = 100;
    private static final int IMAGE_HEIGHT = 144;

    public StoreDetailsAdapter(Activity context, List<BookDetailResponse> list, int spanCount, int spaceWidth,int type) {
        this.context = context;
        this.list = list;
        this.type = type;
        mImageWidth = RecyclerViewUtil.getImageWidth(spanCount - 1, spaceWidth);
        mImageHeight = mImageWidth * ScreenUtil.dpToPxInt(IMAGE_HEIGHT)
                / ScreenUtil.dpToPxInt(IMAGE_WIDTH);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = null;
        if (viewType == SHU) {
            convertView = LayoutInflater.from(context).inflate(R.layout.store_details_layout, parent, false);
            return new DetailsViewHolderShu(convertView);
        } else {
            convertView = LayoutInflater.from(context).inflate(R.layout.store_details_layout_item, parent, false);
            return new DetailsViewHolder(convertView);
        }


    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final BookDetailResponse bookDetailResponse = list.get(position);
        if (bookDetailResponse == null) return;
        if (holder instanceof DetailsViewHolder) {
            DetailsViewHolder detailsViewHolder = (DetailsViewHolder) holder;
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) detailsViewHolder.mShadowLayout.getLayoutParams();
            params.height = mImageHeight - ScreenUtil.dpToPxInt(12);
            detailsViewHolder.mShadowLayout.setLayoutParams(params);
            GlideUtils.loadImage(detailsViewHolder.book_cover, bookDetailResponse.getBook_image(), context);
            detailsViewHolder.book_name.setText(bookDetailResponse.getBook_name());
            detailsViewHolder.author_name.setText(bookDetailResponse.getAuthor_name());
            detailsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityUtil.toReadActivity(context, bookDetailResponse.getBook_id(), bookDetailResponse.getChapter_id());
                }
            });
        } else if (holder instanceof DetailsViewHolderShu) {
            DetailsViewHolderShu shuViewHolder = (DetailsViewHolderShu) holder;
            shuViewHolder.store_title_bar.setVisibility(View.GONE);
            GlideUtils.loadImage(shuViewHolder.book_cover, bookDetailResponse.getBook_image(), context);
            GlideUtils.loadImage(shuViewHolder.author_head, bookDetailResponse.getAuthor_avatar(), context);
            shuViewHolder.book_name.setText(bookDetailResponse.getBook_name());
            shuViewHolder.author_name.setText(bookDetailResponse.getAuthor_name());
            shuViewHolder.book_describe.setText(bookDetailResponse.getBook_intro());
            shuViewHolder.book_state.setText(bookDetailResponse.getBook_finish_flag() == 0 ? "连载" : "完本");
            shuViewHolder.book_num.setText(Util.getFloat(bookDetailResponse.getBook_content_byte()));
            shuViewHolder.book_cate.setText((bookDetailResponse.getBook_category() != null && bookDetailResponse.getBook_category().size() != 0) ? bookDetailResponse.getBook_category().get(0).getClass_name() : "都市");
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if (bookDetailResponse.getBook_url() != null && !bookDetailResponse.getBook_url().isEmpty()) {
//                        Uri uri = Uri.parse(bookDetailResponse.getBook_url());
//                        jumpUrl = uri.getQueryParameter(WebConstant.URL_QUERY);
//                        ActivityUtil.toWebViewActivity(context, jumpUrl);
//                    }
                    ActivityUtil.toReadActivity(context, bookDetailResponse.getBook_id(), bookDetailResponse.getChapter_id());

                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (ISSHU)
            return SHU;
        else if (type == 1)
            return SHU;
        return HENG;
    }

    private class DetailsViewHolder extends RecyclerView.ViewHolder {
        private ImageView book_cover;
        private TextView book_name;
        private TextView author_name;
        private RelativeLayout mShadowLayout;

        public DetailsViewHolder(View itemView) {
            super(itemView);
            book_cover = (ImageView) itemView.findViewById(R.id.book_cover);
            book_name = (TextView) itemView.findViewById(R.id.book_name);
            author_name = (TextView) itemView.findViewById(R.id.author_name);
            mShadowLayout = (RelativeLayout) itemView.findViewById(R.id.book_reader_image_layout);
        }
    }

    private class DetailsViewHolderShu extends RecyclerView.ViewHolder {//详情
        private ImageView book_cover, author_head;
        private TextView book_name, book_describe, book_cate, book_state, book_num, author_name;
        //标题
        private TextView cate_title, cate_title_small;
        private LinearLayout store_title_bar, item_layout;

        public DetailsViewHolderShu(View itemView) {
            super(itemView);
            book_cover = (ImageView) itemView.findViewById(R.id.book_cover);
            author_head = (ImageView) itemView.findViewById(R.id.author_head);
            book_name = (TextView) itemView.findViewById(R.id.book_name);
            author_name = (TextView) itemView.findViewById(R.id.author_name);
            book_describe = (TextView) itemView.findViewById(R.id.book_describe);
            book_cate = (TextView) itemView.findViewById(R.id.book_cate);
            book_state = (TextView) itemView.findViewById(R.id.book_state);
            book_num = (TextView) itemView.findViewById(R.id.book_num);

            cate_title = (TextView) itemView.findViewById(R.id.cate_title);
            cate_title_small = (TextView) itemView.findViewById(R.id.cate_title_small);
            store_title_bar = (LinearLayout) itemView.findViewById(R.id.store_title_bar);
            item_layout = (LinearLayout) itemView.findViewById(R.id.item_layout);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            layoutParams.setMargins(ScreenUtil.dpToPxInt(10), ScreenUtil.dpToPxInt(10), 0, ScreenUtil.dpToPxInt(10));
            item_layout.setLayoutParams(layoutParams);

        }
    }
}
