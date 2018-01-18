package com.spriteapp.booklibrary.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;


/**
 * 消息提醒
 */

public class MessageRemindDialog {
    View mView;
    ImageView close_dialog;
    TextView open_push;

    Activity context;
    Dialog mdialog;

    public MessageRemindDialog(final Activity context, View.OnClickListener onClickListener) {
        this.context = context;
        init(onClickListener);
    }

    public void init(View.OnClickListener onClickListener) {
        mdialog = new AlertDialog.Builder(context, R.style.Theme_follow_dialog).create();
        mdialog.show();

        mView = LayoutInflater.from(context).inflate(R.layout.message_remind_layout, null);
        mdialog.setCanceledOnTouchOutside(true);
        mdialog.setContentView(mView);

        open_push = (TextView) mView.findViewById(R.id.open_push);
        close_dialog = (ImageView) mView.findViewById(R.id.close_dialog);
        close_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdialog.dismiss();
            }
        });
        open_push.setOnClickListener(onClickListener);
    }

    public void dismiss() {
        if (mdialog.isShowing())
            mdialog.dismiss();
    }
}
