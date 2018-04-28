package com.spriteapp.booklibrary.base;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.config.HuaXiSDK;
import com.spriteapp.booklibrary.util.StatusBarUtil;
import com.spriteapp.booklibrary.widget.loading.CustomDialog;
import com.umeng.message.PushAgent;


public abstract class BaseActivity extends AppCompatActivity {

    private CustomDialog dialog;//进度条
    public static int deviceWidth;
    public static int deviceHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(getLayoutResId());
            WindowManager mWindowManager = BaseActivity.this.getWindowManager();
            Display display = mWindowManager.getDefaultDisplay();
            PushAgent.getInstance(this).onAppStart();
            deviceWidth = display.getWidth();
            deviceHeight = display.getHeight();
            setStatusBarColor();
            findViewId();
            initData();
            configViews();
        } catch (Exception e) {
            e.printStackTrace();
            Intent i = getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage(getBaseContext().getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }

    }

    private void setStatusBarColor() {
        StatusBarUtil.setWindowStatusBarColor(this);
    }

    public abstract int getLayoutResId() throws Exception;

    public abstract void initData() throws Exception;

    public abstract void configViews() throws Exception;

    public abstract void findViewId() throws Exception;

    protected void gone(final View... views) {
        if (views != null && views.length > 0) {
            for (View view : views) {
                if (view != null) {
                    view.setVisibility(View.GONE);
                }
            }
        }
    }

    protected void visible(final View... views) {
        if (views != null && views.length > 0) {
            for (View view : views) {
                if (view != null) {
                    view.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    protected void setSelectFalse(View... views) {
        if (views != null && views.length > 0) {
            for (View view : views) {
                if (view != null) {
                    view.setSelected(false);
                }
            }
        }
    }

    protected void hideStatusBar() {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
    }

    protected void showStatusBar() {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
    }

    protected boolean isVisible(View view) {
        return view.getVisibility() == View.VISIBLE;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        HuaXiSDK.getInstance().onActivityResult(requestCode, resultCode, data);
    }

    public void showDialog() {
        try {
            if (dialog == null) {
                dialog = CustomDialog.instance(this);
                dialog.setCancelable(true);
            }
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
    }

    Dialog loadingDialog;

    public void showReaderDialog(String str) {
        dismissReaderDialog();

        LayoutInflater inflater = LayoutInflater.from(BaseActivity.this);
        View view = inflater.inflate(R.layout.reader_loading_layout, null);
        TextView tv_text = (TextView) view.findViewById(R.id.loading_dialog_textview);
        if (!TextUtils.isEmpty(str))
            tv_text.setText(str);
        ImageView iv = (ImageView) view.findViewById(R.id.image_anim);
        AnimationDrawable animationDrawable = (AnimationDrawable) iv.getDrawable();
        animationDrawable.start();
        loadingDialog = new Dialog(this, R.style.Reader_Dialog_Fullscreen);
        loadingDialog.show();
        loadingDialog.setContentView(view);
        loadingDialog.setCanceledOnTouchOutside(false);

    }

    public void dismissReaderDialog() {
        if (loadingDialog != null) {
            try {
                loadingDialog.dismiss();
                loadingDialog = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
