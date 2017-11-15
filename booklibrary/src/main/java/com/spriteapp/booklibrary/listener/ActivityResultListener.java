package com.spriteapp.booklibrary.listener;

import android.content.Intent;

/**
 * Created by kuangxiaoguo on 2017/8/19.
 */

public interface ActivityResultListener {

    void onActivityResult(int requestCode, int resultCode, Intent data);
}
