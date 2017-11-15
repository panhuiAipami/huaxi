package com.spriteapp.booklibrary.recyclerView.model;

import com.spriteapp.booklibrary.model.UserModel;
import com.spriteapp.booklibrary.recyclerView.decorate.Visitable;
import com.spriteapp.booklibrary.recyclerView.factory.TypeFactory;

import java.io.Serializable;

/**
 * Created by kuangxiaoguo on 2017/7/29.
 */

public class StoreUser implements Serializable, Visitable{

    private UserModel userModel;

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    @Override
    public int type(TypeFactory typeFactory) {
        return typeFactory.type(this);
    }
}
