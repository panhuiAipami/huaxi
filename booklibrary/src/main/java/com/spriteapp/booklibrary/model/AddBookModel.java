package com.spriteapp.booklibrary.model;

/**
 * Created by kuangxiaoguo on 2017/7/21.
 */

public class AddBookModel {

    private int bookId;
    private int chapterId;
    private boolean isAddShelf = false;

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

    public boolean isAddShelf() {
        return isAddShelf;
    }

    public void setAddShelf(boolean addShelf) {
        isAddShelf = addShelf;
    }
}
