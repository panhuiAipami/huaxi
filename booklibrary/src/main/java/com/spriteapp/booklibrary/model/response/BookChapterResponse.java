package com.spriteapp.booklibrary.model.response;

import java.io.Serializable;

/**
 * 章节
 * Created by kuangxiaoguo on 2017/7/8.
 */

public class BookChapterResponse implements Serializable {
    private int chapter_id;
    private String chapter_title;
    private int chapter_order;
    private int chapter_is_vip;
    private long chapter_content_byte;
    private int chapter_is_sub;
    private int chapter_price;
    private int bookId;
    private int chapterReadState;

    private boolean is_check = false;
    private int is_download = 0;

    public boolean getIs_download() {
        return is_download == 1;
    }

    public void setIs_download(int is_download) {
        this.is_download = is_download;
    }

    public boolean isIs_check() {
        return is_check;
    }

    public void setIs_check(boolean is_check) {
        this.is_check = is_check;
    }

    public int getChapter_id() {
        return chapter_id;
    }

    public void setChapter_id(int chapter_id) {
        this.chapter_id = chapter_id;
    }

    public String getChapter_title() {
        return chapter_title;
    }

    public void setChapter_title(String chapter_title) {
        this.chapter_title = chapter_title;
    }

    public int getChapter_order() {
        return chapter_order;
    }

    public void setChapter_order(int chapter_order) {
        this.chapter_order = chapter_order;
    }

    public int getChapter_is_vip() {
        return chapter_is_vip;
    }

    public void setChapter_is_vip(int chapter_is_vip) {
        this.chapter_is_vip = chapter_is_vip;
    }

    public long getChapter_content_byte() {
        return chapter_content_byte;
    }

    public void setChapter_content_byte(long chapter_content_byte) {
        this.chapter_content_byte = chapter_content_byte;
    }

    public int getChapter_is_sub() {
        return chapter_is_sub;
    }

    public void setChapter_is_sub(int chapter_is_sub) {
        this.chapter_is_sub = chapter_is_sub;
    }

    public int getChapter_price() {
        return chapter_price;
    }

    public void setChapter_price(int chapter_price) {
        this.chapter_price = chapter_price;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getChapterReadState() {
        return chapterReadState;
    }

    public void setChapterReadState(int chapterReadState) {
        this.chapterReadState = chapterReadState;
    }
}
