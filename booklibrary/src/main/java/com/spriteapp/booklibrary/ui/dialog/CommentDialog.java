package com.spriteapp.booklibrary.ui.dialog;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2018/1/18.
 */

public class CommentDialog extends BaseDialog {
    private Activity context;
    private TextView cancel_comment, send_comment, user_name;
    private EditText user_edit_content;
    String content = "";
    int num = 0;

    public CommentDialog(Activity activity) {
        this.context = activity;
        init();

    }

    public void init() {
        initDialog(context, null, R.layout.new_comment_layout, BaseDialog.TYPE_BOTTOM, true);
        WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
        lp.flags = lp.flags | (WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        mDialog.getWindow().setWindowAnimations(R.style.bottomPopupDialog);
        mDialog.getWindow().setAttributes(lp);


        cancel_comment = (TextView) mDialog.findViewById(R.id.cancel_comment);
        send_comment = (TextView) mDialog.findViewById(R.id.send_comment);
        user_name = (TextView) mDialog.findViewById(R.id.user_name);
        user_edit_content = (EditText) mDialog.findViewById(R.id.user_edit_content);

        cancel_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        user_edit_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                num = user_edit_content.getText().toString().trim().length();
                if (num == 0) {//不可点击
                    send_comment.setEnabled(false);
                } else {//可点击
                    send_comment.setEnabled(true);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void dismiss() {
        if (mDialog.isShowing())
            mDialog.dismiss();
    }

    public void show() {
        mDialog.show();
        user_edit_content.requestFocus();

        final InputMethodManager inputmanager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        new Timer().schedule(new TimerTask() {
            public void run() {
                inputmanager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, 50);
    }

    public void setUserName(String name) {//回复某人
        if (user_name != null && name != null)
            user_name.setText(name);
    }

    public String getContent() {
        content = user_edit_content.getText().toString().trim();
        if (content == null) content = "";
        return content;
    }

    public TextView getSendText() {
        return send_comment;
    }

    public void clearText() {
        user_edit_content.setText("");
    }
}
