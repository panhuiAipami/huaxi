package com.spriteapp.booklibrary.model.response;

import java.io.Serializable;

/**
 * 书籍详情
 * Created by kuangxiaoguo on 2017/7/7.
 */

public class BookDetailResponse implements Serializable {

    private int book_id;
    private String book_name;
    private String book_image;
    private String book_intro;
    private String book_share_url;
    private String book_total_score;
    private int book_total_reads;
    private int book_finish_flag;
    private int book_is_vip;
    private long book_content_byte;
    private int book_chapter_total;
//    private List<String> book_keywords;
    private int book_price;
    private long book_updatetime;
    private int author_id;
    private String author_name;
    private String author_avatar;
    private int last_update_chapter_id;
    private long last_update_chapter_datetime;
    private String last_update_chapter_title;
    private String last_update_chapter_intro;
    private long lastReadTime;
    private long update_time;

    /**
     * 当前阅读章节
     */
    private int chapter_id;
    private int chapter_index;
    private long last_update_book_datetime;
    private int book_add_shelf;
    private int is_recommend_book;

    public long getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(long update_time) {
        this.update_time = update_time;
    }

    public int getBook_id() {
        return book_id;
    }

    public void setBook_id(int book_id) {
        this.book_id = book_id;
    }

    public String getBook_name() {
        return book_name;
    }

    public void setBook_name(String book_name) {
        this.book_name = book_name;
    }

    public String getBook_image() {
        return book_image;
    }

    public void setBook_image(String book_image) {
        this.book_image = book_image;
    }

    public String getBook_intro() {
        return book_intro;
    }

    public void setBook_intro(String book_intro) {
        this.book_intro = book_intro;
    }

    public String getBook_share_url() {
        return book_share_url;
    }

    public void setBook_share_url(String book_share_url) {
        this.book_share_url = book_share_url;
    }

    public String getBook_total_score() {
        return book_total_score;
    }

    public void setBook_total_score(String book_total_score) {
        this.book_total_score = book_total_score;
    }

    public int getBook_total_reads() {
        return book_total_reads;
    }

    public void setBook_total_reads(int book_total_reads) {
        this.book_total_reads = book_total_reads;
    }

    public int getBook_finish_flag() {
        return book_finish_flag;
    }

    public void setBook_finish_flag(int book_finish_flag) {
        this.book_finish_flag = book_finish_flag;
    }

    public int getBook_is_vip() {
        return book_is_vip;
    }

    public void setBook_is_vip(int book_is_vip) {
        this.book_is_vip = book_is_vip;
    }

    public long getBook_content_byte() {
        return book_content_byte;
    }

    public void setBook_content_byte(long book_content_byte) {
        this.book_content_byte = book_content_byte;
    }

    public int getBook_chapter_total() {
        return book_chapter_total;
    }

    public void setBook_chapter_total(int book_chapter_total) {
        this.book_chapter_total = book_chapter_total;
    }

//    public List<String> getBook_keywords() {
//        return book_keywords;
//    }
//
//    public void setBook_keywords(List<String> book_keywords) {
//        this.book_keywords = book_keywords;
//    }

    public int getBook_price() {
        return book_price;
    }

    public void setBook_price(int book_price) {
        this.book_price = book_price;
    }

    public long getBook_updatetime() {
        return book_updatetime;
    }

    public void setBook_updatetime(long book_updatetime) {
        this.book_updatetime = book_updatetime;
    }

    public int getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(int author_id) {
        this.author_id = author_id;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public String getAuthor_avatar() {
        return author_avatar;
    }

    public void setAuthor_avatar(String author_avatar) {
        this.author_avatar = author_avatar;
    }

    public int getLast_update_chapter_id() {
        return last_update_chapter_id;
    }

    public void setLast_update_chapter_id(int last_update_chapter_id) {
        this.last_update_chapter_id = last_update_chapter_id;
    }

    public long getLast_update_chapter_datetime() {
        return last_update_chapter_datetime;
    }

    public void setLast_update_chapter_datetime(long last_update_chapter_datetime) {
        this.last_update_chapter_datetime = last_update_chapter_datetime;
    }

    public String getLast_update_chapter_title() {
        return last_update_chapter_title;
    }

    public void setLast_update_chapter_title(String last_update_chapter_title) {
        this.last_update_chapter_title = last_update_chapter_title;
    }

    public String getLast_update_chapter_intro() {
        return last_update_chapter_intro;
    }

    public void setLast_update_chapter_intro(String last_update_chapter_intro) {
        this.last_update_chapter_intro = last_update_chapter_intro;
    }

    public int getChapter_id() {
        return chapter_id;
    }

    public void setChapter_id(int chapter_id) {
        this.chapter_id = chapter_id;
    }

    public long getLastReadTime() {
        return lastReadTime;
    }

    public void setLastReadTime(long lastReadTime) {
        this.lastReadTime = lastReadTime;
    }

    public int getLast_chapter_index() {
        return chapter_index;
    }

    public void setLast_chapter_index(int last_chapter_index) {
        this.chapter_index = last_chapter_index;
    }

    public long getLast_update_book_datetime() {
        return last_update_book_datetime;
    }

    public void setLast_update_book_datetime(long last_update_book_datetime) {
        this.last_update_book_datetime = last_update_book_datetime;
    }

    public int getBook_add_shelf() {
        return book_add_shelf;
    }

    public void setBook_add_shelf(int book_add_shelf) {
        this.book_add_shelf = book_add_shelf;
    }

    public int getIs_recommend_book() {
        return is_recommend_book;
    }

    public void setIs_recommend_book(int is_recommend_book) {
        this.is_recommend_book = is_recommend_book;
    }

    @Override
    public String toString() {
        return "BookDetailResponse{" +
                "book_id=" + book_id +
                ", book_name='" + book_name + '\'' +
                ", book_image='" + book_image + '\'' +
                ", book_intro='" + book_intro + '\'' +
                ", book_share_url='" + book_share_url + '\'' +
                ", book_total_score='" + book_total_score + '\'' +
                ", book_total_reads=" + book_total_reads +
                ", book_finish_flag=" + book_finish_flag +
                ", book_is_vip=" + book_is_vip +
                ", book_content_byte=" + book_content_byte +
                ", book_chapter_total=" + book_chapter_total +
                ", book_price=" + book_price +
                ", book_updatetime=" + book_updatetime +
                ", author_id=" + author_id +
                ", author_name='" + author_name + '\'' +
                ", author_avatar='" + author_avatar + '\'' +
                ", last_update_chapter_id=" + last_update_chapter_id +
                ", last_update_chapter_datetime=" + last_update_chapter_datetime +
                ", last_update_chapter_title='" + last_update_chapter_title + '\'' +
                ", last_update_chapter_intro='" + last_update_chapter_intro + '\'' +
                ", lastReadTime=" + lastReadTime +
                ", chapter_id=" + chapter_id +
                ", chapter_index=" + chapter_index +
                ", last_update_book_datetime=" + last_update_book_datetime +
                ", book_add_shelf=" + book_add_shelf +
                ", is_recommend_book=" + is_recommend_book +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BookDetailResponse) {
            BookDetailResponse response = (BookDetailResponse) obj;
            return book_id == response.getBook_id();
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return String.valueOf(book_id).hashCode();
    }

}
