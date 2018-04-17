package com.spriteapp.booklibrary.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.base.BaseActivity;
import com.spriteapp.booklibrary.util.Util;


/**
 * 求好评弹窗
 */

public class GoToAppStoreDialog {
    View mView;
    TextView button1, button2, button3;

    Activity context;
    Dialog mdialog;

    public GoToAppStoreDialog(final Activity context, View.OnClickListener onClickListener) {
        this.context = context;
        init(onClickListener);
    }

    public void init(final View.OnClickListener onClickListener) {
        mdialog = new AlertDialog.Builder(context, R.style.Theme_follow_dialog).create();
        mdialog.show();

        mView = LayoutInflater.from(context).inflate(R.layout.remind_dialog_layout, null);
        mdialog.setCanceledOnTouchOutside(false);
        mdialog.setContentView(mView);

        WindowManager.LayoutParams lp = mdialog.getWindow().getAttributes();
        lp.width = BaseActivity.deviceWidth - Util.dp2px(context, 125);
        mdialog.getWindow().setAttributes(lp);

        button1 = (TextView) mView.findViewById(R.id.button1);
        button2 = (TextView) mView.findViewById(R.id.button2);
        button3 = (TextView) mView.findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mdialog != null)
                    mdialog.dismiss();
            }
        });
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onClick(v);
//                context.startActivity(new Intent(context, Back.class));
                if (mdialog != null)
                    mdialog.dismiss();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onClick(v);
                if (mdialog != null)
                    mdialog.dismiss();
            }
        });
    }

}
