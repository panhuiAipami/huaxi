package net.huaxi.reader.dialog;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.tools.commonlibs.dialog.BaseDialog;

import net.huaxi.reader.R;


/**
 * @Description: [详情页的简介窗口]
 * @Author: [Saud]
 * @CreateDate: [16/1/20 17:20]
 * @UpDate: [16/1/20 17:20]
 * @Version: [v1.0]
 */
public class DetailAboutDialog extends BaseDialog {


    private final TextView tv_detail_count;
    private View ll_detail_about_btn;
    private final View iv_detail_about_back;

    public DetailAboutDialog(Activity activity, String str) {
        initDialog(activity, null, R.layout.dialog_detail_about, BaseDialog.TYPE_CENTER_NOMENUCANCEL, true);
        mDialog.getWindow().setWindowAnimations(R.style.RightPopupDialog);
        WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;

        ll_detail_about_btn = mDialog.findViewById(R.id.ll_detail_about_btn);
        ll_detail_about_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        tv_detail_count = (TextView) mDialog.findViewById(R.id.tv_detail_count);
        tv_detail_count.setText(str);
        iv_detail_about_back = mDialog.findViewById(R.id.iv_detail_about_back);
//        iv_detail_about_back.setAlpha(0.5f);
        iv_detail_about_back.setVisibility(View.GONE);
    }


    public void show() {
        mDialog.show();
    }


}
