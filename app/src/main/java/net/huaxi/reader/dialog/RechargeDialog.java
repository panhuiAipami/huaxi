package net.huaxi.reader.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.tools.commonlibs.dialog.BaseDialog;

import net.huaxi.reader.R;

/**
 * Created by Administrator on 2017/7/17.
 */

public class RechargeDialog extends BaseDialog {
    public RechargeDialog(Activity activity) {
        initDialog(activity, null, R.layout.dialog_getrecharge, BaseDialog.TYPE_BOTTOM, true);
        WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
        lp.flags = lp.flags | (WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        mDialog.getWindow().setWindowAnimations(R.style.bottomPopupDialog);
        mDialog.getWindow().setAttributes(lp);
        TextView text1 = (TextView) mDialog.findViewById(R.id.dialgo_Recharge1);
        TextView text2 = (TextView) mDialog.findViewById(R.id.dialgo_Recharge2);
        TextView text3 = (TextView) mDialog.findViewById(R.id.dialgo_Recharge3);
        SpannableString styledText1 = new SpannableString("30元\n+赠0花瓣\n3000花贝");
        styledText1.setSpan(new TextAppearanceSpan(getContext(), R.style.Orange_color_big), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        styledText1.setSpan(new TextAppearanceSpan(getContext(), R.style.Orange_color_small), 4, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        styledText1.setSpan(new TextAppearanceSpan(getContext(), R.style.Gray_color), 10, 16, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        text1.setText(styledText1, TextView.BufferType.SPANNABLE);
        SpannableString styledText2 = new SpannableString("50元\n+赠888花瓣\n5000花贝");
        styledText2.setSpan(new TextAppearanceSpan(getContext(), R.style.Orange_color_big), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        styledText2.setSpan(new TextAppearanceSpan(getContext(), R.style.Orange_color_small), 4, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        styledText2.setSpan(new TextAppearanceSpan(getContext(), R.style.Gray_color), 12, 18, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        text2.setText(styledText2, TextView.BufferType.SPANNABLE);
        SpannableString styledText3 = new SpannableString("98元\n+赠3333花瓣\n10000花贝");
        styledText3.setSpan(new TextAppearanceSpan(getContext(), R.style.Orange_color_big), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        styledText3.setSpan(new TextAppearanceSpan(getContext(), R.style.Orange_color_small), 4, 13, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        styledText3.setSpan(new TextAppearanceSpan(getContext(), R.style.Gray_color), 13, 20, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        text3.setText(styledText3, TextView.BufferType.SPANNABLE);
        mDialog.findViewById(R.id.recharge_cancel).setOnClickListener(new View.OnClickListener() {
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
