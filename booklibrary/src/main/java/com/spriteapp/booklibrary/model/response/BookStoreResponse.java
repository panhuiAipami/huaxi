package com.spriteapp.booklibrary.model.response;

import com.spriteapp.booklibrary.model.store.BookTypeResponse;
import com.spriteapp.booklibrary.model.store.HotSellResponse;

import java.io.Serializable;
import java.util.List;

/**
 * Created by kuangxiaoguo on 2017/7/28.
 */

public class BookStoreResponse implements Serializable {

    private String status;
    private int code;
    private List<HotSellResponse> classes;
    private List<BookTypeResponse> lists;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<HotSellResponse> getClasses() {
        return classes;
    }

    public void setClasses(List<HotSellResponse> classes) {
        this.classes = classes;
    }

    public void setLists(List<BookTypeResponse> lists) {
        this.lists = lists;
    }

    public List<BookTypeResponse> getLists() {
        return lists;
    }
}
