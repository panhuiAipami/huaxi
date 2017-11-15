package com.spriteapp.booklibrary.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;

import com.spriteapp.booklibrary.R;
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

    public static AlertDialog showCommonDialog(Context context, String message, final DialogListener dialogListener) {

        AlertDialog dialog = new AlertDialog.Builder(context, R.style.DialogTheme)
                .setTitle(R.string.book_reader_prompt_text)
                .setMessage(message)
                .setNegativeButton(R.string.book_reader_cancel_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (dialogListener != null) {
                            dialogListener.clickCancel();
                        }
                    }
                })
                .setPositiveButton(R.string.book_reader_sure,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (dialogListener != null) {
                                    dialogListener.clickSure();
                                }
                            }
                        })
                .create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
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
