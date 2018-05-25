package com.spriteapp.booklibrary.util;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.base.BaseActivity;
import com.spriteapp.booklibrary.listener.BaseDialogListener;
import com.spriteapp.booklibrary.listener.DeleteBookListener;
import com.spriteapp.booklibrary.listener.DialogListener;
import com.spriteapp.booklibrary.listener.ReadDialogListener;

/**
 * Created by kuangxiaoguo on 2017/7/19.
 */

public class DialogUtil {

    private static ReadDialogListener mReadListener;
    private static DeleteBookListener mDeleteBookListener;

    public static AlertDialog getPayChapterDialog(final Context context, String title, String message,
                                                  String negativeText, String positiveText) {

        AlertDialog dialog = new AlertDialog.Builder(context, R.style.DialogTheme)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(negativeText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (mReadListener != null) {
                            mReadListener.clickCancelPay();
                        }
                    }
                })
                .setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mReadListener != null) {
                            mReadListener.clickPayChapter();
                        }
                    }
                })
                .create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return keyCode == KeyEvent.KEYCODE_BACK;
            }
        });
        return dialog;
    }

    public static AlertDialog getAutoSubDialog(Context context) {

        AlertDialog dialog = new AlertDialog.Builder(context, R.style.DialogTheme)
                .setTitle(R.string.book_reader_auto_buy)
                .setMessage(R.string.book_reader_auto_buy_prompt)
                .setNegativeButton(R.string.book_reader_hand_buy, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.book_reader_setting_text,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (mReadListener != null) {
                                    mReadListener.clickSetting();
                                }
                            }
                        })
                .create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return keyCode == KeyEvent.KEYCODE_BACK;
            }
        });
        return dialog;
    }

    public static AlertDialog getDeleteBookDialog(final Context context) {

        AlertDialog dialog = new AlertDialog.Builder(context, R.style.DialogTheme)
                .setTitle(R.string.book_reader_prompt)
                .setMessage(R.string.book_reader_delete_book_prompt)
                .setNegativeButton(R.string.book_reader_cancel_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.book_reader_sure,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (mDeleteBookListener != null) {
                                    mDeleteBookListener.deleteBook();
                                }
                            }
                        })
                .create();
        return dialog;
    }

    public static AlertDialog showAddShelfDialog(Context context, String message, final DialogListener dialogListener) {

        View view = View.inflate(context,R.layout.add_book_shelf_dialog_layout,null);
        final AlertDialog dialog = new AlertDialog.Builder(context, R.style.DialogTheme)
//                .setMessage(message)
                .setView(view).create( );
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  // 有白色背景，加这句代码
        params.width = BaseActivity.deviceWidth-ScreenUtil.dpToPxInt(100);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT ;
        dialog.getWindow().setAttributes(params);

        dialog.findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (dialogListener != null) {
                    dialogListener.clickCancel();
                }
            }
        });
        dialog.findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogListener != null) {
                    dialogListener.clickSure();
                }
            }
        });

        return dialog;
    }

    public static void setDialogListener(BaseDialogListener mDialogListener) {
        if (mDialogListener instanceof ReadDialogListener) {
            DialogUtil.mReadListener = (ReadDialogListener) mDialogListener;
        } else if (mDialogListener instanceof DeleteBookListener) {
            DialogUtil.mDeleteBookListener = (DeleteBookListener) mDialogListener;
        }
    }
}
