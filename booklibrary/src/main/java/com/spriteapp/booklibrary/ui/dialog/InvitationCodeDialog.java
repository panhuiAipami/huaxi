package com.spriteapp.booklibrary.ui.dialog;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.base.BaseActivity;
import com.spriteapp.booklibrary.util.Util;

/**
 * Created by userfirst on 2018/3/8.
 */

public class InvitationCodeDialog extends BaseDialog {
    private Activity context;
    private ImageView colse_img;
    private LinearLayout code_layout;

    public InvitationCodeDialog(final Activity context) {
        this.context = context;
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void init() throws Exception {
        initDialog(context, null, R.layout.invitation_code_layout, TYPE_CENTER, true);
        code_layout = (LinearLayout) mDialog.findViewById(R.id.code_layout);
        colse_img = (ImageView) mDialog.findViewById(R.id.colse_img);
//        ViewGroup.LayoutParams layoutParams = code_layout.getLayoutParams();
//        layoutParams.height = (int) (layoutParams.width * 1.3);
//        code_layout.setLayoutParams(layoutParams);
//        Log.d("InvitationCodeDialog", "height===" + layoutParams.width * 1.3 + "width===" + layoutParams.width);
        initListener();
        mDialog.show();
    }

    public void initListener() throws Exception {
        colse_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void dismiss() {
        if (mDialog.isShowing())
            mDialog.dismiss();
    }
}
