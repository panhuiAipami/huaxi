package com.spriteapp.booklibrary.ui.dialog;

import android.app.Activity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;


/**
 * Created by Administrator on 2017/11/6.
 */

public class AppUpdateDialog extends BaseDialog{
    private Activity activity;
    private String text;

    private TextView tv_update;
    private TextView tv_content;
    private ImageView app_dialog_colse;


    public AppUpdateDialog(final Activity activity, String text, final String url) {
        this.activity = activity;
        this.text = text;
        initDialog(activity, null, R.layout.app_update_dialog, BaseDialog.TYPE_CENTER, true);
        WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
        lp.flags = lp.flags | (WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        mDialog.getWindow().setWindowAnimations(R.style.bottomPopupDialog);
        mDialog.getWindow().setAttributes(lp);
        setCancelable(false);

        tv_update = (TextView) mDialog.findViewById(R.id.tv_update);
        tv_content = (TextView) mDialog.findViewById(R.id.tv_content);
        app_dialog_colse = (ImageView) mDialog.findViewById(R.id.app_dialog_colse);

        tv_content.setText(text);

        tv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                AppDownloadDialog _appDownloadDialog = new AppDownloadDialog(activity, url);
                _appDownloadDialog.show();
            }
        });
        app_dialog_colse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


    }

}
