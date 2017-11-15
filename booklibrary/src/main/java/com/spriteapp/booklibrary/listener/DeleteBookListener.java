package com.spriteapp.booklibrary.listener;

/**
 * Created by kuangxiaoguo on 2017/7/21.
 */

public interface DeleteBookListener extends BaseDialogListener{

    void showDeleteDialog(int bookId, int position);

    void deleteBook();
}
