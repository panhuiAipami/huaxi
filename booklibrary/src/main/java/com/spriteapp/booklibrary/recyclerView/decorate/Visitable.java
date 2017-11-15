package com.spriteapp.booklibrary.recyclerView.decorate;


import com.spriteapp.booklibrary.recyclerView.factory.TypeFactory;

/**
 * 所有ItemBean统一实现的接口
 */

public interface Visitable {
    int type(TypeFactory typeFactory);
}
