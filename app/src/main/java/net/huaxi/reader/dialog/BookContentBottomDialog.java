package net.huaxi.reader.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.tools.commonlibs.common.CommonApp;
import com.tools.commonlibs.dialog.BaseDialog;
import net.huaxi.reader.appinterface.DialogInterface;
import net.huaxi.reader.book.BookContentSettings;
import net.huaxi.reader.book.SharedPreferenceUtil;
import net.huaxi.reader.book.datasource.DataSourceManager;
import net.huaxi.reader.common.Constants;
import net.huaxi.reader.util.UMEventAnalyze;

import net.huaxi.reader.R;

import net.huaxi.reader.book.render.BookContentSettingListener;

/**
 * Created by Saud on 15/12/30.
 */
public class BookContentBottomDialog extends BaseDialog {

    private final Activity activity;
    private final View llMore;
    private View ivModel;
    private View tvNext;
    private View tvPrevious;
    private View flTaost;
    private View.OnClickListener onClick;
    private View llDirectory;
    private View llComment;
    private TextView tvChapter;
    private TextView tvProgress;
    private View llSetting;
    private SeekBar sbChapter;
    private DialogInterface dialogCloseListener;
    private boolean isNight = false;
    private BookContentSettingListener bookContentSettingListener;
    private int count;
    private int mProgress;


    public BookContentBottomDialog(Activity activity) {

        this.activity = activity;

        initDialog(activity, null, R.layout.dialog_bookcontent_bottom, BaseDialog.TYPE_BOTTOM_CENTER_NOT_FOCUSABLE, true);
        mDialog.getWindow().setWindowAnimations(R.style.bottomPopupDialog);

        llDirectory = mDialog.findViewById(R.id.ll_bookconten_directory);
        llComment = mDialog.findViewById(R.id.ll_bookconten_comment);
        llSetting = mDialog.findViewById(R.id.ll_bookconten_setting);
        tvPrevious = mDialog.findViewById(R.id.tv_bookcontent_setting_previous);
        flTaost = mDialog.findViewById(R.id.fl_bookcontent_chater_taost);
        tvNext = mDialog.findViewById(R.id.tv_bookcontent_setting_next);
        ivModel = mDialog.findViewById(R.id.iv_setting_model);
        tvChapter = (TextView) mDialog.findViewById(R.id.tv_bookcontent_chapter);
        tvProgress = (TextView) mDialog.findViewById(R.id.tv_bookcontent_progress);
        sbChapter = (SeekBar) mDialog.findViewById(R.id.sb_chapter);
        llMore = mDialog.findViewById(R.id.ll_bookconten_more);

        onClick = new ButtonClick(activity);
        llDirectory.setOnClickListener(onClick);
        llComment.setOnClickListener(onClick);
        llSetting.setOnClickListener(onClick);
        tvPrevious.setOnClickListener(onClick);
        tvNext.setOnClickListener(onClick);
        ivModel.setOnClickListener(onClick);
        llMore.setOnClickListener(onClick);
        sbChapter.setOnSeekBarChangeListener(seekBarChangeListener);
        //设置当前阅读的进度

    }


    public Dialog getDialog() {
        return mDialog;
    }

    public void show() {
        isNight = isNightStyle();
        setStyle();
        mDialog.show();
    }

    //设置切换模式按键的显示样式
    private void setStyle() {
        mProgress = DataSourceManager.getSingleton().getCurrentChapterNo();
        count = DataSourceManager.getSingleton().getChapterCount();
        if (count == 0) {
            count = 1;
        }
        sbChapter.setMax(count);
        sbChapter.setProgress(mProgress);
        ivModel.setSelected(isNight);
    }


    public void setDialogCloseListener(DialogInterface dialogCloseListener) {
        this.dialogCloseListener = dialogCloseListener;
    }

    public void setChapterChengeListener(BookContentSettingListener bookContentSettingListener) {
        this.bookContentSettingListener = bookContentSettingListener;
    }

    //获取当前是否为夜晚主题
    private boolean isNightStyle() {
        return Constants.THEME_NIGHT == SharedPreferenceUtil.getInstanceSharedPreferenceUtil().getBookContentTheme();
    }


    public class ButtonClick implements View.OnClickListener {

        private Activity activity;

        public ButtonClick(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void onClick(View v) {


            switch (v.getId()) {
                case R.id.ll_bookconten_directory:
                    if (dialogCloseListener != null) {//打开主题设置的回调
                        dialogCloseListener.closeSettingWindow();
                        dialogCloseListener.openDirectoryWindow();
                    }
                    break;
                case R.id.ll_bookconten_comment:
                    if (dialogCloseListener != null) {
                        dialogCloseListener.closeSettingWindow();
                    }
                    if (bookContentSettingListener != null) {
                        bookContentSettingListener.onComment();
                    }
                    break;
                case R.id.ll_bookconten_setting:
                    if (dialogCloseListener != null) {
                        dialogCloseListener.closeBottomWindow();
                        dialogCloseListener.openSettingWindow();
                    }
                    break;
                case R.id.tv_bookcontent_setting_previous:
                    if (bookContentSettingListener != null) {
                        bookContentSettingListener.onPreChapter();
                    }
                    break;
                case R.id.tv_bookcontent_setting_next:
                    if (bookContentSettingListener != null) {
                        bookContentSettingListener.onNextChapter();
                    }

                    break;
                case R.id.ll_bookconten_more:
                    if (bookContentSettingListener != null) {
                        bookContentSettingListener.onMoreSetting();
                    }

                    break;
                case R.id.iv_setting_model:

                    if (isNight) {
                        ivModel.setSelected(false);
                        isNight = false;
                        //阅读页-白天
                        UMEventAnalyze.countEvent(activity, UMEventAnalyze.READPAGE_DAY_MODEL);
                        if (bookContentSettingListener != null) {
                            int theme = SharedPreferenceUtil.getInstanceSharedPreferenceUtil().getBookContentLastTheme();
                            BookContentSettings.getInstance().setTheme(theme);
                            bookContentSettingListener.onThemeChanged(theme);
                            dialogCloseListener.isNight(isNight);
                        }
                    } else {
                        ivModel.setSelected(true);
                        isNight = true;
                        //阅读页-夜晚
                        UMEventAnalyze.countEvent(activity, UMEventAnalyze.READPAGE_NIGHT_MODEL);
                        if (bookContentSettingListener != null) {
                            //保存在用的主题
                            int theme = SharedPreferenceUtil.getInstanceSharedPreferenceUtil().getBookContentTheme();
                            SharedPreferenceUtil.getInstanceSharedPreferenceUtil().saveBookContentLastTheme(theme);
                            //设置夜间主题
                            BookContentSettings.getInstance().setTheme(Constants.THEME_NIGHT);
                            bookContentSettingListener.onThemeChanged(Constants.THEME_NIGHT);
                            dialogCloseListener.isNight(isNight);
                        }
                    }
                    break;
            }
        }
    }


    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            int chapterNum = progress;
            if (chapterNum == 0) {
                chapterNum += 1;
            }
            String str = String.format(CommonApp.context().getString(R.string.chatper_num), chapterNum);
            tvChapter.setText(str);
            if (count == 0) {
                count = 1;
            }
            tvProgress.setText((int) seekBar.getProgress() * 100 / count + "%");
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            flTaost.setVisibility(View.VISIBLE);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            flTaost.setVisibility(View.INVISIBLE);
            int chapterNum = seekBar.getProgress();
            if (chapterNum == 0) {
                chapterNum += 1;
            }
            if (bookContentSettingListener != null && seekBar != null) {
                bookContentSettingListener.onChapterChanged(chapterNum);
            }
            UMEventAnalyze.countEvent(activity, UMEventAnalyze.READPAGE_READ_PROGRESS);
        }
    };
}
