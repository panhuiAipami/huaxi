package com.spriteapp.booklibrary.ui.adapter;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.config.HuaXiSDK;
import com.spriteapp.booklibrary.listener.ListenerManager;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.ui.adapter.second.FreeAdapter;
import com.spriteapp.booklibrary.util.ActivityUtil;
import com.spriteapp.booklibrary.util.GlideUtils;
import com.spriteapp.booklibrary.util.Util;

import java.util.List;

/**
 * Created by userfirst on 2018/4/26.
 */

public class GuessAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int SHUPOS = 0;
    private final int HENGPOS = 1;
    private final int SHAREPOS = 2;
    private String jumpUrl = "";
    private Activity context;
    private List<BookDetailResponse> list;
    private BookDetailResponse response;
    private int type;//1为阅读到结尾,2为中途退出

    public GuessAdapter(Activity context, List<BookDetailResponse> list, BookDetailResponse response, int type) {
        this.context = context;
        this.list = list;
        this.response = response;
        this.type = type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = null;
        if (viewType == SHUPOS) {//竖布局
            convertView = LayoutInflater.from(context).inflate(R.layout.store_details_layout, parent, false);
            return new ShuViewHolder(convertView);
        } else if (viewType == HENGPOS) {//横布局
            convertView = LayoutInflater.from(context).inflate(R.layout.store_free_layout, parent, false);
            return new HenViewHolder(convertView);
        } else {//分享布局
            convertView = LayoutInflater.from(context).inflate(R.layout.guess_you_like_layout, parent, false);
            return new ShareViewHolder(convertView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ShuViewHolder) {
            ShuViewHolder shuViewHolder = (ShuViewHolder) holder;
            final BookDetailResponse bookDetailResponse = list.get(position);
            if (bookDetailResponse == null) return;
            if (position == 0 && type == 1) {
                shuViewHolder.store_title_bar.setVisibility(View.VISIBLE);
                shuViewHolder.cate_title.setText(type == 1 ? R.string.other_like : R.string.guess_you_like);
                shuViewHolder.cate_title_small.setVisibility(View.GONE);
            } else {
                shuViewHolder.store_title_bar.setVisibility(View.GONE);
            }
            GlideUtils.loadImage(shuViewHolder.book_cover, bookDetailResponse.getBook_image(), context);
            GlideUtils.loadImage(shuViewHolder.author_head, bookDetailResponse.getAuthor_avatar(), context);
            shuViewHolder.book_name.setText(bookDetailResponse.getBook_name());
            shuViewHolder.author_name.setText(bookDetailResponse.getAuthor_name());
            shuViewHolder.book_describe.setText(bookDetailResponse.getBook_intro());
            shuViewHolder.book_state.setText(bookDetailResponse.getBook_finish_flag() ? "完本" : "连载");
            shuViewHolder.book_num.setText(Util.getFloat(bookDetailResponse.getBook_content_byte()));
            shuViewHolder.book_cate.setText((bookDetailResponse.getBook_keywords() != null && bookDetailResponse.getBook_keywords().size() != 0) ? bookDetailResponse.getBook_keywords().get(0) : "都市");
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if (bookDetailResponse.getBook_url() != null && !bookDetailResponse.getBook_url().isEmpty()) {
//                        Uri uri = Uri.parse(bookDetailResponse.getBook_url());
//                        jumpUrl = uri.getQueryParameter(WebConstant.URL_QUERY);
//                        ActivityUtil.toWebViewActivity(context, jumpUrl);
//                    }
                    ActivityUtil.toReadActivity(context, bookDetailResponse.getBook_id(), bookDetailResponse.getChapter_id());
                    context.finish();

                }
            });

        } else if (holder instanceof HenViewHolder) {
            HenViewHolder henViewHolder = (HenViewHolder) holder;
            if (list.size() > 2 && type == 1) {
                List smallList = list.subList(2, list.size());
                henViewHolder.free_title.setText(R.string.guess_you_like);
                if (smallList != null && smallList.size() != 0) {
                    Log.d("FreeViewHolder", "当前position===" + position);
                    henViewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                    henViewHolder.recyclerView.setAdapter(new FreeAdapter(context, smallList));
                }

            } else if (type == 2) {
                if (list != null && list.size() != 0) {
                    Log.d("FreeViewHolder", "当前position===" + position);
                    henViewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                    henViewHolder.recyclerView.setAdapter(new FreeAdapter(context, list));
                }
            }

        } else if (holder instanceof ShareViewHolder) {

            ShareViewHolder shareViewHolder = (ShareViewHolder) holder;
            shareViewHolder.goto_share.setOnClickListener(new View.OnClickListener() {//去分享
                @Override
                public void onClick(View v) {
                    HuaXiSDK.getInstance().showShareDialog(context, response, true, 1);
                }
            });
            shareViewHolder.goto_shelf.setOnClickListener(new View.OnClickListener() {//去书架
                @Override
                public void onClick(View v) {
                    if (ListenerManager.getInstance().getGotoHomePage() != null) {
                        ListenerManager.getInstance().getGotoHomePage().gotoPage(2);
                        context.finish();
                    }
                }
            });

        }

    }

    @Override
    public int getItemCount() {
        if (list == null) return 0;
        if (type == 1) {

            return list.size() >= 3 ? 4 : list.size() == 0 ? list.size() : list.size() + 1;
        } else {
            return list.size();
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (type == 1) {

            if (position == 0 || position == 1)
                return SHUPOS;
            else if (position == 3 || (list.size() == 2 && position == 2))
                return SHAREPOS;
            return HENGPOS;
        } else {
            return SHUPOS;
        }


    }

    private class ShuViewHolder extends RecyclerView.ViewHolder {

        private ImageView book_cover, author_head;
        private TextView book_name, book_describe, book_cate, book_state, book_num, author_name;
        //标题
        private TextView cate_title, cate_title_small;
        private LinearLayout store_title_bar;

        public ShuViewHolder(View itemView) {
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
        }
    }

    private class HenViewHolder extends RecyclerView.ViewHolder {//标题
        private TextView free_title;
        private RecyclerView recyclerView;

        public HenViewHolder(View itemView) {
            super(itemView);
            free_title = (TextView) itemView.findViewById(R.id.free_title);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.recyclerView);
        }
    }

    private class ShareViewHolder extends RecyclerView.ViewHolder {//标题
        private TextView goto_share, goto_shelf;

        public ShareViewHolder(View itemView) {
            super(itemView);
            goto_share = (TextView) itemView.findViewById(R.id.goto_share);
            goto_shelf = (TextView) itemView.findViewById(R.id.goto_shelf);
        }
    }

}
