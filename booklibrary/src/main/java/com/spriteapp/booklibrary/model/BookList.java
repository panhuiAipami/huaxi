package com.spriteapp.booklibrary.model;

import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.model.store.BookTypeResponse;
import com.spriteapp.booklibrary.recyclerView.decorate.Visitable;
import com.spriteapp.booklibrary.recyclerView.factory.TypeFactory;

import java.io.Serializable;
import java.util.List;

/**
 * Created by kuangxiaoguo on 2017/7/28.
 */

public class BookList implements Serializable, Visitable {

    private List<BookDetailResponse> detailResponseList;
    private BookTypeResponse typeResponse;
    private boolean isMyShelf;

    public List<BookDetailResponse> getDetailResponseList() {
        return detailResponseList;
    }

    public void setDetailResponseList(List<BookDetailResponse> detailResponseList) {
        this.detailResponseList = detailResponseList;
    }

    public boolean isMyShelf() {
        return isMyShelf;
    }

    public void setMyShelf(boolean myShelf) {
        isMyShelf = myShelf;
    }

    public BookTypeResponse getTypeResponse() {
        return typeResponse;
    }

    public void setTypeResponse(BookTypeResponse typeResponse) {
        this.typeResponse = typeResponse;
    }

    @Override
    public int type(TypeFactory typeFactory) {
        return typeFactory.type(this, isMyShelf);
    }
}
