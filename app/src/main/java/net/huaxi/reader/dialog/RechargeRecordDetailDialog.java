package net.huaxi.reader.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tools.commonlibs.dialog.BaseDialog;
import com.tools.commonlibs.tools.DateUtils;

import net.huaxi.reader.R;
import net.huaxi.reader.bean.ChargeRecord;

import java.util.Date;

/**
 * Created by ZMW on 2016/3/17.
 */
public class RechargeRecordDetailDialog extends BaseDialog {
    ChargeRecord record;
    private TextView titleTv,contentTv;
    private Button okBtn,cancelBtn;
    private ImageView icon;
    private CommonDialogListener mCommonDialogListener;

    public void setCommonDialogListener(CommonDialogListener commonDialogListener) {
        mCommonDialogListener = commonDialogListener;
    }
//    public RechargeRecordDetailDialog (Activity activity,ChargeRecord record){
//        initDialog(activity,null, R.layout.dialog_recorddetail_dialog,TYPE_CENTER,false);
//        tvOrderNo= (TextView) mDialog.findViewById(R.id.recorddetail_order_no_textview);
//        tvTime= (TextView) mDialog.findViewById(R.id.recorddetail_order_time_textview);
//        tvMoney= (TextView) mDialog.findViewById(R.id.recorddetail_order_money_textview);
//        tvResult= (TextView) mDialog.findViewById(R.id.recorddetail_order_result_textview);
//        tvOrderNo.setText("单号：" + record.getOrsn());
//        tvTime.setText("时间：" + DateUtils.simpleDateFormat(new Date(record.getOrCdate() * 1000),
//                "yyyy.MM.dd HH:mm"));
//        tvMoney.setText("金额：" + record.getOrAmountPaid() + "元");
//        tvResult.setText( "结果：" +("1".equals(record.getOrState()) ? "成功" : "7".equals(record.getOrState()) ? "失败" : "其他"));
//    }
    public RechargeRecordDetailDialog(Activity activity, String title, ChargeRecord record, String ok, String cancel) {
        initDialog(activity, null, R.layout.dialog_common, BaseDialog.TYPE_CENTER, true);

        WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
        lp.flags = lp.flags | (WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//        mDialog.getWindow().setWindowAnimations(R.style.bottomPopupDialog);
        mDialog.getWindow().setAttributes(lp);
        icon = (ImageView) mDialog.findViewById(R.id.dialog_icon);
        icon.setImageResource(R.mipmap.dialog_recharge);
        titleTv = (TextView) mDialog.findViewById(R.id.dialog_title);
        titleTv.setText(title);
        contentTv = (TextView) mDialog.findViewById(R.id.dialog_content);
        contentTv.setText(getContent(record));
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


    private String getContent(ChargeRecord record) {
        StringBuffer _stringBuffer = new StringBuffer();
        _stringBuffer.append("单号：" + record.getOrsn() + "\n");
        long cdate=(record.getCdate()*1000);
        _stringBuffer.append("时间：" + DateUtils.simpleDateFormat(new Date(cdate),"yyyy-MM-dd HH:mm") + "\n");
        _stringBuffer.append("金额：" + record.getOrAmountPaid() + "元\n");
        _stringBuffer.append("结果：" + ("1".equals(record.getOrState()) ? "成功" : "7".equals(record.getOrState()) ? "失败" : "其他"));
        return _stringBuffer.toString();
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
