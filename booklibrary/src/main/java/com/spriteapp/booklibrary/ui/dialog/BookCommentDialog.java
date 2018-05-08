package com.spriteapp.booklibrary.ui.dialog;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.listener.ListenerManager;
import com.spriteapp.booklibrary.util.AppUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2018/1/18.
 */

public class BookCommentDialog extends BaseDialog {
    private Activity context;
    private TextView cancel_comment, send_comment, select_text_content;
    private EditText user_edit_content;
    private String content = "";
    private int num = 0;
    private int selectTextSection;

    public BookCommentDialog(Activity activity, String text, int selectTextSection) {
        this.context = activity;
        this.selectTextSection = selectTextSection;
        init(text);

    }

    public void init(String text) {
        initDialog(context, null, R.layout.book_comment_layout, BaseDialog.TYPE_BOTTOM, true);
        WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
        lp.flags = lp.flags | (WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        mDialog.getWindow().setWindowAnimations(R.style.bottomPopupDialog);
        mDialog.getWindow().setAttributes(lp);

        cancel_comment = (TextView) mDialog.findViewById(R.id.cancel_comment);
        send_comment = (TextView) mDialog.findViewById(R.id.send_comment);
        select_text_content = (TextView) mDialog.findViewById(R.id.select_text_content);
        if (TextUtils.isEmpty(text)) {
            select_text_content.setVisibility(View.GONE);
        } else {
            select_text_content.setText(text);
        }
        user_edit_content = (EditText) mDialog.findViewById(R.id.user_edit_content);
        user_edit_content.setHorizontallyScrolling(false);
        user_edit_content.setMaxLines(Integer.MAX_VALUE);

        cancel_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        send_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppUtil.isLogin(context) && ListenerManager.getInstance().getSendBookComment() != null) {
                    ListenerManager.getInstance().getSendBookComment().send(selectTextSection, user_edit_content.getText().toString().trim());
                    mDialog.dismiss();
                }
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
        }, 200);
    }


    public String getContent() {
        content = user_edit_content.getText().toString().trim();
        if (content == null) content = "";
        return content;
    }
}
