package net.huaxi.reader;

import android.app.Dialog;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * Created by Administrator on 2017/11/15.
 */

public class BaseActivity extends FragmentActivity {
    private Dialog loadingDialog;
    protected Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                BaseActivity.this.handleMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    /**
     * 处理handler消息
     *
     * @param msg
     */
    protected void handleMessage(Message msg) throws Exception {


    }
    public void showLoadingDialog() {
        showLoadingDialog(BaseActivity.this.getString(R.string.text_loading));
    }

    public void showLoadingDialog(String str) {
        dismissDialog();

        LayoutInflater inflater = LayoutInflater.from(BaseActivity.this);
        View view = inflater.inflate(R.layout.dialog_loading_layout, null);
        TextView tv_text = (TextView) view.findViewById(R.id.loading_dialog_textview);
        tv_text.setText(str);

        loadingDialog = new Dialog(this, R.style.popBottomDialog);
        loadingDialog.show();
        loadingDialog.setContentView(view);
        loadingDialog.setCanceledOnTouchOutside(true);

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.flags = (lp.flags | WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(lp);
//        Window dialogWindow = loadingDialog.getWindow();
//        dialogWindow.setGravity(Gravity.CENTER);
//        android.view.WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
//        layoutParams.height = getWindowManager().getDefaultDisplay().getWidth() * 2 / 3;
//        layoutParams.width = getWindowManager().getDefaultDisplay().getWidth() * 2 / 3;
//        dialogWindow.setAttributes(layoutParams);
    }

    public void dismissDialog() {
        if (loadingDialog != null) {
            try {
                loadingDialog.dismiss();
                loadingDialog = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
