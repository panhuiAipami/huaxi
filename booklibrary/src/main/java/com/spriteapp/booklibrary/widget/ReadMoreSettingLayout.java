package com.spriteapp.booklibrary.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.callback.ReaderMoreSettingCallback;
import com.spriteapp.booklibrary.constant.Constant;
import com.spriteapp.booklibrary.enumeration.AutoSubEnum;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.ui.activity.DownloadFontsActivity;
import com.spriteapp.booklibrary.util.Constants;
import com.spriteapp.booklibrary.util.FileUtils;
import com.spriteapp.booklibrary.util.GlideUtils;
import com.spriteapp.booklibrary.util.SharedPreferencesUtil;
import com.spriteapp.booklibrary.util.ToastUtil;

/**
 * 阅读器更多设置
 * Created by kuangxiaoguo on 2017/7/14.
 */

public class ReadMoreSettingLayout extends LinearLayout {
    public static Typeface t1 = null;
    public static Typeface t2 = null;
    public static Typeface t3 = null;
    public static Typeface t4 = null;
    public static Typeface t5 = null;

    private ReaderMoreSettingCallback moreSettingCallback;
    private Activity mContext;
    private View empty_view;
    private RelativeLayout relative_center_view;
    private LinearLayout linear_right, linear_bottom_view;
    private ImageView book_cover;
    private TextView book_title;
    private TextView author_name;
    private ImageView author_avater;
    private Switch switch_button;
    private SeekBar seekBar;
    private TextView tv_reduce;
    private TextView tv_add;
    private TextView tv_text_size;
    private RadioGroup radio_font, radio_format, radio_bg_color;
    private RadioButton font0, font1, font2, font3, font4, font5;
    private int font_size = 16;
    private View mView;

    public ReaderMoreSettingCallback getMoreSettingCallback() {
        return moreSettingCallback;
    }

    public void setMoreSettingCallback(ReaderMoreSettingCallback moreSettingCallback) {
        this.moreSettingCallback = moreSettingCallback;
    }

    public void setActivity(Activity a) {
        mContext = a;
    }

    public ReadMoreSettingLayout(Context context) {
        this(context, null);
    }

    public ReadMoreSettingLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReadMoreSettingLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context c) {
        mView = LayoutInflater.from(c).inflate(R.layout.book_reader_more_setting_layout, null);
        addView(mView);
        empty_view = mView.findViewById(R.id.empty_view);
        relative_center_view = (RelativeLayout) mView.findViewById(R.id.relative_center_view);
        linear_right = (LinearLayout) mView.findViewById(R.id.linear_right);
        linear_bottom_view = (LinearLayout) mView.findViewById(R.id.linear_bottom_view);
        book_cover = (ImageView) mView.findViewById(R.id.book_cover);
        author_avater = (ImageView) mView.findViewById(R.id.author_avatar);
        book_title = (TextView) mView.findViewById(R.id.book_title);
        author_name = (TextView) mView.findViewById(R.id.author_name);
        switch_button = (Switch) mView.findViewById(R.id.switch_button);
        seekBar = (SeekBar) mView.findViewById(R.id.seekBar);
        tv_reduce = (TextView) mView.findViewById(R.id.tv_reduce);
        tv_add = (TextView) mView.findViewById(R.id.tv_add);
        tv_text_size = (TextView) mView.findViewById(R.id.tv_text_size);
        radio_font = (RadioGroup) mView.findViewById(R.id.radio_font);
        radio_format = (RadioGroup) mView.findViewById(R.id.radio_format);
        radio_bg_color = (RadioGroup) mView.findViewById(R.id.radio_bg_color);
        font0 = (RadioButton) mView.findViewById(R.id.font0);
        font1 = (RadioButton) mView.findViewById(R.id.font1);
        font2 = (RadioButton) mView.findViewById(R.id.font2);
        font3 = (RadioButton) mView.findViewById(R.id.font3);
        font4 = (RadioButton) mView.findViewById(R.id.font4);
        font5 = (RadioButton) mView.findViewById(R.id.font5);
        setListener();
        initTypeFace();

    }


    public void initRaderSetting(BookDetailResponse bookDetail) {
        if (bookDetail != null) {
            book_title.setText(bookDetail.getBook_name());
            author_name.setText(bookDetail.getAuthor_name());
            GlideUtils.loadImage(book_cover, bookDetail.getBook_image(), mContext);
            GlideUtils.loadImage(author_avater, bookDetail.getAuthor_avatar(), mContext);
        }

        boolean isAutoSub = SharedPreferencesUtil.getInstance()
                .getInt(Constant.IS_BOOK_AUTO_SUB) == AutoSubEnum.AUTO_SUB.getValue();
        switch_button.setChecked(isAutoSub);

        int brightness = SharedPreferencesUtil.getInstance().getInt(Constant.READ_PAGE_BRIGHTNESS, 100);
        if (brightness == 0) brightness += 1;
        seekBar.setProgress(brightness);
        setBrightness(mContext, brightness);

        font_size = SharedPreferencesUtil.getInstance().getInt(com.spriteapp.booklibrary.constant.Constant.READ_TEXT_SIZE_POSITION, 16);
        tv_text_size.setTextSize(font_size);

        refreshFontSelect(false);

        int format = SharedPreferencesUtil.getInstance().getInt(com.spriteapp.booklibrary.constant.Constant.READ_PAGE_FONT_FORMAT, 1);
        RadioButton radioButton;
        switch (format) {
            case 1:
                radioButton = (RadioButton) radio_format.findViewById(R.id.format1);
                radioButton.setChecked(true);
                break;
            case 2:
                radioButton = (RadioButton) radio_format.findViewById(R.id.format2);
                radioButton.setChecked(true);
                break;
            case 3:
                radioButton = (RadioButton) radio_format.findViewById(R.id.format3);
                radioButton.setChecked(true);
                break;
        }

        int bg_color = SharedPreferencesUtil.getInstance().getInt(com.spriteapp.booklibrary.constant.Constant.READ_PAGE_BG_COLOR, 1);
        RadioButton radioButtonColor;
        switch (bg_color) {
            case 1:
                radioButtonColor = (RadioButton) radio_bg_color.findViewById(R.id.reader_bg_color1);
                radioButtonColor.setChecked(true);
                break;
            case 2:
                radioButtonColor = (RadioButton) radio_bg_color.findViewById(R.id.reader_bg_color2);
                radioButtonColor.setChecked(true);
                break;
            case 3:
                radioButtonColor = (RadioButton) radio_bg_color.findViewById(R.id.reader_bg_color3);
                radioButtonColor.setChecked(true);
                break;
            case 4:
                radioButtonColor = (RadioButton) radio_bg_color.findViewById(R.id.reader_bg_color4);
                radioButtonColor.setChecked(true);
                break;
            case 5:
                radioButtonColor = (RadioButton) radio_bg_color.findViewById(R.id.reader_bg_color5);
                radioButtonColor.setChecked(true);
                break;
            case 6:
                radioButtonColor = (RadioButton) radio_bg_color.findViewById(R.id.reader_bg_color6);
                radioButtonColor.setChecked(true);
                break;

        }
    }

    public void refreshFontSelect(boolean resetPageFont) {
        int check_font = SharedPreferencesUtil.getInstance().getInt(com.spriteapp.booklibrary.constant.Constant.READ_PAGE_FONT_STYLE, 0);
        Typeface tf = null;
        if (check_font == 0) {
            font0.setChecked(true);
        } else if (check_font == 1) {
            font1.setChecked(true);
            tf = t1;
        } else if (check_font == 2) {
            font2.setChecked(true);
            tf = t2;
        } else if (check_font == 3) {
            font3.setChecked(true);
            tf = t3;
        } else if (check_font == 4) {
            font4.setChecked(true);
            tf = t4;
        } else if (check_font == 5) {
            font5.setChecked(true);
            tf = t5;
        }
        if (resetPageFont) {//修改页面字体
            if (moreSettingCallback != null)
                moreSettingCallback.sendFontStyle(tf);
        }
    }


    //夜间模式
    public void changeMode(boolean isNight) {
        linear_bottom_view.setBackgroundResource(isNight ? R.color.book_reader_read_title_night_color
                : R.color.book_reader_white);
        linear_right.setBackgroundResource(isNight ? R.color.book_reader_read_title_night_color
                : R.color.book_reader_white);
    }

    private void setListener() {
        empty_view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibility(GONE);
            }
        });
        relative_center_view.setOnClickListener(null);
        linear_bottom_view.setOnClickListener(null);

        //设置字体大小
        tv_reduce.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (font_size <= 10) {
                    ToastUtil.showSingleToast("已是最小字体");
                    return;
                }
                tv_text_size.setTextSize(font_size -= 2);
                SharedPreferencesUtil.getInstance().putInt(com.spriteapp.booklibrary.constant.Constant.READ_TEXT_SIZE_POSITION, font_size);
                if (moreSettingCallback != null)
                    moreSettingCallback.sendTextSize(font_size);
            }
        });
        tv_add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (font_size >= 30) {
                    ToastUtil.showSingleToast("已是最大字体");
                    return;
                }
                tv_text_size.setTextSize(font_size += 2);
                SharedPreferencesUtil.getInstance().putInt(com.spriteapp.booklibrary.constant.Constant.READ_TEXT_SIZE_POSITION, font_size);
                if (moreSettingCallback != null)
                    moreSettingCallback.sendTextSize(font_size);
            }
        });

        //设置字体格式
        radio_font.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (!group.findViewById(checkedId).isPressed()) return;
                if (checkedId == R.id.font0) {
                    gotoDownLoadFont(null, 0);
                } else if (checkedId == R.id.font1) {
                    gotoDownLoadFont(t1, 1);
                } else if (checkedId == R.id.font2) {
                    gotoDownLoadFont(t2, 2);
                } else if (checkedId == R.id.font3) {
                    gotoDownLoadFont(t3, 3);
                } else if (checkedId == R.id.font4) {
                    gotoDownLoadFont(t4, 4);
                } else if (checkedId == R.id.font5) {
                    gotoDownLoadFont(t5, 5);
                }
            }
        });

        //阅读页面排版
        radio_format.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (!group.findViewById(checkedId).isPressed()) return;
                int format = 0;
                if (checkedId == R.id.format1) {
                    format = 1;
                } else if (checkedId == R.id.format2) {
                    format = 2;
                } else if (checkedId == R.id.format3) {
                    format = 3;
                }
                SharedPreferencesUtil.getInstance().putInt(com.spriteapp.booklibrary.constant.Constant.READ_PAGE_FONT_FORMAT, format);
                if (moreSettingCallback != null)
                    moreSettingCallback.sengFontFormat(format);
            }
        });

        //阅读背景颜色
        radio_bg_color.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (!group.findViewById(checkedId).isPressed()) return;
                int color = 0;
                if (checkedId == R.id.reader_bg_color1) {
                    color = 1;
                } else if (checkedId == R.id.reader_bg_color2) {
                    color = 2;
                } else if (checkedId == R.id.reader_bg_color3) {
                    color = 3;
                } else if (checkedId == R.id.reader_bg_color4) {
                    color = 4;
                } else if (checkedId == R.id.reader_bg_color5) {
                    color = 5;
                } else if (checkedId == R.id.reader_bg_color6) {
                    color = 6;
                }
                SharedPreferencesUtil.getInstance().putInt(com.spriteapp.booklibrary.constant.Constant.READ_PAGE_BG_COLOR, color);
                if (moreSettingCallback != null)
                    moreSettingCallback.sengReaderBgColor(color);
            }
        });

        //自动订阅按钮
        switch_button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtil.getInstance().putInt(Constant.IS_BOOK_AUTO_SUB,
                        isChecked ? AutoSubEnum.AUTO_SUB.getValue()
                                : AutoSubEnum.NOT_AUTO_SUB.getValue());
            }
        });

        //亮度调节
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mContext != null)
                    setBrightness(mContext, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    /**
     * 设置当前Activity显示时的亮度
     * 屏幕亮度最大数值一般为255，各款手机有所不同
     * screenBrightness 的取值范围在[0,1]之间
     */
    public static void setBrightness(Activity activity, int brightness) {
        SharedPreferencesUtil.getInstance().putInt(Constant.READ_PAGE_BRIGHTNESS, brightness);
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.screenBrightness = Float.valueOf(brightness) * (1f / 255f);
        activity.getWindow().setAttributes(lp);
    }

    public static void initTypeFace() {
        try {
            if (t1 == null && FileUtils.isExists(getFontPath(DownloadFontsActivity.hei))) {
                t1 = Typeface.createFromFile(getFontPath(DownloadFontsActivity.hei));
            }
            if (t2 == null && FileUtils.isExists(getFontPath(DownloadFontsActivity.siyuan))) {
                t2 = Typeface.createFromFile(getFontPath(DownloadFontsActivity.siyuan));
            }
            if (t3 == null && FileUtils.isExists(getFontPath(DownloadFontsActivity.shu))) {
                t3 = Typeface.createFromFile(getFontPath(DownloadFontsActivity.shu));
            }
            if (t4 == null && FileUtils.isExists(getFontPath(DownloadFontsActivity.kai))) {
                t4 = Typeface.createFromFile(getFontPath(DownloadFontsActivity.kai));
            }
            if (t5 == null && FileUtils.isExists(getFontPath(DownloadFontsActivity.fangsong))) {
                t5 = Typeface.createFromFile(getFontPath(DownloadFontsActivity.fangsong));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getFontPath(String font) {
        return Constants.DOWNLOAD_FONTS + "/" + font;
    }

    public void gotoDownLoadFont(Typeface typeface, int font_num) {
        if (font_num != 0 && typeface == null) {
            mContext.startActivity(new Intent(mContext, DownloadFontsActivity.class));
        } else {
            //修改页面字体
            SharedPreferencesUtil.getInstance().putInt(com.spriteapp.booklibrary.constant.Constant.READ_PAGE_FONT_STYLE, font_num);
            if (moreSettingCallback != null)
                moreSettingCallback.sendFontStyle(typeface);
        }
    }

    public static int getReaderBgCoor(int color_type) {
        int color = R.color.reader_book_bg1;
        switch (color_type) {
            case 1:
                color = R.color.reader_book_bg1;
                break;
            case 2:
                color = R.color.reader_book_bg2;
                break;
            case 3:
                color = R.color.reader_book_bg3;
                break;
            case 4:
                color = R.color.reader_book_bg4;
                break;
            case 5:
                color = R.color.reader_book_bg5;
                break;
            case 6:
                color = R.color.reader_book_bg6;
                break;
        }
        return color;
    }

}
