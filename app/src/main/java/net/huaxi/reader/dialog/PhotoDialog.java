package net.huaxi.reader.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.WindowManager;

import com.tools.commonlibs.dialog.BaseDialog;

import net.huaxi.reader.R;

/**
 * Created by ZMW on 2015/12/23.
 */
public class PhotoDialog extends BaseDialog {
    public PhotoDialog(Activity activity) {
        initDialog(activity, null, R.layout.dialog_getphoto, BaseDialog.TYPE_BOTTOM, true);
        WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
        lp.flags = lp.flags | (WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        mDialog.getWindow().setWindowAnimations(R.style.bottomPopupDialog);
        mDialog.getWindow().setAttributes(lp);
        mDialog.findViewById(R.id.dialog_getphoto_cancle_textview).setOnClickListener(new View
                .OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.cancel();
            }
        });

    }

    public Dialog getDialog() {
        return mDialog;
    }
}
