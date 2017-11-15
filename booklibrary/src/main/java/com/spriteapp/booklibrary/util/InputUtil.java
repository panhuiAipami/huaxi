package com.spriteapp.booklibrary.util;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class InputUtil {

    public static void showSoftInput(Context context, View focusView) {
        if (context == null || focusView == null) {
            return;
        }
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(focusView, 0);
    }
}
