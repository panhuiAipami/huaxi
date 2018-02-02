package com.spriteapp.booklibrary.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.constant.WebConstant;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.util.ActivityUtil;
import com.spriteapp.booklibrary.util.GlideUtils;
import com.spriteapp.booklibrary.util.Util;

import java.util.List;

/**
 * Created by Administrator on 2018/2/2.
 */

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity context;
    private List<BookDetailResponse> list;
    private String jumpUrl = "";

    public SearchAdapter(Activity context, List<BookDetailResponse> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = null;
        if (viewType == 0) {//左边文字
            convertView = LayoutInflater.from(context).inflate(R.layout.store_details_layout, parent, false);
            return new SearchViewHolder(convertView);
        } else {
            convertView = LayoutInflater.from(context).inflate(R.layout.store_details_layout, parent, false);
            return new SearchViewHolder(convertView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SearchViewHolder) {
            SearchViewHolder search = (SearchViewHolder) holder;
            final BookDetailResponse bookDetailResponse = list.get(position);
            GlideUtils.loadImage(search.book_cover, bookDetailResponse.getBook_image(), context);
            GlideUtils.loadImage(search.author_head, bookDetailResponse.getAuthor_avatar(), context);
            search.book_name.setText(bookDetailResponse.getBook_name());
            search.author_name.setText(bookDetailResponse.getAuthor_name());
            search.book_describe.setText(bookDetailResponse.getBook_intro());
            search.book_state.setText(bookDetailResponse.getBook_finish_flag() == 0 ? "连载" : "完本");
            search.book_num.setText(Util.getFloat(bookDetailResponse.getBook_content_byte()));
            search.book_cate.setText((bookDetailResponse.getBook_keywords() != null && bookDetailResponse.getBook_keywords().size() != 0) ? bookDetailResponse.getBook_keywords().get(0) : "都市");
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bookDetailResponse.getBook_url() != null && !bookDetailResponse.getBook_url().isEmpty()) {
                        Uri uri = Uri.parse(bookDetailResponse.getBook_url());

                        jumpUrl = uri.getQueryParameter(WebConstant.URL_QUERY);
                        if (TextUtils.isEmpty(jumpUrl)) return;
                        Uri uri1 = Uri.parse(jumpUrl);
                        String book_id = uri1.getQueryParameter(WebConstant.BOOK_ID_QUERY);
                        ActivityUtil.toReadActivity(context, Integer.parseInt(book_id), 0);

//                        ActivityUtil.toWebViewActivity(context, jumpUrl);
                    }
                }
            });

        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    private class SearchViewHolder extends RecyclerView.ViewHolder {
        private ImageView book_cover, author_head;
        private TextView book_name, book_describe, book_cate, book_state, book_num, author_name;

        public SearchViewHolder(View itemView) {
            super(itemView);
            book_cover = (ImageView) itemView.findViewById(R.id.book_cover);
            author_head = (ImageView) itemView.findViewById(R.id.author_head);
            book_name = (TextView) itemView.findViewById(R.id.book_name);
            author_name = (TextView) itemView.findViewById(R.id.author_name);
            book_describe = (TextView) itemView.findViewById(R.id.book_describe);
            book_cate = (TextView) itemView.findViewById(R.id.book_cate);
            book_state = (TextView) itemView.findViewById(R.id.book_state);
            book_num = (TextView) itemView.findViewById(R.id.book_num);
        }
    }
}
