package net.huaxi.reader.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.tools.commonlibs.dialog.BaseDialog;
import net.huaxi.reader.book.BookContentSettings;
import net.huaxi.reader.common.Constants;
import net.huaxi.reader.common.Utility;
import net.huaxi.reader.util.UMEventAnalyze;

import net.huaxi.reader.R;
import net.huaxi.reader.book.BookContentModel;
import net.huaxi.reader.book.SharedPreferenceUtil;
import net.huaxi.reader.book.render.BookContentSettingListener;
import net.huaxi.reader.book.render.ReadPageState;
import net.huaxi.reader.util.ScreenLightUtils;

/**
 * Created by Saud on 15/12/30.
 */
public class BookContentBottomSettingDialog extends BaseDialog {

    private final TextView tvSize;
    private BookContentModel bookContentModel;
    private Activity activity;
    private RadioButton rbBackground1;
    private RadioButton rbBackground2;
    private RadioButton rbBackground3;
    private RadioButton rbBackground4;
    private LinearLayout llBackgroundSetting;
    private View.OnClickListener onClick;
    private View flTextsizeMin;
    private View flTextsizeMax;
    private SeekBar sbLight;
    private BookContentSettingListener bookContentSettingListener;
    private int style;

    public BookContentBottomSettingDialog(Activity activity, BookContentModel bookContentModel) {

        this.activity = activity;
        this.bookContentModel = bookContentModel;

        initDialog(activity, null, R.layout.dialog_bookcontent_bottom_setting, BaseDialog.TYPE_BOTTOM_CENTER_NOT_FOCUSABLE, true);
        mDialog.getWindow().setWindowAnimations(R.style.bottomPopupDialog);


        flTextsizeMin = mDialog.findViewById(R.id.fl_bookconten_textsize_min);
        flTextsizeMax = mDialog.findViewById(R.id.fl_bookconten_textsize_max);
        sbLight = (SeekBar) mDialog.findViewById(R.id.sb_light);
        llBackgroundSetting = (LinearLayout) mDialog.findViewById(R.id.ll_bookcontent_background_setting);

        rbBackground1 = (RadioButton) mDialog.findViewById(R.id.rb_bookcontent_bg_setting1);
        rbBackground2 = (RadioButton) mDialog.findViewById(R.id.rb_bookcontent_bg_setting2);
        rbBackground3 = (RadioButton) mDialog.findViewById(R.id.rb_bookcontent_bg_setting3);
        rbBackground4 = (RadioButton) mDialog.findViewById(R.id.rb_bookcontent_bg_setting4);
        tvSize = (TextView) mDialog.findViewById(R.id.tv_size);
        onClick = new ButtonClick(activity);
        flTextsizeMin.setOnClickListener(onClick);
        flTextsizeMax.setOnClickListener(onClick);
        rbBackground1.setOnClickListener(onClick);
        rbBackground2.setOnClickListener(onClick);
        rbBackground3.setOnClickListener(onClick);
        rbBackground4.setOnClickListener(onClick);
        sbLight.setOnSeekBarChangeListener(seekBarChangeListener);

        int lightNum = ScreenLightUtils.getSaveBrightness();
        sbLight.setProgress(lightNum);


    }

    public void show() {
        setStyle();

        initText();

        if (bookContentModel.getBookState() == ReadPageState.BOOKTYPE_NORMAL) {//页面字体是否可设置
            flTextsizeMin.setClickable(true);
            flTextsizeMax.setClickable(true);
        } else {
            flTextsizeMin.setClickable(false);
            flTextsizeMax.setClickable(false);

        }
        mDialog.show();
    }

    /**
     * 获取字号大小，并显示
     */
    private void initText() {
        int textSize = BookContentSettings.getInstance().getTextSize();
        if (textSize != 0) {
            tvSize.setText(Utility.px2dip(activity,textSize) + "");
        }
    }

    private void setStyle() {
        //设置radiobutton的样式
        style = SharedPreferenceUtil.getInstanceSharedPreferenceUtil().getBookContentTheme();
        if (style == Constants.THEME_NIGHT) {
            style = SharedPreferenceUtil.getInstanceSharedPreferenceUtil().getBookContentLastTheme();
        }
        setRadioButtonFalse();
        switch (style) {
            case 0:
                rbBackground1.setChecked(true);
                UMEventAnalyze.countEvent(activity,UMEventAnalyze.READPAGE_BACKGROUND_YELLOW);
                break;
            case 1:
                rbBackground2.setChecked(true);
                UMEventAnalyze.countEvent(activity,UMEventAnalyze.READPAGE_BACKGROUND_WHITE);
                break;
            case 2:
                rbBackground3.setChecked(true);
                UMEventAnalyze.countEvent(activity,UMEventAnalyze.READPAGE_BACKGROUND_PINK);
                break;
            case 3:
                rbBackground4.setChecked(true);
                UMEventAnalyze.countEvent(activity,UMEventAnalyze.READPAGE_BACKGROUND_GREEN);
                break;
            default:
                break;
        }
    }

    public Dialog getDialog() {
        return mDialog;
    }

    public void setChapterChengeListener(BookContentSettingListener bookContentSettingListener) {
        this.bookContentSettingListener = bookContentSettingListener;
    }


    public class ButtonClick implements View.OnClickListener {

        private Activity activity;

        public ButtonClick(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void onClick(View v) {

            if (v instanceof CompoundButton) {
                setRadioButtonCheckedFalse((CompoundButton) v);
            }

            switch (v.getId()) {
                case R.id.fl_bookconten_textsize_min:
                    if (bookContentSettingListener != null) {
                        bookContentSettingListener.onTextSizeSmall();
                        initText();
                    }
                    UMEventAnalyze.countEvent(activity,UMEventAnalyze.READPAGE_TEXTSIZE_MIN);
                    break;
                case R.id.fl_bookconten_textsize_max:
                    if (bookContentSettingListener != null) {
                        bookContentSettingListener.onTextSizeBig();
                        initText();
                    }
                    UMEventAnalyze.countEvent(activity,UMEventAnalyze.READPAGE_TEXTSIZE_MAX);
                    break;
                case R.id.rb_bookcontent_bg_setting1:
                    if (bookContentSettingListener != null) {
                        SharedPreferenceUtil.getInstanceSharedPreferenceUtil().saveBookContentTheme(Constants.THEME_WHITE);
                        bookContentSettingListener.onThemeChanged(Constants.THEME_WHITE);
                    }
                    break;
                case R.id.rb_bookcontent_bg_setting2:
                    if (bookContentSettingListener != null) {
                        SharedPreferenceUtil.getInstanceSharedPreferenceUtil().saveBookContentTheme(Constants.THEME_PINK);
                        bookContentSettingListener.onThemeChanged(Constants.THEME_PINK);
                    }
                    break;
                case R.id.rb_bookcontent_bg_setting3:
                    if (bookContentSettingListener != null) {
                        SharedPreferenceUtil.getInstanceSharedPreferenceUtil().saveBookContentTheme(Constants.THEME_GREEN);
                        bookContentSettingListener.onThemeChanged(Constants.THEME_GREEN);
                    }
                    break;
                case R.id.rb_bookcontent_bg_setting4:
                    if (bookContentSettingListener != null) {
                        SharedPreferenceUtil.getInstanceSharedPreferenceUtil().saveBookContentTheme(Constants.THEME_YELLOW);
                        bookContentSettingListener.onThemeChanged(Constants.THEME_YELLOW);
                    }
                    break;

            }

        }
    }


    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            ScreenLightUtils.setScreenBrightness(activity, progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            UMEventAnalyze.countEvent(activity,UMEventAnalyze.READPAGE_LIGHT_CHANGE);
            if (bookContentSettingListener != null && seekBar != null) {
                ScreenLightUtils.savaScreenBrightness(seekBar.getProgress());
            }
        }
    };


    private void setRadioButtonCheckedFalse(CompoundButton buttonView) {
        setRadioButtonFalse();
        buttonView.setChecked(true);
    }

    private void setRadioButtonFalse() {
        rbBackground1.setChecked(false);
        rbBackground2.setChecked(false);
        rbBackground3.setChecked(false);
        rbBackground4.setChecked(false);
    }


}
