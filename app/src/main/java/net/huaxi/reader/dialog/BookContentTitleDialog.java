package net.huaxi.reader.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.tools.commonlibs.dialog.BaseDialog;
import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.Utils;
import net.huaxi.reader.appinterface.DialogInterface;
import net.huaxi.reader.book.SharedPreferenceUtil;
import net.huaxi.reader.model.OpenBookSettingViewHelper;
import net.huaxi.reader.util.UMEventAnalyze;

import net.huaxi.reader.R;

import net.huaxi.reader.book.render.BookContentSettingListener;

/**
 * Created by Saud on 15/12/30.
 */
public class BookContentTitleDialog extends BaseDialog implements View.OnClickListener {

    private final Activity activity;
    private final OpenBookSettingViewHelper openBookSettingViewHelper;
    private final View fListen;
    private View flBack;
    private View flDownload;
    private View flShare;
    private DialogInterface dialogCloseListener;
    private View ll_bookconten_detail;
    private View ll_bookconten_share;
    private ImageView ivShareligth;
    private final static int BOOK_CONTENT_BACKGROUND_TYPE_4 = 4;
    private PopupWindow popupWindow;
    private BookContentSettingListener bookContentSettingListener;
    private boolean isNight;
    private View rlTitle;

    public BookContentTitleDialog(Activity activity, OpenBookSettingViewHelper openBookSettingViewHelper) {

        this.activity = activity;
        this.openBookSettingViewHelper = openBookSettingViewHelper;

        initDialog(activity, null, R.layout.dialog_bookcontent_title, BaseDialog.TYPE_TOP_CENTER_NOT_FOCUSABLE, true);
        mDialog.getWindow().setWindowAnimations(R.style.titlePopupDialog);
        if (Build.VERSION.SDK_INT > 18) {
            mDialog.getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        rlTitle = mDialog.findViewById(R.id.rl_status_bar_height);
        flBack = mDialog.findViewById(R.id.fl_bookcontent_title_back);
        flDownload = mDialog.findViewById(R.id.fl_bookcontent_title_download);
        fListen = mDialog.findViewById(R.id.fl_bookcontent_title_listen);
        flShare = mDialog.findViewById(R.id.fl_bookconten_title_share);
        ivShareligth = (ImageView) mDialog.findViewById(R.id.iv_bookconten_title_shareligth);
        flBack.setOnClickListener(this);
        flDownload.setOnClickListener(this);
        flShare.setOnClickListener(this);
        fListen.setOnClickListener(this);

    }


    public Dialog getDialog() {
        return mDialog;
    }

    public void show() {

        isNight = isNightStyle();
        setStyle(isNight);
        mDialog.show();
    }

    public void setStyle(boolean isNight) {
        if (isNight) {
            ivShareligth.setVisibility(View.VISIBLE);
        } else {
            ivShareligth.setVisibility(View.INVISIBLE);

        }
    }

    //获取当前是否为夜晚主题
    private boolean isNightStyle() {
        return BOOK_CONTENT_BACKGROUND_TYPE_4 == SharedPreferenceUtil.getInstanceSharedPreferenceUtil().getBookContentTheme();

    }

    public void setDialogCloseListener(DialogInterface dialogCloseListener) {
        this.dialogCloseListener = dialogCloseListener;
    }

    public void setChapterChengeListener(BookContentSettingListener bookContentSettingListener) {
        this.bookContentSettingListener = bookContentSettingListener;
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.fl_bookcontent_title_back:
                if (dialogCloseListener != null) {
                    dialogCloseListener.closeSettingWindow();
                }
                if (bookContentSettingListener != null) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            bookContentSettingListener.onBack();
                        }
                    }, 200);

                }
                break;
            case R.id.fl_bookcontent_title_download:
                if (dialogCloseListener != null) {
                    dialogCloseListener.closeSettingWindow();
                }
                if (bookContentSettingListener != null) {
                    bookContentSettingListener.onDownload();
                }
                break;
            case R.id.fl_bookcontent_title_listen:
                if (dialogCloseListener != null) {
                    dialogCloseListener.closeSettingWindow();
                }
                if (bookContentSettingListener != null) {
                    bookContentSettingListener.onListenBook();
                }
                break;
            case R.id.fl_bookconten_title_share:
                LogUtils.debug("菜单响应了");

                if (popupWindow == null) {
                    popupWindow = initShareView();
                }
                popupWindow.setOutsideTouchable(false);
                popupWindow.showAsDropDown(flShare, -Utils.dip2px(activity, 75), Utils.dip2px(activity, 12));
                break;
            case R.id.ll_bookconten_detail:
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
                if (openBookSettingViewHelper != null) {
                    openBookSettingViewHelper.closeSettingWindow();
                }
                if (bookContentSettingListener != null) {
                    bookContentSettingListener.onOpenDetail();
                }
                UMEventAnalyze.countEvent(activity, UMEventAnalyze.READPAGE_DETAIL);
                break;
            case R.id.ll_bookconten_share:
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
                if (openBookSettingViewHelper != null) {
                    openBookSettingViewHelper.closeSettingWindow();
                }

                if (openBookSettingViewHelper != null) {
                    openBookSettingViewHelper.showShareWindow();
                }
                UMEventAnalyze.countEvent(activity, UMEventAnalyze.READPAGE_SHARE);
                break;
        }

    }


    private PopupWindow initShareView() {
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_bookcontent_title_pop, null);
        view.setAlpha(0.8f);
        ll_bookconten_detail = view.findViewById(R.id.ll_bookconten_detail);
        ll_bookconten_share = view.findViewById(R.id.ll_bookconten_share);
        ll_bookconten_detail.setOnClickListener(this);
        ll_bookconten_share.setOnClickListener(this);
        PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setClippingEnabled(false);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setAnimationStyle(R.style.ShareDialogTheme);
        return popupWindow;
    }
}
