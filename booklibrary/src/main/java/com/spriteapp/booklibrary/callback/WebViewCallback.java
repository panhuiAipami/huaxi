package com.spriteapp.booklibrary.callback;

/**
 * Created by kuangxiaoguo on 2017/7/18.
 */

public interface WebViewCallback {
    void getWeChatPay(String productId);

    void getAliPay(String productId);

    void addBookToShelf(int bookId);

    void freeRead(int bookId, int chapterId);
}
