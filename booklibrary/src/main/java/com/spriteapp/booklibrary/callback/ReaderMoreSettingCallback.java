package com.spriteapp.booklibrary.callback;

import android.graphics.Typeface;

/**
 * Created by kuangxiaoguo on 2017/7/17.
 */

public interface ReaderMoreSettingCallback {

    void sendTextSize(int textSize);
    void sendFontStyle(Typeface typeface);
    void sengFontFormat(int format);
    void sengReaderBgColor(int color);
}
