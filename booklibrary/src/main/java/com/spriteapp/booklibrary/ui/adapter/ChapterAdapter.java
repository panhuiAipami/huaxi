package com.spriteapp.booklibrary.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.enumeration.ChapterEnum;
import com.spriteapp.booklibrary.model.response.BookChapterResponse;
import com.spriteapp.booklibrary.util.CollectionUtil;

import java.util.List;

/**
 * Created by kuangxiaoguo on 2017/7/14.
 */

public class ChapterAdapter extends BaseAdapter {

    private Context mContext;
    private List<BookChapterResponse> mCatalogList;
    private LayoutInflater mInflater;
    private int mCurrentChapter;
    private boolean isNight;

    public ChapterAdapter(Context mContext, List<BookChapterResponse> mCatalogList) {
        this.mContext = mContext;
        this.mCatalogList = mCatalogList;
        mInflater = LayoutInflater.from(mContext);
    }

    public void setCurrentChapter(int mCurrentChapter) {
        this.mCurrentChapter = mCurrentChapter;
    }

    public int getCurrentChapter() {
        return mCurrentChapter;
    }

    @Override
    public int getCount() {
        return CollectionUtil.isEmpty(mCatalogList) ? 0 : mCatalogList.size();
    }

    @Override
    public Object getItem(int position) {
        return mCatalogList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.book_reader_item_chapter_list, null);
            holder.chapterTextView = (TextView) convertView.findViewById(R.id.book_reader_chapter_text_view);
            holder.freeChapterTextView = (TextView) convertView.findViewById(R.id.book_reader_free_chapter_text_view);
            holder.lineView = convertView.findViewById(R.id.book_reader_line_view);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        BookChapterResponse bookChapterResponse = mCatalogList.get(position);
        if (bookChapterResponse == null) {
            return convertView;
        }
        boolean hasRead = bookChapterResponse.getChapterReadState() == ChapterEnum.HAS_READ.getCode();
        String chapterTitle = bookChapterResponse.getChapter_title();
        if (!TextUtils.isEmpty(chapterTitle)) {
            holder.chapterTextView.setText(chapterTitle.trim());
        }
        boolean isCurrentChapter = bookChapterResponse.getChapter_id() == mCurrentChapter;
        if (isCurrentChapter) {
            holder.chapterTextView.setTextColor(mContext.getResources()
                    .getColor(isNight ? R.color.book_reader_main_night_color :
                            R.color.book_reader_main_color));
        } else {
            holder.chapterTextView.setTextColor(mContext.getResources()
                    .getColor(isNight ? hasRead ? R.color.book_reader_chapter_item_night_color :
                            R.color.book_reader_chapter_not_read_night_color :
                            hasRead ? R.color.book_reader_common_text_color :
                                    R.color.book_reader_chapter_not_read_day_color));
        }
        boolean isVipChapter = ChapterEnum.CHAPTER_IS_VIP.getCode()
                == bookChapterResponse.getChapter_is_vip();
        holder.freeChapterTextView.setVisibility(isVipChapter ? View.GONE : View.VISIBLE);
        if (isCurrentChapter) {
            holder.freeChapterTextView.setTextColor(mContext.getResources()
                    .getColor(isNight ? R.color.book_reader_chapter_free_text_night_color :
                            R.color.book_reader_chapter_free_text_day_color));
        } else {
            holder.freeChapterTextView.setTextColor(mContext.getResources()
                    .getColor(isNight ? R.color.book_reader_chapter_not_read_night_color :
                            R.color.book_reader_chapter_not_read_day_color));
        }
        holder.lineView.setBackgroundResource(isNight ? R.color.book_reader_divide_line_night_color :
                R.color.book_reader_divide_line_color);
        return convertView;
    }

    public void setNight(boolean night) {
        isNight = night;
    }

    private class ViewHolder {
        TextView chapterTextView;
        TextView freeChapterTextView;
        View lineView;
    }
}
