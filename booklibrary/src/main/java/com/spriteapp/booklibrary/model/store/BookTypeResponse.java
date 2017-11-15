package com.spriteapp.booklibrary.model.store;

import com.spriteapp.booklibrary.model.response.BookDetailResponse;

import java.util.List;

/**
 * 热门推荐 新书抢先等
 * Created by kuangxiaoguo on 2017/7/29.
 */

public class BookTypeResponse {

    private String name;
    private String url;
    private int count;
    private List<BookDetailResponse> lists;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<BookDetailResponse> getLists() {
        return lists;
    }

    public void setLists(List<BookDetailResponse> lists) {
        this.lists = lists;
    }
}
