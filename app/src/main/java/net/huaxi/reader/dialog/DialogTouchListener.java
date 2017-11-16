package net.huaxi.reader.dialog;

import android.view.KeyEvent;
import android.view.MotionEvent;

/**
 * Created by Administrator on 2017/8/3.
 */

public interface DialogTouchListener {
    public void touchHandle(MotionEvent event);
    public boolean keyHandle(int keyCode, KeyEvent event);
}
