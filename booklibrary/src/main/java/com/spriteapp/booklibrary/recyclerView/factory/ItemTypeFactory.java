package com.spriteapp.booklibrary.recyclerView.factory;

import android.content.Context;
import android.view.View;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.model.BookList;
import com.spriteapp.booklibrary.model.UserModel;
import com.spriteapp.booklibrary.recyclerView.model.HotSellModel;
import com.spriteapp.booklibrary.recyclerView.model.StoreUser;
import com.spriteapp.booklibrary.recyclerView.model.TypeModel;
import com.spriteapp.booklibrary.recyclerView.viewholder.BaseViewHolder;
import com.spriteapp.booklibrary.recyclerView.viewholder.BookViewHolder;
import com.spriteapp.booklibrary.recyclerView.viewholder.HotSellViewHolder;
import com.spriteapp.booklibrary.recyclerView.viewholder.ShelfViewHolder;
import com.spriteapp.booklibrary.recyclerView.viewholder.TypeViewHolder;
import com.spriteapp.booklibrary.recyclerView.viewholder.UserViewHolder;


public class ItemTypeFactory implements TypeFactory {
    //  将id作为type传入adapter
    private static final int USER_ACCOUNT_LAYOUT = R.layout.book_reader_store_user_account_item;
    private static final int SHELF_LAYOUT = R.layout.book_reader_store_my_shelf_item;
    private static final int HOT_SELL_LAYOUT = R.layout.book_reader_store_hot_sell_item;
    private static final int TYPE_LAYOUT = R.layout.book_reader_store_type_item;
    private static final int BOOK_LAYOUT = R.layout.book_reader_store_book_item;

    @Override
    public int type(StoreUser storeUser) {
        return USER_ACCOUNT_LAYOUT;
    }

    @Override
    public int type(BookList detail, boolean isMyShelf) {
        return isMyShelf ? SHELF_LAYOUT : BOOK_LAYOUT;
    }

    @Override
    public int type(TypeModel typeModel) {
        return TYPE_LAYOUT;
    }

    @Override
    public int type(HotSellModel hotSellModel) {
        return HOT_SELL_LAYOUT;
    }

    @Override
    public BaseViewHolder createViewHolder(int type, View itemView, Context context) {
        if (type == USER_ACCOUNT_LAYOUT) {
            return new UserViewHolder(itemView, context);
        } else if (type == SHELF_LAYOUT) {
            return new ShelfViewHolder(itemView, context);
        } else if (type == HOT_SELL_LAYOUT) {
            return new HotSellViewHolder(itemView, context);
        } else if (type == TYPE_LAYOUT) {
            return new TypeViewHolder(itemView, context);
        } else if (type == BOOK_LAYOUT) {
            return new BookViewHolder(itemView, context);
        }
        return null;
    }
}
