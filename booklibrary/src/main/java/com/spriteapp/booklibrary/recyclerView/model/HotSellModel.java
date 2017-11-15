package com.spriteapp.booklibrary.recyclerView.model;

import com.spriteapp.booklibrary.model.store.HotSellResponse;
import com.spriteapp.booklibrary.recyclerView.decorate.Visitable;
import com.spriteapp.booklibrary.recyclerView.factory.TypeFactory;

import java.util.List;

/**
 * Created by kuangxiaoguo on 2017/7/28.
 */

public class HotSellModel implements Visitable {

    private List<HotSellResponse> responseList;

    public List<HotSellResponse> getResponseList() {
        return responseList;
    }

    public void setResponseList(List<HotSellResponse> responseList) {
        this.responseList = responseList;
    }

    @Override
    public int type(TypeFactory typeFactory) {
        return typeFactory.type(this);
    }
}
