package com.spriteapp.booklibrary.model;

/**
 * Created by kuangxiaoguo on 2017/7/24.
 */

public class UpdateProgressModel {

    private int bookId;
    private int chapterId;
    private int chapterIndex;
    private int chapterTotal;

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getChapterId() {
        return chapterId;
    }

    public void setChapterId(int chapterId) {
        this.chapterId = chapterId;
    }

    public int getChapterIndex() {
        return chapterIndex;
    }

    public void setChapterIndex(int chapterIndex) {
        this.chapterIndex = chapterIndex;
    }

    public int getChapterTotal() {
        return chapterTotal;
    }

    public void setChapterTotal(int chapterTotal) {
        this.chapterTotal = chapterTotal;
    }
}
