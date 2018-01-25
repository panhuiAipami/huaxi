package com.spriteapp.booklibrary.listener;

/**
 * Created by Administrator on 2018/1/25.
 */

public interface DelBookShelf {
    /**
     * @param book_id 删除的书籍id
     * @param pos     下标
     * @param num     共删除几本书
     * @param act     1为添加2为删除
     */
    void del_book(int book_id, int pos, int num, int act);
}
