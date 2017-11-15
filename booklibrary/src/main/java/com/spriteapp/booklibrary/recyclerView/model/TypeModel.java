package com.spriteapp.booklibrary.recyclerView.model;

import com.spriteapp.booklibrary.recyclerView.decorate.Visitable;
import com.spriteapp.booklibrary.recyclerView.factory.TypeFactory;

/**
 * Created by kuangxiaoguo on 2017/7/28.
 */

public class TypeModel implements Visitable {

    @Override
    public int type(TypeFactory typeFactory) {
        return typeFactory.type(this);
    }
}
