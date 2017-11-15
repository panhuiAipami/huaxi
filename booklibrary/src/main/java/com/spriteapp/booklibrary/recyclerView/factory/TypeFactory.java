package com.spriteapp.booklibrary.recyclerView.factory;

import android.content.Context;
import android.view.View;

import com.spriteapp.booklibrary.model.BookList;
import com.spriteapp.booklibrary.recyclerView.model.HotSellModel;
import com.spriteapp.booklibrary.recyclerView.model.StoreUser;
import com.spriteapp.booklibrary.recyclerView.model.TypeModel;
import com.spriteapp.booklibrary.recyclerView.viewholder.BaseViewHolder;


public interface TypeFactory {
    //  定义所有的返回类型
    int type(StoreUser storeUser);

    int type(BookList detail, boolean isMyShelf);

    int type(TypeModel typeModel);

    int type(HotSellModel hotSellModel);

    BaseViewHolder createViewHolder(int type, View itemView, Context context);
}

