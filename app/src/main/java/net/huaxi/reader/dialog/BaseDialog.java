package net.huaxi.reader.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.spriteapp.booklibrary.R;


/**
 * Created by Administrator on 2017/8/3.
 */

public class BaseDialog {
    public static final int TYPE_CENTER = 0;
    public static final int TYPE_BOTTOM = 1;
    public static final int TYPE_TOP_RIGHT_NOT_FOCUSABLE = 2;
    public static final int TYPE_CENTER_NOT_FOCUSABLE = 3;
    public static final int TYPE_CENTER_NOMENUCANCEL = 4;
    public static final int TYPE_LEFT_TOP_NOT_FOCUSABLE = 5;
    public static final int TYPE_TOP_CENTER_NOT_FOCUSABLE = 6;
    public static final int TYPE_TOP_RIGHT_DOWN_NOT_FOCUSABLE = 7;
    public static final int TYPE_BOTTOM_CENTER_NOT_FOCUSABLE = 8;
    protected CustomDialog mDialog;
    public boolean isMenuCancel = true;
    private Activity activity;

    /**
     * 自定义Dialog样式;
     *
     * @param act
     * @param inflateView
     * @param layoutResId
     * @param isCanceledOnTouchOutside
     * @param isCanTouchOutside
     * @param isDim
     *            背景灰暗
     * @param isFullScreen
     *            是否全屏
     */
    @SuppressWarnings("deprecation")
    public void initDialog(Activity act, View inflateView, int layoutResId,
                           boolean isCanceledOnTouchOutside, boolean isCanTouchOutside,
                           boolean isDim, boolean isFullScreen) {
        this.activity=act;
        // initDialog(act, inflateView, layoutResId, TYPE_BOTTOM);
        mDialog = new CustomDialog(act, R.style.popBottomDialog);
        if (inflateView == null) {
            mDialog.setContentView(layoutResId);
        } else {
            mDialog.setContentView(inflateView);
        }
        mDialog.setCanceledOnTouchOutside(isCanceledOnTouchOutside);
        mDialog.getWindow().setType(
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
        lp.dimAmount = 0.8f;
        if (isFullScreen) {
            WindowManager.LayoutParams actlp = (act).getWindow()
                    .getAttributes();
            lp.width = actlp.width;
        }
        // 设置背景是否透明
        if (!isDim) {
            // 背景透明
            lp.flags = lp.flags & (~WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        } else {
            // 背景暗淡
            lp.flags = (lp.flags | WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
        if (isCanTouchOutside) {
            lp.flags = lp.flags
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        }
        lp.gravity = android.view.Gravity.BOTTOM;
        if (Build.VERSION.SDK != null && Integer.valueOf(Build.VERSION.SDK) > 3) {
            // mDialog.getWindow().setWindowAnimations(R.style.CustomDialogTheme);
        }
        mDialog.getWindow().setAttributes(lp);
    }

    @SuppressWarnings("deprecation")
    public void initDialog(Activity act, View inflateView, int layoutResId,
                           int type, boolean isFullScreen) {
        this.activity=act;
        mDialog = new CustomDialog(act, R.style.popBottomDialog);
        if (inflateView == null) {
            mDialog.setContentView(layoutResId);
        } else {
            mDialog.setContentView(inflateView);
        }
        mDialog.setCanceledOnTouchOutside(true);

        WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
        if (isFullScreen) {
            WindowManager.LayoutParams actlp = act.getWindow().getAttributes();
            lp.width = actlp.width;
        }
        switch (type) {
            case TYPE_CENTER:
                break;
            case TYPE_CENTER_NOMENUCANCEL:
                isMenuCancel = false;
                break;
            case TYPE_BOTTOM_CENTER_NOT_FOCUSABLE:
                lp.flags = lp.flags & (~WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                lp.gravity = android.view.Gravity.BOTTOM
                        | android.view.Gravity.CENTER_HORIZONTAL;
                if (Build.VERSION.SDK != null
                        && Integer.valueOf(Build.VERSION.SDK) > 3) {
                    mDialog.getWindow().setWindowAnimations(
                            R.style.CustomDialogTheme);
                }
                break;
            case TYPE_BOTTOM:
                lp.flags = lp.flags & (~WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                lp.gravity = android.view.Gravity.BOTTOM;
                if (Build.VERSION.SDK != null
                        && Integer.valueOf(Build.VERSION.SDK) > 3) {
                    // 1.5以上可以自定义windows动画, 或者只有g3或是sense_ui有bug, 需要更多机型测试
                    // mDialog.getWindow().setWindowAnimations(
                    // R.style.CustomDialogTheme);
                }
                break;
            case TYPE_TOP_RIGHT_NOT_FOCUSABLE:
                lp.flags = lp.flags & (~WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                // lp.dimAmount = 0;
                lp.gravity = android.view.Gravity.TOP | android.view.Gravity.RIGHT;
                if (Build.VERSION.SDK != null
                        && Integer.valueOf(Build.VERSION.SDK) > 3) {
                    // mDialog.getWindow().setWindowAnimations(
                    // R.style.Animation_lampcordAnim);
                }
                break;
            case TYPE_TOP_RIGHT_DOWN_NOT_FOCUSABLE:
                lp.flags = lp.flags & (~WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                // lp.dimAmount = 0;
                lp.gravity = android.view.Gravity.TOP | android.view.Gravity.RIGHT;
                lp.y = dip2px(getContext(), 48);
                lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                if (Build.VERSION.SDK != null
                        && Integer.valueOf(Build.VERSION.SDK) > 3) {
                    // mDialog.getWindow().setWindowAnimations(
                    // R.style.Animation_scalepointAnim);
                }
                break;
            case TYPE_TOP_CENTER_NOT_FOCUSABLE:
                lp.flags = lp.flags & (~WindowManager.LayoutParams.FLAG_DIM_BEHIND) | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                // lp.dimAmount = 0;
                lp.gravity = android.view.Gravity.TOP
                        | android.view.Gravity.CENTER_HORIZONTAL;
                if (Build.VERSION.SDK != null
                        && Integer.valueOf(Build.VERSION.SDK) > 3) {
                    // mDialog.getWindow().setWindowAnimations(
                    // R.style.Animation_topbarAnim);
                    mDialog.getWindow().setWindowAnimations(
                            R.style.CustomDialogTheme);
                }
                break;
            case TYPE_CENTER_NOT_FOCUSABLE:
                // lp.flags = lp.flags &
                // (~WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                // | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                // lp.gravity = android.view.Gravity.CENTER;
                // if (Build.VERSION.SDK != null
                // && Integer.valueOf(Build.VERSION.SDK) > 3) {
                // mDialog.getWindow().setWindowAnimations(
                // R.style.Animation_orientationLockAnim);
                // }
                break;
            case TYPE_LEFT_TOP_NOT_FOCUSABLE:
                lp.flags = lp.flags & (~WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                lp.gravity = android.view.Gravity.TOP | android.view.Gravity.LEFT;
                break;
        }
        mDialog.getWindow().setAttributes(lp);
    }

    public int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public void setGravity(int gravity) {
        WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
        lp.gravity = gravity;
    }

    public void setOnCancelListener(DialogInterface.OnCancelListener clistener) {
        mDialog.setOnCancelListener(clistener);

    }

    public void setOnDismissListener(
            DialogInterface.OnDismissListener clistener) {
        mDialog.setOnDismissListener(clistener);

    }

    public void setCanceledOnTouchOutside(boolean cancel) {
        mDialog.setCanceledOnTouchOutside(cancel);
    }

    public void setCancelable(boolean cancel) {
        mDialog.setCancelable(cancel);
    }

    public void show() {

        mDialog.show();
    }

    public void dismiss() {
        mDialog.dismiss();

    }

    public void hide() {
        mDialog.hide();
    }

    public void cancel() {
        mDialog.cancel();
    }

    public Context getContext() {
        return mDialog.getContext();
    }

    public boolean isShowing() {
        return mDialog.isShowing();
    }

    public Object getTouchListener() {
        return mDialog.getTouchListener();
    }

    public void setTouchListener(DialogTouchListener touchListener) {
        mDialog.setTouchListener(touchListener);
    }

    Object object = null;

    public void setTag(Object obj) {
        object = obj;
    }

    public Object getTag() {
        return object;
    }

    public class CustomDialog extends Dialog {

        private DialogTouchListener touchListener = null;

        public CustomDialog(Context context) {
            super(context);
        }

        public CustomDialog(Context context, int theme) {
            super(context, theme);
        }

        public Object getTouchListener() {
            return touchListener;
        }

        public void setTouchListener(DialogTouchListener touchListener) {
            this.touchListener = touchListener;
        }

        public boolean onKeyDown(int keyCode, KeyEvent event) {
            if (null != touchListener
                    && touchListener.keyHandle(keyCode, event)) {
                return true;
            }
            switch (keyCode) {
                case KeyEvent.KEYCODE_MENU:
                    // if(BaseDialog.this instanceof ManageDialog){
                    // ((ManageDialog)BaseDialog.this).doMenuOnClick();
                    // return false;
                    // }
                    if (isMenuCancel && isShowing()) {
                        cancel();
                        return true;
                    }
                    break;
            }
            return super.onKeyDown(keyCode, event);
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
            if (touchListener != null) {
                touchListener.touchHandle(ev);
            }
            return super.dispatchTouchEvent(ev);
        }

    }

}
