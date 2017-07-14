package net.huaxi.reader.model;

import android.app.Activity;
import android.content.Intent;

import net.huaxi.reader.appinterface.DialogInterface;
import net.huaxi.reader.book.ChapterClickListener;
import net.huaxi.reader.util.UMEventAnalyze;
import net.huaxi.reader.util.listener.ScreenUtils;
import net.huaxi.reader.appinterface.MenuChangeListener;
import net.huaxi.reader.book.BookContentModel;
import net.huaxi.reader.book.render.BookContentSettingListener;
import net.huaxi.reader.dialog.BookContentBottomDialog;
import net.huaxi.reader.dialog.BookContentBottomSettingDialog;
import net.huaxi.reader.dialog.BookContentCatalogueDialog;
import net.huaxi.reader.dialog.BookContentShareDialog;
import net.huaxi.reader.dialog.BookContentTitleDialog;

/**
 * @Description: [打开读书页面底部和顶部设置的帮助类]
 * @Author: [Saud]
 * @CreateDate: [15/12/30 21:30]
 * @UpDate: [15/12/30 21:30]
 * @Version: [v1.0]
 */
public class OpenBookSettingViewHelper implements DialogInterface {


    private BookContentModel bookContentModel;
    private Activity activity;
    private BookContentBottomDialog bottomDialog;
    private BookContentTitleDialog titltDialog;
    private BookContentBottomSettingDialog settingDialog;
    private BookContentSettingListener bookContentSettingListener;
    private BookContentCatalogueDialog bookContentCatalogueDialog;
    private ChapterClickListener chapterClickListener;
    private BookContentShareDialog shareDialog;
    private MenuChangeListener menuChangeListener;


    public OpenBookSettingViewHelper(Activity activity, BookContentModel bookContentModel) {
        this.activity = activity;
        this.bookContentModel=bookContentModel;
    }


    public void showSettingWindow() {

        if (bottomDialog == null) {
            bottomDialog = new BookContentBottomDialog(activity);
            bottomDialog.setChapterChengeListener(bookContentSettingListener);
        }
        if (titltDialog == null) {
            titltDialog = new BookContentTitleDialog(activity, this);
            titltDialog.setChapterChengeListener(bookContentSettingListener);
        }
        bottomDialog.setDialogCloseListener(this);
        titltDialog.setDialogCloseListener(this);
        this.bottomDialog.show();
        this.titltDialog.show();
        if (menuChangeListener != null && isAnyonWindowShowing()) {
            menuChangeListener.changeCallBack(true);
        }
        ScreenUtils.full(activity, false);
    }

    public void showThemeSettingWindow() {
        //阅读页-设置
        UMEventAnalyze.countEvent(activity,UMEventAnalyze.READPAGE_SETTINGS);
        if (settingDialog == null) {
            settingDialog = new BookContentBottomSettingDialog(activity,bookContentModel);
            settingDialog.setChapterChengeListener(bookContentSettingListener);
        }
        this.settingDialog.show();
        if (menuChangeListener != null && isAnyonWindowShowing()) {
            menuChangeListener.changeCallBack(true);
        }
    }

    @Override
    public void closeSettingWindow() {
        if (bottomDialog != null && bottomDialog.isShowing()) {
            bottomDialog.dismiss();
        }
        if (titltDialog != null && titltDialog.isShowing()) {
            titltDialog.dismiss();
        }
        if (settingDialog != null && settingDialog.isShowing()) {
            settingDialog.dismiss();
        }
        if (shareDialog != null && shareDialog.isShowing()) {
            shareDialog.dismiss();
        }
        if (menuChangeListener != null && !isAnyonWindowShowing()) {
            menuChangeListener.changeCallBack(false);
        }
        ScreenUtils.full(activity, true);
    }


    @Override
    public void closeBottomWindow() {
        if (bottomDialog != null && bottomDialog.isShowing()) {
            bottomDialog.dismiss();
        }
    }


    @Override
    public void openSettingWindow() {
        showThemeSettingWindow();
    }

    @Override
    public void openDirectoryWindow() {
        //阅读页-目录
        UMEventAnalyze.countEvent(activity,UMEventAnalyze.READPAGE_CATELOG);
        //打开目录
        if (bookContentCatalogueDialog == null) {
            bookContentCatalogueDialog = new BookContentCatalogueDialog(activity);
            bookContentCatalogueDialog.setChapterClickListener(chapterClickListener);
            bookContentCatalogueDialog.setOnDismissListener(new android.content.DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(android.content.DialogInterface dialog) {
                    if (menuChangeListener != null) {
                        menuChangeListener.changeCallBack(false);
                    }
                }
            });
        }
        bookContentCatalogueDialog.setOnDismissListener(new android.content.DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(android.content.DialogInterface dialog) {
                ScreenUtils.full(activity,true);
            }
        });
        ScreenUtils.full(activity,false);
        //设置全屏
        bookContentCatalogueDialog.show();
        if (menuChangeListener != null && isAnyonWindowShowing()) {
            menuChangeListener.changeCallBack(true);
        }

    }

    @Override
    public void isNight(boolean isnight) {
        titltDialog.setStyle(isnight);
    }


    /**
     * 第一个设置主题和章节的窗口是否显示
     *
     * @return
     */
    public boolean isShowing() {
        if (titltDialog != null && bottomDialog != null) {
            return titltDialog.isShowing() && bottomDialog.isShowing();
        } else {
            return false;
        }
    }

    /**
     * 任何一个窗口是否显示
     *
     * @return
     */
    public boolean isAnyonWindowShowing() {

        return isShowing() || isSettingShow() || isCatalogShowing() || isShareShowing();
    }


    /**
     * 分享窗口是否显示
     *
     * @return
     */
    private boolean isShareShowing() {
        if (shareDialog != null) {
            return shareDialog.isShowing();
        } else {
            return false;
        }
    }


    /**
     * 目录是否显示
     *
     * @return
     */
    public boolean isCatalogShowing() {
        if (bookContentCatalogueDialog != null) {
            return bookContentCatalogueDialog.isShowing();

        } else {
            return false;
        }
    }


    /**
     * 第二个设置背景的窗口是否显示
     *
     * @return
     */
    public boolean isSettingShow() {
        if (titltDialog != null && settingDialog != null) {
            return titltDialog.isShowing() && settingDialog.isShowing();
        } else {
            return false;
        }
    }

    public void setChapterChengeListener(BookContentSettingListener bookContentSettingListener) {
        this.bookContentSettingListener = bookContentSettingListener;
    }

    public void setChapterClickListener(ChapterClickListener chapterClickListener) {
        this.chapterClickListener = chapterClickListener;
    }


    /**
     * 打开分享窗口
     */
    public void showShareWindow() {
        if (shareDialog == null) {
            shareDialog = new BookContentShareDialog(activity);
        }
        shareDialog.show();

        if (menuChangeListener != null && isAnyonWindowShowing()) {
            menuChangeListener.changeCallBack(true);
        }
        shareDialog.setOnDismissListener(new android.content.DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(android.content.DialogInterface dialog) {
                if (menuChangeListener != null && !isAnyonWindowShowing()) {
                    menuChangeListener.changeCallBack(false);
                }
            }
        });
    }


    public void setMenuChangeListener(MenuChangeListener menuChangeListener) {
        this.menuChangeListener = menuChangeListener;
    }

    /**
     * 分享后activity的回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (shareDialog != null) {
            shareDialog.onActivityResult(requestCode, resultCode, data);
        }
    }
}
