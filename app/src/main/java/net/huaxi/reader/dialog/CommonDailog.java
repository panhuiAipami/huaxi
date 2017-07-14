package net.huaxi.reader.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.tools.commonlibs.dialog.BaseDialog;

import net.huaxi.reader.R;

/**
 * 弹窗通用模板
 * ryantao
 * 16/6/13.
 */
public class CommonDailog extends BaseDialog {

    private TextView titleTv,contentTv;
    private Button okBtn,cancelBtn;
    private CommonDialogListener mCommonDialogListener;

    public void setCommonDialogListener(CommonDialogListener commonDialogListener) {
        mCommonDialogListener = commonDialogListener;
    }

    /**
     * 构造函数
     * @param activity
     * @param title 标题
     * @param content 内容
     * @param ok  确认按钮文本
     * @param cancel    取消按钮文本
     * @param okClickListener   确认按钮回调
     * @param cancelClickListener   取消按钮回调
     */
    public CommonDailog(Activity activity, String title, String content, String ok, String cancel) {
        initDialog(activity, null, R.layout.dialog_common, BaseDialog.TYPE_CENTER, true);
        WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
        lp.flags = lp.flags | (WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//        mDialog.getWindow().setWindowAnimations(R.style.bottomPopupDialog);
        mDialog.getWindow().setAttributes(lp);
        titleTv = (TextView) mDialog.findViewById(R.id.dialog_title);
        titleTv.setText(title);
        contentTv = (TextView) mDialog.findViewById(R.id.dialog_content);
        contentTv.setText(content);
        okBtn = (Button) mDialog.findViewById(R.id.dialog_btn_ok);
        okBtn.setText(ok);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCommonDialogListener != null) {
                    mCommonDialogListener.ok(mDialog);
                }
            }
        });
        cancelBtn = (Button) mDialog.findViewById(R.id.dialog_btn_cancel);
        if (TextUtils.isEmpty(cancel)) {
            cancelBtn.setVisibility(View.GONE);
        }else {
            cancelBtn.setVisibility(View.VISIBLE);
            cancelBtn.setText(cancel);
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCommonDialogListener != null) {
                        mCommonDialogListener.cancel(mDialog);
                    }
                }
            });
        }
    }

    public interface CommonDialogListener{
        public void ok(Dialog dialog);
        public void cancel(Dialog dialog);
    }

    /**
     * 隐藏取消按钮
     */
    public void dismissCancel() {
        if (cancelBtn != null) {
            cancelBtn.setVisibility(View.GONE);
        }
    }


}
