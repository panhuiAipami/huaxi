package com.spriteapp.booklibrary.model.response;

import java.util.List;

/**
 * 章节分组，免费为第一组，后面每20章分一组
 * Created by panhui on 2018/3/1.
 */

public class GroupChapter {
    private int is_free;
    private int start_chapter;
    private int end_chapter;
    private boolean is_check;
    private boolean is_open;
    private int price;
    private int is_download = 0;

    private List<BookChapterResponse> mChapterList;

    public boolean getIs_download() {
        return is_download == 1;
    }

    public void setIs_download(int is_download) {
        this.is_download = is_download;
    }

    public boolean getIs_free() {
        return is_free == 0;
    }

    public void setIs_free(int is_free) {
        this.is_free = is_free;
    }

    public int getStart_chapter() {
        return start_chapter;
    }

    public void setStart_chapter(int start_chapter) {
        this.start_chapter = start_chapter;
    }

    public int getEnd_chapter() {
        return end_chapter;
    }

    public void setEnd_chapter(int end_chapter) {
        this.end_chapter = end_chapter;
    }

    public boolean isIs_check() {
        return is_check;
    }

    public void setIs_check(boolean is_check) {
        this.is_check = is_check;
    }

    public boolean isIs_open() {
        return is_open;
    }

    public void setIs_open(boolean is_open) {
        this.is_open = is_open;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public List<BookChapterResponse> getmChapterList() {
        return mChapterList;
    }

    public void setmChapterList(List<BookChapterResponse> mChapterList) {
        this.mChapterList = mChapterList;
    }

    @Override
    public String toString() {
        return "GroupChapter{" +
                "is_free=" + is_free +
                ", start_chapter=" + start_chapter +
                ", end_chapter=" + end_chapter +
                ", is_check=" + is_check +
                ", is_open=" + is_open +
                ", price=" + price +
                ", mChapterList=" + mChapterList +
                '}';
    }
}
