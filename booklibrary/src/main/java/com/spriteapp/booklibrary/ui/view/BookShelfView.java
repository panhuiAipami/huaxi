package com.spriteapp.booklibrary.ui.view;

import com.spriteapp.booklibrary.base.Base;
import com.spriteapp.booklibrary.base.BaseView;
import com.spriteapp.booklibrary.model.UserModel;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.model.response.BookStoreResponse;
import com.spriteapp.booklibrary.model.response.LoginResponse;

import java.util.List;

/**
 * Created by kuangxiaoguo on 2017/7/11.
 */

public interface BookShelfView extends BaseView<List<BookDetailResponse>> {

    void setLoginInfo(LoginResponse response);

    void setDeleteBookResponse();

    void setAddShelfResponse();

    void setBookDetail(BookDetailResponse data);

    void setBookStoreData(BookStoreResponse storeData);

    void setUserInfo(UserModel userInfo);
}
