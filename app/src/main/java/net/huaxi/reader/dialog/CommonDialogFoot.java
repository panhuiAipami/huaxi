package net.huaxi.reader.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.tools.commonlibs.dialog.BaseDialog;
import com.tools.commonlibs.tools.StringUtils;

import net.huaxi.reader.R;

/**
 * Function:    底部通用Dialog;
 * Author:      taoyf
 * Create:
 * Modify:      2016-09-02
 */
public class CommonDialogFoot extends BaseDialog {

    private TextView tipTv;
    private Button okBtn;
    private Button cancelBtn;

    public CommonDialogFoot(Activity activity,String tips,String ok,String cancel) {
        initDialog(activity, null, R.layout.dialog_bottom_layout, BaseDialog.TYPE_BOTTOM, true);
        WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
        lp.flags = lp.flags | (WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        tipTv = (TextView) getDialog().findViewById(R.id.dialog_foot_tips);
        okBtn = (Button) getDialog().findViewById(R.id.dialog_foot_logout);
        if (StringUtils.isEmpty(ok)) {
            okBtn.setVisibility(View.GONE);
        }else{
            okBtn.setVisibility(View.VISIBLE);
        }
        okBtn.setText(ok);
        cancelBtn = (Button) getDialog().findViewById(R.id.dialog_foot_cancel);
        if (StringUtils.isEmpty(cancel)) {
            cancelBtn.setVisibility(View.GONE);
        }else{
            cancelBtn.setVisibility(View.VISIBLE);
        }
        cancelBtn.setText(cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.cancel();
            }
        });
        tipTv = (TextView) mDialog.findViewById(R.id.dialog_foot_tips);
        tipTv.setText(tips);


        mDialog.getWindow().setWindowAnimations(R.style.bottomPopupDialog);
        mDialog.getWindow().setAttributes(lp);
    }

    public Dialog getDialog() {
        return mDialog;
    }

    public Button getOkButton(){
        return okBtn;
    }

    public Button getCancelButton(){
        return cancelBtn;
    }



}
