package com.spriteapp.booklibrary.listener;

import android.view.View;

/**
 * Created by Administrator on 2018/1/22.
 */

public interface SendBookComment {
    void send(int section,String text);
    void show(View v,int pid,int x, int y);

}
