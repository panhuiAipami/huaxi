package com.spriteapp.booklibrary.callback;

import android.graphics.Typeface;

/**
 * Created by kuangxiaoguo on 2017/7/17.
 */

public interface ReaderMoreSettingCallback {

    void sendTextSize(float textSize);
    void sendFontStyle(Typeface typeface);
    void sendFontFormat(int format);
    void sendReaderBgColor(int color);
    void sendPageMode(int pageMode);
    void sendProgressFormat(int progressFormat);
}
