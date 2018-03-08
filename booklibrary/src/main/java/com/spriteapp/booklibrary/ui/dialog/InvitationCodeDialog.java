package com.spriteapp.booklibrary.ui.dialog;

import android.app.Activity;
import android.view.View;

import com.spriteapp.booklibrary.R;

/**
 * Created by userfirst on 2018/3/8.
 */

public class InvitationCodeDialog extends BaseDialog {
    private Activity context;

    public InvitationCodeDialog(final Activity context) {
        this.context = context;
        init();
    }

    public void init() {
        initDialog(context, null, R.layout.invitation_code_layout, TYPE_CENTER, true);
        mDialog.show();
    }

    public void dismiss() {
        if (mDialog.isShowing())
            mDialog.dismiss();
    }
}
