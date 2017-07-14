package com.tools.commonlibs.dialog;

import android.view.KeyEvent;
import android.view.MotionEvent;

public interface DialogTouchListener {
	public void touchHandle(MotionEvent event);
	public boolean keyHandle(int keyCode, KeyEvent event);
}
