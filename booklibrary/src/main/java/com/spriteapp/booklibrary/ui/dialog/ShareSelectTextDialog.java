package com.spriteapp.booklibrary.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;

import com.spriteapp.booklibrary.R;

/**
 * Created by panhui on 2018/4/26.
 */

public class ShareSelectTextDialog extends Dialog {
    public ShareSelectTextDialog(@NonNull Context context) {
        this(context, R.style.Dialog_Fullscreen);

    }
    public ShareSelectTextDialog(Context context,int style){
        super(context,style);
        View view = LayoutInflater.from(context).inflate(R.layout.reader_text_share_layout, null);
        setContentView(view);
    }
}
